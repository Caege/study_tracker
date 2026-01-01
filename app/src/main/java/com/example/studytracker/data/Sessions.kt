package com.example.studytracker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = Tag::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["tagId"])]
)
data class Session(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tagId: Int,
    val startTime: Long,
    val endTime: Long,
    val duration: Long,
    val notes: String = ""
)
