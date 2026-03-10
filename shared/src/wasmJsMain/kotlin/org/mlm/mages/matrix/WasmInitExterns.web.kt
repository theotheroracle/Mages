@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
@file:JsModule("./wasm/mages_bridge.js")

package org.mlm.mages.matrix

import kotlin.js.JsAny
import kotlin.js.Promise

external fun ensureMagesFfi(): Promise<JsAny?>
