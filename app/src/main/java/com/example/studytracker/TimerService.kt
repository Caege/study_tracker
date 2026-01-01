package com.example.studytracker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.Service.START_NOT_STICKY
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat.startForeground
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.studytracker.data.AppDatabase
import com.example.studytracker.data.Session
import com.example.studytracker.data.SessionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.Manifest
import android.R.attr.action
import android.app.PendingIntent
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import com.example.studytracker.TimerService.Companion.EXTRA_DURATION
import com.example.studytracker.TimerService.Companion.EXTRA_PAUSE
import com.example.studytracker.TimerService.Companion.EXTRA_RESUME
import com.example.studytracker.TimerService.Companion.EXTRA_STOP
import com.example.studytracker.data.Constants
import com.example.studytracker.data.Constants.Companion.NOTIFICATION_TIMER_ID
import com.example.studytracker.data.Tag
import com.example.studytracker.data.ThemeChangeReceiver
import com.example.studytracker.data.TimerServiceRepository
import com.example.studytracker.data.TimerState
import com.example.studytracker.utils.currentMilli
import com.example.studytracker.utils.formatSecondsToMMSS
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlin.math.ceil

const val ACTION_TIMER_PROGRESS = "com.example.studytracker.ACTION_TIMER_PROGRESS"
const val EXTRA_PROGRESS = "EXTRA_PROGRESS"

class TimerService : Service() {
	/* repository */
	private val repository = TimerServiceRepository
	val sessionRepository: SessionRepository
		get() = (application as StudyTrackerApplication).container.sessionRepository
	val context = this
	var currentTagId: Int? = null
	private var job: Job? = null
	private var totalSeconds: Int = 0
	private var startTime: Long = 0L       // when current run started
	private var endTime: Long = 0L         // when timer should finish
	private var remainingTime: Int = 0     // seconds left when paused
	private var isPaused: Boolean = false
	private var currentStartTime: Long? = null
	var isBreakTimer: Boolean? = null

	companion object {
		const val EXTRA_DURATION = "duration"
		const val EXTRA_PAUSE = "pauseTimer"
		const val EXTRA_RESUME = "resumeTimer"
		const val EXTRA_STOP = "stopTimer"
		const val EXTRA_STARTED = "started"
		const val EXTRA_IS_PAUSED = "isPaused"
		const val CHANNEL_ID = "test_timer"
		const val PAUSE_CODE = 0
		const val RESUME_CODE = 2
		const val STOP_CODE = 3
		const val EXTRA_TAG = "tag"
		const val BREAK_TIMER = "break_timer"


	}

	private lateinit var themeChangeReceiver: ThemeChangeReceiver
	override fun onCreate() {
		super.onCreate()

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				CHANNEL_ID,
				"Timer Notifications",
				NotificationManager.IMPORTANCE_DEFAULT
			)
			val manager = getSystemService(NotificationManager::class.java)
			manager.createNotificationChannel(channel)
		}
		// Register theme change listener
		themeChangeReceiver = ThemeChangeReceiver(this) {
			// When theme changes → refresh notification
			refreshNotification()
		}
		themeChangeReceiver.register()


	}

	override fun onDestroy() {
		super.onDestroy()
		themeChangeReceiver.unregister()
	}



	private fun refreshNotification() {
		val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		nm.notify(NOTIFICATION_TIMER_ID, buildNotification(remainingTime))
	}

	fun showSessionCompleteNotification(isBreak: Boolean = false) {
		val title = if (isBreak) "Break finished!" else "Study session completed!"
		val message = if (isBreak) "Time to get back to studying 📚" else "Take a well-deserved break ☕"
		val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID_ALERT)
			.setContentTitle(title)
			.setContentText(message)
			.setSmallIcon(R.drawable.notif_icon) // your checkmark icon
			.setPriority(NotificationCompat.PRIORITY_HIGH) // heads-up notification
			.setDefaults(NotificationCompat.DEFAULT_ALL)   // sound + vibration
			.setAutoCancel(true) // disappears when tapped
			.build()
		val notificationManager = getSystemService(NotificationManager::class.java)

		notificationManager.notify(Constants.NOTIFICATION_ALERT_ID, notification)
	}

	private fun buildNotification(remainingSeconds: Int): Notification {
		val themedContext = ContextThemeWrapper(this, R.style.Theme_StudyTracker)
		val endTime = SystemClock.elapsedRealtime() + ((remainingSeconds + 1) * 1000L)
		val remoteViews = RemoteViews(packageName, R.layout.notification_timer)
		val tv = TypedValue()
		val result = themedContext.theme.resolveAttribute(R.attr.myNotificationTextColor, tv, true)
		val themedTextColor = tv.data

		Log.d("theme_shit", result.toString())
		val pauseIntent = Intent(this, TimerService::class.java).apply {
			action = "ACTION_PAUSE_RESUME"
			putExtra(EXTRA_PAUSE, true) // if currently running, pause; if paused, resume
			// inverse
		}
		val pausePendingIntent = PendingIntent.getService(
			this, PAUSE_CODE, pauseIntent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)
		val resumeIntent = Intent(this, TimerService::class.java).apply {
			action = "ACTION_PAUSE_RESUME"
			// if currently running, pause; if paused, resume
			putExtra(EXTRA_RESUME, true)  // inverse
		}
		val resumePendingIntent = PendingIntent.getService(
			this, RESUME_CODE, resumeIntent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)
		// Stop button
		val stopIntent = Intent(this, TimerService::class.java).apply {
			action = "ACTION_STOP"
			putExtra(EXTRA_STOP, true)
		}
		val stopPendingIntent = PendingIntent.getService(
			this, STOP_CODE, stopIntent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)





		if (isPaused) {
			remoteViews.apply {
				setTextViewText(R.id.notification_title, "Timer paused")
				remoteViews.setInt(R.id.notification_title, "setTextColor", themedTextColor)
				setChronometer(
					R.id.notification_timer,
					endTime,   // "base" time
					"%s", // format
					false        // start running
				)
				setChronometerCountDown(R.id.notification_timer, true)
				remoteViews.setInt(R.id.notification_timer, "setTextColor", themedTextColor)
			}

		} else {
			remoteViews.apply {
				setTextViewText(R.id.notification_title, "Timer running")
				remoteViews.setInt(R.id.notification_title, "setTextColor", themedTextColor)
				setChronometer(
					R.id.notification_timer,
					endTime,   // "base" time
					"%s", // format
					true        // start running
				)

				setChronometerCountDown(R.id.notification_timer, true)
				remoteViews.setInt(R.id.notification_timer, "setTextColor", themedTextColor)
			}
		}
		val notificationIntent = Intent(this, MainActivity::class.java)
		val pendingIntent = PendingIntent.getActivity(
			this,
			0,
			notificationIntent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)
		val builder = NotificationCompat.Builder(this, CHANNEL_ID)
			.setSmallIcon(R.drawable.notif_icon)
			.setStyle(NotificationCompat.DecoratedCustomViewStyle()) // your icon
			.setCustomContentView(remoteViews) // use custom RemoteViews
			.setContentIntent(pendingIntent)
			.setOngoing(true)
			.setOnlyAlertOnce(true)
		//	.addAction(R.drawable.play_arrow_24dp, if (isPaused) "Resume" else "Pause", pausePendingIntent)
		//	.build()
		if (isPaused) {
			builder.addAction(R.drawable.play_arrow_24dp, "Resume", resumePendingIntent)
		} else {
			builder.addAction(R.drawable.pause_24, "Pause", pausePendingIntent)
		}

		builder.addAction(R.drawable.pause_24, "Reset", stopPendingIntent)
		return builder.build()
	}

	@RequiresApi(Build.VERSION_CODES.O)
	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		currentStartTime = if (currentStartTime != null) currentStartTime else currentMilli()
		val duration = intent?.getIntExtra(EXTRA_DURATION, 0) ?: 0

		totalSeconds = if (duration == 0) totalSeconds else duration

		Log.d("timerCore", "totalSeconds : $totalSeconds")
		val pause = intent?.getBooleanExtra(EXTRA_PAUSE, false) ?: false
		val resume = intent?.getBooleanExtra(EXTRA_RESUME, false) ?: false
		val stop = intent?.getBooleanExtra(EXTRA_STOP, false) ?: false
		//		val tag = intent?.getIntExtra(EXTRA_TAG, 0) ?: 0
		intent?.let {
			if (it.hasExtra(EXTRA_TAG)) {
				currentTagId = it.getIntExtra(EXTRA_TAG, 0)
			}
		}
		//		val tagResult = CoroutineScope(Dispatchers.IO).async {
		//			sessionRepository.getTagByName(tag)
		//		}
		when {
			pause -> {
				isPaused = true

				broadcastProgress(remainingTime, isStarted = false, isPaused = true)
				startForeground(NOTIFICATION_TIMER_ID, buildNotification(remainingTime))
				return START_NOT_STICKY
			}

			resume -> {
				if (isPaused) {
					isPaused = false
					startTimer(remainingTime, currentTagId ?: 0)
					startForeground(NOTIFICATION_TIMER_ID, buildNotification(remainingTime))
				}
				return START_NOT_STICKY
			}

			stop -> {
				job?.cancel()

				broadcastProgress(0, isStarted = false, isPaused = false)
				currentStartTime = null
				//				val progressIntent = Intent(ACTION_TIMER_PROGRESS)
				//				progressIntent.putExtra(EXTRA_PROGRESS, 0)
				//				progressIntent.setPackage(packageName)
				//				sendBroadcast(progressIntent, )
				stopSelf()
				return START_NOT_STICKY
			}

			else -> {
				// start new timer
				remainingTime = duration


				startForeground(NOTIFICATION_TIMER_ID, buildNotification(duration))
				startTimer(duration, currentTagId ?: 0)
			}
		}

		return START_NOT_STICKY
	}

	@RequiresApi(Build.VERSION_CODES.O)
	private fun startTimer(duration: Int, tagId: Int) {
		Log.d("sessionshit", currentStartTime.toString())
		//		totalSeconds = duration
		if (!isPaused) {
			startTime = SystemClock.elapsedRealtime()
			endTime = startTime + duration * 1000L
		} else {
			startTime = SystemClock.elapsedRealtime()
			endTime = startTime + remainingTime * 1000L
			isPaused = false
		}
		val startTime = currentMilli()

		job?.cancel()
		job = CoroutineScope(Dispatchers.Default).launch {
			while (isActive) {
				if (isPaused) {
					remainingTime = ceil((endTime - SystemClock.elapsedRealtime()) / 1000.0).toInt()
					break
				}
				val now = SystemClock.elapsedRealtime()
				remainingTime = ((endTime - now) / 1000).toInt().coerceAtLeast(0)

				broadcastProgress(
					remainingSeconds = remainingTime,
					isStarted = true,
					isPaused = false
				)

				if (remainingTime <= 0) {
					val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
					val wakeLock = powerManager.newWakeLock(
						PowerManager.PARTIAL_WAKE_LOCK,
						"MyApp::SessionCompleteWakeLock"
					)
					// Set a timeout for the wake lock to prevent it from being held indefinitely
					wakeLock.acquire(10 * 1000L /* 10 seconds */)


					showSessionCompleteNotification()
					currentStartTime?.let {
						Log.d("sessionshit", "session added")
						sessionRepository.insertSession(
							Session(
								tagId = tagId,
								startTime = it,
								endTime = currentMilli(),
								duration = totalSeconds.toLong(),
								notes = ""
							)
						)
					}

					if (wakeLock.isHeld) {
						wakeLock.release()
					}
					val stopIntent = Intent(context, TimerService::class.java).apply {
						action = "ACTION_STOP"
						putExtra(EXTRA_STOP, true)
					}
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
						startForegroundService(stopIntent)
					} else {
						startService(stopIntent)
					}
					//					stopSelf()
					break
				}
				val delayMillis = (1000 - (SystemClock.elapsedRealtime() % 1000))
				//test delay
				delay(delayMillis)
				//				delay(100)
			}
			//			for (i in duration downTo 0) {
			//				if (isPaused) {
			//					remainingTime = i
			//					break // exit loop, will resume later
			//				}
			//
			//				remainingTime = i
			//
			//				broadcastProgress(remainingSeconds = remainingTime, isStarted = true, isPaused = false)
			//
			//				if (i == 0) {
			//					sessionRepository.insertSession(
			//						Session(
			//							tagId = tagId,
			//							startTime = startTime,
			//							endTime = currentMilli(),
			//							duration = totalSeconds.toLong(),
			//							notes = ""
			//						)
			//					)
			//					val stopIntent = Intent(context, TimerService::class.java).apply {
			//						action = "ACTION_STOP"
			//						putExtra(EXTRA_STOP, true)
			//					}
			//					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			//						startForegroundService(stopIntent)
			//					} else {
			//						startService(stopIntent)
			//					}
			//
			//
			//				}
			//				delay(1000)
			//			}
			//			if (!isPaused) stopSelf()
		}
	}

	private fun broadcastProgressTest(remainingSeconds: Int, isStarted: Boolean, isPaused: Boolean) {
		val progressIntent = Intent(ACTION_TIMER_PROGRESS)
		progressIntent.putExtra(EXTRA_PROGRESS, remainingSeconds)
		progressIntent.putExtra(EXTRA_STARTED, isStarted)
		progressIntent.putExtra(EXTRA_IS_PAUSED, isPaused)
		progressIntent.setPackage(packageName)
		sendBroadcast(progressIntent)

	}

	private fun broadcastProgress(remainingSeconds: Int, isStarted: Boolean, isPaused: Boolean) {
		val currentState = TimerState(
			progress = remainingSeconds,
			isStarted = isStarted,
			isPaused = isPaused,
		)

		repository.updateTimerState(currentState)

	}

	override fun onBind(intent: Intent?): IBinder? = null
}