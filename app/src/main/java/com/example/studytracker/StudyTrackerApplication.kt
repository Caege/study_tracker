package com.example.studytracker

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build


import com.example.studytracker.data.AppContainer
import com.example.studytracker.data.AppDataContainer
import com.example.studytracker.data.Constants

class StudyTrackerApplication : Application() {
	lateinit var container : AppContainer
	override fun onCreate() {
		//remove in production
//		deleteDatabase("app_database")
		super.onCreate()
		container = AppDataContainer(this)

		//create notification alert channel
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				Constants.NOTIFICATION_CHANNEL_ID_ALERT,
				"Session Alerts",
				NotificationManager.IMPORTANCE_HIGH
			).apply {
				description = "Notifies when sessions or breaks are over"
				enableVibration(true)

				enableLights(true)


					setSound(
						RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
						AudioAttributes.Builder()
						.setUsage(AudioAttributes.USAGE_NOTIFICATION)
						.build()
					)
			}

			val notificationManager = getSystemService(NotificationManager::class.java)
			notificationManager.createNotificationChannel(channel)
		}

	}
}