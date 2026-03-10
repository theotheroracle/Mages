import { SessionStore } from "../session_store.js";

function accountKey(accountId, homeserverUrl) {
  return accountId ?? `hs:${homeserverUrl}`;
}

export class RuntimeSessionStore {
  constructor() {
    this.store = new SessionStore();
  }

  load(accountId, homeserverUrl) {
    return this.store.load(accountKey(accountId, homeserverUrl));
  }

  save(accountId, homeserverUrl, session) {
    this.store.save(accountKey(accountId, homeserverUrl), session);
  }

  clear(accountId, homeserverUrl) {
    this.store.clear(accountKey(accountId, homeserverUrl));
  }
}
