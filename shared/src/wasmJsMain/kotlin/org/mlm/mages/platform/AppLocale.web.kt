package org.mlm.mages.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf

actual object LocalAppLocale { // TODO: Wire later
    private val appLocale = staticCompositionLocalOf { "en-US" } 

    actual val current: String
        @Composable get() = appLocale.current

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        return appLocale.provides(value ?: appLocale.current)
    }
}
