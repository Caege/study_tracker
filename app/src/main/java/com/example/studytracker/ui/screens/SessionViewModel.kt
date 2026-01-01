package com.example.studytracker.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studytracker.data.DailyDuration
import com.example.studytracker.data.DailyDurationDate
import com.example.studytracker.data.Session
import com.example.studytracker.data.SessionRepository
import com.example.studytracker.data.Tag
import com.example.studytracker.data.SessionWithTag
import com.example.studytracker.data.TimerPreferencesRepository
import com.example.studytracker.utils.createDateFromDay
import com.example.studytracker.utils.formatMillisToDayMonthYear
import com.example.studytracker.utils.getTodayRange
import com.example.studytracker.utils.millisToYearMonth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

class SessionViewModel(private val repository: SessionRepository, private val timerPreferencesRepository: TimerPreferencesRepository) : ViewModel
																																													() {

	private val _monthBreakdown = MutableStateFlow<List<DailyDurationDate>>(emptyList())
	val monthBreakdown: StateFlow<List<DailyDurationDate>> = _monthBreakdown.asStateFlow()

	private val _past7days = MutableStateFlow<Int>(0)
	val past7days: StateFlow<Int> = _past7days


	private val _studyGoal = timerPreferencesRepository.studyGoal.stateIn(viewModelScope,
		SharingStarted.WhileSubscribed(5000),0)

	val studyGoal = _studyGoal

	private val dailyGoalHours = _studyGoal.value






	// Expose tags as StateFlow for UI
	val allTags: StateFlow<List<Tag>> =
		repository.getAllTagsStream()
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = emptyList()
			)
	val allSessions = repository.getSessionWithTagStream().stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = emptyList()
	)

	val monthBreakDownDate = MutableStateFlow<List<DailyDurationDate>>(emptyList())


/** FOR STUDY HISTORY SCREEN */
	@RequiresApi(Build.VERSION_CODES.O)
	fun loadMonthBreakdown(milli : Long) {
	//		Log.d("month_shit", "selected month, ${month}")
	val month = millisToYearMonth(milli)
	viewModelScope.launch {
		val monthStart = month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
		val monthEnd =
			month.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()


		val results = repository.getDailyDurationsForMonth(monthStart, monthEnd)


		val map = results.associateBy { it.day.toInt() }



		Log.d("month_shit", "month start: ${formatMillisToDayMonthYear(monthStart)}, month end : ${monthEnd}")

		_monthBreakdown.value = (1..month.lengthOfMonth()).map { day ->
			map[day] ?: DailyDuration(day.toString().padStart(2, '0'), 0)
		}.filter { it.totalDuration > 0 }.map {
			Log.d("month_shit", "loop day : ${it.day}")
			DailyDurationDate(
				day = createDateFromDay(
					it.day,
					referenceMillis = milli
				), totalDuration = it.totalDuration
			)
		}
	}

	Log.d("month_shit", monthBreakdown.toString())

}


	/** Fetch a tag with all its sessions */
	//	fun getTagWithSessions(tagId: Int): Flow<SessionWithTag?> =
	//		repository.getTagWithSessionsStream(tagId)


	/** Fetch sessions for today */
	@RequiresApi(Build.VERSION_CODES.O)
	fun getTodaysSessions(): Flow<List<SessionWithTag>> {
		val (start, end) = getTodayRange()  // your java.time util function
		return repository.getSessionsForDayStream(start, end)
	}

	/** Insert a tag */
	fun insertTag(tag: Tag, onInserted: (Long) -> Unit = {}) {
		viewModelScope.launch {
			val id = repository.insertTag(tag)
			onInserted(id)
		}
	}

	/** Insert a session */
	fun insertSession(session: Session, onInserted: (Long) -> Unit = {}) {
		viewModelScope.launch {
			val id = repository.insertSession(session)
			onInserted(id)
		}
	}

	@RequiresApi(Build.VERSION_CODES.O)
	val streakStatsFlow: StateFlow<StreakStats> =
		allSessions // Flow<List<SessionWithTag>>
			.map { sessions ->
				withContext(Dispatchers.Default) {
					calculateStats(sessions, dailyGoalHours)
				}
			}
			.stateIn(
				viewModelScope,
				SharingStarted.WhileSubscribed(5000),
				StreakStats(0, 0L)
			)




	@RequiresApi(Build.VERSION_CODES.O)
	private fun calculateStats(
		sessions: List<SessionWithTag>,
		dailyGoalHours: Int
	): StreakStats {
		val zoneId = ZoneId.systemDefault()
		val dailyGoalSeconds = dailyGoalHours * 3600

		// Group sessions by day
		val sessionsByDate = sessions.groupBy { swt ->
			Instant.ofEpochMilli(swt.session.startTime).atZone(zoneId).toLocalDate()
		}

		// Calculate daily totals
		val dailyTotals = sessionsByDate.mapValues { (_, dailySessions) ->
			dailySessions.sumOf { it.session.duration }
		}

		// Past 7 days total
		val today = LocalDate.now(zoneId)
		val last7Days = (0..6).map { today.minusDays(it.toLong()) }
		val totalSeconds = last7Days.sumOf { dailyTotals[it] ?: 0L }

		// Streak calculation
		var streak = 0
		var currentDate = today
		while (true) {
			val total = dailyTotals[currentDate] ?: 0L
			if (total >= dailyGoalSeconds) {
				streak++
				currentDate = currentDate.minusDays(1)
			} else break
		}

		return StreakStats(streak, totalSeconds)
	}
//
//	@RequiresApi(Build.VERSION_CODES.O)
//	suspend fun calculateStreak(
//		sessions: List<SessionWithTag>,
//		dailyGoalHours: Int
//	): Int = withContext(Dispatchers.Default) {
//		val zoneId = ZoneId.systemDefault()
//		val dailyGoalSeconds = dailyGoalHours * 3600
//
//		// Step 1: Group sessions by day
//		val sessionsByDate = sessions.groupBy { swt ->
//			Instant.ofEpochMilli(swt.session.startTime).atZone(zoneId).toLocalDate()
//		}
//		Log.d("7days", sessionsByDate.toString())
//
//		// Step 2: Calculate total duration per day
//		val dailyTotals = sessionsByDate.mapValues { (_, dailySessions) ->
//			dailySessions.sumOf { it.session.duration }
//		}
//		Log.d("7days", dailyTotals.toString())
//
//		// Calculate total seconds for last 7 days
//		val today = LocalDate.now(zoneId)
//		val last7Days = (0..6).map { today.minusDays(it.toLong()) }
//		val totalSeconds = last7Days.sumOf { dailyTotals[it] ?: 0L }
//
//		// safely post value on main thread
//		withContext(Dispatchers.Main) {
//			_past7days.value = totalSeconds.toInt()
//		}
//
//		// Step 3: Count streak backwards from today
//		var streak = 0
//		var currentDate = today
//
//		while (true) {
//			val total = dailyTotals[currentDate] ?: 0L
//			if (total >= dailyGoalSeconds) {
//				streak++
//				currentDate = currentDate.minusDays(1)
//			} else {
//				break // stop streak when a day fails
//			}
//		}
//
//		streak
//	}


	// change study goal
	fun changeStudyGoal(hrs : Int) {

		viewModelScope.launch {
			timerPreferencesRepository.changeStudyGoal(hrs)
		}

	}

}


data class StreakStats(
	val streak: Int,
	val past7DaysTotalSeconds: Long
)