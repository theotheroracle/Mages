package org.mlm.mages.ui.components

data class AttachmentData(
    val path: String,
    val mimeType: String,
    val fileName: String,
    val sizeBytes: Long,
    val sourceKind: AttachmentSourceKind = AttachmentSourceKind.LocalPath,
)

enum class AttachmentSourceKind {
    LocalPath,
    WebObjectUrl,
    WebBlobToken,
}