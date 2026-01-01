package com.example.studytracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertTag(tag: Tag): Long

	@Query("SELECT * FROM tags WHERE name = :tagName LIMIT 1")
	suspend fun getTagByName(tagName: String): Tag?

	@Query("SELECT * FROM tags WHERE id = :tagId LIMIT 1")
	suspend fun getTagById(tagId: Int): Tag?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertSession(session: Session): Long


	//we need seperate relationship function for this
//	@Transaction
//	@Query("SELECT * FROM tags WHERE id = :tagId")
//	fun getTagWithSessionsFlow(tagId: Int): Flow<SessionWithTag?>

//	@Transaction
//	@Query("SELECT * FROM sessions")
//	 fun getSessionsWithTag(): Flow<List<SessionWithTag>>


	@Query("""
    SELECT 
        strftime('%d', datetime(startTime / 1000, 'unixepoch', 'localtime')) AS day,
       SUM(duration) AS totalDuration
    FROM sessions
    WHERE startTime BETWEEN :monthStart AND :monthEnd
    GROUP BY day
    ORDER BY day
""")
	suspend fun getDailyDurationsForMonth(monthStart: Long, monthEnd: Long): List<DailyDuration>

	@Transaction
	@Query("SELECT * FROM sessions")
	fun getSessionsWithTag(): Flow<List<SessionWithTag>>

	@Query("SELECT * FROM sessions WHERE startTime >= :dayStart AND startTime < :dayEnd")
	fun getSessionsForDayFlow(dayStart: Long, dayEnd: Long): Flow<List<SessionWithTag>>

	@Query("SELECT * FROM tags")
	fun getAllTags(): Flow<List<Tag>>


	//delete
	@Query("DELETE FROM sessions")
	suspend fun clearSessions()

	@Query("DELETE FROM tags")
	suspend fun clearTags()
}