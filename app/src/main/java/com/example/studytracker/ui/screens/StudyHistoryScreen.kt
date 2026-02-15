package com.example.studytracker.ui.screens

import android.R.attr.data
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studytracker.R
import com.example.studytracker.data.DailyDurationDate
import com.example.studytracker.ui.AppViewModelProvider
import com.example.studytracker.ui.components.CardWrapperMedium
import com.example.studytracker.ui.components.SessionCard
import com.example.studytracker.ui.navigation.NavigationDestination
import com.example.studytracker.ui.theme.Dimens
import com.example.studytracker.utils.createDateFromDay
import com.example.studytracker.utils.currentMilli
import com.example.studytracker.utils.formatLocalDate
import com.example.studytracker.utils.formatMillisToMonthYear
import com.example.studytracker.utils.formatSecondsToReadableTime
import com.example.studytracker.utils.millisToYearMonth
import com.google.protobuf.duration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

object StudyHistoryScreen : NavigationDestination {
	override val route: String = "study_history"
	override val titleRes: String = "Study History"

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyHistoryTopAppBar(
	onBackClick: () -> Unit,
	onDropdownClick: () -> Unit,
	onCalendarClick: () -> Unit,
	onFilterClick: () -> Unit,
	monthLabel: String = "Jan 2025"
) {
	val context = LocalContext.current


	TopAppBar(
		title = {
			Text("Summary")
		},
		navigationIcon = {
			IconButton(onClick = onBackClick) {
				Icon(
					imageVector = Icons.AutoMirrored.Filled.ArrowBack,
					contentDescription = "Back"
				)
			}
		},
		actions = {
			Row(
				verticalAlignment = CenterVertically,
				modifier = Modifier.clickable {
					onCalendarClick()
					//					Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
				}
			) {
				//				IconButton(onClick = onDropdownClick, modifier = Modifier.size(24.dp)) {
				//					Icon(
				//						imageVector = Icons.Default.ArrowDropDown,
				//						contentDescription = "Dropdown"
				//					)
				//				}
				IconButton(onClick = onCalendarClick, modifier = Modifier.size(24.dp)) {
					Icon(
						imageVector = Icons.Default.DateRange,
						contentDescription = "Calendar"
					)
				}
				Text(
					text = monthLabel,
					style = MaterialTheme.typography.titleMedium,
					modifier = Modifier.padding(start = 4.dp)
				)
			}
			IconButton(onClick = onFilterClick) {
				Icon(
					Icons.Default.FilterList,
					contentDescription = "Filter"
				)
			}
		},
	)
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyHistoryScaffold(
	viewModel: SessionViewModel = viewModel(
		factory = AppViewModelProvider
			.Factory
	), onBackClick: () -> Unit
) {
	var showDatePicker by remember { mutableStateOf(false) }
	var selectedMilli by remember { mutableLongStateOf(currentMilli()) }
	val localDate = Instant.ofEpochMilli(selectedMilli).atZone(ZoneId.systemDefault()).toLocalDate()



	LaunchedEffect(selectedMilli) {
		//		Log.d("month_shit", "loading data with date : ${formatLocalDate(localDate)}")
		//		val ym = millisToYearMonth(selectedMilli)
		viewModel.loadMonthBreakdown(selectedMilli)
	}
	val monthBreakDown by viewModel.monthBreakdown.collectAsState()
	Log.d("month_shit", "monthBreakDown : ${monthBreakDown}")
	//	val monthBreakDownDate = monthBreakDown.filter { it.totalDuration > 0 }.map {
	//		Log.d("month_shit", "loop day : ${it.day}")
	//		DailyDurationDate(
	//			day = createDateFromDay(
	//				it.day,
	//				referenceMillis = selectedMilli
	//			), totalDuration = it.totalDuration
	//		)
	//	}
	val monthBreakDownDate = monthBreakDown
	val tempMonthBreakDownDate by viewModel.tempMonthBreakDown.collectAsState()

	Log.d("testing", "monthBreakDownDate : ${monthBreakDownDate}")
	val TOTAL_STUDY_TIME = monthBreakDownDate.sumOf { it.totalDuration }




	Scaffold(
		contentWindowInsets = WindowInsets(0, 0, 0, 0),
		topBar = {
			StudyHistoryTopAppBar(
				onBackClick = { onBackClick() }, onFilterClick = {}, onCalendarClick = {
					showDatePicker = !showDatePicker
				},
				onDropdownClick = {}, monthLabel = formatMillisToMonthYear(selectedMilli)
			)
		},
	) { innerPadding ->
		// Main content
		//		LazyColumn(
		//			contentPadding = innerPadding,
		//			modifier = Modifier.fillMaxSize()
		//		) {
		//			items(20) { index ->
		//				Text(
		//					text = "Item $index",
		//					modifier = Modifier
		//						.fillMaxWidth()
		//						.padding(16.dp)
		//				)
		//			}
		//		}
		AnimatedVisibility(
			visible = showDatePicker,
			enter = fadeIn(animationSpec = tween(200)) + scaleIn(
				initialScale = 0.8f, // start slightly smaller
				animationSpec = tween(200, easing = FastOutSlowInEasing)
			),
			exit = fadeOut(animationSpec = tween(200)) + scaleOut(
				targetScale = 0.8f,
				animationSpec = tween(200, easing = FastOutSlowInEasing)
			)
		) {
			//				Box(modifier = Modifier.animateContentSize()) {
			//					AlertDialog(
			//						onDismissRequest = { showDatePicker = false },
			//						confirmButton = {
			//							TextButton(onClick = { showDatePicker = false }) {
			//								Text("OK")
			//							}
			//						},
			//						text = {
			//
			//								MaterialTheme {
			//									MyDatePicker(
			//										setMilli = { selectedMilli = it },
			//										closeDatePicker = { showDatePicker = false })
			//								}
			//
			//
			//						}
			//					)
			//				}
			MyDatePicker(setMilli = { selectedMilli = it }, closeDatePicker = { showDatePicker = false })
		}



		_root_ide_package_.com.example.studytracker.ui.screens.StudyHistoryScreen(
			modifier = Modifier.padding(
				innerPadding
			),
			totalStudy = TOTAL_STUDY_TIME,
			dailyBreakDownList = monthBreakDownDate,
			tempMonthBreakDownDate = tempMonthBreakDownDate
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudyHistoryScreen(
	modifier: Modifier,
	totalStudy: Long,
	dailyBreakDownList: List<DailyDurationDate>,
	sessionViewModel: SessionViewModel =
		viewModel(factory = AppViewModelProvider.Factory),
	tempMonthBreakDownDate: UiState<List<DailyDurationDate>>
) {
	val totalStudyToHours = totalStudy / 60 / 60
	//set up bottom modal
	var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
	val sheetState = rememberModalBottomSheetState()

	Box() {
		Column(
			modifier = modifier
				.padding(horizontal = 16.dp)
				.fillMaxSize(),
			verticalArrangement = Arrangement.spacedBy(24.dp)
		) {
			//top card
			Column(
				//				modifier = modifier
				//					.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)
			) {
				Card(
					modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
						containerColor =
							MaterialTheme.colorScheme.surfaceContainerLow
					)
				) {
					Column(
						modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(
							16
								.dp
						)
					) {
						//						Text("SUMMARY", style = MaterialTheme.typography.titleMedium)
						Column() {
							Text("Total hours studied this month")

							Text(
								buildAnnotatedString {
									withStyle(style = SpanStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)) {
										append("${totalStudyToHours} ")
									}
									withStyle(style = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)) {
										append("hours")
									}
									//									withStyle(style = SpanStyle(fontSize = 24.sp)) {
									//										append("${mins} ")
									//									}
									//
									//									withStyle(style = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)) {
									//										append("mins ")
									//									}
								})
							//							Text("${totalStudyToHours} hours")
						}
					}
				}
			}
			//-----------------
			when (tempMonthBreakDownDate) {
				is UiState.Loading -> {
					Box(
						modifier = Modifier.fillMaxWidth()
							.weight(1f).offset(y = -100.dp)
							, contentAlignment =
							Alignment
								.Center
					) {
						LoadingIndicator()
					}
				}

				is UiState.Error -> {
					Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
						Text("I'm not sure how you ended up here!")
					}
				}

				is UiState.Success -> {
					val data = tempMonthBreakDownDate.data
					LazyColumn {
						if (data.isEmpty()) {
							item {
								CardWrapperMedium {
									EmptyStudyListUI()
								}
							}


						} else {
							item {
								CardWrapperMedium {
									DailyBreakDownCard(dailyBreakDownList = data, onDateClick = {
										selectedDate = it
									})
								}
							}

						}
					}


				}
			}
		}


//		LazyColumn(
//			modifier = modifier
//				.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)
//		) {
//			item {
//				Column(
//					//				modifier = modifier
//					//					.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)
//				) {
//					Card(
//						modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
//							containerColor =
//								MaterialTheme.colorScheme.surfaceContainerLow
//						)
//					) {
//						Column(
//							modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(
//								16
//									.dp
//							)
//						) {
//							//						Text("SUMMARY", style = MaterialTheme.typography.titleMedium)
//							Column() {
//								Text("Total hours studied this month")
//
//								Text(
//									buildAnnotatedString {
//										withStyle(style = SpanStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)) {
//											append("${totalStudyToHours} ")
//										}
//										withStyle(style = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)) {
//											append("hours")
//										}
//										//									withStyle(style = SpanStyle(fontSize = 24.sp)) {
//										//										append("${mins} ")
//										//									}
//										//
//										//									withStyle(style = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)) {
//										//										append("mins ")
//										//									}
//									})
//								//							Text("${totalStudyToHours} hours")
//							}
//						}
//					}
//				}
//			}
//			item {
//				when (tempMonthBreakDownDate) {
//					is UiState.Loading -> {
//						Box(
//							modifier = Modifier
//								.fillParentMaxSize()
//								.background(color = Color.Red),
//							contentAlignment = Alignment
//								.Center
//						) {
//							CircularProgressIndicator()
//						}
//					}
//
//					is UiState.Error -> {
//						Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//							Text("I'm not sure how you ended up here!")
//						}
//					}
//
//					is UiState.Success -> {
//						val data = tempMonthBreakDownDate.data
//
//						if (data.isEmpty()) {
//							CardWrapperMedium {
//								EmptyStudyListUI()
//							}
//
//						} else {
//							CardWrapperMedium {
//								DailyBreakDownCard(dailyBreakDownList = data, onDateClick = {
//									selectedDate = it
//								})
//							}
//						}
//
//					}
//				}
//			}
//
//		}
		//bottom sheet
		if (selectedDate != null) {
			ModalBottomSheet(
				onDismissRequest = { selectedDate = null },
				sheetState = sheetState,
				scrimColor = Color.Black.copy(alpha = 0.6f)
			) {
				selectedDate?.let {
					val sessionList by sessionViewModel.getDaySessions(it).collectAsState(emptyList())
					SessionCard(sessionList, formatLocalDate(it))
				}
			}
		}
	}


}

// Responsible for displaying each daily session
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyBreakDownCard(
	dailyBreakDownList: List<DailyDurationDate>, onDateClick: (LocalDate) ->
	Unit
) {
	Text(
		"DAILY BREAKDOWN", style = MaterialTheme.typography.titleMedium, color = MaterialTheme
			.colorScheme.primary
	)
	Spacer(modifier = Modifier.height(8.dp))

	Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
		val listSize = dailyBreakDownList.size
		dailyBreakDownList.forEachIndexed { index, item ->
			DailyBreakDownItem(dailyBreakDown = item, onDateClick, last = listSize - 1 == index)
		}
	}
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyBreakDownItem(
	dailyBreakDown: DailyDurationDate, onDateClick: (LocalDate) -> Unit, last:
	Boolean
) {
	var showBottomSheet by remember { mutableStateOf(false) }

	Column(
		verticalArrangement = Arrangement.spacedBy(8.dp),
		modifier =
			Modifier.clickable {
				onDateClick(dailyBreakDown.day)
			}
	) {
		Row(
			modifier = Modifier.fillMaxWidth(), Arrangement.SpaceBetween, verticalAlignment =
				Alignment.CenterVertically
		) {
			Row(
				verticalAlignment = CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(16.dp)
			) {
				Log.d("testing", dailyBreakDown.toString())
				Box(
					modifier = Modifier
						.size(18.dp)
						.clip(CircleShape)
						.background(MaterialTheme.colorScheme.primary)
				)
				Column(verticalArrangement = Arrangement.spacedBy(Dimens.MediumPadding)) {
					Row(horizontalArrangement = Arrangement.spacedBy(Dimens.MediumPadding)) {
						Icon(
							painter = painterResource(R.drawable.event_24), contentDescription = null, modifier =
								Modifier
									.size(24.dp)
						)

						Text(formatLocalDate(dailyBreakDown.day))
					}

					Text(
						formatSecondsToReadableTime(dailyBreakDown.totalDuration), modifier = Modifier
							.offset(x = 8.dp)
					)
				}
			}

			IconButton(onClick = { onDateClick(dailyBreakDown.day) }) {
				Icon(
					Icons.Default.KeyboardArrowDown, contentDescription = null, modifier =
						Modifier
							.size(24.dp)
				)
			}
		}


		if (!last) {
			HorizontalDivider(thickness = 2.dp)
		}
	}


}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DatePicker(
//	state: DatePickerState,
//	modifier: Modifier = Modifier,
//	dateFormatter: DatePickerFormatter = remember { DatePickerDefaults.dateFormatter() },
//	colors: DatePickerColors = DatePickerDefaults.colors(),
//	title: (@Composable () -> Unit)? = {
//		DatePickerDefaults.DatePickerTitle(
//			displayMode = state.displayMode,
//			modifier = Modifier.padding(DatePickerTitlePadding),
//			contentColor = colors.titleContentColor,
//		)
//	},
//	headline: (@Composable () -> Unit)? = {
//		DatePickerDefaults.DatePickerHeadline(
//			selectedDateMillis = state.selectedDateMillis,
//			displayMode = state.displayMode,
//			dateFormatter = dateFormatter,
//			modifier = Modifier.padding(DatePickerHeadlinePadding),
//			contentColor = colors.headlineContentColor,
//		)
//	},
//	showModeToggle: Boolean = true,
//	focusRequester: FocusRequester? = remember { FocusRequester() }
//) {
//	// Main container for the DatePicker
//	Column(
//		modifier = modifier
//			.fillMaxWidth()
//			.padding(16.dp)
//			.background(
//				color = colors.containerColor,
//				shape = MaterialTheme.shapes.medium
//			)
//			.padding(16.dp)
//	) {
//		// Title section
//		title?.invoke()
//
//		// Headline section (shows selected date)
//		headline?.invoke()
//
//		Spacer(modifier = Modifier.height(16.dp))
//
//		// Date picker content based on display mode
//		when (state.displayMode) {
//			DisplayMode.Picker -> {
//				// Date picker content
//				// You can implement the calendar view here
//				// This is a simplified implementation
//				Text("Date Picker Content")
//			}
//			DisplayMode.Input -> {
//				// Input fields for date
//				// This is a simplified implementation
//				Text("Date Input Fields")
//			}
//		}
//
//		// Mode toggle button
//		if (showModeToggle) {
//			TextButton(
//				onClick = {
//					state.displayMode = when (state.displayMode) {
//						DisplayMode.Picker -> DisplayMode.Input
//						DisplayMode.Input -> DisplayMode.Picker
//					}
//				},
//				modifier = Modifier.padding(top = 8.dp)
//			) {
//				Text(
//					text = when (state.displayMode) {
//						DisplayMode.Picker -> "Switch to Input"
//						DisplayMode.Input -> "Switch to Calendar"
//					}
//				)
//			}
//		}
//	}
//}
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePicker(setMilli: (Long) -> Unit, closeDatePicker: () -> Unit) {
	val datePickerState = rememberDatePickerState(initialSelectedDateMillis = currentMilli())

	DatePickerDialog(
		onDismissRequest = { closeDatePicker },
		confirmButton = {
			TextButton(onClick = {
				datePickerState.selectedDateMillis?.let { millis ->
					setMilli(millis)
					closeDatePicker()
				}
			}, modifier = Modifier.offset(x = -Dimens.MediumPadding)) { Text("OK") }
		}
	) {
		DatePicker(
			modifier = Modifier.padding(Dimens.SmallPadding),
			state = datePickerState
		)
	}


}

@Composable
fun EmptyStudyListUI() {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		// Optional quirky image
		//		Image(
		//			painter = painterResource(id = R.drawable.ic_empty_box), // replace with your drawable
		//			contentDescription = "No study sessions",
		//			modifier = Modifier.size(120.dp)
		//		)
		Box(
			modifier = Modifier
				.size(120.dp)
				.background(Color.LightGray)
		)

		Spacer(modifier = Modifier.height(16.dp))
		// Quirky title
		Text(
			text = "Oops! No study this month 😅",
			fontSize = 20.sp,
			fontWeight = FontWeight.Bold
		)

		Spacer(modifier = Modifier.height(8.dp))
		// Fun subtitle
		Text(
			text = "Looks like you took a break. Time to hit the books!",
			fontSize = 16.sp,
			color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
			textAlign = androidx.compose.ui.text.style.TextAlign.Center
		)

		Spacer(modifier = Modifier.height(24.dp))
		// Optional action button
		Button(onClick = { /* Navigate to add study session */ }) {
			Text("Add your first session")
		}
	}
}