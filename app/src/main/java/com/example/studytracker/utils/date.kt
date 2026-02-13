package com.example.studytracker.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun getTodayRange(): Pair<Long, Long> {
	val today = LocalDate.now()
	val zone = ZoneId.systemDefault()

	val startOfDay = today.atStartOfDay(zone).toInstant().toEpochMilli()
	val endOfDay = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

	return startOfDay to endOfDay
}

// get any date range
@RequiresApi(Build.VERSION_CODES.O)
fun getDayRange(date: LocalDate): Pair<Long, Long> {
	val zone = ZoneId.systemDefault()

	val startOfDay = date
		.atStartOfDay(zone)
		.toInstant()
		.toEpochMilli()

	val endOfDay = date
		.plusDays(1)
		.atStartOfDay(zone)
		.toInstant()
		.toEpochMilli()

	return startOfDay to endOfDay
}


@RequiresApi(Build.VERSION_CODES.O)
fun getMillisForToday(hour: Int, minute: Int): Long {
	// Today's date
	val today = LocalDate.now()

	// Time from picker
	val time = LocalTime.of(hour, minute)

	// Combine date + time
	val dateTime = today.atTime(time)

	// Convert to milliseconds
	return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}


@RequiresApi(Build.VERSION_CODES.O)
fun millisToTimeString(millis: Long, truncateAMPM: Boolean = false): String {

	if(truncateAMPM) {
		val formatter = DateTimeFormatter.ofPattern("hh:mm", Locale.getDefault())
		return Instant.ofEpochMilli(millis)
			.atZone(ZoneId.systemDefault())
			.format(formatter)
	}
	val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
	return Instant.ofEpochMilli(millis)
		.atZone(ZoneId.systemDefault())
		.format(formatter)
}


@RequiresApi(Build.VERSION_CODES.O)
fun millisToYearMonth(millis: Long, zoneId: ZoneId = ZoneId.systemDefault()): YearMonth {
	return Instant.ofEpochMilli(millis)
		.atZone(zoneId)
		.toLocalDate()
		.let { YearMonth.from(it) }
}

@RequiresApi(Build.VERSION_CODES.O)
fun currentMilli() : Long {
	return Instant.now().toEpochMilli()
}


@RequiresApi(Build.VERSION_CODES.O)
fun formatMillisToMonthYear(millis: Long, zoneId: ZoneId = ZoneId.systemDefault()): String {
	val formatter = DateTimeFormatter.ofPattern("MMM yyyy") // "Jan 2025"
	return Instant.ofEpochMilli(millis)
		.atZone(zoneId)
		.toLocalDate()
		.format(formatter)
}


@RequiresApi(Build.VERSION_CODES.O)
fun createDateFromDay(dayString: String, referenceMillis: Long, zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
	val referenceDate = Instant.ofEpochMilli(referenceMillis)
		.atZone(zoneId)
		.toLocalDate()

	Log.d("month_shit", referenceDate.month.toString())

	val dayOfMonth = dayString.toIntOrNull() ?: 1 // default to 1 if parsing fails

	return LocalDate.of(referenceDate.year, referenceDate.month, dayOfMonth)
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatMillisToDayMonthYear(millis: Long): String {
	val instant = Instant.ofEpochMilli(millis)
	val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
		.withZone(ZoneId.systemDefault())
	return formatter.format(instant)
}



@RequiresApi(Build.VERSION_CODES.O)
fun formatLocalDate(date: LocalDate): String {
	val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy") // "Jan 01, 2025"
	return date.format(formatter)
}

fun formatSecondsToReadableTime(seconds: Long, short: Boolean = false): String {
	val hours = seconds / 3600
	val minutes = (seconds % 3600) / 60

	return if (short) {
		 buildString {
			if (hours > 0) append("${hours}h")
			if (minutes > 0) {
				if (isNotEmpty()) append(" ")
				append("${minutes}m")
			}
			if (isEmpty()) append("0m") // when duration = 0
		}
	} else {
		"${hours} hrs ${minutes} mins"
	}
}


fun formatSecondsToMMSS(
	seconds: Int,
	short: Boolean = true,
	noPadding: Boolean = false
): String {
	val minutes = seconds / 60
	val secs = seconds % 60

	return if (short) {
		// Short format: "MM:SS"
		"%02d:%02d".format(minutes, secs)
	} else {
		if (noPadding) {
			// No zero-padding, omit 0 units
			when {
				minutes == 0 && secs > 0 -> "${secs}s"
				minutes > 0 && secs == 0 -> "${minutes}m"
				minutes == 0 && secs == 0 -> "0s"
				else -> "${minutes}m ${secs}s"
			}
		} else {
			// Padded format: "MMm SSs"
			"%02dm %02ds".format(minutes, secs)
		}
	}
}


fun secondsToHoursMinutes(seconds: Int): Pair<Int, Int> {
	val hours = seconds / 3600
	val minutes = (seconds % 3600) / 60
	return Pair(hours, minutes)
}