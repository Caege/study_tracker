package com.example.studytracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.studytracker.ui.theme.ExtraTypography
import com.example.studytracker.utils.formatSecondsToMMSS

@Composable
fun OngoingSessionCard(min : Int, duration : Int) {

	CardWrapperMedium {
		Text("Ongoing session : ${min} mins")
		Text("15:00", style = ExtraTypography.displayLargeAlt)
	}
}


@Composable
fun TimeText(duration: Int) {
	Text(formatSecondsToMMSS(duration), style = ExtraTypography.displayLargeAlt)
}


@Preview
@Composable
fun OngoingSessionCardPreview() {
	Column(modifier = Modifier.fillMaxWidth()) {
		OngoingSessionCard(min = 8, duration = 9)
	}

}