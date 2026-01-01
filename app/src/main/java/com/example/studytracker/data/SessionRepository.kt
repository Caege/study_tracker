package com.example.studytracker.data

import kotlinx.coroutines.flow.Flow

interface SessionRepository {

	/** Retrieve all tags */
	fun getAllTagsStream(): Flow<List<Tag>>


	suspend  fun getTagByName(name : String) : Tag?

	/** Retrieve a tag with all its sessions */
//	fun getTagWithSessionsStream(tagId: Int): Flow<SessionWithTag?>


	suspend fun getDailyDurationsForMonth(monthStart: Long, monthEnd: Long): List<DailyDuration>


	fun getSessionWithTagStream(): Flow<List<SessionWithTag>>


	suspend fun getTagById(tagId :Int) : Tag?

	/** Insert a tag */
	suspend fun insertTag(tag: Tag): Long

	/** Insert a session */
	suspend fun insertSession(session: Session): Long

	/** Get sessions for a specific day */
	fun getSessionsForDayStream(dayStart: Long, dayEnd: Long): Flow<List<SessionWithTag>>

	//delete sessions
	suspend fun clearAllHistory( )
}


class OfflineSessionRepository(private val dao: SessionDao) : SessionRepository {

	override fun getAllTagsStream(): Flow<List<Tag>> = dao.getAllTags() // You need this DAO method
	override suspend  fun getTagByName(name: String): Tag? = 	dao.getTagByName(name)
	override suspend  fun getTagById(tagId: Int): Tag? = dao.getTagById(tagId)


//	override fun getTagWithSessionsStream(tagId: Int): Flow<SessionWithTag?> = dao.getTagWithSessionsFlow(tagId)
	override fun getSessionWithTagStream(): Flow<List<SessionWithTag>>  = dao.getSessionsWithTag()
	override suspend fun getDailyDurationsForMonth(
		monthStart: Long,
		monthEnd: Long
	): List<DailyDuration> {
		return dao.getDailyDurationsForMonth(monthStart, monthEnd)
	}

	override suspend fun insertTag(tag: Tag): Long = dao.insertTag(tag)

	override suspend fun insertSession(session: Session): Long = dao.insertSession(session)

	override fun getSessionsForDayStream(dayStart: Long, dayEnd: Long): Flow<List<SessionWithTag>> =
		dao.getSessionsForDayFlow(dayStart, dayEnd) // You need a Flow variant

	override suspend fun clearAllHistory() = dao.clearTags()
}