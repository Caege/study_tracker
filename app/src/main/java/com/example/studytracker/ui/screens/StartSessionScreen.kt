package com.example.studytracker.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.studytracker.R
import com.example.studytracker.data.Tag
import com.example.studytracker.data.TimerDurationInputList
//import com.example.studytracker.notification.createNotificationChannel
//import com.example.studytracker.notification.startOngoingTimer
import com.example.studytracker.ui.AppViewModelProvider
import com.example.studytracker.ui.TimerViewModel
import com.example.studytracker.ui.components.TagChip
import com.example.studytracker.ui.components.TimeText
import com.example.studytracker.ui.navigation.NavigationDestination
import com.example.studytracker.ui.theme.Dimens
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object StartSessionDestination : NavigationDestination {
	override val route: String = "start_session"
	override val titleRes: String = "Start Session"

}

enum class Destination(val route: String, val label: String) {
	POMODORO("pomodoro", "Pomodoro"),
	BREAK("break", "Break")
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StartSessionScreen(
	viewModel: TimerViewModel = viewModel(
		LocalActivity.current as
						ComponentActivity, factory = AppViewModelProvider.Factory
	),
	sessionViewModel:
	SessionViewModel = viewModel(factory = AppViewModelProvider.Factory),
	//	navController: NavController
	onBackClick : () -> Unit
) {
//selected time card
	val selectedTime by viewModel.totalDuration.collectAsState()
	val selectedBreakTime by viewModel.breakDuration.collectAsState()

	//timer progress
	val timerProgress = viewModel.timerState.collectAsState().value.progress
	val breakTimerProgress = viewModel.breakTimerState.collectAsState().value.progress

	//is timer started ?
	val isTimerStarted = viewModel.timerState.collectAsState().value.isStarted
	val isBreakTimerStarted = viewModel.breakTimerState.collectAsState().value.isStarted

	//is timer paused ?
	val isTimerPaused = viewModel.timerState.collectAsState().value.isPaused
	val isBreakTimerPaused = viewModel.breakTimerState.collectAsState().value.isPaused

	//current selected tag ?
	val selectedTag by viewModel.selectedTag.collectAsState()

	Log.d("test_shit", selectedTag.toString())
	val tagsList: List<Tag> by sessionViewModel.allTags.collectAsState()
	var showDialog by remember { mutableStateOf(false) }
	val context = LocalContext.current
	val onPauseClick = {
		(viewModel::pauseTimer)(context)

	}
	val onResumeClick = {

		viewModel.resumeTimer(context)
	}
	val onBreakPauseClick = {
		(viewModel::pauseBreakTimer)(context)

	}
	val onBreakResumeClick = {

		viewModel.resumeBreakTimer(context)
	}


	Log.d(
		"stateme", "selected time : ${selectedTime}, paused : ${isTimerPaused}, started : " +
						"${isTimerStarted}, timerProgress : ${timerProgress}"
	)


	if (showDialog) {
		MinimalDialog(onDismissRequest = { showDialog = false }, onConfirm = {
			sessionViewModel.insertTag(Tag(name = it))
		})
	}

	Scaffold(
		topBar = {
			TopAppBar(
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.surface,
					titleContentColor = MaterialTheme.colorScheme.onSurface,
				),
				title = {
					Text("Study Session")
				},
				navigationIcon = {
					IconButton(onClick = {onBackClick()}) {
						Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
					}
				}
			)
		},
	) { innerPadding ->
		val startDestination = Destination.POMODORO
		val pagerState = rememberPagerState(pageCount = { Destination.entries.size })
		val coroutineScope = rememberCoroutineScope()

		Surface(modifier = Modifier.padding(innerPadding)) {
			Column(
				verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier
					.animateContentSize()
			) {
				PrimaryTabRow(
					selectedTabIndex = pagerState.currentPage,
				) {
					Destination.entries.forEachIndexed { index, destination ->
						Tab(
							selected = pagerState.currentPage == index,
							onClick = {
								//						navController.navigate(route = destination.route)
								coroutineScope.launch {
									pagerState.animateScrollToPage(index) // 👈 move pager
								}
							},
							text = {
								Text(
									text = destination.label,
									maxLines = 2,
									overflow = TextOverflow.Ellipsis
								)
							}
						)
					}
				}

				Column(
					modifier = Modifier.verticalScroll(rememberScrollState())
				) {
					Box(
						modifier = Modifier
							.fillMaxWidth(),
						contentAlignment = Alignment.TopCenter
					) {
						var containerHeight by remember { mutableIntStateOf(0) }
						HorizontalPager(
							state = pagerState,
							key = { page ->
								Destination.entries[page].name
							},
							modifier = Modifier
								.onGloballyPositioned { layoutCoordinates ->
									containerHeight = layoutCoordinates.size.height
								}
								.animateContentSize(),
							verticalAlignment = Alignment.Top,
							) { page ->
							when (page) {
								0 -> PomodoroTimer(
									modifier = Modifier,
									viewModel = viewModel,
									isTimerStarted = isTimerStarted,
									context = context,
									selectedTime = selectedTime,
									tagsList = tagsList,
									selectedTag = selectedTag,
									showDialog = { showDialog = it },
									timerProgress = timerProgress,
									onPauseClick = onPauseClick,
									onResumeClick = onResumeClick,
									isTimerPaused = isTimerPaused,
									containerHeight = containerHeight
								)

								1 -> BreakTimer(
									modifier = Modifier,
									viewModel = viewModel,
									isTimerStarted = isBreakTimerStarted,
									context = context,
									selectedTime = selectedBreakTime,
									tagsList = tagsList,
									selectedTag = selectedTag,
									showDialog = { showDialog = it },
									timerProgress = breakTimerProgress,
									onPauseClick = onBreakPauseClick,
									onResumeClick = onBreakResumeClick,
									isTimerPaused = isBreakTimerPaused,
									containerHeight = containerHeight
								)
							}
						}
					}
				}
				//				when {
				//					selectedDestination == 0 -> PomodoroTimer(
				//						modifier = Modifier,
				//						viewModel = viewModel,
				//						isTimerStarted = isTimerStarted,
				//						context = context,
				//						selectedTime = selectedTime,
				//						tagsList = tagsList,
				//						selectedTag = selectedTag,
				//						showDialog = { showDialog = it },
				//						timerProgress = timerProgress,
				//						onPauseClick = onPauseClick,
				//						onResumeClick = onResumeClick,
				//						isTimerPaused = isTimerPaused
				//					)
				//
				//					selectedDestination == 1 -> BreakTimer(
				//						modifier = Modifier,
				//						viewModel = viewModel,
				//						isTimerStarted = isBreakTimerStarted,
				//						context = context,
				//						selectedTime = selectedBreakTime,
				//						tagsList = tagsList,
				//						selectedTag = selectedTag,
				//						showDialog = { showDialog = it },
				//						timerProgress = breakTimerProgress,
				//						onPauseClick = onBreakPauseClick,
				//						onResumeClick = onBreakResumeClick,
				//						isTimerPaused = isBreakTimerPaused
				//					)
				//				}
			}
		}
	}


}

@Composable
fun BreakTimer(
	modifier: Modifier, viewModel: TimerViewModel, isTimerStarted: Boolean,
	context: Context, selectedTime: Int, tagsList: List<Tag>, selectedTag: Tag?,
	showDialog: (Boolean) -> Unit, timerProgress: Int, onPauseClick: () -> Unit,
	onResumeClick: () -> Unit, isTimerPaused: Boolean,
	containerHeight: Int
) {
	val heightInDp = with(LocalDensity.current) { containerHeight.toDp() }
	//	Box(modifier = Modifier.height(  heightInDp
	//	)){
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(horizontal = Dimens.LargePadding), verticalArrangement = Arrangement.spacedBy(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Card(
			modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
				containerColor =
				MaterialTheme.colorScheme.surfaceContainerLow
			)
		) {
			SelectTime(onTimeSelected = {
				//						selectedTime = it
				viewModel.changeBreakDuration(it)
				if (isTimerStarted) {
					viewModel.stopBreakTimer(context)
					//						isTimerStarted = false
				}
				//					isTimerStarted = true
				//
				//					viewModel.startTimer(selectedTime * 60)
				//
				//					createNotificationChannel(context)
				//					// Start a 10-second timer
				//					startOngoingTimer(context, selectedTime * 60, timerProgress)
			}, selectedTime = selectedTime, tagsList = tagsList, setShowingDialog = {
				showDialog(it)
			}, onSelectTag =
			{  }, currentTag = selectedTag,
				showTags = false,
				label = "How many minutes would you like for your break?"
			)
		}


		Log.d("timer", "totatlMinutes=$selectedTime , progress=$timerProgress")
		PreviewTimer(
			totalMinutes = selectedTime, timerProgress = timerProgress, onPauseClick = onPauseClick,
			onResumeClick = onResumeClick, isTimerStarted = isTimerStarted, startTimer = {
				//					isTimerStarted = true
//				Toast.makeText(context, "Break timer started", Toast.LENGTH_SHORT).show()


				viewModel.startBreakTimer(selectedTime * 60)
				//					createNotificationChannel(context)
				// Start a 10-second timer
				//					startOngoingTimer(context, selectedTime * 60, timerProgress)
			}, isPaused
			= isTimerPaused
		)

		TimeText(timerProgress)
	}
	//	}//box


}

@Composable
fun PomodoroTimer(
	modifier: Modifier, viewModel: TimerViewModel, isTimerStarted: Boolean,
	context: Context, selectedTime: Int, tagsList: List<Tag>, selectedTag: Tag?,
	showDialog: (Boolean) -> Unit, timerProgress: Int, onPauseClick: () -> Unit,
	onResumeClick: () -> Unit, isTimerPaused: Boolean,
	containerHeight: Int
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(horizontal = Dimens.LargePadding), verticalArrangement = Arrangement.spacedBy(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Card(
			modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
				containerColor =
				MaterialTheme.colorScheme.surfaceContainerLow
			)
		) {
			SelectTime(onTimeSelected = {
				//						selectedTime = it
				viewModel.changeTotalDuration(it)
				if (isTimerStarted) {
					viewModel.stopTimer(context)
					//						isTimerStarted = false
				}
				//					isTimerStarted = true
				//
				//					viewModel.startTimer(selectedTime * 60)
				//
				//					createNotificationChannel(context)
				//					// Start a 10-second timer
				//					startOngoingTimer(context, selectedTime * 60, timerProgress)
			}, selectedTime = selectedTime, tagsList = tagsList, setShowingDialog = {
				showDialog(it)
			}, onSelectTag =
			{
				Log.d("test_shit", "update tag ran")
				viewModel.updateTag(it.id) }, currentTag = selectedTag
			)
		}

		Log.d("timer", "totatlMinutes=$selectedTime , progress=$timerProgress")
		PreviewTimer(
			totalMinutes = selectedTime, timerProgress = timerProgress, onPauseClick = onPauseClick,
			onResumeClick = onResumeClick, isTimerStarted = isTimerStarted, startTimer = {
				//					isTimerStarted = true
				if (selectedTag == null) {
					Toast.makeText(context, "Please select a tag", Toast.LENGTH_SHORT).show()
				}
				selectedTag?.let { tag ->
					viewModel.startTimer(selectedTime * 60, tag)
				}
				//					createNotificationChannel(context)
				// Start a 10-second timer
				//					startOngoingTimer(context, selectedTime * 60, timerProgress)
			}, isPaused
			= isTimerPaused
		)

		TimeText(timerProgress)
	}

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectTime(
	label: String = "How many minutes would you like to study?",
	onTimeSelected: (Int) -> Unit, selectedTime: Int, tagsList: List<Tag>,
	setShowingDialog: (Boolean) -> Unit, onSelectTag: (Tag) -> Unit, currentTag: Tag?,
	showTags: Boolean = true
) {
	Column(modifier = Modifier.padding(8.dp)) {
		Text(label)

		FlowRow(horizontalArrangement = Arrangement.spacedBy(Dimens.LargePadding)) {
			TimerDurationInputList.values.forEachIndexed { index, item ->
				TagChip(
					text =
					if (item.durationMinutes > 1) {
						"${item.durationMinutes} mins"
					} else {
						"${item.durationMinutes} min"
					},
					onClick = {
						onTimeSelected(item.durationMinutes)
					},
					isSelected = item.durationMinutes == selectedTime,
					onRemove = {},
					clearIcon = false
				)
			}
		}
		if (showTags) {
			TagsList(
				tagsList, setShowingDialog, onSelectTag = onSelectTag, currentTag = currentTag,
				textLabel = "What would you like to study ?"
			)
		}
	}
}

@Composable
fun TimerPie(
	totalSeconds: Int,
	remainingSeconds: Int,
	isStarted: Boolean,
	modifier: Modifier = Modifier
		.fillMaxSize()
		.background(Color.Red),
	onPauseClick: () -> Unit,
	onResumeClick: () -> Unit,
	startTimer: () -> Unit,
	isPaused: Boolean
) {
	var sweepAngle = 0.toFloat()

	if (totalSeconds != 0) {
		sweepAngle = (remainingSeconds.toFloat() / totalSeconds.toFloat()) * 360f
	}


	Log.d("sweep", sweepAngle.toString())


	BoxWithConstraints {
		val availableWidth = maxWidth
		val availableHeight = maxHeight
		val circleSize = min(availableWidth, availableHeight)
		val circleColor = MaterialTheme.colorScheme.surfaceContainerHighest
		val arcColor = MaterialTheme.colorScheme.primaryFixedDim

		Box(modifier = Modifier.size(circleSize), contentAlignment = Alignment.Center) {
			Canvas(modifier = Modifier.fillMaxSize()) {
				val size = this.size
				// Background full circle
				drawCircle(
					color = circleColor,
					radius = size.minDimension / 2,
					center = Offset(size.width / 2, size.width / 2)
				)
				// inset padding so arc is smaller
				val inset = 50 // adjust as needed
				val arcSize = Size(size.width - inset * 2, size.width - inset * 2)
				// Foreground shrinking arc
				drawArc(
					color = arcColor,
					startAngle = -90f,
					sweepAngle = if (remainingSeconds.toFloat() == 0.toFloat()) {
						360f
					} else {
						sweepAngle
					},
					useCenter = true,
					topLeft = Offset(inset.toFloat(), inset.toFloat()),
					size = arcSize
				)
			}

			if (isStarted) {
				Icon(
					painter = painterResource(R.drawable.pause_24), contentDescription = null,
					modifier
					= Modifier
						.size(96.dp)
						.clickable {
							onPauseClick()
						},
					tint = MaterialTheme.colorScheme.onPrimary,
				)
			} else {
				Icon(
					painter = painterResource(R.drawable.play_arrow_24dp), contentDescription = null,
					modifier = Modifier
						.size(96.dp)
						.clickable {
							//							if(isPaused) {
							//								onResumeClick()
							//							} else {
							//								startTimer()
							//							}
							//							if(isStarted) {
							//								onResumeClick()
							//							} else {
							//								startTimer()
							//							}
							if (isPaused) {
								onResumeClick()
							} else {
								startTimer()
							}
						}, tint = MaterialTheme.colorScheme.onPrimary
				)
			}
		}
	}


}

@Composable
fun TimerScreen(
	totalMinutes: Int,
	timerProgress: Int,
	modifier: Modifier = Modifier.fillMaxWidth(),
	onPauseClick: () -> Unit, onResumeClick: () -> Unit,
	isTimerStarted: Boolean,
	startTimer: () -> Unit,
	isPaused: Boolean
) {
	//	var remainingSeconds by remember(totalMinutes) { mutableStateOf(totalMinutes * 60) }
	//	LaunchedEffect(totalMinutes) {
	//		remainingSeconds = totalMinutes * 60
	//		while (remainingSeconds > 0) {
	//			delay(1000L)
	//			remainingSeconds--
	//		}
	//	}
	TimerPie(
		totalSeconds = totalMinutes * 60,
		remainingSeconds = timerProgress,
		isStarted = isTimerStarted,
		modifier = modifier,
		onPauseClick = onPauseClick,
		onResumeClick = onResumeClick,
		startTimer = startTimer,
		isPaused = isPaused
	)
}

@Composable
fun PreviewTimer(
	totalMinutes: Int,
	timerProgress: Int,
	onPauseClick: () -> Unit,
	onResumeClick: () -> Unit,
	isTimerStarted: Boolean,
	startTimer: () -> Unit,
	isPaused: Boolean
) {
	TimerScreen(
		totalMinutes = totalMinutes, timerProgress = timerProgress, onPauseClick = onPauseClick,
		onResumeClick = onResumeClick, isTimerStarted = isTimerStarted, startTimer = startTimer,
		isPaused = isPaused
	) // 10-minute timer
}