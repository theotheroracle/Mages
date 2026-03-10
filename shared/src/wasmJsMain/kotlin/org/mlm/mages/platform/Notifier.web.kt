package org.mlm.mages.platform

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.github.mlmgames.settings.core.SettingsRepository
import org.mlm.mages.MatrixService
import org.mlm.mages.settings.AppSettings

actual object Notifier {
    actual fun notifyRoom(title: String, body: String) {
    }

    actual fun setCurrentRoom(roomId: String?) {
    }

    actual fun setWindowFocused(focused: Boolean) {
    }

    actual fun shouldNotify(roomId: String, senderIsMe: Boolean): Boolean {
        return !senderIsMe
    }
}

@Composable
actual fun BindLifecycle(service: MatrixService) {
}

@Composable
actual fun BindNotifications(
    service: MatrixService,
    settingsRepository: SettingsRepository<AppSettings>
) {
}

@Composable
actual fun rememberQuitApp(): () -> Unit {
    return {
    }
}
