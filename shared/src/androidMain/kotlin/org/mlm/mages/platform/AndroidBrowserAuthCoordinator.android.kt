package org.mlm.mages.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import kotlinx.coroutines.CompletableDeferred

object AndroidBrowserAuthCoordinator {
    const val scheme = "com.github.mlm-games.mages"
    const val oauthRedirectUri = "$scheme:/oauth"
    const val ssoRedirectUri = "$scheme:/sso"

    private val lock = Any()
    private var pendingCallback: CompletableDeferred<String>? = null

    fun beginLogin(): CompletableDeferred<String> {
        val deferred = CompletableDeferred<String>()
        synchronized(lock) {
            pendingCallback?.cancel()
            pendingCallback = deferred
        }
        return deferred
    }

    fun isCallback(uri: Uri): Boolean = uri.scheme == scheme

    fun handleCallback(uri: Uri) {
        synchronized(lock) {
            pendingCallback?.complete(uri.toString())
            pendingCallback = null
        }
    }

    fun clear(deferred: CompletableDeferred<String>) {
        synchronized(lock) {
            if (pendingCallback === deferred) {
                pendingCallback = null
            }
        }
    }

    fun launch(context: Context, url: String): Boolean {
        val uri = Uri.parse(url)

        val customTabOk = runCatching {
            CustomTabsIntent.Builder().build().launchUrl(context, uri)
        }.isSuccess

        if (customTabOk) return true

        return runCatching {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }.isSuccess
    }
}
