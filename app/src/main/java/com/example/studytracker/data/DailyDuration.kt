package com.example.studytracker.data

data class DailyDuration(
    val day: String,          // "01", "02", ...
    val totalDuration: Long   // in millis or minutes
)
