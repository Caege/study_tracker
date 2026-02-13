package com.example.studytracker.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studytracker.data.SessionWithTag
import com.example.studytracker.ui.screens.FromDateToItem
import com.example.studytracker.ui.theme.Dimens
import com.example.studytracker.utils.formatSecondsToMMSS
import com.example.studytracker.utils.formatSecondsToReadableTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SessionCard(sessionList: List<SessionWithTag>,title :String ,  modifier: Modifier =
	Modifier) {
	val sessionSize = sessionList.size
	val totalSeconds = sessionList.sumOf { it.session.duration }
	val hours = totalSeconds / 3600
	val minutes = totalSeconds / 60


	Card(
		colors = CardDefaults.cardColors(
			containerColor =
				MaterialTheme.colorScheme.surfaceContainerLow
		)
	) {
		Column(
			modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp), verticalArrangement
			= Arrangement.spacedBy(Dimens.LargePadding)
		) {
			Text(
				title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme
					.colorScheme.primary
			)

			Row {
				Text("${sessionSize} sessions • ${formatSecondsToReadableTime(totalSeconds, true)}")
			}
			Column(
				verticalArrangement
				= Arrangement.spacedBy(8.dp)
			) {
				sessionList.forEachIndexed { index, sessionWithTag ->
					val duration = formatSecondsToMMSS(
						sessionWithTag.session.duration.toInt(), false,
						noPadding = true
					)
					FromDateToItem(
						from = sessionWithTag.session.startTime,
						end = sessionWithTag.session.endTime,
						tag = sessionWithTag.tag.name,
						duration = duration,
						last = sessionSize - 1 == index
					)
				}
			}
		}
	}
}