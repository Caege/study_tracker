package com.example.studytracker.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object TimerServiceRepository {

	private val _timerState = MutableStateFlow(TimerState())
	val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

	fun updateTimerState(newState: TimerState) {
		_timerState.value = newState
	}

	fun reset() {
		_timerState.value = TimerState()
	}
}