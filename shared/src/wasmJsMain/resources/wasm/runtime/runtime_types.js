/**
 * Shared web runtime DTO shapes for the Matrix facade.
 *
 * These are documentation-first definitions so the JS-owned runtime can evolve
 * without dragging Kotlin/Wasm into low-level SDK details.
 */

export const WebSyncPhase = {
  IDLE: "idle",
  RUNNING: "running",
  BACKING_OFF: "backing_off",
  ERROR: "error",
};

export function ok(value = undefined) {
  return { ok: true, value };
}

export function err(error) {
  return { ok: false, error };
}
