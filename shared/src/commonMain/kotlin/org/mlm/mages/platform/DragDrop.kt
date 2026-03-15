package org.mlm.mages.platform

import androidx.compose.ui.Modifier
import org.mlm.mages.ui.components.AttachmentData

/**
 * Adds file drag&drop support.
 *
 * onDrop receives a list of [AttachmentData] representing dropped files.
 * - Desktop: real file paths from the OS
 * - Android: content URIs are copied to cacheDir and returned as file paths
 * - Web: browser File objects are converted to AttachmentData with proper sourceKind
 */
expect fun Modifier.fileDrop(
    enabled: Boolean,
    onDragEnter: () -> Unit,
    onDragExit: () -> Unit,
    onDrop: (List<AttachmentData>) -> Unit
): Modifier