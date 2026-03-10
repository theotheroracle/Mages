package org.mlm.mages.platform

actual object MagesPaths {
    actual fun init() {
        // No filesystem initialisation needed on web; storage is handled by IndexedDB via matrix-sdk.
    }

    actual fun storeDir(): String {
        // Not used on web; matrix-sdk-indexeddb uses its own store.
        return "indexeddb://mages"
    }
}
