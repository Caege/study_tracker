package com.example.studytracker.data

import androidx.room.Embedded
import androidx.room.Relation

data class SessionWithTag(
	@Embedded val session: Session,

	@Relation(
		parentColumn = "tagId",   // column inside Session
		entityColumn = "id"       // column inside Tag
	)
	val tag: Tag
)