import * as magesFfi from "./mages_ffi.js";

let initPromise = null;

export function ensureMagesFfi() {
  if (initPromise == null) {
    initPromise = Promise.resolve(magesFfi);
  }
  return initPromise;
}
