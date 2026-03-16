package org.mlm.mages.platform

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import kotlinx.browser.document
import kotlinx.coroutines.launch
import org.mlm.mages.ui.components.AttachmentData
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event

actual fun Modifier.fileDrop(
    enabled: Boolean,
    onDragEnter: () -> Unit,
    onDragExit: () -> Unit,
    onDrop: (List<AttachmentData>) -> Unit
): Modifier = composed {
    if (!enabled) return@composed this

    val scope = rememberCoroutineScope()
    val currentOnDragEnter = rememberUpdatedState(onDragEnter)
    val currentOnDragExit = rememberUpdatedState(onDragExit)
    val currentOnDrop = rememberUpdatedState(onDrop)

    DisposableEffect(enabled) {
        val root = document.documentElement as? HTMLElement
        if (root == null) {
            onDispose { }
        } else {
            var dragDepth = 0

            val dragEnterHandler: (Event) -> Unit = let@{ event ->
                val transfer = dataTransferOf(event)
                if (!dataTransferHasFiles(transfer)) return@let

                event.preventDefault()
                event.stopPropagation()

                dragDepth += 1
                if (dragDepth == 1) {
                    currentOnDragEnter.value.invoke()
                }
            }

            val dragOverHandler: (Event) -> Unit = let@{ event ->
                val transfer = dataTransferOf(event)
                if (!dataTransferHasFiles(transfer)) return@let

                event.preventDefault()
                event.stopPropagation()
            }

            val dragLeaveHandler: (Event) -> Unit = let@{ event ->
                val transfer = dataTransferOf(event)
                if (!dataTransferHasFiles(transfer)) return@let

                event.preventDefault()
                event.stopPropagation()

                dragDepth = (dragDepth - 1).coerceAtLeast(0)
                if (dragDepth == 0) {
                    currentOnDragExit.value.invoke()
                }
            }

            val dropHandler: (Event) -> Unit = let@{ event ->
                val transfer = dataTransferOf(event)
                if (!dataTransferHasFiles(transfer)) return@let

                event.preventDefault()
                event.stopPropagation()
                dragDepth = 0

                scope.launch {
                    val attachments = mutableListOf<AttachmentData>()
                    val files = extractFilesFromDataTransfer(transfer)

                    for (file in files) {
                        try {
                            attachments += browserFileToAttachmentData(file)
                        } catch (_: Throwable) {
                        }
                    }

                    if (attachments.isNotEmpty()) {
                        currentOnDrop.value.invoke(attachments)
                    }

                    currentOnDragExit.value.invoke()
                }
            }

            root.addEventListener("dragenter", dragEnterHandler)
            root.addEventListener("dragover", dragOverHandler)
            root.addEventListener("dragleave", dragLeaveHandler)
            root.addEventListener("drop", dropHandler)

            onDispose {
                root.removeEventListener("dragenter", dragEnterHandler)
                root.removeEventListener("dragover", dragOverHandler)
                root.removeEventListener("dragleave", dragLeaveHandler)
                root.removeEventListener("drop", dropHandler)
            }
        }
    }

    this
}
