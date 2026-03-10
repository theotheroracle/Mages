package org.mlm.mages.platform

import androidx.compose.runtime.Composable

@Composable
actual fun rememberFileOpener(): (String, String?) -> Boolean {
    return { path, _ ->
        path.startsWith("http://") || path.startsWith("https://") || path.startsWith("blob:")
    }
}
