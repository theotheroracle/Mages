package org.mlm.mages.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.browser.window

actual object LocalAppLocale {
    private val appLocale = staticCompositionLocalOf { 
        window.navigator.language.ifBlank { "en-US" }
    }

    actual val current: String
        @Composable get() = appLocale.current

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        return appLocale.provides(value ?: appLocale.current)
    }
}
