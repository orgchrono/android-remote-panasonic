package com.vhs.digacommander.data.model

/**
 * Observable lifecycle states of automated macro pipelines.
 */
sealed interface MacroExecutionState {
    data object Idle : MacroExecutionState
    data class Running(val step: Int, val totalSteps: Int, val currentLabel: String) : MacroExecutionState
    data object Completed : MacroExecutionState
    data class Error(val message: String) : MacroExecutionState
    data object Cancelled : MacroExecutionState
}
