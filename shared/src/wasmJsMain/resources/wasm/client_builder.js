import { WasmClientBridge } from "./mages_ffi_client.js";

export async function createClient(homeserverUrl, baseStoreDir, accountId) {
  return await WasmClientBridge.create(
    homeserverUrl,
    baseStoreDir,
    accountId ?? undefined,
  );
}
