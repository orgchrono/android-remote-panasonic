package com.vhs.digacommander

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vhs.digacommander.data.ir.ConsumerIrTransmitter
import com.vhs.digacommander.data.ir.SharedPrefsIrConfigStore
import com.vhs.digacommander.presentation.theme.DigaDarkBackground
import com.vhs.digacommander.presentation.theme.PanasonicDigaCommanderTheme
import com.vhs.digacommander.presentation.ui.DigaMainScreen
import com.vhs.digacommander.presentation.viewmodel.DigaViewModel

/**
 * Main Android activity hosting the Jetpack Compose DIGA Commander interface.
 *
 * Composition root only: it wires the hardware transmitter and the persisted IR settings into the
 * ViewModel. The ViewModel (not the Activity) owns macro lifetime, so rotating the tablet no
 * longer aborts a running macro.
 */
class MainActivity : ComponentActivity() {
    private val viewModel: DigaViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val hardwareDefault = ConsumerIrTransmitter.defaultConfigForDevice()
                val configStore = SharedPrefsIrConfigStore(applicationContext, hardwareDefault)
                val transmitter = ConsumerIrTransmitter(applicationContext, configStore.load())
                return DigaViewModel(transmitter = transmitter, configStore = configStore) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PanasonicDigaCommanderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DigaDarkBackground,
                ) {
                    DigaMainScreen(viewModel = viewModel)
                }
            }
        }
    }
}
