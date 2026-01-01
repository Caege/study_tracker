package com.example.studytracker.ui

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.studytracker.StudyTrackerApplication
import com.example.studytracker.ui.screens.AddSessionViewModel
import com.example.studytracker.ui.screens.SessionViewModel
import com.example.studytracker.ui.screens.SettingsViewModel

object AppViewModelProvider {

	val Factory = viewModelFactory {
		initializer {
			AddSessionViewModel(studyTrackerApplication().container.tagsRepository)
		}

		initializer {
			SessionViewModel(studyTrackerApplication().container.sessionRepository,
				studyTrackerApplication().container.timerPreferencesRepository )
		}

		initializer {
			TimerViewModel(
				studyTrackerApplication(),
				sessionRepository = studyTrackerApplication().container.sessionRepository,
				timerPreferencesRepository = studyTrackerApplication().container.timerPreferencesRepository
			)
		}

		initializer {
			SettingsViewModel(studyTrackerApplication().container.sessionRepository)
		}
	}
}

fun CreationExtras.studyTrackerApplication(): StudyTrackerApplication =
	(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as StudyTrackerApplication)