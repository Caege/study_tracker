package com.example.studytracker.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.studytracker.BreakTimerService
import com.example.studytracker.BreakTimerService.Companion.EXTRA_DURATION_BREAK
import com.example.studytracker.TimerService
import com.example.studytracker.TimerService.Companion.BREAK_TIMER
import com.example.studytracker.TimerService.Companion.EXTRA_DURATION
import com.example.studytracker.TimerService.Companion.EXTRA_TAG
import com.example.studytracker.data.BreakServiceRepository
import com.example.studytracker.data.BreakTimerState
import com.example.studytracker.data.SessionRepository
import com.example.studytracker.data.Tag
import com.example.studytracker.data.TimerPreferencesRepository
import com.example.studytracker.data.TimerServiceRepository
import com.example.studytracker.data.TimerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
//import com.example.studytracker.notification.startOngoingTimer
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TimerViewModel(
	application: Application, val sessionRepository: SessionRepository,
	val timerPreferencesRepository: TimerPreferencesRepository
) :
	AndroidViewModel(application) {

		val timerStateRepository = TimerServiceRepository

	val breakTimerStateRepository = BreakServiceRepository

	val totalDuration: StateFlow<Int> =
		timerPreferencesRepository.totalDuration
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000L),
				initialValue = 0
			)


	val breakDuration: StateFlow<Int> =
		timerPreferencesRepository.breakDuration
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000L),
				initialValue = 0
			)

	val selectedTagId = timerPreferencesRepository.selectedTagId	.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000L),
		initialValue = 1
	)


	val studyGoal = timerPreferencesRepository.studyGoal.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000L),
		initialValue = 0
	)



	@OptIn(ExperimentalCoroutinesApi::class)
	val selectedTag: StateFlow<Tag?> =
		selectedTagId
			.filterNotNull()
			.flatMapLatest { id ->  flow { emit(sessionRepository.getTagById(id)) } }
			.stateIn(
				viewModelScope,
				SharingStarted.WhileSubscribed(5000),
				null
			)

//	private var _selectedTag = MutableStateFlow<Tag?>(null)
//
//	private var _cha = viewModelScope.async {
//		_selectedTag.value = sessionRepository.getTagById(selectedTagId.value)
//	}
//	val selectedTag = _selectedTag.asStateFlow()


//	var isTimerStarted = MutableStateFlow(false)
//	var isTimerPaused = MutableStateFlow(false)
//	private val _timerProgress = MutableStateFlow(0)
//	private val _shitProgress = MutableStateFlow(0)
//	val shitProgress = _shitProgress.asStateFlow()
//	val timerProgress: StateFlow<Int> = _timerProgress.asStateFlow()


	fun updateTag(tagId: Int) {
		viewModelScope.launch {
			timerPreferencesRepository.changeSelectedTagId(tagId)
		}
	}


	fun changeStudyGoal(goal : Int) : Unit {
		viewModelScope.launch {
			Log.d("setting_shit", "changed: $goal")
			timerPreferencesRepository.changeStudyGoal(goal)
		}
	}



	val timerState: StateFlow<TimerState> =
		timerStateRepository.timerState.stateIn(
			viewModelScope,
			SharingStarted.WhileSubscribed(5000),
			TimerState()
		)

	val breakTimerState : StateFlow<BreakTimerState> = breakTimerStateRepository.timerState.stateIn(
		viewModelScope,
		SharingStarted.WhileSubscribed(5000),
		BreakTimerState()
	)

//	fun updateProgress(value: Int, isStarted: Boolean, isPaused: Boolean) {
//
//		viewModelScope.launch(Dispatchers.Main) {
//			_timerProgress.value = value
//			isTimerStarted.value = isStarted
//			isTimerPaused.value = isPaused
//
//		}
//	}

//	private var timerJob: Job? = null
//	override fun onCleared() {
//		super.onCleared()
//		timerJob?.cancel()
//		timerJob = null
//	}
	//	fun startTimer(durationSeconds: Int) {
	//		timerJob?.cancel()  // cancel previous timer if any
	//		_timerProgress.value = 0
	//
	//		isTimerStarted.value = true
	//
	//		val intent = Intent(getApplication(), TimerService::class.java).apply {
	//			putExtra("durationSeconds", durationSeconds)
	//		}
	//
	//		ContextCompat.startForegroundService(getApplication(), intent)
	//
	//		timerJob = viewModelScope.launch {
	//			for (i in durationSeconds downTo 0) {
	//				_timerProgress.value = i
	//
	//
	//				delay(1000) // update every second
	//			}
	//
	//			isTimerStarted.value = false
	//		}
	//	}
	fun startTimer(durationSeconds: Int, tag: Tag?) {
		val tagName = tag?.name ?: "default"

		Log.d("TimerCore", "timer started")
		// mark timer started
		//		isTimerStarted.value = true
		//		_timerProgress.value = durationSeconds
		// Start the foreground service ONCE
		viewModelScope.launch {
			val tag = sessionRepository.getTagByName(tagName)
			val intent = Intent(getApplication(), TimerService::class.java).apply {
				putExtra(EXTRA_DURATION, durationSeconds)
				putExtra(EXTRA_TAG, tag?.id)
			}
			ContextCompat.startForegroundService(getApplication(), intent)
		}


	}
	//
	//	fun resumeTimer() {
	//		timerJob?.cancel()
	//		isTimerStarted.value = true
	//
	//		timerJob = viewModelScope.launch {
	//
	//			for (i in _timerProgress.value downTo 0)  {
	////				_timerProgress.value = i
	//				Log.d("timer" ,"progress=$i")
	//
	////				startOngoingTimer(getApplication(), durationSeconds.value, timerProgress.value )
	//
	////				val intent = Intent(getApplication(), TimerService::class.java)
	////				intent.putExtra("durationSeconds", _timerProgress.value)
	////				ContextCompat.startForegroundService(getApplication(), intent)
	//				delay(1000) // update every second
	//			}
	//
	//
	//
	//		}
	//	}
	//	fun setDuration(durationSeconds: Int) {
	//		this.durationSeconds.value = durationSeconds
	//	}
	//
	//	fun pauseTimer() {
	//		timerJob?.cancel()
	//		isTimerStarted.value = false
	//		val intent = Intent(getApplication(), TimerService::class.java).apply {
	//			putExtra("pauseTimer", true)
	//		}
	//		ContextCompat.startForegroundService(getApplication(), intent)
	//	}
	//
	//	fun stopTimer() {
	//		timerJob?.cancel()
	//
	//
	//		isTimerStarted.value = false
	//	}
	fun pauseTimer(context: Context) {
		Log.d("TimerCore", "timer paused")
		//		isTimerStarted.value = false
		val intent = Intent(context, TimerService::class.java)
		intent.putExtra(TimerService.EXTRA_PAUSE, true)
		intent.putExtra(EXTRA_TAG,selectedTag.value?.id)
		context.startService(intent)
	}

	fun resumeTimer(context: Context) {
		//		isTimerStarted.value = true
		Log.d("TimerCore", "timer resumed")
		val intent = Intent(context, TimerService::class.java)
		intent.putExtra(TimerService.EXTRA_RESUME, true)
		intent.putExtra(EXTRA_TAG,selectedTag.value?.id)
		context.startService(intent)
	}

	fun stopTimer(context: Context) {
		Log.d("TimerCore", "timer stopped")
		//		isTimerStarted.value = false
		val intent = Intent(context, TimerService::class.java)
		intent.putExtra(EXTRA_TAG,selectedTag.value?.id)
		intent.putExtra(TimerService.EXTRA_STOP, true)
		context.startService(intent)
	}

	fun startBreakTimer(durationSeconds: Int) {
//		val tagName = tag?.name ?: "default"

		Log.d("TimerCore", "timer started")
		// mark timer started
		//		isTimerStarted.value = true
		//		_timerProgress.value = durationSeconds
		// Start the foreground service ONCE
		viewModelScope.launch {
//			val tag = sessionRepository.getTagByName(tagName)
			val intent = Intent(getApplication(), BreakTimerService::class.java).apply {
				putExtra(BreakTimerService.EXTRA_DURATION_BREAK, durationSeconds)

//				putExtra(EXTRA_TAG, tag?.id)
			}
			ContextCompat.startForegroundService(getApplication(), intent)
		}


	}


	fun pauseBreakTimer(context: Context) {
		Log.d("TimerCore", "timer paused")
		//		isTimerStarted.value = false
		val intent = Intent(context, BreakTimerService::class.java)
		intent.putExtra(BreakTimerService.EXTRA_PAUSE_BREAK, true)
//		intent.putExtra(EXTRA_TAG,selectedTag.value?.id)
		context.startService(intent)
	}

	fun resumeBreakTimer(context: Context) {
		//		isTimerStarted.value = true
		Log.d("TimerCore", "timer resumed")
		val intent = Intent(context, BreakTimerService::class.java)
		intent.putExtra(BreakTimerService.EXTRA_RESUME_BREAK, true)
//		intent.putExtra(EXTRA_TAG,selectedTag.value?.id)
		context.startService(intent)
	}

	fun stopBreakTimer(context: Context) {
		Log.d("TimerCore", "timer stopped")
		//		isTimerStarted.value = false
		val intent = Intent(context, BreakTimerService::class.java)
//		intent.putExtra(EXTRA_TAG,selectedTag.value?.id)
		intent.putExtra(BreakTimerService.EXTRA_STOP_BREAK, true)
		context.startService(intent)
	}


	//data preference
	fun changeTotalDuration(totalDuration: Int) {
		viewModelScope.launch {
			timerPreferencesRepository.changeTotalDuration(totalDuration)
		}
	}


	fun changeBreakDuration(breakDuration: Int) {
		viewModelScope.launch {
			timerPreferencesRepository.changeBreakDuration(breakDuration)
		}
	}
}
