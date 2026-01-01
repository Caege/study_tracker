package com.example.studytracker.data

data class TimerDurationInput(val durationMinutes: Int)
object TimerDurationInputList {
	val values = listOf(
		TimerDurationInput(1),
		TimerDurationInput(5),
		TimerDurationInput(10),
		TimerDurationInput(15),
		TimerDurationInput(20),
		TimerDurationInput(25),
		TimerDurationInput(30),
		TimerDurationInput(40),
		TimerDurationInput(50)
		)

}