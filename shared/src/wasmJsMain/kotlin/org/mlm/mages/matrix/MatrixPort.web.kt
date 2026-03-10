package org.mlm.mages.matrix

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import org.mlm.mages.AttachmentInfo
import org.mlm.mages.MessageEvent
import org.mlm.mages.RoomSummary
import org.mlm.mages.platform.clearWebBlob
import org.mlm.mages.platform.retrieveWebBlob
import kotlin.js.JsAny

class WebStubMatrixPort : MatrixPort {
    private var facade: WebMatrixFacade? = null
    private var currentHs: String? = null
    private var currentAccountId: String? = null

    private fun requireFacade(): WebMatrixFacade {
        return facade ?: error("Matrix client not initialized. Wait for init call.")
    }

    private fun decodeTimelineDiff(diffJson: String): TimelineDiff<MessageEvent>? {
        val obj = wasmJson.parseToJsonElement(diffJson) as? JsonObject ?: return null
        if (obj.isEmpty()) return null

        val entry = obj.entries.first()
        val key = entry.key
        val value = entry.value as? JsonObject

        return when (key) {
            "Reset" -> TimelineDiff.Reset(
                wasmJson.decodeFromJsonElement<List<MessageEvent>>(value?.get("values") ?: return null)
            )

            "Clear" -> TimelineDiff.Clear()

            "Append" -> TimelineDiff.Append(
                wasmJson.decodeFromJsonElement<List<MessageEvent>>(value?.get("values") ?: return null)
            )

            "PushBack" -> TimelineDiff.Append(
                listOf(wasmJson.decodeFromJsonElement<MessageEvent>(value?.get("value") ?: return null))
            )

            "PushFront" -> TimelineDiff.Prepend(
                wasmJson.decodeFromJsonElement<MessageEvent>(value?.get("value") ?: return null)
            )

            "UpdateByItemId" -> TimelineDiff.UpdateByItemId(
                itemId = (value?.get("item_id") as? JsonPrimitive)?.content ?: return null,
                item = wasmJson.decodeFromJsonElement<MessageEvent>(value["value"] ?: return null)
            )

            "RemoveByItemId" -> TimelineDiff.RemoveByItemId(
                itemId = (value?.get("item_id") as? JsonPrimitive)?.content ?: return null,
            )

            "UpsertByItemId" -> TimelineDiff.UpsertByItemId(
                itemId = (value?.get("item_id") as? JsonPrimitive)?.content ?: return null,
                item = wasmJson.decodeFromJsonElement<MessageEvent>(value["value"] ?: return null)
            )

            else -> null
        }
    }

    override suspend fun init(hs: String, accountId: String?) {
        if (currentHs == hs && currentAccountId == accountId && facade != null) return

        ensureWasmBridgeReady()
        facade?.free()
        facade = createWebMatrixFacade(
            hs,
            org.mlm.mages.platform.MagesPaths.storeDir(),
            accountId,
        )
        currentHs = hs
        currentAccountId = accountId
    }

    override suspend fun login(user: String, password: String, deviceDisplayName: String?) {
        val result = requireFacade().login(user, password, deviceDisplayName).await<kotlin.js.JsAny?>()
        val error = result?.toString()?.takeIf { it.isNotBlank() }
        if (error != null) {
            throw IllegalStateException(error)
        }
        if (!isLoggedIn()) {
            throw IllegalStateException("Login failed")
        }
    }

    override suspend fun listRooms(): List<RoomSummary> =
        run {
            val raw = requireFacade().listRooms().await<WebRoomSummaryListValue?>()
            val arr = raw.toJsonArray()
            wasmJson.decodeFromJsonElement<List<RoomSummary>>(arr)
        }

    override suspend fun recent(roomId: String, limit: Int): List<MessageEvent> =
        wasmJson.decodeFromJsonElement(requireFacade().getRoomTimeline(roomId, limit).await<WebTimelineItemsValue?>().toJsonArray())

    override fun timelineDiffs(roomId: String): Flow<TimelineDiff<MessageEvent>> = callbackFlow {
        val subscription = requireFacade().observeTimeline(
            roomId = roomId,
            onDiff = { diffJson ->
                val diff = diffJson?.let(::decodeTimelineDiff) ?: return@observeTimeline
                trySend(diff)
            },
            onError = { error ->
                val message = error ?: "Timeline error"
                close(IllegalStateException(message))
            }
        )
        awaitClose {
            requireFacade().unobserveTimeline(subscription)
        }
    }

    override suspend fun send(roomId: String, body: String, formattedBody: String?): Boolean {
        val result = requireFacade().sendText(roomId, body, formattedBody).await<WebSendResultValue?>().toJsonObject()
        return (result?.get("ok") as? JsonPrimitive)?.content == "true"
    }

    override suspend fun sendQueueSetEnabled(enabled: Boolean): Boolean =
        requireFacade().sendQueueSetEnabled(enabled)

    override suspend fun roomSendQueueSetEnabled(roomId: String, enabled: Boolean): Boolean =
        requireFacade().roomSendQueueSetEnabled(roomId, enabled)

    override suspend fun sendExistingAttachment(
        roomId: String,
        attachment: AttachmentInfo,
        body: String?,
        onProgress: ((Long, Long?) -> Unit)?
    ): Boolean = requireFacade().sendExistingAttachment(roomId, wasmJson.encodeToString(attachment), body)

    override fun isLoggedIn(): Boolean = facade?.isLoggedIn() == true

    override fun close() {
        facade?.free()
        facade = null
        currentHs = null
        currentAccountId = null
    }

    override suspend fun setTyping(roomId: String, typing: Boolean): Boolean =
        requireFacade().setTyping(roomId, typing)

    override fun whoami(): String? = facade?.whoami()

    override fun accountManagementUrl(): String? = null

    override fun setupRecovery(observer: MatrixPort.RecoveryObserver): ULong = 0uL

    override fun observeRecoveryState(observer: MatrixPort.RecoveryStateObserver): ULong = 0uL

    override fun unobserveRecoveryState(subId: ULong): Boolean = false

    override fun observeBackupState(observer: MatrixPort.BackupStateObserver): ULong = 0uL

    override fun unobserveBackupState(subId: ULong): Boolean = false

    override suspend fun backupExistsOnServer(fetch: Boolean): Boolean = false

    override suspend fun setKeyBackupEnabled(enabled: Boolean): Boolean = false

    override suspend fun enqueueText(roomId: String, body: String, txnId: String?): String {
        val ok = send(roomId, body, null)
        return if (ok) (txnId ?: "web-send-$roomId-${body.hashCode()}") else ""
    }

    override fun observeSends(): Flow<SendUpdate> = callbackFlow {
        val token = requireFacade().observeSends { updateValue ->
            val update = runCatching {
                wasmJson.decodeFromJsonElement<SendUpdate>(updateValue.toJsonElement())
            }.getOrNull() ?: return@observeSends
            trySend(update)
        }
        awaitClose { requireFacade().unobserveSends(token) }
    }

    override suspend fun roomTags(roomId: String): Pair<Boolean, Boolean>? = null

    override suspend fun setRoomFavourite(roomId: String, favourite: Boolean): Result<Unit> =
        Result.failure(UnsupportedOperationException())

    override suspend fun setRoomLowPriority(roomId: String, lowPriority: Boolean): Result<Unit> =
        Result.failure(UnsupportedOperationException())

    override suspend fun thumbnailToCache(
        info: AttachmentInfo,
        width: Int,
        height: Int,
        crop: Boolean
    ): Result<String> = runCatching {
        requireFacade().thumbnailToCache(wasmJson.encodeToString(info), width, height, crop)
            ?: error("Thumbnail generation failed")
    }

    override fun observeConnection(observer: MatrixPort.ConnectionObserver): ULong = 0uL

    override fun stopConnectionObserver(token: ULong) {}

    override fun startVerificationInbox(observer: MatrixPort.VerificationInboxObserver): ULong = 0uL

    override fun stopVerificationInbox(token: ULong) {}

    override suspend fun retryByTxn(roomId: String, txnId: String): Boolean = false

    override fun stopTypingObserver(token: ULong) {
        requireFacade().unobserveTyping(token.toDouble())
    }

    override suspend fun paginateBack(roomId: String, count: Int): Boolean =
        requireFacade().paginateBackwards(roomId, count)

    override suspend fun paginateForward(roomId: String, count: Int): Boolean =
        requireFacade().paginateForwards(roomId, count)

    override suspend fun markRead(roomId: String): Boolean = requireFacade().markRead(roomId)

    override suspend fun markReadAt(roomId: String, eventId: String): Boolean =
        requireFacade().markReadAt(roomId, eventId)

    override suspend fun react(roomId: String, eventId: String, emoji: String): Boolean =
        requireFacade().react(roomId, eventId, emoji)

    override suspend fun reply(
        roomId: String,
        inReplyToEventId: String,
        body: String,
        formattedBody: String?
    ): Boolean = requireFacade().reply(roomId, inReplyToEventId, body)

    override suspend fun edit(
        roomId: String,
        targetEventId: String,
        newBody: String,
        formattedBody: String?
    ): Boolean = requireFacade().edit(roomId, targetEventId, newBody)

    override suspend fun redact(roomId: String, eventId: String, reason: String?): Boolean =
        requireFacade().redact(roomId, eventId, reason)

    override suspend fun getUserPowerLevel(roomId: String, userId: String): Long = 0

    override suspend fun getPinnedEvents(roomId: String): List<String> = emptyList()

    override suspend fun setPinnedEvents(roomId: String, eventIds: List<String>): Boolean = false

    override fun observeTyping(roomId: String, onUpdate: (List<String>) -> Unit): ULong =
        requireFacade().observeTyping(roomId) { users ->
            val parsed = runCatching {
                wasmJson.decodeFromJsonElement<List<String>>(users.toJsonElement())
            }.getOrDefault(emptyList())
            onUpdate(parsed)
        }.toULong()

    override fun startSupervisedSync(observer: MatrixPort.SyncObserver) {
        facade?.startSync { stateValue ->
            val state = runCatching {
                wasmJson.decodeFromJsonElement<MatrixPort.SyncStatus>(stateValue.toJsonElement())
            }.getOrElse {
                MatrixPort.SyncStatus(MatrixPort.SyncPhase.Error, it.message)
            }
            observer.onState(state)
        }
    }

    override suspend fun listMyDevices(): List<DeviceSummary> = emptyList()

    override suspend fun startSelfSas(targetDeviceId: String, observer: VerificationObserver): String = ""

    override suspend fun startUserSas(userId: String, observer: VerificationObserver): String = ""

    override suspend fun acceptVerificationRequest(
        flowId: String,
        otherUserId: String?,
        observer: VerificationObserver
    ): Boolean = false

    override suspend fun acceptSas(
        flowId: String,
        otherUserId: String?,
        observer: VerificationObserver
    ): Boolean = false

    override suspend fun confirmVerification(flowId: String): Boolean = false

    override suspend fun cancelVerification(flowId: String): Boolean = false

    override suspend fun cancelVerificationRequest(flowId: String, otherUserId: String?): Boolean = false

    override fun enterForeground() {}

    override fun enterBackground() {}

    override suspend fun logout(): Boolean {
        val ignored = requireFacade().logout().await<JsAny?>()
        facade?.free()
        facade = null
        currentHs = null
        currentAccountId = null
        return true
    }

    override suspend fun checkVerificationRequest(userId: String, flowId: String): Boolean = false

    override suspend fun sendAttachmentFromPath(
        roomId: String,
        path: String,
        mime: String,
        filename: String?,
        onProgress: ((Long, Long?) -> Unit)?
    ): Boolean {
        val bytes = retrieveWebBlob(path)
        if (bytes != null) {
            clearWebBlob(path)
            return sendAttachmentBytes(roomId, bytes, mime, filename ?: path)
        }
        return false
    }

    override suspend fun sendAttachmentBytes(
        roomId: String,
        data: ByteArray,
        mime: String,
        filename: String,
        onProgress: ((Long, Long?) -> Unit)?
    ): Boolean = requireFacade().sendAttachmentBytes(roomId, filename, mime, data.toJsReference())

    override suspend fun downloadAttachmentToCache(
        info: AttachmentInfo,
        filenameHint: String?
    ): Result<String> = runCatching {
        requireFacade().downloadAttachmentToCacheFile(wasmJson.encodeToString(info), filenameHint)
            ?: error("Attachment download failed")
    }

    override suspend fun downloadAttachmentToPath(
        info: AttachmentInfo,
        savePath: String,
        onProgress: ((Long, Long?) -> Unit)?
    ): Result<String> = Result.failure(UnsupportedOperationException())

    override suspend fun searchRoom(
        roomId: String,
        query: String,
        limit: Int,
        offset: Int?
    ): SearchPage = SearchPage(emptyList(), null)

    override suspend fun recoverWithKey(recoveryKey: String): Boolean = false

    override fun observeReceipts(roomId: String, observer: ReceiptsObserver): ULong = 0uL

    override fun stopReceiptsObserver(token: ULong) {}

    override suspend fun dmPeerUserId(roomId: String): String? = null

    override suspend fun isEventReadBy(roomId: String, eventId: String, userId: String): Boolean = false

    override fun startCallInbox(observer: MatrixPort.CallObserver): ULong = 0uL

    override fun stopCallInbox(token: ULong) {}

    override suspend fun registerUnifiedPush(
        appId: String,
        pushKey: String,
        gatewayUrl: String,
        deviceName: String,
        lang: String,
        profileTag: String?
    ): Boolean = false

    override suspend fun unregisterUnifiedPush(appId: String, pushKey: String): Boolean = false

    override suspend fun roomUnreadStats(roomId: String): UnreadStats? = null

    override suspend fun ownLastRead(roomId: String): Pair<String?, Long?> = null to null

    override fun observeOwnReceipt(roomId: String, observer: ReceiptsObserver): ULong = 0uL

    override suspend fun markFullyReadAt(roomId: String, eventId: String): Boolean = false

    override suspend fun encryptionCatchupOnce(): Boolean = false

    override fun observeRoomList(observer: MatrixPort.RoomListObserver): ULong {
        val token = requireFacade().observeRoomList(
            onReset = { itemsValue ->
                val raw = runCatching { itemsValue.toJsonArray() }.getOrElse {
                    kotlinx.serialization.json.JsonArray(emptyList())
                }
                val items = runCatching {
                    wasmJson.decodeFromJsonElement<List<RoomListEntry>>(raw)
                }.getOrDefault(emptyList())
                observer.onReset(items)
            },
            onUpdate = { itemValue ->
                val item = runCatching {
                    wasmJson.decodeFromJsonElement<RoomListEntry>(itemValue.toJsonElement())
                }.getOrNull()
                if (item != null) {
                    observer.onUpdate(item)
                }
            }
        )
        return token.toULong()
    }

    override fun unobserveRoomList(token: ULong) {
        requireFacade().unobserveRoomList(token.toDouble())
    }

    override suspend fun fetchNotification(roomId: String, eventId: String): RenderedNotification? = null

    override suspend fun fetchNotificationsSince(
        sinceMs: Long,
        maxRooms: Int,
        maxEvents: Int
    ): List<RenderedNotification> = emptyList()

    override fun roomListSetUnreadOnly(token: ULong, unreadOnly: Boolean): Boolean =
        requireFacade().setRoomListUnreadOnly(token.toDouble(), unreadOnly)

    override suspend fun loginSsoLoopback(openUrl: (String) -> Boolean, deviceName: String?): Boolean = false

    override suspend fun loginOauthLoopback(openUrl: (String) -> Boolean, deviceName: String?): Boolean = false

    override suspend fun homeserverLoginDetails(): HomeserverLoginDetails =
        wasmJson.decodeFromJsonElement(
            requireFacade().homeserverLoginDetails().toJsonElement()
        )

    override suspend fun searchUsers(term: String, limit: Int): List<DirectoryUser> = emptyList()

    override suspend fun getUserProfile(userId: String): DirectoryUser? = null

    override suspend fun publicRooms(
        server: String?,
        search: String?,
        limit: Int,
        since: String?
    ): PublicRoomsPage =
        wasmJson.decodeFromJsonElement(
            requireFacade().publicRooms(server, search, limit, since).await<WebPublicRoomsPageValue?>().toJsonElement()
        )

    override suspend fun joinByIdOrAlias(idOrAlias: String): Result<Unit> = Result.failure(UnsupportedOperationException()) // requireFacade().joinByIdOrAlias(idOrAlias)

    override suspend fun ensureDm(userId: String): String? = requireFacade().ensureDm(userId)

    override suspend fun resolveRoomId(idOrAlias: String): String? = requireFacade().resolveRoomId(idOrAlias)

    override suspend fun listInvited(): List<RoomProfile> = emptyList()

    override suspend fun acceptInvite(roomId: String): Boolean = false

    override suspend fun leaveRoom(roomId: String): Result<Unit> =
        Result.failure(UnsupportedOperationException())

    override suspend fun createRoom(
        name: String?,
        topic: String?,
        invitees: List<String>,
        isPublic: Boolean,
        roomAlias: String?
    ): String? = null

    override suspend fun setRoomName(roomId: String, name: String): Result<Unit> =
        Result.failure(UnsupportedOperationException())

    override suspend fun setRoomTopic(roomId: String, topic: String): Result<Unit> =
        Result.failure(UnsupportedOperationException())

    override suspend fun roomProfile(roomId: String): RoomProfile? = null

    override suspend fun roomNotificationMode(roomId: String): RoomNotificationMode? = null

    override suspend fun setRoomNotificationMode(
        roomId: String,
        mode: RoomNotificationMode
    ): Result<Unit> = Result.failure(UnsupportedOperationException())

    override suspend fun listMembers(roomId: String): List<MemberSummary> =
        wasmJson.decodeFromJsonElement(requireFacade().listMembers(roomId).toJsonElement())

    override suspend fun reactions(roomId: String, eventId: String): List<ReactionChip> =
        wasmJson.decodeFromJsonElement(requireFacade().reactionsForEvent(roomId, eventId).toJsonElement())

    override suspend fun reactionsBatch(
        roomId: String,
        eventIds: List<String>
    ): Map<String, List<ReactionChip>> =
        wasmJson.decodeFromJsonElement(
            requireFacade().reactionsBatch(roomId, wasmJson.encodeToString(eventIds)).toJsonElement()
        )

    override suspend fun sendThreadText(
        roomId: String,
        rootEventId: String,
        body: String,
        replyToEventId: String?,
        latestEventId: String?,
        formattedBody: String?
    ): Boolean = false

    override suspend fun threadSummary(
        roomId: String,
        rootEventId: String,
        perPage: Int,
        maxPages: Int
    ): ThreadSummary = ThreadSummary(rootEventId, roomId, 0, null)

    override suspend fun threadReplies(
        roomId: String,
        rootEventId: String,
        from: String?,
        limit: Int,
        forward: Boolean
    ): ThreadPage = ThreadPage(rootEventId, roomId, emptyList(), null, null)

    override suspend fun isSpace(roomId: String): Boolean =
        mySpaces().any { it.roomId == roomId }

    override suspend fun mySpaces(): List<SpaceInfo> =
        wasmJson.decodeFromJsonElement(requireFacade().mySpaces().toJsonElement())

    override suspend fun createSpace(
        name: String,
        topic: String?,
        isPublic: Boolean,
        invitees: List<String>
    ): String? = requireFacade().createSpace(name, topic, isPublic, wasmJson.encodeToString(invitees).toJsReference())

    override suspend fun spaceAddChild(
        spaceId: String,
        childRoomId: String,
        order: String?,
        suggested: Boolean?
    ): Boolean = requireFacade().spaceAddChild(spaceId, childRoomId, order, suggested)

    override suspend fun spaceRemoveChild(spaceId: String, childRoomId: String): Boolean =
        requireFacade().spaceRemoveChild(spaceId, childRoomId)

    override suspend fun spaceHierarchy(
        spaceId: String,
        from: String?,
        limit: Int,
        maxDepth: Int?,
        suggestedOnly: Boolean
    ): SpaceHierarchyPage? = runCatching {
        wasmJson.decodeFromJsonElement<SpaceHierarchyPage>(
            requireFacade().spaceHierarchy(spaceId, from, limit, maxDepth, suggestedOnly).toJsonElement()
        )
    }.getOrNull()

    override suspend fun spaceInviteUser(spaceId: String, userId: String): Boolean =
        requireFacade().spaceInviteUser(spaceId, userId)

    override suspend fun setPresence(presence: Presence, status: String?): Result<Unit> =
        Result.failure(UnsupportedOperationException())

    override suspend fun getPresence(userId: String): Pair<Presence, String?>? = null

    override suspend fun ignoreUser(userId: String): Result<Unit> =
        Result.failure(UnsupportedOperationException())

    override suspend fun unignoreUser(userId: String): Result<Unit> =
        Result.failure(UnsupportedOperationException())

    override suspend fun ignoredUsers(): List<String> = emptyList()

    override suspend fun roomDirectoryVisibility(roomId: String): RoomDirectoryVisibility? = null

    override suspend fun setRoomDirectoryVisibility(
        roomId: String,
        visibility: RoomDirectoryVisibility
    ): Result<Unit> = Result.failure(UnsupportedOperationException())

    override suspend fun publishRoomAlias(roomId: String, alias: String): Boolean = false

    override suspend fun unpublishRoomAlias(roomId: String, alias: String): Boolean = false

    override suspend fun setRoomCanonicalAlias(
        roomId: String,
        alias: String?,
        altAliases: List<String>
    ): Result<Unit> = Result.failure(UnsupportedOperationException())

    override suspend fun roomAliases(roomId: String): List<String> = emptyList()

    override suspend fun roomJoinRule(roomId: String): RoomJoinRule? = null

    override suspend fun setRoomJoinRule(roomId: String, rule: RoomJoinRule): Result<Unit> =
        Result.failure(UnsupportedOperationException())

    override suspend fun roomHistoryVisibility(roomId: String): RoomHistoryVisibility? = null

    override suspend fun setRoomHistoryVisibility(
        roomId: String,
        visibility: RoomHistoryVisibility
    ): Result<Unit> = Result.failure(UnsupportedOperationException())

    override suspend fun roomPowerLevels(roomId: String): RoomPowerLevels? = null

    override suspend fun canUserBan(roomId: String, userId: String): Boolean = false

    override suspend fun canUserInvite(roomId: String, userId: String): Boolean = false

    override suspend fun canUserRedactOther(roomId: String, userId: String): Boolean = false

    override suspend fun updatePowerLevelForUser(
        roomId: String,
        userId: String,
        powerLevel: Long
    ): Result<Unit> = Result.failure(UnsupportedOperationException())

    override suspend fun applyPowerLevelChanges(
        roomId: String,
        changes: RoomPowerLevelChanges
    ): Result<Unit> = Result.failure(UnsupportedOperationException())

    override suspend fun reportContent(
        roomId: String,
        eventId: String,
        score: Int?,
        reason: String?
    ): Result<Unit> = Result.failure(UnsupportedOperationException())

    override suspend fun reportRoom(roomId: String, reason: String?): Result<Unit> =
        Result.failure(UnsupportedOperationException())

    override suspend fun banUser(roomId: String, userId: String, reason: String?): Result<Unit> =
        Result.failure(UnsupportedOperationException())

    override suspend fun unbanUser(roomId: String, userId: String, reason: String?): Result<Unit> =
        Result.failure(UnsupportedOperationException())

    override suspend fun kickUser(roomId: String, userId: String, reason: String?): Result<Unit> =
        Result.failure(UnsupportedOperationException())

    override suspend fun inviteUser(roomId: String, userId: String): Result<Unit> =
        Result.failure(UnsupportedOperationException())

    override suspend fun enableRoomEncryption(roomId: String): Result<Unit> =
        Result.failure(UnsupportedOperationException())

    override suspend fun roomSuccessor(roomId: String): RoomUpgradeInfo? =
        runCatching { wasmJson.decodeFromJsonElement<RoomUpgradeInfo>(requireFacade().roomSuccessor(roomId).toJsonElement()) }.getOrNull()

    override suspend fun roomPredecessor(roomId: String): RoomPredecessorInfo? =
        runCatching { wasmJson.decodeFromJsonElement<RoomPredecessorInfo>(requireFacade().roomPredecessor(roomId).toJsonElement()) }.getOrNull()

    override suspend fun startLiveLocationShare(roomId: String, durationMs: Long): Result<Unit> =
        Result.failure(UnsupportedOperationException())

    override suspend fun stopLiveLocationShare(roomId: String): Result<Unit> =
        Result.failure(UnsupportedOperationException())

    override suspend fun sendLiveLocation(roomId: String, geoUri: String): Result<Unit> =
        Result.failure(UnsupportedOperationException())

    override fun observeLiveLocation(roomId: String, onShares: (List<LiveLocationShare>) -> Unit): ULong = 0uL

    override fun stopObserveLiveLocation(token: ULong) {}

    override suspend fun sendPoll(roomId: String, question: String, answers: List<String>): Boolean = false

    override fun seenByForEvent(roomId: String, eventId: String, limit: Int): List<SeenByEntry> = emptyList()

    override suspend fun mxcThumbnailToCache(mxcUri: String, width: Int, height: Int, crop: Boolean): String =
        requireFacade().mxcThumbnailToCache(mxcUri, width, height, crop).orEmpty()

    override suspend fun loadRoomListCache(): List<RoomListEntry> =
        wasmJson.decodeFromJsonElement(requireFacade().loadRoomListCache().toJsonArray())

    override suspend fun sendPollResponse(roomId: String, pollEventId: String, answers: List<String>): Boolean = false

    override suspend fun sendPollEnd(roomId: String, pollEventId: String): Boolean = false

    override suspend fun startElementCall(
        roomId: String,
        intent: CallIntent,
        elementCallUrl: String?,
        parentUrl: String?,
        languageTag: String?,
        theme: String?,
        observer: CallWidgetObserver
    ): CallSession? = null

    override fun callWidgetFromWebview(sessionId: ULong, message: String): Boolean = false

    override fun stopElementCall(sessionId: ULong): Boolean = false

    override suspend fun roomPreview(idOrAlias: String): Result<RoomPreview> {
        return Result.failure(UnsupportedOperationException("Not implemented on web"))
    }

    override suspend fun knock(idOrAlias: String): Boolean {
        return false
    }

    override suspend fun listKnockRequests(roomId: String): List<KnockRequestSummary> {
        return emptyList()
    }

    override suspend fun acceptKnockRequest(roomId: String, userId: String): Result<Unit> {
        return Result.failure(UnsupportedOperationException("Not implemented on web"))
    }

    override suspend fun declineKnockRequest(roomId: String, userId: String, reason: String?): Result<Unit> {
        return Result.failure(UnsupportedOperationException("Not implemented on web"))
    }
}

actual fun createMatrixPort(): MatrixPort = WebStubMatrixPort()
