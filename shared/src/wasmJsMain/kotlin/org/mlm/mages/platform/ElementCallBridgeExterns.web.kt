@file:JsModule("./wasm/mages_bridge.js")

package org.mlm.mages.platform

import kotlin.js.JsName

@JsName("createElementCallIframe")
external fun createElementCallIframe(
    containerId: String,
    widgetUrl: String,
    onMessage: (String) -> Unit
): Boolean

@JsName("sendToElementCallIframe")
external fun sendToElementCallIframe(message: String): Boolean

@JsName("sendElementActionResponse")
external fun sendElementActionResponse(originalMessage: String): Boolean

@JsName("removeElementCallIframe")
external fun removeElementCallIframe()

@JsName("setElementCallMinimized")
external fun setElementCallMinimized(minimized: Boolean)
