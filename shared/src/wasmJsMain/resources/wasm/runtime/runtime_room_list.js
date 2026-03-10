/**
 * JS-owned room list adapter boundary.
 *
 * Current state: consumes the generated `WasmClient` surface directly.
 * Future target: swap internals to richer generated room-list services without
 * changing the facade contract.
 */
export class RuntimeRoomList {
  constructor(client) {
    this.client = client;
  }

  async listRooms() {
    return await this.client.rooms();
  }

  loadCache() {
    return this.client.load_room_list_cache() ?? [];
  }

  observe(onReset, onUpdate) {
    return this.client.observe_room_list(onReset, onUpdate);
  }

  unobserve(token) {
    return this.client.unobserve_room_list(token);
  }

  setUnreadOnly(token, unreadOnly) {
    return this.client.room_list_set_unread_only(token, unreadOnly);
  }
}
