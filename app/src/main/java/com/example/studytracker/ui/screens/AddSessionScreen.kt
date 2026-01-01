package com.example.studytracker.ui.screens

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studytracker.data.Session
import com.example.studytracker.data.Tag
import com.example.studytracker.ui.AppViewModelProvider
import com.example.studytracker.ui.components.TagChip
import com.example.studytracker.ui.navigation.NavigationDestination
import com.example.studytracker.utils.getMillisForToday
import com.google.protobuf.copy

object AddSessionDestination : NavigationDestination {
	override val route: String = "add_session"
	override val titleRes: String = "Add Session"

}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddSessionScreen(
	viewModel: AddSessionViewModel = viewModel(
		factory =
		AppViewModelProvider.Factory,


	),
onBackClick : () -> Unit,
	sessionViewModel: SessionViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
val context = LocalContext.current
	// room database made proto dataStore redundant
//	val tagsList: List<String> by viewModel.tagsList.collectAsState()

	val tagsList : List<Tag> by sessionViewModel.allTags.collectAsState()

	//hold start and end times
	var startTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }
	var endTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }

	var selectedTag by remember { mutableStateOf<Tag?>(null) }
Scaffold(	topBar = {
	TopAppBar(
		navigationIcon = {
			IconButton(onClick = {onBackClick()}) {
				Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null) }
			}
			,
		colors = TopAppBarDefaults.topAppBarColors(
			containerColor = MaterialTheme.colorScheme.surface,
			titleContentColor = MaterialTheme.colorScheme.onSurface,
		),
		title = {
			Text("Add session")
		},

	)
}) { innerPadding ->
	Surface(modifier = Modifier.padding(innerPadding)) {
		Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
			var showDialog by remember { mutableStateOf(false) }



			if (showDialog) {
				MinimalDialog(onDismissRequest = { showDialog = false }, onConfirm = {
					sessionViewModel.insertTag(Tag(name = it))
				})
			}
			SelectTimeCard(
				showDialog = showDialog,
				setShowingDialog = { showDialog = it },
				setStartTime = {startTimeMillis = it},
				setEndTime = { endTimeMillis = it},
				tagsList = tagsList,
				onSelectTag = { selectedTag = it},
				currentTag = selectedTag
			)

			Spacer(modifier = Modifier.weight(1f))

			Button (onClick = {


				if (endTimeMillis <= startTimeMillis) {
					Toast.makeText(context, "End time must be greater than start time", Toast.LENGTH_SHORT).show()
					return@Button // exit just the onClick lambda, not the whole composable function
				}

				selectedTag?.let { tag ->
					val session = Session(
						tagId = tag.id,              // safe, non-null
						startTime = startTimeMillis,
						endTime = endTimeMillis,
						duration = (endTimeMillis - startTimeMillis)/1000,
						notes = "My session notes"
					)
					sessionViewModel.insertSession(session)
				}

				//			sessionViewModel.insertSession()
			},interactionSource = remember { MutableInteractionSource() }, modifier = Modifier
				.fillMaxWidth()) {
				Text("ADD SESSION")
			}
		}
	}
}





}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagsList(tagsList: List<Tag>, setShowingDialog: (Boolean) -> Unit, onSelectTag : (Tag) ->
Unit, currentTag: Tag?, textLabel : String = "What did you study ?") {
	Column {
		Text(textLabel)

		FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
			tagsList.forEach {
				TagChip(text = it.name, onClick = {onSelectTag(it)}, isSelected = currentTag?.name == it
					.name, onRemove = {})
			}

			IconButton(onClick = {
				setShowingDialog(true)
			}) {
				Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(24.dp))
			}
		}
	}
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SelectTimeCard(
	showDialog: Boolean,
	setShowingDialog: (Boolean) -> Unit,
	setStartTime : (Long) -> Unit,
	setEndTime : (Long) -> Unit,
	onSelectTag : (Tag) -> Unit,
	currentTag: Tag?,
	tagsList: List<Tag>
) {
	Card(colors = CardDefaults.cardColors(
		containerColor = MaterialTheme.colorScheme.surfaceContainer
	)) {
		Column(
			modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp), verticalArrangement
			= Arrangement.spacedBy(40.dp)
		) {
			Column(
				verticalArrangement
				= Arrangement.spacedBy(16.dp)
			) {
				Text("SELECT TIME")
				Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
					TimeInput(label = "Start Time", onClick = {setStartTime(it)})
					TimeInput(label = "End Time", onClick = {setEndTime(it)})
				}
			}

			TagsList(tagsList, setShowingDialog, onSelectTag = onSelectTag, currentTag = currentTag)
		}
	}
}

@Composable
fun MinimalDialog(onDismissRequest: () -> Unit, onConfirm: (String) -> Unit) {
	var tagValue by remember { mutableStateOf("") }
	Dialog(onDismissRequest = { onDismissRequest() }) {
		Card(
			modifier = Modifier
				.fillMaxWidth(),
			shape = RoundedCornerShape(16.dp),
		) {
			Column(
				modifier = Modifier.padding(
					horizontal = 24
						.dp, vertical = 20.dp
				), verticalArrangement = Arrangement
					.spacedBy(8.dp)
			) {
				Text(
					text = "Create a tag",
					modifier = Modifier
						.wrapContentSize(Alignment.Center),
					style = MaterialTheme.typography.labelMedium,
					textAlign = TextAlign.Center,
				)

				OutlinedTextField(
					value = tagValue,
					onValueChange = { tagValue = it },
					label = { Text("Tag") },
					modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp)
				)

				Row(
					modifier = Modifier
						.fillMaxWidth(),
//						.padding(horizontal = 32.dp),
					//					horizontalArrangement = Arrangement.spacedBy(32
					//					.dp, Alignment
					//					.CenterHorizontally)

						horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
				) {
					TextButton(
						onClick = { onDismissRequest() },
						colors = ButtonDefaults.textButtonColors(
							containerColor = Color.Transparent,
							contentColor = MaterialTheme.colorScheme.primary
						),
						contentPadding = PaddingValues(0.dp)
					) {
						Text("Cancel")
					}


					TextButton(
						onClick = {
							onConfirm(tagValue)

							onDismissRequest()
						},
						colors = ButtonDefaults.textButtonColors().copy(
							containerColor = Color.Transparent,
							contentColor = MaterialTheme.colorScheme.primary
						),
						contentPadding = PaddingValues(0.dp)
					) {
						Text("Done")
					}
					//
					//					Button(onClick = {}) {
					//						Text("Done")
					//					}
				}
			}
		}
	}
}


@Composable
fun TimePickerField(
	label: String,
	value: String,
	onClick: () -> Unit,
	icon: ImageVector = Icons.Outlined.DateRange
) {
	val focusManager = LocalFocusManager.current
	TextField(
		value = value,
		onValueChange = {}, // Disable direct editing
		label = { Text(label) },
		readOnly = true,
		trailingIcon = {
			Icon(icon, contentDescription = null)
		},
		modifier = Modifier
			.fillMaxWidth()
			.clickable {
				focusManager.clearFocus()
				onClick()
			},
		colors = TextFieldDefaults.colors().copy
			(
            disabledContainerColor = Color.Transparent,
            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,

            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
		enabled = false // Prevent keyboard
	)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimeInput(label: String, onClick: (Long) -> Unit) {
	val context = LocalContext.current
	var selectedTime by remember { mutableStateOf("") }

	TimePickerField(
		label = label,
		value = if (selectedTime.isEmpty()) "Select time" else selectedTime,
		onClick = {
			val timePicker = TimePickerDialog(
				context,
				{ _, hourOfDay, minute ->
					val amPm = if (hourOfDay < 12) "AM" else "PM"
					val hour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
					selectedTime = String.format("%02d:%02d %s", hour, minute, amPm)

					val milliSeconds = getMillisForToday(hourOfDay, minute)
					onClick(milliSeconds)

				},
				8, 0, false // default 8:00 AM
			)
			timePicker.show()
		}
	)
}
