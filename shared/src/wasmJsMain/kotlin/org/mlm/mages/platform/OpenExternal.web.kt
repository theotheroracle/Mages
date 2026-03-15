package org.mlm.mages.platform

import androidx.compose.runtime.Composable
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLAnchorElement

@Composable
actual fun rememberFileOpener(): (String, String?) -> Boolean {
    return { url, _ -> openUrl(url) }
}

fun openUrl(url: String): Boolean {
    return try {
        when {
            url.startsWith("http://") || url.startsWith("https://") ->
                window.open(url, "_blank", "noopener,noreferrer") != null

            url.startsWith("blob:") || url.startsWith("data:") ->
                window.open(url, "_blank") != null

            else -> false
        }
    } catch (_: Throwable) {
        false
    }
}

fun downloadUrl(url: String, filename: String): Boolean {
    return try {
        val anchor = document.createElement("a") as HTMLAnchorElement
        anchor.href = url
        anchor.download = filename
        anchor.setAttribute("style", "display:none")
        document.body?.appendChild(anchor)
        anchor.click()
        document.body?.removeChild(anchor)
        true
    } catch (_: Throwable) {
        false
    }
}
