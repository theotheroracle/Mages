package org.mlm.mages.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.mlm.mages.ui.components.AttachmentData

@Composable
actual fun rememberClipboardAttachmentHandler(): ClipboardAttachmentHandler {
    return object : ClipboardAttachmentHandler {
        override fun hasAttachment(): Boolean = false

        override suspend fun getAttachments(): List<AttachmentData> = emptyList()
    }
}
