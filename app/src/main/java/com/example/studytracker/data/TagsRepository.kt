package com.example.studytracker.data

import androidx.datastore.core.DataStore
import com.example.studytracker.Tags
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TagsRepository(
	private val dataStore: DataStore<Tags>
) {
	val tags: Flow<List<String>> = dataStore.data
		.map { prefs -> prefs.tagsList }

	suspend fun updateTags(item : String) {
		dataStore.updateData { prefs ->
			prefs.toBuilder()
				.addTags(item)
				.build()
		}
	}

	suspend fun clearTags() {
		dataStore.updateData { prefs ->
			prefs.toBuilder()
				.clearTags()  // <--- clears the repeated field
				.build()
		}
	}
}