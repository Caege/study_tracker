package com.example.studytracker.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object BreakServiceRepository {

	private val _breakTimerState = MutableStateFlow(BreakTimerState())
	val timerState: StateFlow<BreakTimerState> = _breakTimerState.asStateFlow()

	fun updateBreakTimerState(newState: BreakTimerState) {
		_breakTimerState.value = newState
	}

	fun reset() {
		_breakTimerState.value = BreakTimerState()
	}
}