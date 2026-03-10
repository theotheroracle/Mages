const SESSION_KEY = "mages_web_sessions_v1";

function loadAll() {
  try {
    const raw = globalThis.localStorage?.getItem(SESSION_KEY);
    return raw ? JSON.parse(raw) : {};
  } catch {
    return {};
  }
}

function saveAll(sessions) {
  globalThis.localStorage?.setItem(SESSION_KEY, JSON.stringify(sessions));
}

export class SessionStore {
  load(accountId) {
    const all = loadAll();
    return all[accountId] ?? null;
  }

  save(accountId, session) {
    const all = loadAll();
    all[accountId] = session;
    saveAll(all);
  }

  clear(accountId) {
    const all = loadAll();
    delete all[accountId];
    saveAll(all);
  }
}
