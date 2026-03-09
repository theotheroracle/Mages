package org.mlm.mages.platform

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

actual object LocalAppLocale {
    private var defaultLocale: Locale? = null

    actual val current: String
        @Composable get() = Locale.getDefault().toLanguageTag()

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val currentConfiguration = LocalConfiguration.current
        if (defaultLocale == null) {
            defaultLocale = Locale.getDefault()
        }

        val locale = value?.let(Locale::forLanguageTag) ?: defaultLocale!!
        val configuration = Configuration(currentConfiguration).apply {
            setLocale(locale)
        }

        Locale.setDefault(locale)
        val resources = LocalContext.current.resources
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return LocalConfiguration.provides(configuration)
    }
}
