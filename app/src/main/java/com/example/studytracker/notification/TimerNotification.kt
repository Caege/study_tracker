package com.example.studytracker.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.studytracker.utils.formatSecondsToMMSS
//
//fun createNotificationChannel(context: Context) {
//	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//		val channel = NotificationChannel(
//			"timer_channel",
//			"Timer Notifications",
//			NotificationManager.IMPORTANCE_LOW
//		).apply {
//			description = "Notifications for ongoing timer"
//		}
//		val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//		manager.createNotificationChannel(channel)
//	}
//}
//
//fun showTimerProgressNotification(
//	context: Context,
//	notificationId: Int,
//	maxProgress: Int,
//	currentProgress: Int
//) {
//	val notification = NotificationCompat.Builder(context, "timer_channel")
//		.setSmallIcon(android.R.drawable.ic_dialog_info)
//		.setContentTitle("Timer Running")
//		.setContentText("${formatSecondsToMMSS(currentProgress, false)}")
//		.setOngoing(true)  // makes it non-dismissible
//		.setOnlyAlertOnce(true)  // don’t alert every update
//		.setProgress(maxProgress, maxProgress - currentProgress, false) // false = determinate
//		.build()
//
//	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
//		ContextCompat.checkSelfPermission(
//			context,
//			Manifest.permission.POST_NOTIFICATIONS
//		) == PackageManager.PERMISSION_GRANTED) {
//		// Show your notification
//		//		with(NotificationManagerCompat.from(context)) {
//		//			notify(notificationId, notificationBuilder.build())
//		//		}
//		NotificationManagerCompat.from(context).notify(notificationId, notification)
//	}
//
//
//}
//
//
//fun startOngoingTimer(context: Context, durationSeconds: Int, currentProgress: Int) {
//
//	val notificationId = 1
//
//
//
//
//	showTimerProgressNotification(context, notificationId, durationSeconds, currentProgress)
//	// Timer finished
//	if (currentProgress == 0) {
//		NotificationManagerCompat.from(context).cancel(notificationId)
//		// Optional: Show a finished notification
//		val finishedNotification = NotificationCompat.Builder(context, "timer_channel")
//			.setSmallIcon(android.R.drawable.ic_dialog_info)
//			.setContentTitle("Timer Finished")
//			.setContentText("Your timer has ended!")
//			.build()
//
//		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
//			ContextCompat.checkSelfPermission(
//				context,
//				Manifest.permission.POST_NOTIFICATIONS
//			) == PackageManager.PERMISSION_GRANTED) {
//			NotificationManagerCompat.from(context).notify(2, finishedNotification)
//		}
//
//	}
//
//
//}