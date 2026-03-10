import * as magesFfi from "./mages_ffi.js";

let initPromise = null;

export function ensureMagesFfi() {
  if (initPromise == null) {
    initPromise = Promise.resolve(magesFfi);
  }
  return initPromise;
}

export function normalizeWasmValue(value) {
  if (typeof value === "bigint") {
    return Number(value);
  }
  if (Array.isArray(value)) {
    return value.map(normalizeWasmValue);
  }
  if (value && typeof value === "object") {
    const out = {};
    for (const [key, entry] of Object.entries(value)) {
      out[key] = normalizeWasmValue(entry);
    }
    return out;
  }
  return value;
}

const SESSION_KEY = "mages_web_sessions_v1";

function loadAllSessions() {
  try {
    const raw = globalThis.localStorage?.getItem(SESSION_KEY);
    return raw ? JSON.parse(raw) : {};
  } catch {
    return {};
  }
}

function saveAllSessions(sessions) {
  globalThis.localStorage?.setItem(SESSION_KEY, JSON.stringify(sessions));
}

class SessionStore {
  load(accountId) {
    const all = loadAllSessions();
    return all[accountId] ?? null;
  }

  save(accountId, session) {
    const all = loadAllSessions();
    all[accountId] = session;
    saveAllSessions(all);
  }

  clear(accountId) {
    const all = loadAllSessions();
    delete all[accountId];
    saveAllSessions(all);
  }
}

function accountKey(accountId, homeserverUrl) {
  return accountId ?? `hs:${homeserverUrl}`;
}

function err(error) {
  return { ok: false, error };
}

class WasmClientBridge {
  constructor(client) {
    this.client = client;
  }

  static async create(homeserverUrl, baseStoreDir, accountId) {
    const client = await magesFfi.WasmClient.createAsync(
      homeserverUrl,
      baseStoreDir,
      accountId ?? undefined,
    );
    return new WasmClientBridge(client);
  }

  free() {
    this.client.free();
  }

  logout() {
    return this.client.logout?.() ?? false;
  }

  whoami() {
    return this.client.whoami() ?? null;
  }

  is_logged_in() {
    return this.client.is_logged_in();
  }

  async login(username, password, deviceDisplayName) {
    const result = await this.client.loginAsync(username, password, deviceDisplayName ?? undefined);
    return result ?? null;
  }

  login_oauth_loopback_available() {
    return this.client.login_oauth_loopback_available();
  }

  login_sso_loopback_available() {
    return this.client.login_sso_loopback_available();
  }

  homeserver_login_details() {
    return normalizeWasmValue(this.client.homeserver_login_details());
  }

  async rooms() {
    return normalizeWasmValue(await this.client.rooms());
  }

  async recent_events(roomId, limit) {
    return normalizeWasmValue(await this.client.recent_events(roomId, limit));
  }

  paginate_backwards(roomId, count) {
    return this.client.paginate_backwards(roomId, count);
  }

  paginate_forwards(roomId, count) {
    return this.client.paginate_forwards(roomId, count);
  }

  mark_read(roomId) {
    return this.client.mark_read(roomId);
  }

  mark_read_at(roomId, eventId) {
    return this.client.mark_read_at(roomId, eventId);
  }

  react(roomId, eventId, emoji) {
    return this.client.react(roomId, eventId, emoji);
  }

  reply(roomId, inReplyTo, body) {
    return this.client.reply(roomId, inReplyTo, body);
  }

  edit(roomId, targetEventId, newBody) {
    return this.client.edit(roomId, targetEventId, newBody);
  }

  redact(roomId, eventId, reason) {
    return this.client.redact(roomId, eventId, reason ?? undefined);
  }

  set_typing(roomId, typing) {
    return this.client.set_typing(roomId, typing);
  }

  observe_typing(roomId, onUpdate) {
    return this.client.observe_typing(roomId, (users) => {
      onUpdate(normalizeWasmValue(users ?? []));
    });
  }

  unobserve_typing(id) {
    return this.client.unobserve_typing(id);
  }

  reactions_for_event(roomId, eventId) {
    return normalizeWasmValue(this.client.reactions_for_event(roomId, eventId));
  }

  reactions_batch(roomId, eventIdsJson) {
    return normalizeWasmValue(this.client.reactions_batch(roomId, eventIdsJson));
  }

  async public_rooms(server, search, limit, since) {
    return normalizeWasmValue(
      await this.client.public_rooms(
        server ?? undefined,
        search ?? undefined,
        limit,
        since ?? undefined,
      ),
    );
  }

  join_by_id_or_alias(idOrAlias) {
    return this.client.join_by_id_or_alias(idOrAlias);
  }

  resolve_room_id(idOrAlias) {
    return this.client.resolve_room_id(idOrAlias) ?? null;
  }

  list_members(roomId) {
    return normalizeWasmValue(this.client.list_members(roomId));
  }

  load_room_list_cache() {
    return normalizeWasmValue(this.client.load_room_list_cache() ?? []);
  }

  send_queue_set_enabled(enabled) {
    return this.client.send_queue_set_enabled(enabled);
  }

  room_send_queue_set_enabled(roomId, enabled) {
    return this.client.room_send_queue_set_enabled(roomId, enabled);
  }

  async send_message(roomId, body) {
    return normalizeWasmValue(await this.client.send_message(roomId, body));
  }

  observe_sends(onUpdate) {
    return this.client.observe_sends((update) => {
      onUpdate(normalizeWasmValue(update ?? null));
    });
  }

  unobserve_sends(id) {
    return this.client.unobserve_sends(id);
  }

  ensure_dm(userId) {
    return this.client.ensure_dm(userId) ?? null;
  }

  my_spaces() {
    return normalizeWasmValue(this.client.my_spaces() ?? []);
  }

  create_space(name, topic, isPublic, invitees) {
    return this.client.create_space(name, topic ?? undefined, isPublic, invitees) ?? null;
  }

  space_add_child(spaceId, childRoomId, order, suggested) {
    return this.client.space_add_child(spaceId, childRoomId, order ?? undefined, suggested ?? undefined);
  }

  space_remove_child(spaceId, childRoomId) {
    return this.client.space_remove_child(spaceId, childRoomId);
  }

  space_hierarchy(spaceId, from, limit, maxDepth, suggestedOnly) {
    return normalizeWasmValue(
      this.client.space_hierarchy(
        spaceId,
        from ?? undefined,
        limit,
        maxDepth ?? undefined,
        suggestedOnly,
      ),
    );
  }

  space_invite_user(spaceId, userId) {
    return this.client.space_invite_user(spaceId, userId);
  }

  send_attachment_bytes(roomId, filename, mime, data) {
    return this.client.send_attachment_bytes(roomId, filename, mime, data);
  }

  send_existing_attachment(roomId, attachmentJson, body) {
    return this.client.send_existing_attachment(roomId, attachmentJson, body ?? undefined);
  }

  download_attachment_to_cache_file(infoJson, filenameHint) {
    return this.client.download_attachment_to_cache_file(infoJson, filenameHint ?? undefined) ?? null;
  }

  thumbnail_to_cache(infoJson, width, height, crop) {
    return this.client.thumbnail_to_cache(infoJson, width, height, crop) ?? null;
  }

  mxc_thumbnail_to_cache(mxcUri, width, height, crop) {
    return this.client.mxc_thumbnail_to_cache(mxcUri, width, height, crop) ?? null;
  }

  room_successor(roomId) {
    return normalizeWasmValue(this.client.room_successor(roomId));
  }

  room_predecessor(roomId) {
    return normalizeWasmValue(this.client.room_predecessor(roomId));
  }

  observe_timeline(roomId, onDiff, onError) {
    return this.client.observe_timeline(
      roomId,
      (diff) => onDiff(normalizeWasmValue(diff ?? null)),
      (message) => onError(message ?? ""),
    );
  }

  unobserve_timeline(id) {
    return this.client.unobserve_timeline(id);
  }

  observe_room_list(onReset, onUpdate) {
    return this.client.observe_room_list(
      (items) => onReset(normalizeWasmValue(items ?? [])),
      (item) => onUpdate(normalizeWasmValue(item ?? null)),
    );
  }

  unobserve_room_list(id) {
    return this.client.unobserve_room_list(id);
  }

  room_list_set_unread_only(id, unreadOnly) {
    return this.client.room_list_set_unread_only(id, unreadOnly);
  }

  start_supervised_sync(onState) {
    this.client.start_supervised_sync((state) => {
      onState(normalizeWasmValue(state ?? null));
    });
  }
}

export class WebMatrixFacade {
  constructor(client, homeserverUrl, baseStoreDir, accountId) {
    this.client = client;
    this.homeserverUrl = homeserverUrl;
    this.baseStoreDir = baseStoreDir;
    this.accountId = accountId ?? null;
    this.roomListToken = null;
    this.sessionStore = new SessionStore();
  }

  static async create(homeserverUrl, baseStoreDir, accountId) {
    const client = await WasmClientBridge.create(homeserverUrl, baseStoreDir, accountId);
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
    const result = await this.client.logout();
    this.sessionStore.clear(accountKey(this.accountId, this.homeserverUrl));
    return result;
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
    return await this.client.rooms();
  }

  loadRoomListCache() {
    return this.client.load_room_list_cache();
  }

  async getRoomTimeline(roomId, limit = 50) {
    return await this.client.recent_events(roomId, limit);
  }

  observeTimeline(roomId, onDiff, onError) {
    const token = this.client.observe_timeline(roomId, onDiff, (error) => {
      onError?.(error ?? "Timeline error");
    });
    return () => {
      this.client.unobserve_timeline(token);
    };
  }

  unobserveTimeline(unsubscribe) {
    unsubscribe();
    return true;
  }

  async sendText(roomId, body, formattedBody) {
    void formattedBody;
    const okResult = await this.client.send_message(roomId, body);
    return okResult ? { ok: true } : err("Failed to send message");
  }

  paginateBackwards(roomId, count) {
    return this.client.paginate_backwards(roomId, count);
  }

  paginateForwards(roomId, count) {
    return this.client.paginate_forwards(roomId, count);
  }

  markRead(roomId) {
    return this.client.mark_read(roomId);
  }

  markReadAt(roomId, eventId) {
    return this.client.mark_read_at(roomId, eventId);
  }

  react(roomId, eventId, emoji) {
    return this.client.react(roomId, eventId, emoji);
  }

  reply(roomId, inReplyTo, body) {
    return this.client.reply(roomId, inReplyTo, body);
  }

  edit(roomId, targetEventId, newBody) {
    return this.client.edit(roomId, targetEventId, newBody);
  }

  redact(roomId, eventId, reason) {
    return this.client.redact(roomId, eventId, reason);
  }

  setTyping(roomId, typing) {
    return this.client.set_typing(roomId, typing);
  }

  observeTyping(roomId, onUpdate) {
    return this.client.observe_typing(roomId, onUpdate);
  }

  unobserveTyping(token) {
    return this.client.unobserve_typing(token);
  }

  reactionsForEvent(roomId, eventId) {
    return this.client.reactions_for_event(roomId, eventId);
  }

  reactionsBatch(roomId, eventIdsJson) {
    return this.client.reactions_batch(roomId, eventIdsJson);
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
    const token = this.client.observe_room_list(onReset, onUpdate);
    this.roomListToken = token;
    return token;
  }

  unobserveRoomList(token) {
    if (this.roomListToken === token) {
      this.roomListToken = null;
    }
    return this.client.unobserve_room_list(token);
  }

  setRoomListUnreadOnly(token, unreadOnly) {
    return this.client.room_list_set_unread_only(token, unreadOnly);
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
    this.client.start_supervised_sync(onState);
  }
}

export function as_web_matrix_facade(value) {
  return value;
}
