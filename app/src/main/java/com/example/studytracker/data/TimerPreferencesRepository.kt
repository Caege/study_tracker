package com.example.studytracker.data

import android.content.ContentValues.TAG
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

import kotlinx.coroutines.flow.map
import java.io.IOException


class TimerPreferencesRepository(
	private val dataStore: DataStore<Preferences>
){
	private companion object {
		val TOTAL_DURATION = intPreferencesKey("total_duration")
		val BREAK_DURATION = intPreferencesKey("break_duration")
		val STUDY_GOAL = intPreferencesKey("study_goal")
		val SELECTED_TAG_ID = intPreferencesKey("selected_tag_id")
	}

	suspend fun changeTotalDuration(totalDuration : Int) {
		dataStore.edit { preferences ->
			preferences[TOTAL_DURATION] = totalDuration
		}

	}

	suspend fun changeSelectedTagId(tagID : Int) {
		dataStore.edit { preferences ->
			preferences[SELECTED_TAG_ID] = tagID
		}

	}


	suspend fun changeBreakDuration(breakDuration : Int) {
		dataStore.edit { preferences ->
			preferences[BREAK_DURATION] = breakDuration
		}

	}

	suspend fun changeStudyGoal(goal: Int) : Unit {
		dataStore.edit { preferences ->
			preferences[STUDY_GOAL] = goal
		}
	}


	val totalDuration: Flow<Int> = dataStore.data
			.catch {
				if(it is IOException) {
					Log.e(TAG, "Error reading preferences.", it)
					emit(emptyPreferences())
				} else {
					throw it
				}
			}
			.map { preferences ->
				preferences[TOTAL_DURATION] ?: 0
			}

	val breakDuration: Flow<Int> = dataStore.data
		.catch {
			if(it is IOException) {
				Log.e(TAG, "Error reading preferences.", it)
				emit(emptyPreferences())
			} else {
				throw it
			}
		}
		.map { preferences ->
			preferences[BREAK_DURATION] ?: 0
		}



	val studyGoal: Flow<Int> = dataStore.data
		.catch { exception ->
			if (exception is IOException) {
				Log.e(TAG, "Error reading preferences.", exception)
				emit(emptyPreferences())
			} else {
				throw exception
			}
		}
		.map { preferences ->
			preferences[STUDY_GOAL] ?: 0   // default 0 if not set
		}


	val selectedTagId: Flow<Int> = dataStore.data
		.catch { exception ->
			if (exception is IOException) {
				Log.e(TAG, "Error reading preferences.", exception)
				emit(emptyPreferences())
			} else {
				throw exception
			}
		}
		.map { preferences ->
			preferences[SELECTED_TAG_ID] ?: 1   // default 0 if not set
		}
	}



