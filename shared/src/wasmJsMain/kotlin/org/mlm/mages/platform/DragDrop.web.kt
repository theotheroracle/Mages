package org.mlm.mages.platform

import androidx.compose.ui.Modifier

actual fun Modifier.fileDrop(
    enabled: Boolean,
    onDragEnter: () -> Unit,
    onDragExit: () -> Unit,
    onDrop: (List<String>) -> Unit
): Modifier {
    // File drag-and-drop via DOM events is not yet wired into Compose/WASM.
    return this
}
