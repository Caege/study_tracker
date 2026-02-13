package com.example.studytracker.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontVariation.weight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studytracker.data.SessionWithTag
import com.example.studytracker.ui.AppViewModelProvider
import com.example.studytracker.ui.components.Dot
import com.example.studytracker.ui.components.SessionCard
import com.example.studytracker.ui.components.SummaryListItem
import com.example.studytracker.ui.navigation.NavigationDestination
import com.example.studytracker.ui.theme.Dimens
import com.example.studytracker.ui.theme.ExtraTypography
import com.example.studytracker.utils.formatSecondsToMMSS
import com.example.studytracker.utils.formatSecondsToReadableTime
import com.example.studytracker.utils.millisToTimeString
import com.example.studytracker.utils.secondsToHoursMinutes
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import kotlin.math.roundToInt

object HomeDestination : NavigationDestination {
	override val route = "home"
	override val titleRes = "Study Tracker"
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
	onAddSessionScreenClick: () -> Unit, onStartSessionScreenClick: () -> Unit,
	onStudyHistoryScreenClick: () -> Unit, sessionViewModel: SessionViewModel =
		viewModel(factory = AppViewModelProvider.Factory),
	onSettingsClick : () -> Unit
) {
val coroutineScope = rememberCoroutineScope()
	val sessionList: List<SessionWithTag> by sessionViewModel.getTodaysSessions().collectAsState(
		emptyList()
	)



	Log.d("month_shit2", sessionList.toString())
//todayStudyTime is in seconds
	val todayStudyTime = sessionList.sumOf { it.session.duration }
	val allSessions = sessionViewModel.allSessions.collectAsState().value
val streak = sessionViewModel.streakStatsFlow.collectAsState().value.streak

	val past7days = sessionViewModel.streakStatsFlow.collectAsState().value.past7DaysTotalSeconds.toInt()
//sessionViewModel.changeStudyGoal(1)

	// studyGoal returns hrs so convert to seconds
	val studyGoal = sessionViewModel.studyGoal.collectAsState().value * 3600


	val todayStudyProgression : Float = (todayStudyTime.toFloat()/studyGoal.toFloat()) * 100
Log.d("HomeScreen", "todayStudyProgression: $todayStudyProgression")

	val sessionGroup : List<SessionWithTag> = sessionViewModel.allSessions.collectAsState().value
  val scrollBehavior = enterAlwaysScrollBehavior()

	//Log.d("HomeScreen", "sessionGroup: ${sessionGroup.groupBy { LocalDate.from(startTime) }}")
Scaffold (
	topBar = {
		TopAppBar(
			colors = TopAppBarDefaults.topAppBarColors(
				containerColor = MaterialTheme.colorScheme.surface,
				titleContentColor = MaterialTheme.colorScheme.onSurface,
			),
			title = {
				Text("Study Tracker")
			},
			scrollBehavior = scrollBehavior,

			actions = {
				IconButton(onClick = {onSettingsClick()}) {
					Icon(Icons.Default.Settings, contentDescription = null)
				}
			}
		)
	},

) {innerPadding ->

	Surface(modifier = Modifier
		.padding(innerPadding)
		.padding(
			horizontal =
			Dimens.LargePadding
		),

		) {
		LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),) {
			item {
				SummeryCard(onStudyHistoryScreenClick, todayStudyTime, streak, past7days, todayStudyProgression)
			}

			if(sessionList.isNotEmpty()){
				item {
//					TodaysSessionCard(sessionList = sessionList)

					SessionCard(sessionList = sessionList, title = "TODAY'S SESSIONS")
				}


			}

			item {
				Row(
					modifier = Modifier
						.fillMaxWidth()
					,
					horizontalArrangement = Arrangement
						.spacedBy(
							24
								.dp, Alignment.CenterHorizontally
						)
				) {
					OutlinedButton(onClick = { onAddSessionScreenClick() }) {
						Text("ADD SESSION")
					}

					Button(onClick = { onStartSessionScreenClick() }) {
						Text("START SESSION")
					}
				}
			}


		}


//		Column(
//			modifier = Modifier
//				.fillMaxSize()
//				.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)
//		) {
//			SummeryCard(onStudyHistoryScreenClick, todayStudyTime, streak, past7days, todayStudyProgression)
//
//			if(sessionList.isNotEmpty()){
//				TodaysSessionCard(sessionList = sessionList, modifier = Modifier.weight(1f))
//			}
//
//
//			Row(
//				modifier = Modifier
//					.fillMaxWidth()
//					.align(Alignment.CenterHorizontally),
//				horizontalArrangement = Arrangement
//					.spacedBy(
//						24
//							.dp, Alignment.CenterHorizontally
//					)
//			) {
//				OutlinedButton(onClick = { onAddSessionScreenClick() }) {
//					Text("ADD SESSION")
//				}
//
//				Button(onClick = { onStartSessionScreenClick() }) {
//					Text("START SESSION")
//				}
//			}
//		}
	}

}




}

@Composable
fun SummeryCard(onStudyHistoryScreenClick: () -> Unit, todayStudyTime: Long, streak: Int,
								past7days: Int, todayStudyProgression : Float) {
	Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor =
	MaterialTheme.colorScheme.surfaceContainerLow)) {
		Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp), verticalArrangement
		= Arrangement.spacedBy(Dimens.LargePadding)) {
			Text("SUMMARY", style = MaterialTheme.typography.titleMedium, color = MaterialTheme
				.colorScheme.primary)

//			HorizontalDivider(modifier = Modifier .fillMaxWidth() // take full width
//				.padding(0.dp))

			Column(verticalArrangement = Arrangement.spacedBy(Dimens.LargePadding)) {
				ProgressBarDemo(todayStudyProgression)


				Column(verticalArrangement = Arrangement.spacedBy(Dimens.MediumPadding)) {
					SummaryListItem(
						"Today's study time"
					) { StudyTimeSummary(todayStudyTime) }


					SummaryListItem(
						"Streak"
					) { StreakSummary(streak) }
					SummaryListItem(
						"Hours studied in the past 7 days"
					) { HoursStudiedSummary(past7days) }

					Button(
						onClick = { onStudyHistoryScreenClick() },
					) {
						Text("GO TO STUDY HISTORY")
					}
				}

			}

		}
	}
}
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun TodaysSessionCard(sessionList: List<SessionWithTag>) {
//	Card(
//		modifier = Modifier
//			.fillMaxWidth()
//	) {
//
//		LazyColumn {
//			items(sessionList) {
//				FromDateToItem(it.)
//			}
//		}
//
//	}
//}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodaysSessionCard(sessionList: List<SessionWithTag>, modifier: Modifier = Modifier) {
	val sessionSize = sessionList.size
	val totalSeconds = sessionList.sumOf { it.session.duration }

	val hours = totalSeconds / 3600
	val minutes = totalSeconds / 60


	Card(colors = CardDefaults.cardColors(containerColor =
	MaterialTheme.colorScheme.surfaceContainerLow)) {
		Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp), verticalArrangement
		= Arrangement.spacedBy(Dimens.LargePadding)) {

			Text("TODAY'S SESSIONS", style = MaterialTheme.typography.titleMedium, color = MaterialTheme
				.colorScheme.primary)

			Row {
				Text("${sessionSize} sessions • ${formatSecondsToReadableTime(totalSeconds,true)}")
			}
			Column(verticalArrangement
			= Arrangement.spacedBy(8.dp)) {
				sessionList.forEachIndexed {index, sessionWithTag ->
					val duration = formatSecondsToMMSS(sessionWithTag.session.duration.toInt(), false,
						noPadding = true)
					FromDateToItem(
						from = sessionWithTag.session.startTime,
						end = sessionWithTag.session.endTime,
						tag = sessionWithTag.tag.name,
						duration = duration,
						last = sessionSize -1 == index
					)
				}
			}
		}



	}
//	Box (modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme
//		.surface)) {
//
//
//
//
//
//	}
//	BoxWithConstraints(modifier = modifier) {
//		val maxHeight = this.maxHeight
////		LazyColumn(
////			verticalArrangement = Arrangement.spacedBy(8.dp),
////			contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
////			modifier = Modifier
////				.heightIn(max = maxHeight)
////				.clip(RoundedCornerShape(8.dp))
////				.background
////					(
////					MaterialTheme.colorScheme
////						.surfaceVariant
////				)
////		) {
////			itemsIndexed(sessionList) {index: Int, sessionWithTag ->
//////				val duration =
//////					((sessionWithTag.session.endTime - sessionWithTag.session.startTime) / (1000 * 60)).toInt()
////
////				val duration = formatSecondsToMMSS(sessionWithTag.session.duration.toInt(), false,
////					noPadding = true)
////				FromDateToItem(
////					from = sessionWithTag.session.startTime,
////					end = sessionWithTag.session.endTime,
////					tag = sessionWithTag.tag.name,
////					duration = duration,
////					last = sessionSize -1 == index
////				)
////		}
////
////		}
//	}


}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FromDateToItem(from: Long, end: Long, tag: String, duration: String, last: Boolean) {
	//	val formatterFrom = DateTimeFormatter.ofPattern("HH:mm")
	//	val formatted = from.format(formatterFrom)
	//	val formatterTo = DateTimeFormatter.ofPattern("hh:mm a")
	//	val formatted12Hour = end.format(formatterTo)
	val from = millisToTimeString(from, true)
	val end = millisToTimeString(end)
	//	return "$formatted - $formatted12Hour"
	Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
		Row {
			Column {
				Row(
					verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(
						10
							.dp
					)
				) {
					Dot()
					Text("$from - $end")
				}

				SuggestionChip(
					onClick = { },
					label = { Text(tag) }
				)
			}

			Spacer(modifier = Modifier.weight(1f))

			Row(
				verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(
					10
						.dp
				)
			) {
				Dot()
				Text(duration)
			}
		}


		if(!last){
			HorizontalDivider(
				thickness = 2.dp,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
			)
		}

	}


}

@Composable
fun StudyTimeSummary(duration: Long) {
val (hrs, mins) = secondsToHoursMinutes(duration.toInt())

	Text(
		buildAnnotatedString {

			if(hrs > 0) {
				withStyle(style = ExtraTypography.displayLargeAlt.toSpanStyle().copy(
					fontSize = 24.sp
				)) {
					append("${hrs}")
				}
				withStyle(style = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)) {
					append(" hrs ")
				}
			}

			withStyle(style = ExtraTypography.displayLargeAlt.toSpanStyle().copy(
				fontSize = 24.sp
			)) {
				append("${mins}")
			}

			withStyle(style = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)) {
				append(" mins ")
			}

		}
	)
}

@Composable
fun StreakSummary(days: Int) {
	Text(
		buildAnnotatedString {
			withStyle(style = ExtraTypography.displayLargeAlt.toSpanStyle().copy(
				fontSize = 24.sp
			)) {
				append("${days}")
			}
			withStyle(style = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)) {
				append(" days")
			}
			//			withStyle(style = SpanStyle(fontSize = 24.sp)) {
			//				append("${mins} ")
			//			}
			//
			//			withStyle(style = SpanStyle(fontSize = 16.sp)) {
			//				append("mins ")
			//			}
		}
	)
}

@Composable
fun HoursStudiedSummary(past7days: Int) {

	val (hrs, mins) = secondsToHoursMinutes(past7days)
	Text(
		buildAnnotatedString {
			withStyle(style = ExtraTypography.displayLargeAlt.toSpanStyle().copy(
				fontSize = 24.sp
			)) {
				append("${hrs}")
			}
			withStyle(style = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)) {
				append(" hrs")
			}
			//			withStyle(style = SpanStyle(fontSize = 24.sp)) {
			//				append("${mins} ")
			//			}
			//
			//			withStyle(style = SpanStyle(fontSize = 16.sp)) {
			//				append("mins ")
			//			}
		}
	)
}

@Composable
fun RoundedProgressBar(
	progress: Float, // from 0f to 1f
	modifier: Modifier = Modifier,
	backgroundColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primaryContainer,
	progressColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {

	Log.d("ProgressBar", progress.toString())
	Box(
		modifier
			.fillMaxWidth()
			.height(Dimens.ExtraLargePadding)
			.clip(RoundedCornerShape(50))
			.background(backgroundColor)
	) {
		Box(
			Modifier
				.fillMaxHeight()
				.fillMaxWidth(progress)
				.background(progressColor)
		)
	}
}

@Composable
fun ProgressBarDemo(todayStudyProgression: Float) {
	val safeTodayStudyProgression = todayStudyProgression.takeIf { !it.isNaN() } ?: 0f

	val progress = (safeTodayStudyProgression/100 ).coerceAtMost(1f)

//	val roundedStr = todayStudyProgression.takeIf { !it.isNaN() }?.roundToInt()?.toString() ?: "0"
	val roundedStr = "${safeTodayStudyProgression.roundToInt().coerceAtMost(100).toString()}%"
	Column(
		modifier = Modifier
			.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(Dimens.MediumPadding)
	) {
		RoundedProgressBar(progress = progress)

//		Spacer(Modifier.height(16.dp))
//
//		Text(
//			text = "${(progress * 100).toInt()}%",
//			style = MaterialTheme.typography.bodyMedium
//		)


		Text(
			buildAnnotatedString {
				append("You completed ")
				withStyle(style = ExtraTypography.displayLargeAlt.toSpanStyle().copy(
					fontSize = 18.sp,
					fontWeight = FontWeight.Bold

				)) {
					append(roundedStr)
				}

				append(" of today's goal")


			}
		)
	}
}