package com.example.studytracker.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class ThemeChangeReceiver(
	private val context: Context,
	private val onThemeChanged: () -> Unit
) : BroadcastReceiver() {

	override fun onReceive(context: Context, intent: Intent?) {
		if (intent?.action == Intent.ACTION_CONFIGURATION_CHANGED) {
			onThemeChanged()
		}
	}

	fun register() {
		val filter = IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
		context.registerReceiver(this, filter)
	}

	fun unregister() {
		context.unregisterReceiver(this)
	}
}