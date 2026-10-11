package com.vhs.digacommander.data.model

/**
 * Immutable command entry within an automated execution sequence.
 *
 * @property label Human-readable description displayed in the virtual VFD during execution.
 * @property prontoHex Philips Pronto Hex formatted IR payload.
 * @property postDelayMs Post-transmission pause duration in milliseconds (default: 500 ms anti-brick window).
 */
data class MacroCommand(
    val label: String,
    val prontoHex: String,
    val postDelayMs: Long = 500L
)
