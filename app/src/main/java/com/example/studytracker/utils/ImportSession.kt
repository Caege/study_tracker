package com.example.studytracker.utils

import com.example.studytracker.data.SessionRepository
import com.example.studytracker.data.SessionWithTag
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

val gson = Gson()
val sessinWithTagType = object : TypeToken<List<SessionWithTag>>() {}.type
val jsonString = ""
val sessionsWithTags: List<SessionWithTag> = gson.fromJson(jsonString, sessinWithTagType)

suspend fun importSessions(repository: SessionRepository, sessionsWithTags: List<SessionWithTag>) {
	sessionsWithTags.forEach { st ->
		// Insert Tag first
		val tagId = repository.insertTag(st.tag) // returns ID (auto-generated)

		// Insert Session with correct tagId
		val sessionToInsert = st.session.copy(tagId = tagId.toInt())
		repository.insertSession(sessionToInsert)
	}
}


suspend fun importJsonToRoom(repository: SessionRepository, jsonString: String) {
	val gson = Gson()
	val type = object : TypeToken<List<SessionWithTag>>() {}.type
	val sessionsWithTags: List<SessionWithTag> = gson.fromJson(jsonString, type)

	importSessions(repository, sessionsWithTags)
}