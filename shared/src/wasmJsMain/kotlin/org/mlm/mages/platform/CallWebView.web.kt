package org.mlm.mages.platform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

private val LOCAL_ONLY_WIDGET_ACTIONS = setOf(
    "io.element.device_mute",
    "io.element.join",
    "io.element.close",
    "io.element.tile_layout",
    "io.element.spotlight_layout",
    "set_always_on_screen",
    "minimize",
    "im.vector.hangup",
)

private val CLOSE_ACTIONS = setOf(
    "io.element.close",
    "im.vector.hangup",
)

@Composable
actual fun CallWebViewHost(
    widgetUrl: String,
    onMessageFromWidget: (String) -> Unit,
    onClosed: () -> Unit,
    onMinimizeRequested: () -> Unit,
    minimized: Boolean,
    widgetBaseUrl: String?,
    modifier: Modifier,
    onAttachController: (CallWebViewController?) -> Unit
): CallWebViewController {
    val controller = remember(widgetUrl) {
        object : CallWebViewController {
            override fun sendToWidget(message: String) {
                runCatching { sendToElementCallIframe(message) }
            }

            override fun close() {
                onClosed()
            }
        }
    }

    DisposableEffect(widgetUrl) {
        val containerId = "element-call-container"

        val wrappedOnMessage: (String) -> Unit = { message ->
            val action = extractActionFromMessage(message)

            when {
                action in LOCAL_ONLY_WIDGET_ACTIONS -> {
                    runCatching { sendElementActionResponse(message) }

                    when (action) {
                        in CLOSE_ACTIONS -> onClosed()
                        "minimize" -> onMinimizeRequested()
                        else -> {
                            // Ack locally, do NOT forward to Rust.
                            // Rust widget driver only handles Matrix widget API actions.
                        }
                    }
                }

                else -> {
                    onMessageFromWidget(message)
                }
            }
        }

        try {
            createElementCallIframe(
                containerId,
                widgetUrl,
                wrappedOnMessage
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        onAttachController(controller)

        onDispose {
            try {
                removeElementCallIframe()
            } catch (_: Exception) { }
            onAttachController(null)
        }
    }

    DisposableEffect(minimized) {
        setElementCallMinimized(minimized)
        onDispose {}
    }

    // Empty Box - iframe is added via JS
    Box(modifier = modifier.fillMaxSize())
    
    return controller
}

private fun extractActionFromMessage(message: String): String? {
    return runCatching {
        Regex(""""action"\s*:\s*"([^"]+)"""")
            .find(message)
            ?.groupValues
            ?.getOrNull(1)
    }.getOrNull()
}
