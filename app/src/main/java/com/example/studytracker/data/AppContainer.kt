package com.example.studytracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val TIMER_PREFERENCE_NAME = "timer_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
	name = TIMER_PREFERENCE_NAME
)


interface AppContainer {
	val tagsRepository : TagsRepository
	val sessionRepository : SessionRepository
	val timerPreferencesRepository : TimerPreferencesRepository
}


class AppDataContainer(private val context: Context) : AppContainer {
	override val tagsRepository: TagsRepository by lazy { TagsRepository(context.tagsStore) }
	override val sessionRepository : SessionRepository = OfflineSessionRepository(AppDatabase
		.getDatabase(context).sessionDao())

	override val timerPreferencesRepository = TimerPreferencesRepository(context.dataStore)
}