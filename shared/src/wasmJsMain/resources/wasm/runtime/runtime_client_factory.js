import { GeneratedRuntime } from "./generated_runtime.js";
import { normalizeWasmValue } from "../mages_ffi_client.js";

function normalizeClient(rawClient) {
  return {
    free() {
      rawClient.free();
    },

    whoami() {
      return rawClient.whoami() ?? null;
    },

    is_logged_in() {
      return rawClient.is_logged_in();
    },

    async login(username, password, deviceDisplayName) {
      const result = await rawClient.loginAsync(username, password, deviceDisplayName ?? undefined);
      return result ?? null;
    },

    login_sso_loopback_available() {
      return rawClient.login_sso_loopback_available();
    },

    login_oauth_loopback_available() {
      return rawClient.login_oauth_loopback_available();
    },

    homeserver_login_details() {
      return normalizeWasmValue(rawClient.homeserver_login_details());
    },

    async rooms() {
      return normalizeWasmValue(await rawClient.rooms());
    },

    async recent_events(roomId, limit) {
      return normalizeWasmValue(await rawClient.recent_events(roomId, limit));
    },

    paginate_backwards(roomId, count) {
      return rawClient.paginate_backwards(roomId, count);
    },

    paginate_forwards(roomId, count) {
      return rawClient.paginate_forwards(roomId, count);
    },

    mark_read(roomId) {
      return rawClient.mark_read(roomId);
    },

    mark_read_at(roomId, eventId) {
      return rawClient.mark_read_at(roomId, eventId);
    },

    react(roomId, eventId, emoji) {
      return rawClient.react(roomId, eventId, emoji);
    },

    reply(roomId, inReplyTo, body) {
      return rawClient.reply(roomId, inReplyTo, body);
    },

    edit(roomId, targetEventId, newBody) {
      return rawClient.edit(roomId, targetEventId, newBody);
    },

    redact(roomId, eventId, reason) {
      return rawClient.redact(roomId, eventId, reason ?? undefined);
    },

    set_typing(roomId, typing) {
      return rawClient.set_typing(roomId, typing);
    },

    observe_typing(roomId, onUpdate) {
      return rawClient.observe_typing(roomId, (users) => {
        onUpdate(normalizeWasmValue(users ?? []));
      });
    },

    unobserve_typing(id) {
      return rawClient.unobserve_typing(id);
    },

    reactions_for_event(roomId, eventId) {
      return normalizeWasmValue(rawClient.reactions_for_event(roomId, eventId));
    },

    reactions_batch(roomId, eventIdsJson) {
      return normalizeWasmValue(rawClient.reactions_batch(roomId, eventIdsJson));
    },

    async public_rooms(server, search, limit, since) {
      return normalizeWasmValue(await rawClient.public_rooms(server ?? undefined, search ?? undefined, limit, since ?? undefined));
    },

    join_by_id_or_alias(idOrAlias) {
      return rawClient.join_by_id_or_alias(idOrAlias);
    },

    resolve_room_id(idOrAlias) {
      return rawClient.resolve_room_id(idOrAlias) ?? null;
    },

    list_members(roomId) {
      return normalizeWasmValue(rawClient.list_members(roomId));
    },

    load_room_list_cache() {
      return normalizeWasmValue(rawClient.load_room_list_cache() ?? []);
    },

    send_queue_set_enabled(enabled) {
      return rawClient.send_queue_set_enabled(enabled);
    },

    room_send_queue_set_enabled(roomId, enabled) {
      return rawClient.room_send_queue_set_enabled(roomId, enabled);
    },

    async send_message(roomId, body) {
      return normalizeWasmValue(await rawClient.send_message(roomId, body));
    },

    observe_sends(onUpdate) {
      return rawClient.observe_sends((update) => {
        onUpdate(normalizeWasmValue(update ?? null));
      });
    },

    unobserve_sends(id) {
      return rawClient.unobserve_sends(id);
    },

    ensure_dm(userId) {
      return rawClient.ensure_dm(userId) ?? null;
    },

    my_spaces() {
      return normalizeWasmValue(rawClient.my_spaces() ?? []);
    },

    create_space(name, topic, isPublic, invitees) {
      return rawClient.create_space(name, topic ?? undefined, isPublic, invitees) ?? null;
    },

    space_add_child(spaceId, childRoomId, order, suggested) {
      return rawClient.space_add_child(spaceId, childRoomId, order ?? undefined, suggested ?? undefined);
    },

    space_remove_child(spaceId, childRoomId) {
      return rawClient.space_remove_child(spaceId, childRoomId);
    },

    space_hierarchy(spaceId, from, limit, maxDepth, suggestedOnly) {
      return normalizeWasmValue(rawClient.space_hierarchy(spaceId, from ?? undefined, limit, maxDepth ?? undefined, suggestedOnly));
    },

    space_invite_user(spaceId, userId) {
      return rawClient.space_invite_user(spaceId, userId);
    },

    send_attachment_bytes(roomId, filename, mime, data) {
      return rawClient.send_attachment_bytes(roomId, filename, mime, data);
    },

    send_existing_attachment(roomId, attachmentJson, body) {
      return rawClient.send_existing_attachment(roomId, attachmentJson, body ?? undefined);
    },

    download_attachment_to_cache_file(infoJson, filenameHint) {
      return rawClient.download_attachment_to_cache_file(infoJson, filenameHint ?? undefined) ?? null;
    },

    thumbnail_to_cache(infoJson, width, height, crop) {
      return rawClient.thumbnail_to_cache(infoJson, width, height, crop) ?? null;
    },

    mxc_thumbnail_to_cache(mxcUri, width, height, crop) {
      return rawClient.mxc_thumbnail_to_cache(mxcUri, width, height, crop) ?? null;
    },

    room_successor(roomId) {
      return normalizeWasmValue(rawClient.room_successor(roomId));
    },

    room_predecessor(roomId) {
      return normalizeWasmValue(rawClient.room_predecessor(roomId));
    },

    observe_timeline(roomId, onDiff, onError) {
      return rawClient.observe_timeline(
        roomId,
        (diff) => onDiff(normalizeWasmValue(diff ?? null)),
        (message) => onError(message ?? "")
      );
    },

    unobserve_timeline(id) {
      return rawClient.unobserve_timeline(id);
    },

    observe_room_list(onReset, onUpdate) {
      return rawClient.observe_room_list(
        (items) => onReset(normalizeWasmValue(items ?? [])),
        (item) => onUpdate(normalizeWasmValue(item ?? null))
      );
    },

    unobserve_room_list(id) {
      return rawClient.unobserve_room_list(id);
    },

    room_list_set_unread_only(id, unreadOnly) {
      return rawClient.room_list_set_unread_only(id, unreadOnly);
    },

    start_supervised_sync(onState) {
      rawClient.start_supervised_sync((state) => {
        onState(normalizeWasmValue(state ?? null));
      });
    },

    logout() {
      return rawClient.logout();
    },
  };
}

export class RuntimeClientFactory {
  constructor() {
    this.generated = new GeneratedRuntime();
  }

  async create({ homeserverUrl, baseStoreDir, accountId }) {
    const rawClient = await this.generated.createClient({ homeserverUrl, baseStoreDir, accountId });
    return normalizeClient(rawClient);
  }

  async restore({ homeserverUrl, baseStoreDir, accountId, sessionRecord }) {
    const rawClient = await this.generated.createClient({ homeserverUrl, baseStoreDir, accountId });
    if (sessionRecord?.session) {
      rawClient.restore_session(sessionRecord.session);
    }
    return normalizeClient(rawClient);
  }

  async logout(client, sessionRecord) {
    void sessionRecord;
    return client.logout();
  }
}
