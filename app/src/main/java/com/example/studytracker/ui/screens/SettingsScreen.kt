package com.example.studytracker.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase.deleteDatabase
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studytracker.data.TimerPreferencesRepository
import com.example.studytracker.ui.AppViewModelProvider
import com.example.studytracker.ui.TimerViewModel
import com.example.studytracker.ui.navigation.NavigationDestination
import com.example.studytracker.ui.theme.Dimens
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object SettingsDestination : NavigationDestination {
	override val route: String = "settings_screen"
	override val titleRes: String = "Settings"

}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
	launcher: ActivityResultLauncher<String>,
	importLauncher: ActivityResultLauncher<Array<String>>,
	viewModel: SettingsViewModel =
		viewModel(factory = AppViewModelProvider.Factory),
	timerViewModel: TimerViewModel = viewModel(factory = AppViewModelProvider.Factory),
	onBackClick : () -> Unit
) {
	val labelTextStyle = MaterialTheme.typography.labelMedium
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
	val context = LocalContext.current
	Scaffold(
		topBar = {
			MediumTopAppBar(
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
					titleContentColor = MaterialTheme.colorScheme.primary,
				),
				title = {
					Text(
						"Settings",
						maxLines = 1,
						overflow = TextOverflow.Ellipsis
					)
				},
				navigationIcon = {
					IconButton(onClick = {onBackClick() }) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = "Localized description"
						)
					}
				},
				//				actions = {
				//					IconButton(onClick = { /* do something */ }) {
				//						Icon(
				//							imageVector = Icons.Filled.Menu,
				//							contentDescription = "Localized description"
				//						)
				//					}
				//				},
				scrollBehavior = scrollBehavior
			)
		},
	) { innerPadding ->
		Column(
			modifier = Modifier
				.padding(innerPadding)
				.padding(horizontal = Dimens.LargePadding)
				.padding(top = Dimens.ExtraLargePadding),
			verticalArrangement =
			Arrangement.spacedBy
				(Dimens.ExtraLargePadding)
		) {
			SettingsBlock("NOTIFICATIONS") {

					NotificationItem()


			}

			SettingsBlock("PREFERENCES") {
				StudyGoalItem(
					"Daily Study Goal",
					label = "Set your target study hours per day",
					Icons.Default.Book,
					studyGoal = timerViewModel.studyGoal.collectAsState().value,
					onStudyGoalChange = {timerViewModel.changeStudyGoal(it)}
				)
			}

			SettingsBlock("DATA & STORAGE") {
				SettingsItem("Export Session", "Save study history as a JSON file for backup or sharing",
					Icons.Default.Archive, onClick = { launcher.launch("sessions_${getDateString()}.json") })

				SettingsItem(
					"Import Session",
					"Load study history from a previously saved JSON backup",
					Icons.Default.Unarchive,
					onClick = { importLauncher.launch(arrayOf("application/json")) }
				)

				SettingsItem("Clear History", "This will permanently delete all session data" , Icons.Default.Delete, onClick = {
					viewModel.clearHistory()
					Toast.makeText(context, "History cleared", Toast.LENGTH_SHORT).show()
				})
			}
		}
	}
}

@RequiresApi(Build.VERSION_CODES.O)
fun getDateString(): String {
	val currentDate = LocalDateTime.now()
	val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
	val date = currentDate.format(formatter)

	return date
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NotificationItem() {
	val context = LocalContext.current
	var notificationsEnabled by remember {
		mutableStateOf(context.hasNotificationPermission())
	}
	val requestPermissionLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestPermission(),
		onResult = { isGranted: Boolean ->
			// Update your state (switch checked) depending on result
			if (isGranted) {
				notificationsEnabled = true
			} else {
				notificationsEnabled = false
			}
		}
	)




	Row(
		modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
	) {
		IconButton(onClick = {}, interactionSource = null) {
			Icon(
				Icons.Default.Notifications,
				contentDescription = null,
				tint = MaterialTheme.colorScheme
					.onSurfaceVariant
			)
		}


		Column(modifier = Modifier
			.weight(1f)) {
			Text(
				"Enable Notifications", style = MaterialTheme.typography.titleMediumEmphasized,
				color =
				MaterialTheme
					.colorScheme.onSurface
			)
			Text(
				"Show study reminders and timer updates",
				style = MaterialTheme.typography.labelLarge,
				color = MaterialTheme.colorScheme
					.onSurfaceVariant
			)
		}

		Switch(
			modifier = Modifier.padding(start = 8.dp),
			checked = notificationsEnabled,
			onCheckedChange = { checked ->
				if (checked) {
					if (context.hasNotificationPermission()) {
						notificationsEnabled = true
					} else {
						// Ask for permission
						requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
					}
				} else {
					// User disabled it manually in app settings (your own preference)
					//					notificationsEnabled = false
					val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
						putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
					}
					context.startActivity(intent)
				}
			},
			enabled = true,
		)
	}


}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsItem(title: String, label: String, Icon: ImageVector, onClick: () -> Unit = {}) {
	Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
		onClick()
	}) {
		IconButton(onClick = {}, interactionSource = null) {
			Icon(Icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
		}


		Column {
			Text(
				title, style = MaterialTheme.typography.titleMediumEmphasized, color = MaterialTheme
					.colorScheme.onSurface
			)
			Text(
				label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme
					.onSurfaceVariant
			)
		}
	}

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StudyGoalItem(title: String, label: String, Icon: ImageVector, onClick: () -> Unit = {},
									studyGoal : Int, onStudyGoalChange : (Int) -> Unit) {
	var goalHours = studyGoal.toFloat()
	val onGoalChange: (Float) -> Unit = { onStudyGoalChange(it.toInt()) }
	Column {
		Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
			onClick()
		}) {
			IconButton(onClick = {}, interactionSource = null) {
				Icon(Icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
			}


			Column {
				Text(
					title, style = MaterialTheme.typography.titleMediumEmphasized, color = MaterialTheme
						.colorScheme.onSurface
				)
				Text(
					label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme
						.onSurfaceVariant
				)
			}
		}


		Row(
			verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(
				horizontal =
				Dimens.ExtraLargePadding
			)
		) {
			Slider(
				value = goalHours,
				onValueChange = onGoalChange,
				valueRange = 1f..12f,
				steps = 11, // gives 1 hour increments between 1 and 12
				modifier = Modifier.weight(1f)
			)
			Spacer(modifier = Modifier.width(8.dp))
			Text(
				text = "${goalHours.toInt()}h",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.primary
			)
		}
	}


}

@Composable
fun SettingsBlock(blockName: String, content: @Composable () -> Unit) {
	Column(verticalArrangement = Arrangement.spacedBy(Dimens.ExtraLargePadding)) {
		Text(
			blockName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme
				.colorScheme.primary
		)

		content()
	}

}

fun Context.hasNotificationPermission(): Boolean {
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		ContextCompat.checkSelfPermission(
			this,
			Manifest.permission.POST_NOTIFICATIONS
		) == PackageManager.PERMISSION_GRANTED
	} else {
		true // below Android 13, permission is auto-granted
	}
}


