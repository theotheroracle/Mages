import { err } from "./runtime_types.js";

export class RuntimeTimeline {
  constructor(client) {
    this.client = client;
  }

  async snapshot(roomId, limit = 50) {
    return await this.client.recent_events(roomId, limit);
  }

  observe(roomId, handlers) {
    const token = this.client.observe_timeline(
      roomId,
      (diff) => handlers.onDiff(diff),
      (error) => handlers.onError?.(error ?? "Timeline error")
    );
    return () => {
      this.client.unobserve_timeline(token);
    };
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
}
