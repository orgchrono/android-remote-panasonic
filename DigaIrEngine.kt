package com.vhs.digacommander.core.ir

import android.content.Context
import android.hardware.ConsumerIrManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.roundToInt

/**
 * Conversor matemático de códigos no padrão Philips Pronto Hex para
 * matriz de pulsos alternados em microssegundos aceita pelo ConsumerIrManager do Android.
 */
object ProntoHexConverter {

    data class IrSignal(
        val carrierFrequency: Int,
        val patternMicroseconds: IntArray
    )

    /**
     * Converte uma string Pronto Hex em um par (frequência em Hz, vetor de microssegundos).
     */
    fun convert(prontoHex: String): IrSignal {
        val tokens = prontoHex.trim().split("\\s+".toRegex())
        require(tokens.size >= 4) { "Formato Pronto Hex inválido: cabeçalho incompleto" }

        val formatCode = tokens[0].toInt(16)
        require(formatCode == 0) { "Apenas sinais Pronto em modo aprendido cru (0000) são suportados" }

        val freqCode = tokens[1].toInt(16)
        require(freqCode > 0) { "Código de frequência inválido" }

        // Frequência de clock da portadora: f = 1.000.000 / (freqCode * 0.241246)
        val carrierFreq = (1_000_000.0 / (freqCode * 0.241246)).roundToInt()
        val cycleToMicroseconds = 1_000_000.0 / carrierFreq

        val rawDataWords = tokens.subList(4, tokens.size)
        val pattern = IntArray(rawDataWords.size) { index ->
            val cycles = rawDataWords[index].toInt(16)
            (cycles * cycleToMicroseconds).roundToInt()
        }

        return IrSignal(carrierFreq, pattern)
    }
}

/**
 * Wrapper de transmissão direta com o hardware infravermelho nativo do Samsung SM-T805M.
 */
class ConsumerIrTransmitter(context: Context) {

    private val irManager: ConsumerIrManager? =
        context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager

    val isAvailable: Boolean
        get() = irManager?.hasIrEmitter() == true

    /**
     * Valida se a portadora (geralmente ~37kHz a 38kHz para Panasonic) é suportada pelo emissor.
     */
    fun isFrequencySupported(frequencyHz: Int): Boolean {
        if (!isAvailable) return false
        val ranges = irManager?.carrierFrequencies ?: return false
        return ranges.any { frequencyHz in it.minFrequency..it.maxFrequency }
    }

    /**
     * Transmite um pulso infravermelho de baixo nível.
     */
    fun transmit(signal: ProntoHexConverter.IrSignal) {
        check(isAvailable) { "Hardware IR indisponível neste dispositivo" }
        irManager?.transmit(signal.carrierFrequency, signal.patternMicroseconds)
    }

    fun transmitPronto(prontoHex: String) {
        val signal = ProntoHexConverter.convert(prontoHex)
        transmit(signal)
    }
}

/**
 * Estados observáveis da execução de macros no painel de controle.
 */
sealed class MacroExecutionState {
    object Idle : MacroExecutionState()
    data class Running(val step: Int, val totalSteps: Int, val currentLabel: String) : MacroExecutionState()
    object Completed : MacroExecutionState()
    data class Error(val message: String) : MacroExecutionState()
    object Cancelled : MacroExecutionState()
}

/**
 * Pipeline de execução de macros com garantia anti-brick:
 * 1. Fila de execução estritamente serializada com Mutex.
 * 2. Temporização monotônica de 500 ms (janela de segurança do Renesas SH-4).
 * 3. Cancelamento em tempo real sem deixar o gravador em estado indefinido.
 */
class DigaMacroPipeline(
    private val transmitter: ConsumerIrTransmitter,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val mutex = Mutex()
    private var activeJob: Job? = null

    private val _state = MutableStateFlow<MacroExecutionState>(MacroExecutionState.Idle)
    val state: StateFlow<MacroExecutionState> = _state.asStateFlow()

    data class MacroCommand(
        val label: String,
        val prontoHex: String,
        val postDelayMs: Long = 500L
    )

    fun executeMacro(commands: List<MacroCommand>) {
        activeJob?.cancel()
        activeJob = coroutineScope.launch {
            mutex.withLock {
                try {
                    for ((index, cmd) in commands.withIndex()) {
                        ensureActive()
                        _state.value = MacroExecutionState.Running(
                            step = index + 1,
                            totalSteps = commands.size,
                            currentLabel = cmd.label
                        )

                        transmitter.transmitPronto(cmd.prontoHex)
                        delay(cmd.postDelayMs)
                    }
                    _state.value = MacroExecutionState.Completed
                } catch (e: CancellationException) {
                    _state.value = MacroExecutionState.Cancelled
                } catch (e: Exception) {
                    _state.value = MacroExecutionState.Error(e.message ?: "Erro desconhecido")
                } finally {
                    delay(1000)
                    if (_state.value is MacroExecutionState.Completed || _state.value is MacroExecutionState.Cancelled) {
                        _state.value = MacroExecutionState.Idle
                    }
                }
            }
        }
    }

    fun cancelMacro() {
        activeJob?.cancel()
    }
}
