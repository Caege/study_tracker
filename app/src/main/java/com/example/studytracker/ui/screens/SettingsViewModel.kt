package com.example.studytracker.ui.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studytracker.StudyTrackerApplication
import com.example.studytracker.data.Session
import com.example.studytracker.data.SessionRepository
import com.example.studytracker.data.SessionWithTag
import com.example.studytracker.data.TagsRepository
import com.example.studytracker.utils.gson
import com.example.studytracker.utils.jsonString
import com.example.studytracker.utils.sessinWithTagType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
	val repository: SessionRepository
) : ViewModel() {
	val gson = Gson()


	suspend fun sessionToJson(context: Context): String {
		val application = context.applicationContext as StudyTrackerApplication
		val jsonString = viewModelScope.async {
			val sessionsWithTags: List<SessionWithTag> = repository
				.getSessionWithTagStream().first()

			Log.d("testshit", sessionsWithTags.toString())
			//			val gson = Gson()
			gson.toJson(sessionsWithTags)
		}

		return jsonString.await()


	}

	private suspend fun importSessions(sessionsWithTags: List<SessionWithTag>) {
		for (st in sessionsWithTags) {
			val existingTag = repository.getTagByName(st.tag.name)
			val tagId = existingTag?.id ?: repository.insertTag(st.tag).toInt()
			val sessionToInsert = st.session.copy(tagId = tagId)
			val result = repository.insertSession(sessionToInsert)
			Log.d("DB", "Inserted session: $result")
		}

//		sessionsWithTags.forEach { st ->
//			// Insert Tag first
//			val tagId = repository.insertTag(st.tag) // returns ID (auto-generated)
//
//			// Insert Session with correct tagId
//			val sessionToInsert = st.session.copy(tagId = tagId.toInt())
//			Log.d("DB", sessionToInsert.toString())
//			val result = repository.insertSession(sessionToInsert)
//			Log.d("DB", "Inserted session: $result")
//		}
	}

	suspend fun importJsonToRoom(jsonString: String) {
//		Log.d("setting_shit", jsonString)
		val sessionWithTagType = object : TypeToken<List<SessionWithTag>>() {}.type
		val sessionsWithTags: List<SessionWithTag> = gson.fromJson(jsonString, sessionWithTagType)

		importSessions(sessionsWithTags)
	}


	fun clearHistory() {
		viewModelScope.launch {
			repository.clearAllHistory()
		}
	}


}

