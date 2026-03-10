import * as magesFfi from "./mages_ffi.js";

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

export class WasmClientBridge {
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

  whoami() {
    return this.client.whoami() ?? null;
  }

  is_logged_in() {
    return this.client.is_logged_in();
  }

  async login(username, password, deviceDisplayName) {
    const result = this.client.login(username, password, deviceDisplayName ?? undefined);
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
    return normalizeWasmValue(await this.client.public_rooms(server ?? undefined, search ?? undefined, limit, since ?? undefined));
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
    return normalizeWasmValue(this.client.space_hierarchy(spaceId, from ?? undefined, limit, maxDepth ?? undefined, suggestedOnly));
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
      (message) => onError(message ?? "")
    );
  }

  unobserve_timeline(id) {
    return this.client.unobserve_timeline(id);
  }

  observe_room_list(onReset, onUpdate) {
    return this.client.observe_room_list(
      (items) => onReset(normalizeWasmValue(items ?? [])),
      (item) => onUpdate(normalizeWasmValue(item ?? null))
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

export function as_wasm_client_bridge(value) {
  return value;
}
