package com.example.studytracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.example.studytracker.TimerService.Companion.EXTRA_IS_PAUSED
import com.example.studytracker.TimerService.Companion.EXTRA_STARTED
import com.example.studytracker.ui.AppViewModelProvider
import com.example.studytracker.ui.TimerViewModel
import com.example.studytracker.ui.screens.SettingsViewModel
//import com.example.studytracker.notification.createNotificationChannel
//import com.example.studytracker.notification.startOngoingTimer
import com.example.studytracker.ui.theme.StudyTrackerTheme
import com.example.studytracker.utils.saveJsonToUri
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity() : ComponentActivity() {

	private val viewModel: TimerViewModel by viewModels()
	private val settingsViewModel : SettingsViewModel by viewModels{
		AppViewModelProvider.Factory
	}


	@RequiresApi(Build.VERSION_CODES.O)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

//export session
		val createFileLauncher: ActivityResultLauncher<String> =
			registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
				uri?.let { selectedUri ->


					lifecycleScope.launch {
						val jsonString = settingsViewModel.sessionToJson(this@MainActivity)
						saveJsonToUri(selectedUri, this@MainActivity, jsonString )
					} }
			}

//import session
	 val openJsonLauncher : ActivityResultLauncher<Array<String>> =
			registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
				if (uri != null) {
					// Persist permission if you need future access
					contentResolver.takePersistableUriPermission(
						uri,
						Intent.FLAG_GRANT_READ_URI_PERMISSION
					)

					// Read JSON from the Uri
					val inputStream = contentResolver.openInputStream(uri)
					val jsonText = inputStream?.bufferedReader().use { it?.readText() }

					lifecycleScope.launch {
						jsonText?.let {
							settingsViewModel.importJsonToRoom(it)
						}

					}
				}
			}


		setContent {
			StudyTrackerTheme {

					StudyTrackerApp(modifier = Modifier, launcher = createFileLauncher, importLauncher = openJsonLauncher)
//MainScreen()


			}
		}



	}


//	private val progressReceiver = object : BroadcastReceiver() {
//		override fun onReceive(context: Context?, intent: Intent?) {
//			val progress = intent?.getIntExtra(EXTRA_PROGRESS, 0) ?: 0
//		val isStarted = intent?.getBooleanExtra(EXTRA_STARTED, false) ?: false
//			val isPaused = intent?.getBooleanExtra(EXTRA_IS_PAUSED, false) ?: false
//
//			viewModel.updateProgress(progress, isStarted, isPaused)
//
//		}
//	}

//	@RequiresApi(Build.VERSION_CODES.O)
//	override fun onStart() {
//		super.onStart()
//		registerReceiver(
//			progressReceiver,
//			IntentFilter(ACTION_TIMER_PROGRESS),
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//				Context.RECEIVER_NOT_EXPORTED  // For Android 13+ (API 33+)
//			} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//				Context.RECEIVER_NOT_EXPORTED  // For Android 12 (API 31-32)
//			} else {
//				Context.RECEIVER_EXPORTED  // For older versions
//			}
//		)
//	}

//	override fun onStop() {
//		super.onStop()
//		unregisterReceiver(progressReceiver)
//	}
}



