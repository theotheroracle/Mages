// AUTO-GENERATED from mages_ffi.d.ts — do not edit
@file:JsModule("./wasm/mages_ffi.js")

package org.mlm.mages.matrix

import kotlin.js.JsAny
import kotlin.js.JsName
import kotlin.js.Promise

external class WasmClient {
    companion object {
        fun createAsync(
            homeserverUrl: String,
            baseStoreDir: String,
            accountId: String? = kotlin.js.definedExternally
        ): Promise<JsAny?>
    }

    fun free()
    fun acceptInvite(roomId: String): Promise<JsAny?>
    fun acceptSas(flowId: String, otherUserId: JsAny?, onPhase: JsAny?, onEmojis: JsAny?, onError: JsAny?): Promise<JsAny?>
    fun acceptVerificationRequest(flowId: String, otherUserId: JsAny?, onPhase: JsAny?, onEmojis: JsAny?, onError: JsAny?): Promise<JsAny?>
    fun accountManagementUrl(): String?
    fun applyPowerLevelChanges(roomId: String, changesJson: String): Boolean
    fun backupExistsOnServer(fetch: Boolean): Promise<JsAny?>
    fun banUser(roomId: String, userId: String, reason: String? = kotlin.js.definedExternally): Boolean
    fun callWidgetFromWebview(sessionId: Double, message: String): Boolean
    fun cancelVerification(flowId: String): Promise<JsAny?>
    fun cancelVerificationRequest(flowId: String, otherUserId: String? = kotlin.js.definedExternally): Promise<JsAny?>
    @JsName("check_verification_request")
    fun checkVerificationRequest(userId: String, flowId: String): Promise<JsAny?>
    fun confirmVerification(flowId: String): Promise<JsAny?>
    fun createRoom(name: JsAny?, topic: JsAny?, invitees: JsAny?, isPublic: Boolean, roomAlias: String? = kotlin.js.definedExternally): String?
    @JsName("create_space")
    fun createSpace(name: String, topic: JsAny?, isPublic: Boolean, invitees: JsAny?): Promise<JsAny?>
    fun dmPeerUserId(roomId: String): Promise<JsAny?>
    fun downloadAttachmentToCacheFile(infoJson: String, filenameHint: String? = kotlin.js.definedExternally): JsAny?
    fun edit(roomId: String, targetEventId: String, newBody: String): Promise<JsAny?>
    fun enableRoomEncryption(roomId: String): Boolean
    fun encryptionCatchupOnce(): Boolean
    fun enqueueText(roomId: String, body: String, txnId: String? = kotlin.js.definedExternally): String
    @JsName("ensure_dm")
    fun ensureDm(userId: String): Promise<JsAny?>
    fun enterBackground(): Unit
    fun enterForeground(): Unit
    @JsName("fetch_notification")
    fun fetchNotification(roomId: String, eventId: String): Promise<JsAny?>
    @JsName("fetch_notifications_since")
    fun fetchNotificationsSince(sinceMs: Double, maxRooms: Double, maxEvents: Double): Promise<JsAny?>
    fun finishLoginFromRedirect(callbackUrlOrQuery: String, ExpectedState: String, ExpectedIssuer: String? = kotlin.js.definedExternally): Promise<JsAny?>
    fun getPinnedEvents(roomId: String): JsAny?
    fun getPresence(userId: String): Promise<JsAny?>
    fun getUserPowerLevel(roomId: String, userId: String): Promise<JsAny?>
    fun getUserProfile(userId: String): JsAny?
    fun homeserverLoginDetails(): Promise<JsAny?>
    fun ignoreUser(userId: String): Boolean
    fun ignoredUsers(): JsAny?
    fun inviteUser(roomId: String, userId: String): Boolean
    fun isEventReadBy(roomId: String, eventId: String, userId: String): Promise<JsAny?>
    fun isLoggedIn(): Boolean
    fun isUserIgnored(userId: String): Boolean
    @JsName("is_space")
    fun isSpace(roomId: String): Boolean
    fun joinByIdOrAlias(idOrAlias: String): Promise<JsAny?>
    fun kickUser(roomId: String, userId: String, reason: String? = kotlin.js.definedExternally): Boolean
    fun knock(idOrAlias: String): Promise<JsAny?>
    fun leaveRoom(roomId: String): Promise<JsAny?>
    fun listInvited(): Promise<JsAny?>
    fun listMembers(roomId: String): Promise<JsAny?>
    fun listMyDevices(): Promise<JsAny?>
    fun loadRoomListCache(): JsAny?
    fun loginAsync(username: String, password: String, deviceDisplayName: String? = kotlin.js.definedExternally): Promise<JsAny?>
    fun loginOauthBrowser(redirectUri: String, DeviceName: String? = kotlin.js.definedExternally): Promise<JsAny?>
    fun loginOauthLoopbackAvailable(): Promise<JsAny?>
    fun loginSsoLoopbackAvailable(): Promise<JsAny?>
    @JsName("login_sso_browser")
    fun loginSsoBrowser(RedirectUri: String, DeviceName: String? = kotlin.js.definedExternally): Promise<JsAny?>
    fun logout(): Boolean
    fun markFullyReadAt(roomId: String, eventId: String): Promise<JsAny?>
    fun markRead(roomId: String): Promise<JsAny?>
    fun markReadAt(roomId: String, eventId: String): Promise<JsAny?>
    fun monitorConnection(onChange: JsAny?): Double
    @JsName("mxc_thumbnail_to_cache")
    fun mxcThumbnailToCache(mxcUri: String, width: Double, height: Double, crop: Boolean): Promise<JsAny?>
    @JsName("my_spaces")
    fun mySpaces(): Promise<JsAny?>
    fun observeBackupState(onUpdate: JsAny?): Double
    fun observeLiveLocation(roomId: String, onUpdate: JsAny?): Double
    fun observeOwnReceipt(roomId: String, onChanged: JsAny?): Double
    fun observeReceipts(roomId: String, onChanged: JsAny?): Double
    fun observeRecoveryState(onUpdate: JsAny?): Double
    fun observeRoomList(onReset: JsAny?, onUpdate: JsAny?): Double
    fun observeSends(onUpdate: JsAny?): Double
    fun observeTimeline(roomId: String, onDiff: JsAny?, onError: JsAny?): Double
    fun observeTyping(roomId: String, onUpdate: JsAny?): Double
    @JsName("own_last_read")
    fun ownLastRead(roomId: String): Promise<JsAny?>
    fun paginateBackwards(roomId: String, count: Double): Promise<JsAny?>
    fun paginateForwards(roomId: String, count: Double): Promise<JsAny?>
    fun publicRooms(server: JsAny?, search: JsAny?, limit: Double, since: String? = kotlin.js.definedExternally): JsAny?
    fun publishRoomAlias(roomId: String, alias: String): Boolean
    fun react(roomId: String, eventId: String, emoji: String): Promise<JsAny?>
    fun reactionsBatch(roomId: String, eventIdsJson: String): JsAny?
    fun reactionsForEvent(roomId: String, eventId: String): Promise<JsAny?>
    fun recentEvents(roomId: String, limit: Double): Promise<JsAny?>
    fun recoverWithKey(recoveryKey: String): Promise<JsAny?>
    fun recoveryState(): String
    fun redact(roomId: String, eventId: String, reason: String? = kotlin.js.definedExternally): Promise<JsAny?>
    fun registerUnifiedpush(appId: String, pushKey: String, gatewayUrl: String, deviceName: String, lang: String, profileTag: String? = kotlin.js.definedExternally): Boolean
    fun reply(roomId: String, inReplyTo: String, body: String): Promise<JsAny?>
    fun reportContent(roomId: String, eventId: String, score: Double? = kotlin.js.definedExternally, reason: String? = kotlin.js.definedExternally): Boolean
    fun reportRoom(roomId: String, reason: String? = kotlin.js.definedExternally): Boolean
    fun resolveRoomId(idOrAlias: String): Promise<JsAny?>
    fun retryByTxn(roomId: String, txnId: String): Boolean
    fun roomAliases(roomId: String): JsAny?
    fun roomDirectoryVisibility(roomId: String): String?
    fun roomHistoryVisibility(roomId: String): String?
    fun roomJoinRule(roomId: String): String?
    fun roomListSetUnreadOnly(token: Double, unreadOnly: Boolean): Boolean
    fun roomNotificationMode(roomId: String): Promise<JsAny?>
    fun roomPowerLevels(roomId: String): Promise<JsAny?>
    fun roomProfile(roomId: String): Promise<JsAny?>
    fun roomSendQueueSetEnabled(roomId: String, enabled: Boolean): Boolean
    fun roomTags(roomId: String): JsAny?
    fun roomUnreadStats(roomId: String): JsAny?
    @JsName("room_predecessor")
    fun roomPredecessor(roomId: String): Promise<JsAny?>
    @JsName("room_preview")
    fun roomPreview(idOrAlias: String): Promise<JsAny?>
    @JsName("room_successor")
    fun roomSuccessor(roomId: String): Promise<JsAny?>
    fun rooms(): Promise<JsAny?>
    fun searchRoom(roomId: String, query: String, limit: Double, offset: Double? = kotlin.js.definedExternally): JsAny?
    fun searchUsers(term: String, limit: Double): JsAny?
    fun seenByForEvent(roomId: String, eventId: String, limit: Double): JsAny?
    fun sendMessage(roomId: String, body: String): Promise<JsAny?>
    fun sendPollEnd(roomId: String, pollEventId: String): Boolean
    fun sendPollResponse(roomId: String, pollEventId: String, answersJson: String): Boolean
    fun sendPollStart(roomId: String, question: String, answersJson: String, kind: String, maxSelections: Double): Boolean
    fun sendQueueSetEnabled(enabled: Boolean): Boolean
    @JsName("send_attachment_bytes")
    fun sendAttachmentBytes(roomId: String, filename: String, mime: String, data: JsAny?): Promise<JsAny?>
    @JsName("send_existing_attachment")
    fun sendExistingAttachment(roomId: String, attachmentJson: String, body: String? = kotlin.js.definedExternally): Promise<JsAny?>
    @JsName("send_live_location")
    fun sendLiveLocation(roomId: String, geoUri: String): Promise<JsAny?>
    @JsName("send_thread_text")
    fun sendThreadText(roomId: String, rootEventId: String, body: String, replyToEventId: String? = kotlin.js.definedExternally, latestEventId: String? = kotlin.js.definedExternally, formattedBody: String? = kotlin.js.definedExternally): Promise<JsAny?>
    fun setKeyBackupEnabled(enabled: Boolean): Promise<JsAny?>
    fun setPinnedEvents(roomId: String, eventIds: JsAny?): Boolean
    fun setPresence(presence: String, status: String? = kotlin.js.definedExternally): Promise<JsAny?>
    fun setRoomCanonicalAlias(roomId: String, alias: JsAny?, altAliasesJson: String): Boolean
    fun setRoomDirectoryVisibility(roomId: String, visibility: String): Boolean
    fun setRoomFavourite(roomId: String, favourite: Boolean): Promise<JsAny?>
    fun setRoomHistoryVisibility(roomId: String, visibility: String): Boolean
    fun setRoomJoinRule(roomId: String, rule: String): Boolean
    fun setRoomLowPriority(roomId: String, lowPriority: Boolean): Promise<JsAny?>
    fun setRoomName(roomId: String, name: String): Promise<JsAny?>
    fun setRoomTopic(roomId: String, topic: String): Promise<JsAny?>
    fun setTyping(roomId: String, typing: Boolean): Promise<JsAny?>
    @JsName("set_room_notification_mode")
    fun setRoomNotificationMode(roomId: String, mode: String): Promise<JsAny?>
    fun setupRecovery(onProgress: JsAny?, onDone: JsAny?, onError: JsAny?): Double
    fun shutdown(): Unit
    @JsName("space_add_child")
    fun spaceAddChild(spaceId: String, childRoomId: String, order: String? = kotlin.js.definedExternally, suggested: Boolean? = kotlin.js.definedExternally): Promise<JsAny?>
    @JsName("space_hierarchy")
    fun spaceHierarchy(spaceId: String, from: JsAny?, limit: Double, maxDepth: JsAny?, suggestedOnly: Boolean): Promise<JsAny?>
    @JsName("space_invite_user")
    fun spaceInviteUser(spaceId: String, userId: String): Promise<JsAny?>
    @JsName("space_remove_child")
    fun spaceRemoveChild(spaceId: String, childRoomId: String): Promise<JsAny?>
    fun startCallInbox(onInvite: JsAny?): Double
    fun startSelfSas(targetDeviceId: String, onPhase: JsAny?, onEmojis: JsAny?, onError: JsAny?): Promise<JsAny?>
    fun startSupervisedSync(onState: JsAny?): Unit
    fun startUserSas(userId: String, onPhase: JsAny?, onEmojis: JsAny?, onError: JsAny?): Promise<JsAny?>
    fun startVerificationInbox(onRequest: JsAny?, onError: JsAny?): Double
    @JsName("start_element_call")
    fun startElementCall(roomId: String, intent: String, elementCallUrl: JsAny?, parentUrl: JsAny?, languageTag: JsAny?, theme: JsAny?, onToWidget: JsAny?): Promise<JsAny?>
    @JsName("start_live_location")
    fun startLiveLocation(roomId: String, durationMs: Double): Promise<JsAny?>
    fun stopCallInbox(token: Double): Boolean
    fun stopElementCall(sessionId: Double): Boolean
    @JsName("stop_live_location")
    fun stopLiveLocation(roomId: String): Promise<JsAny?>
    @JsName("thread_replies")
    fun threadReplies(roomId: String, rootEventId: String, from: JsAny?, limit: Double, forward: Boolean): Promise<JsAny?>
    @JsName("thread_summary")
    fun threadSummary(roomId: String, rootEventId: String, perPage: Double, maxPages: Double): Promise<JsAny?>
    @JsName("thumbnail_to_cache")
    fun thumbnailToCache(infoJson: String, width: Double, height: Double, crop: Boolean): Promise<JsAny?>
    fun unbanUser(roomId: String, userId: String, reason: String? = kotlin.js.definedExternally): Boolean
    fun unignoreUser(userId: String): Boolean
    fun unobserveBackupState(id: Double): Boolean
    fun unobserveConnection(subId: Double): Boolean
    fun unobserveLiveLocation(subId: Double): Boolean
    fun unobserveReceipts(subId: Double): Boolean
    fun unobserveRecoveryState(id: Double): Boolean
    fun unobserveRoomList(token: Double): Boolean
    fun unobserveSends(id: Double): Boolean
    fun unobserveTimeline(subId: Double): Boolean
    fun unobserveTyping(subId: Double): Boolean
    fun unobserveVerificationInbox(subId: Double): Boolean
    fun unpublishRoomAlias(roomId: String, alias: String): Boolean
    fun unregisterUnifiedpush(appId: String, pushkey: String): Boolean
    fun updatePowerLevelForUser(roomId: String, userId: String, powerLevel: Double): Boolean
    fun whoami(): String?
}

external fun asWasmClient(value: JsAny?): WasmClient
