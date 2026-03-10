package org.mlm.mages

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.github.mlmgames.settings.core.SettingsRepository
import io.github.mlmgames.settings.core.datastore.createSettingsDataStore
import kotlinx.coroutines.flow.emptyFlow
import org.mlm.mages.di.KoinApp
import org.mlm.mages.settings.AppSettingsSchema

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val settingsRepo = SettingsRepository(
        createSettingsDataStore("mages_settings"),
        AppSettingsSchema
    )

    ComposeViewport {
        KoinApp(settingsRepo) {
            App(settingsRepo, deepLinks = emptyFlow())
        }
    }
}
