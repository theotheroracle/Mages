package org.mlm.mages.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.koin.android.ext.android.inject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.mlm.mages.MatrixService
import org.mlm.mages.platform.BindLifecycle
import org.mlm.mages.platform.SessionBootstrapper
import org.mlm.mages.push.ConversationShortcutPublisher
import org.mlm.mages.ui.screens.RoomScreen
import org.mlm.mages.ui.theme.MainTheme
import org.mlm.mages.ui.viewmodel.RoomViewModel

class BubbleConversationActivity : ComponentActivity() {
    private val service: MatrixService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val roomId = intent.getStringExtra(ConversationShortcutPublisher.EXTRA_ROOM_ID)
        if (roomId.isNullOrBlank()) {
            finish()
            return
        }

        setContent {
            BindLifecycle(service, resetSyncState = false)

            var ready by remember { mutableStateOf(false) }

            LaunchedEffect(roomId) {
                SessionBootstrapper.ensureReadyAndSyncing(service)
                if (service.isLoggedIn() && service.portOrNull != null) {
                    ready = true
                } else {
                    finish()
                }
            }

            MainTheme {
                if (!ready) {
                    LoadingIndicator()
                } else {
                    val vm: RoomViewModel = koinViewModel { parametersOf(roomId, "") }
                    RoomScreen(
                        viewModel = vm,
                        onBack = { finish() },
                        onOpenInfo = { },
                        onNavigateToRoom = { _, _ -> },
                        onNavigateToThread = { _, _, _ -> },
                        onStartCall = { },
                        onStartVoiceCall = { },
                        onOpenForwardPicker = { _, _ -> },
                        isBubbleMode = true,
                    )
                }
            }
        }
    }
}