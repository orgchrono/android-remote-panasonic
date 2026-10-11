package com.vhs.digacommander

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vhs.digacommander.data.ir.ConsumerIrTransmitter
import com.vhs.digacommander.domain.pipeline.DigaMacroPipeline
import com.vhs.digacommander.presentation.theme.DigaDarkBackground
import com.vhs.digacommander.presentation.theme.PanasonicDigaCommanderTheme
import com.vhs.digacommander.presentation.ui.DigaMainScreen
import com.vhs.digacommander.presentation.viewmodel.DigaViewModel

/**
 * Main Android activity hosting the Jetpack Compose DIGA Commander interface.
 * Initializes the hardware Consumer IR transmitter, coroutine pipeline,
 * and binds the reactive MVVM presentation layer.
 */
class MainActivity : ComponentActivity() {
    private lateinit var transmitter: ConsumerIrTransmitter
    private lateinit var macroPipeline: DigaMacroPipeline

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize hardware Consumer IR abstraction and monotonic coroutine pipeline
        transmitter = ConsumerIrTransmitter(this)
        macroPipeline = DigaMacroPipeline(transmitter)

        // Bind ViewModel scoped to ComponentActivity lifecycle
        val factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DigaViewModel(transmitter, macroPipeline) as T
                }
            }
        val viewModel = ViewModelProvider(this, factory)[DigaViewModel::class.java]

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

    override fun onDestroy() {
        super.onDestroy()
        if (::macroPipeline.isInitialized) {
            macroPipeline.cancelMacro()
        }
    }
}
