package com.vhs.digacommander

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.vhs.digacommander.core.ir.ConsumerIrTransmitter
import com.vhs.digacommander.core.ir.DigaMacroPipeline
import com.vhs.digacommander.ui.DigaApp
import com.vhs.digacommander.ui.theme.DigaDarkBackground
import com.vhs.digacommander.ui.theme.PanasonicDigaCommanderTheme

class MainActivity : ComponentActivity() {

    private lateinit var transmitter: ConsumerIrTransmitter
    private lateinit var macroPipeline: DigaMacroPipeline

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa a camada de hardware IR e pipeline seguro de macros
        transmitter = ConsumerIrTransmitter(this)
        macroPipeline = DigaMacroPipeline(transmitter)

        setContent {
            PanasonicDigaCommanderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DigaDarkBackground
                ) {
                    DigaApp(
                        transmitter = transmitter,
                        macroPipeline = macroPipeline
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        macroPipeline.cancelMacro()
    }
}
