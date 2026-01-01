package com.example.studytracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.studytracker.Tags

private const val DATA_STORE_FILE_NAME = "tags.pb"

val Context.tagsStore: DataStore<Tags> by dataStore(
	fileName = DATA_STORE_FILE_NAME,
	serializer = UserPreferencesSerializer
)