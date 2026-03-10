package org.mlm.mages.platform

import androidx.compose.runtime.Composable

@Composable
actual fun rememberShareHandler(): (ShareContent) -> Unit {
    return { content ->
        val text = content.text ?: content.allFilePaths.joinToString("\n")
    }
}
