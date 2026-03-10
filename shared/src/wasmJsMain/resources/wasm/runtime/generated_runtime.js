import {
  createRuntimeClient,
  restoreRuntimeClient,
  logoutRuntimeClient,
} from "./generated_runtime_entry.js";

export class GeneratedRuntime {
  async createClient(options) {
    return await createRuntimeClient(options);
  }

  async restoreClient(options) {
    return await restoreRuntimeClient(options);
  }

  async logoutClient(client, sessionRecord) {
    return await logoutRuntimeClient(client, sessionRecord);
  }
}
