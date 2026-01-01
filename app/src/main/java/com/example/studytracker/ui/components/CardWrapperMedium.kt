package com.example.studytracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CardWrapperMedium(content: @Composable () -> Unit) {
	Card(colors = CardDefaults.cardColors(containerColor =
	MaterialTheme.colorScheme.surfaceContainerLow)) {
		Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
			content()
		}
	}
}