package com.example.studytracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SummaryListItem(top: String, bottom : @Composable () -> Unit){
	Column {
		Text(top)
		bottom()
	}
}