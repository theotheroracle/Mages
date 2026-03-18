// AUTO-GENERATED from mages_ffi.d.ts — do not edit
@file:JsModule("./wasm/mages_ffi.js")

package org.mlm.mages.matrix

import kotlin.js.JsAny
import kotlin.js.JsBoolean
import kotlin.js.JsName
import kotlin.js.JsString
import kotlin.js.Promise
import kotlin.js.definedExternally

external class WasmClient {
    companion object {
        fun createAsync(
            homeserverUrl: String,
            baseStoreDir: String,
            accountId: String? = kotlin.js.definedExternally
        ): Promise<WasmClient>
    }

    fun free()

    fun acceptInvite(roomId: String): Promise<JsBoolean>

    fun acceptSas(flowId: String, otherUserId: JsAny?, onPhase: JsAny?, onEmojis: JsAny?, onError: JsAny?): Promise<JsBoolean>

    fun acceptVerificationRequest(flowId: String, otherUserId: JsAny?, onPhase: JsAny?, onEmojis: JsAny?, onError: JsAny?): Promise<JsBoolean>

    @JsName("account_management_url")
    fun accountManagementUrl(): String?

    @JsName("apply_power_level_changes")
    fun applyPowerLevelChanges(roomId: String, changesJson: String): Boolean

    fun backupExistsOnServer(fetch: Boolean): Promise<JsBoolean>

    fun banUser(roomId: String, userId: String, reason: String? = kotlin.js.definedExternally): Boolean

    @JsName("call_widget_from_webview")
    fun callWidgetFromWebview(sessionId: Double, message: String): Boolean

    fun cancelVerification(flowId: String): Promise<JsBoolean>

    fun cancelVerificationRequest(flowId: String, otherUserId: String? = kotlin.js.definedExternally): Promise<JsBoolean>

    @JsName("check_verification_request")
    fun checkVerificationRequest(userId: String, flowId: String): Promise<JsBoolean>

    fun confirmVerification(flowId: String): Promise<JsBoolean>

    fun createRoom(name: JsAny?, topic: JsAny?, invitees: JsAny?, isPublic: Boolean, roomAlias: String? = kotlin.js.definedExternally): String?

    @JsName("create_space")
    fun createSpace(name: String, topic: JsAny?, isPublic: Boolean, invitees: JsAny?): Promise<JsAny?>

    fun dmPeerUserId(roomId: String): Promise<JsAny?>

    @JsName("download_attachment_to_cache_file")
    fun downloadAttachmentToCacheFile(infoJson: String, filenameHint: String? = kotlin.js.definedExternally): JsAny?

    fun edit(roomId: String, targetEventId: String, newBody: String): Promise<JsBoolean>

    fun enableRoomEncryption(roomId: String): Boolean

    @JsName("encryption_catchup_once")
    fun encryptionCatchupOnce(): Boolean

    @JsName("enqueue_text")
    fun enqueueText(roomId: String, body: String, txnId: String? = kotlin.js.definedExternally): String

    @JsName("ensure_dm")
    fun ensureDm(userId: String): Promise<JsAny?>

    @JsName("enter_background")
    fun enterBackground(): Unit

    @JsName("enter_foreground")
    fun enterForeground(): Unit

    @JsName("fetch_notification")
    fun fetchNotification(roomId: String, eventId: String): Promise<JsAny?>

    @JsName("fetch_notifications_since")
    fun fetchNotificationsSince(sinceMs: Double, maxRooms: Double, maxEvents: Double): Promise<JsAny?>

    fun finishLoginFromRedirect(callbackUrlOrQuery: String, ExpectedState: String, ExpectedIssuer: String? = kotlin.js.definedExternally): Promise<JsBoolean>

    fun getPinnedEvents(roomId: String): JsAny?

    fun getPresence(userId: String): Promise<JsAny?>

    fun getUserPowerLevel(roomId: String, userId: String): Promise<JsAny?>

    fun getUserProfile(userId: String): JsAny?

    fun homeserverLoginDetails(): Promise<JsAny?>

    fun ignoreUser(userId: String): Boolean

    @JsName("ignored_users")
    fun ignoredUsers(): JsAny?

    fun inviteUser(roomId: String, userId: String): Boolean

    fun isEventReadBy(roomId: String, eventId: String, userId: String): Promise<JsBoolean>

    fun isLoggedIn(): Boolean

    @JsName("is_space")
    fun isSpace(roomId: String): Boolean

    @JsName("is_user_ignored")
    fun isUserIgnored(userId: String): Boolean

    fun joinByIdOrAlias(idOrAlias: String): Promise<JsAny?>

    fun kickUser(roomId: String, userId: String, reason: String? = kotlin.js.definedExternally): Boolean

    fun knock(idOrAlias: String): Promise<JsBoolean>

    fun leaveRoom(roomId: String): Promise<JsBoolean>

    fun listInvited(): Promise<JsAny?>

    fun listMembers(roomId: String): Promise<JsAny?>

    fun listMyDevices(): Promise<JsAny?>

    @JsName("load_room_list_cache")
    fun loadRoomListCache(): JsAny?

    fun loginAsync(username: String, password: String, deviceDisplayName: String? = kotlin.js.definedExternally): Promise<JsAny?>

    fun loginOauthBrowser(redirectUri: String, DeviceName: String? = kotlin.js.definedExternally): Promise<JsAny?>

    fun loginOauthLoopbackAvailable(): Promise<JsBoolean>

    fun loginSsoLoopbackAvailable(): Promise<JsBoolean>

    @JsName("login_sso_browser")
    fun loginSsoBrowser(RedirectUri: String, DeviceName: String? = kotlin.js.definedExternally): Promise<JsAny?>

    fun logout(): Boolean

    fun markFullyReadAt(roomId: String, eventId: String): Promise<JsBoolean>

    fun markRead(roomId: String): Promise<JsBoolean>

    fun markReadAt(roomId: String, eventId: String): Promise<JsBoolean>

    @JsName("monitor_connection")
    fun monitorConnection(onChange: JsAny?): Double

    @JsName("mxc_thumbnail_to_cache")
    fun mxcThumbnailToCache(mxcUri: String, width: Double, height: Double, crop: Boolean): Promise<JsString>

    @JsName("my_spaces")
    fun mySpaces(): Promise<JsAny?>

    fun observeBackupState(onUpdate: JsAny?): Double

    fun observeReceipts(roomId: String, onChanged: JsAny?): Double

    fun observeRecoveryState(onUpdate: JsAny?): Double

    fun observeRoomList(onReset: JsAny?, onUpdate: JsAny?): Double

    fun observeSends(onUpdate: JsAny?): Double

    fun observeTimeline(roomId: String, onDiff: JsAny?, onError: JsAny?): Double

    fun observeTyping(roomId: String, onUpdate: JsAny?): Double

    @JsName("observe_live_location")
    fun observeLiveLocation(roomId: String, onUpdate: JsAny?): Double

    @JsName("observe_own_receipt")
    fun observeOwnReceipt(roomId: String, onChanged: JsAny?): Double

    @JsName("own_last_read")
    fun ownLastRead(roomId: String): Promise<JsAny?>

    fun paginateBackwards(roomId: String, count: Double): Promise<JsBoolean>

    fun paginateForwards(roomId: String, count: Double): Promise<JsBoolean>

    fun publicRooms(server: JsAny?, search: JsAny?, limit: Double, since: String? = kotlin.js.definedExternally): JsAny?

    @JsName("publish_room_alias")
    fun publishRoomAlias(roomId: String, alias: String): Boolean

    fun react(roomId: String, eventId: String, emoji: String): Promise<JsBoolean>

    fun reactionsBatch(roomId: String, eventIdsJson: String): JsAny?

    fun reactionsForEvent(roomId: String, eventId: String): Promise<JsAny?>

    fun recentEvents(roomId: String, limit: Double): Promise<JsAny?>

    fun recoverWithKey(recoveryKey: String): Promise<JsBoolean>

    @JsName("recovery_state")
    fun recoveryState(): String

    fun redact(roomId: String, eventId: String, reason: String? = kotlin.js.definedExternally): Promise<JsBoolean>

    @JsName("register_unifiedpush")
    fun registerUnifiedpush(appId: String, pushKey: String, gatewayUrl: String, deviceName: String, lang: String, profileTag: String? = kotlin.js.definedExternally): Boolean

    fun reply(roomId: String, inReplyTo: String, body: String): Promise<JsBoolean>

    @JsName("report_content")
    fun reportContent(roomId: String, eventId: String, score: Double? = kotlin.js.definedExternally, reason: String? = kotlin.js.definedExternally): Boolean

    @JsName("report_room")
    fun reportRoom(roomId: String, reason: String? = kotlin.js.definedExternally): Boolean

    fun resolveRoomId(idOrAlias: String): Promise<JsAny?>

    @JsName("retry_by_txn")
    fun retryByTxn(roomId: String, txnId: String): Boolean

    fun roomNotificationMode(roomId: String): Promise<JsAny?>

    fun roomPowerLevels(roomId: String): Promise<JsAny?>

    fun roomProfile(roomId: String): Promise<JsAny?>

    fun roomTags(roomId: String): JsAny?

    @JsName("room_aliases")
    fun roomAliases(roomId: String): JsAny?

    @JsName("room_directory_visibility")
    fun roomDirectoryVisibility(roomId: String): String?

    @JsName("room_history_visibility")
    fun roomHistoryVisibility(roomId: String): String?

    @JsName("room_join_rule")
    fun roomJoinRule(roomId: String): String?

    @JsName("room_list_set_unread_only")
    fun roomListSetUnreadOnly(token: Double, unreadOnly: Boolean): Boolean

    @JsName("room_predecessor")
    fun roomPredecessor(roomId: String): Promise<JsAny?>

    @JsName("room_preview")
    fun roomPreview(idOrAlias: String): Promise<JsAny?>

    @JsName("room_send_queue_set_enabled")
    fun roomSendQueueSetEnabled(roomId: String, enabled: Boolean): Boolean

    @JsName("room_successor")
    fun roomSuccessor(roomId: String): Promise<JsAny?>

    @JsName("room_unread_stats")
    fun roomUnreadStats(roomId: String): JsAny?

    fun rooms(): Promise<JsAny?>

    fun searchUsers(term: String, limit: Double): JsAny?

    @JsName("search_room")
    fun searchRoom(roomId: String, query: String, limit: Double, offset: Double? = kotlin.js.definedExternally): JsAny?

    @JsName("seen_by_for_event")
    fun seenByForEvent(roomId: String, eventId: String, limit: Double): JsAny?

    fun sendMessage(roomId: String, body: String): Promise<JsBoolean>

    fun sendQueueSetEnabled(enabled: Boolean): Boolean

    @JsName("send_attachment_bytes")
    fun sendAttachmentBytes(roomId: String, filename: String, mime: String, data: JsAny?): Promise<JsBoolean>

    @JsName("send_existing_attachment")
    fun sendExistingAttachment(roomId: String, attachmentJson: String, body: String? = kotlin.js.definedExternally): Promise<JsBoolean>

    @JsName("send_live_location")
    fun sendLiveLocation(roomId: String, geoUri: String): Promise<JsBoolean>

    @JsName("send_poll_end")
    fun sendPollEnd(roomId: String, pollEventId: String): Boolean

    @JsName("send_poll_response")
    fun sendPollResponse(roomId: String, pollEventId: String, answersJson: String): Boolean

    @JsName("send_poll_start")
    fun sendPollStart(roomId: String, question: String, answersJson: String, kind: String, maxSelections: Double): Boolean

    @JsName("send_thread_text")
    fun sendThreadText(roomId: String, rootEventId: String, body: String, replyToEventId: String? = kotlin.js.definedExternally, latestEventId: String? = kotlin.js.definedExternally, formattedBody: String? = kotlin.js.definedExternally): Promise<JsBoolean>

    fun setKeyBackupEnabled(enabled: Boolean): Promise<JsBoolean>

    fun setPinnedEvents(roomId: String, eventIds: JsAny?): Boolean

    fun setPresence(presence: String, status: String? = kotlin.js.definedExternally): Promise<JsBoolean>

    fun setRoomFavourite(roomId: String, favourite: Boolean): Promise<JsBoolean>

    fun setRoomLowPriority(roomId: String, lowPriority: Boolean): Promise<JsBoolean>

    fun setRoomName(roomId: String, name: String): Promise<JsBoolean>

    fun setRoomTopic(roomId: String, topic: String): Promise<JsBoolean>

    fun setTyping(roomId: String, typing: Boolean): Promise<JsBoolean>

    @JsName("set_room_canonical_alias")
    fun setRoomCanonicalAlias(roomId: String, alias: JsAny?, altAliasesJson: String): Boolean

    @JsName("set_room_directory_visibility")
    fun setRoomDirectoryVisibility(roomId: String, visibility: String): Boolean

    @JsName("set_room_history_visibility")
    fun setRoomHistoryVisibility(roomId: String, visibility: String): Boolean

    @JsName("set_room_join_rule")
    fun setRoomJoinRule(roomId: String, rule: String): Boolean

    @JsName("set_room_notification_mode")
    fun setRoomNotificationMode(roomId: String, mode: String): Promise<JsBoolean>

    fun setupRecovery(onProgress: JsAny?, onDone: JsAny?, onError: JsAny?): Double

    fun shutdown(): Unit

    @JsName("space_add_child")
    fun spaceAddChild(spaceId: String, childRoomId: String, order: String? = kotlin.js.definedExternally, suggested: Boolean? = kotlin.js.definedExternally): Promise<JsBoolean>

    @JsName("space_hierarchy")
    fun spaceHierarchy(spaceId: String, from: JsAny?, limit: Double, maxDepth: JsAny?, suggestedOnly: Boolean): Promise<JsAny?>

    @JsName("space_invite_user")
    fun spaceInviteUser(spaceId: String, userId: String): Promise<JsBoolean>

    @JsName("space_remove_child")
    fun spaceRemoveChild(spaceId: String, childRoomId: String): Promise<JsBoolean>

    fun startSelfSas(targetDeviceId: String, onPhase: JsAny?, onEmojis: JsAny?, onError: JsAny?): Promise<JsString>

    fun startSupervisedSync(onState: JsAny?): Unit

    fun startUserSas(userId: String, onPhase: JsAny?, onEmojis: JsAny?, onError: JsAny?): Promise<JsString>

    fun startVerificationInbox(onRequest: JsAny?, onError: JsAny?): Double

    @JsName("start_call_inbox")
    fun startCallInbox(onInvite: JsAny?): Double

    @JsName("start_element_call")
    fun startElementCall(roomId: String, intent: String, elementCallUrl: JsAny?, parentUrl: JsAny?, languageTag: JsAny?, theme: JsAny?, onToWidget: JsAny?): Promise<JsAny?>

    @JsName("start_live_location")
    fun startLiveLocation(roomId: String, durationMs: Double): Promise<JsBoolean>

    @JsName("stop_call_inbox")
    fun stopCallInbox(token: Double): Boolean

    @JsName("stop_element_call")
    fun stopElementCall(sessionId: Double): Boolean

    @JsName("stop_live_location")
    fun stopLiveLocation(roomId: String): Promise<JsBoolean>

    @JsName("thread_replies")
    fun threadReplies(roomId: String, rootEventId: String, from: JsAny?, limit: Double, forward: Boolean): Promise<JsAny?>

    @JsName("thread_summary")
    fun threadSummary(roomId: String, rootEventId: String, perPage: Double, maxPages: Double): Promise<JsAny?>

    @JsName("thumbnail_to_cache")
    fun thumbnailToCache(infoJson: String, width: Double, height: Double, crop: Boolean): Promise<JsString>

    fun unbanUser(roomId: String, userId: String, reason: String? = kotlin.js.definedExternally): Boolean

    fun unignoreUser(userId: String): Boolean

    fun unobserveBackupState(id: Double): Boolean

    fun unobserveReceipts(subId: Double): Boolean

    fun unobserveRecoveryState(id: Double): Boolean

    fun unobserveRoomList(token: Double): Boolean

    fun unobserveSends(id: Double): Boolean

    fun unobserveTimeline(subId: Double): Boolean

    fun unobserveVerificationInbox(subId: Double): Boolean

    @JsName("unobserve_connection")
    fun unobserveConnection(subId: Double): Boolean

    @JsName("unobserve_live_location")
    fun unobserveLiveLocation(subId: Double): Boolean

    @JsName("unobserve_typing")
    fun unobserveTyping(subId: Double): Boolean

    @JsName("unpublish_room_alias")
    fun unpublishRoomAlias(roomId: String, alias: String): Boolean

    @JsName("unregister_unifiedpush")
    fun unregisterUnifiedpush(appId: String, pushkey: String): Boolean

    @JsName("update_power_level_for_user")
    fun updatePowerLevelForUser(roomId: String, userId: String, powerLevel: Double): Boolean

    fun whoami(): String?
}
