/**
 * JS-owned sync runtime boundary.
 *
 * Current state: delegates to the existing client-facing sync callback.
 * Future target: own sync state translation fully in JS.
 */
export class RuntimeSync {
  constructor(client) {
    this.client = client;
  }

  start(onState) {
    this.client.start_supervised_sync(onState);
    return () => {};
  }
}
