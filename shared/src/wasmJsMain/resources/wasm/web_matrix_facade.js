import { SessionStore } from "./session_store.js";
import { RuntimeClientFactory } from "./runtime/runtime_client_factory.js";
import { RuntimeRoomList } from "./runtime/runtime_room_list.js";
import { RuntimeTimeline } from "./runtime/runtime_timeline.js";
import { RuntimeSync } from "./runtime/runtime_sync.js";

function accountKey(accountId, homeserverUrl) {
  return accountId ?? `hs:${homeserverUrl}`;
}

export class WebMatrixFacade {
  constructor(client, homeserverUrl, baseStoreDir, accountId) {
    this.client = client;
    this.homeserverUrl = homeserverUrl;
    this.baseStoreDir = baseStoreDir;
    this.accountId = accountId ?? null;
    this.roomListToken = null;
    this.sessionStore = new SessionStore();
    this.clientFactory = new RuntimeClientFactory();
    this.roomList = new RuntimeRoomList(client);
    this.timeline = new RuntimeTimeline(client);
    this.sync = new RuntimeSync(client);
  }

  static async create(homeserverUrl, baseStoreDir, accountId) {
    const clientFactory = new RuntimeClientFactory();
    const client = await clientFactory.create({ homeserverUrl, baseStoreDir, accountId });
    return new WebMatrixFacade(client, homeserverUrl, baseStoreDir, accountId ?? null);
  }

  free() {
    if (this.roomListToken != null) {
      this.client.unobserve_room_list(this.roomListToken);
      this.roomListToken = null;
    }
    this.client.free();
  }

  async login(username, password, deviceDisplayName) {
    const result = await this.client.login(username, password, deviceDisplayName ?? undefined);
    if (this.isLoggedIn()) {
      this.sessionStore.save(accountKey(this.accountId, this.homeserverUrl), {
        homeserverUrl: this.homeserverUrl,
        accountId: this.accountId,
        userId: this.whoami(),
      });
    }
    return result;
  }

  async logout() {
    this.sessionStore.clear(accountKey(this.accountId, this.homeserverUrl));
    return false;
  }

  isLoggedIn() {
    return this.client.is_logged_in();
  }

  loginOauthLoopbackAvailable() {
    return this.client.login_oauth_loopback_available();
  }

  loginSsoLoopbackAvailable() {
    return this.client.login_sso_loopback_available();
  }

  homeserverLoginDetails() {
    return this.client.homeserver_login_details();
  }

  whoami() {
    return this.client.whoami() ?? null;
  }

  async listRooms() {
    return await this.roomList.listRooms();
  }

  loadRoomListCache() {
    return this.roomList.loadCache();
  }

  async getRoomTimeline(roomId, limit = 50) {
    return await this.timeline.snapshot(roomId, limit);
  }

  observeTimeline(roomId, onDiff, onError) {
    return this.timeline.observe(roomId, { onDiff, onError });
  }

  unobserveTimeline(unsubscribe) {
    unsubscribe();
    return true;
  }

  async sendText(roomId, body, formattedBody) {
    return await this.timeline.sendText(roomId, body, formattedBody);
  }

  paginateBackwards(roomId, count) {
    return this.timeline.paginateBackwards(roomId, count);
  }

  paginateForwards(roomId, count) {
    return this.timeline.paginateForwards(roomId, count);
  }

  markRead(roomId) {
    return this.timeline.markRead(roomId);
  }

  markReadAt(roomId, eventId) {
    return this.timeline.markReadAt(roomId, eventId);
  }

  react(roomId, eventId, emoji) {
    return this.timeline.react(roomId, eventId, emoji);
  }

  reply(roomId, inReplyTo, body) {
    return this.timeline.reply(roomId, inReplyTo, body);
  }

  edit(roomId, targetEventId, newBody) {
    return this.timeline.edit(roomId, targetEventId, newBody);
  }

  redact(roomId, eventId, reason) {
    return this.timeline.redact(roomId, eventId, reason);
  }

  setTyping(roomId, typing) {
    return this.timeline.setTyping(roomId, typing);
  }

  observeTyping(roomId, onUpdate) {
    return this.timeline.observeTyping(roomId, onUpdate);
  }

  unobserveTyping(token) {
    return this.timeline.unobserveTyping(token);
  }

  reactionsForEvent(roomId, eventId) {
    return this.timeline.reactionsForEvent(roomId, eventId);
  }

  reactionsBatch(roomId, eventIdsJson) {
    return this.timeline.reactionsBatch(roomId, eventIdsJson);
  }

  async publicRooms(server, search, limit, since) {
    return await this.client.public_rooms(server ?? undefined, search ?? undefined, limit, since ?? undefined);
  }

  joinByIdOrAlias(idOrAlias) {
    return this.client.join_by_id_or_alias(idOrAlias);
  }

  resolveRoomId(idOrAlias) {
    return this.client.resolve_room_id(idOrAlias) ?? null;
  }

  listMembers(roomId) {
    return this.client.list_members(roomId);
  }

  observeRoomList(onReset, onUpdate) {
    const token = this.roomList.observe(onReset, onUpdate);
    this.roomListToken = token;
    return token;
  }

  unobserveRoomList(token) {
    if (this.roomListToken === token) {
      this.roomListToken = null;
    }
    return this.roomList.unobserve(token);
  }

  setRoomListUnreadOnly(token, unreadOnly) {
    return this.roomList.setUnreadOnly(token, unreadOnly);
  }

  sendQueueSetEnabled(enabled) {
    return this.client.send_queue_set_enabled(enabled);
  }

  roomSendQueueSetEnabled(roomId, enabled) {
    return this.client.room_send_queue_set_enabled(roomId, enabled);
  }

  observeSends(onUpdate) {
    return this.client.observe_sends(onUpdate);
  }

  unobserveSends(token) {
    return this.client.unobserve_sends(token);
  }

  ensureDm(userId) {
    return this.client.ensure_dm(userId);
  }

  mySpaces() {
    return this.client.my_spaces();
  }

  createSpace(name, topic, isPublic, invitees) {
    const parsedInvitees = Array.isArray(invitees) ? invitees : JSON.parse(invitees);
    return this.client.create_space(name, topic ?? undefined, isPublic, parsedInvitees);
  }

  spaceAddChild(spaceId, childRoomId, order, suggested) {
    return this.client.space_add_child(spaceId, childRoomId, order ?? undefined, suggested ?? undefined);
  }

  spaceRemoveChild(spaceId, childRoomId) {
    return this.client.space_remove_child(spaceId, childRoomId);
  }

  spaceHierarchy(spaceId, from, limit, maxDepth, suggestedOnly) {
    return this.client.space_hierarchy(spaceId, from ?? undefined, limit, maxDepth ?? undefined, suggestedOnly);
  }

  spaceInviteUser(spaceId, userId) {
    return this.client.space_invite_user(spaceId, userId);
  }

  sendAttachmentBytes(roomId, filename, mime, data) {
    const bytes = data instanceof Uint8Array ? data : new Uint8Array(data);
    return this.client.send_attachment_bytes(roomId, filename, mime, bytes);
  }

  sendExistingAttachment(roomId, attachmentJson, body) {
    return this.client.send_existing_attachment(roomId, attachmentJson, body ?? undefined);
  }

  downloadAttachmentToCacheFile(infoJson, filenameHint) {
    return this.client.download_attachment_to_cache_file(infoJson, filenameHint ?? undefined);
  }

  thumbnailToCache(infoJson, width, height, crop) {
    return this.client.thumbnail_to_cache(infoJson, width, height, crop);
  }

  mxcThumbnailToCache(mxcUri, width, height, crop) {
    return this.client.mxc_thumbnail_to_cache(mxcUri, width, height, crop);
  }

  roomSuccessor(roomId) {
    return this.client.room_successor(roomId);
  }

  roomPredecessor(roomId) {
    return this.client.room_predecessor(roomId);
  }

  startSync(onState) {
    this.sync.start(onState);
  }
}

export function as_web_matrix_facade(value) {
  return value;
}
