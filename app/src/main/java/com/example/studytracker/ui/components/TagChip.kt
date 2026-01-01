package com.example.studytracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun TagChip(
	text: String,
	onClick: () -> Unit,
	isSelected: Boolean,
	onRemove: () -> Unit,
	clearIcon: Boolean = true
) {
	InputChip(
		selected = isSelected,
		onClick = onClick,
		colors = InputChipDefaults.inputChipColors(
			containerColor = MaterialTheme.colorScheme.surfaceVariant,
			labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
			selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
			selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
		),
		label = { Text(text) },
		trailingIcon = if (clearIcon) {
			{
				Box(
					modifier = Modifier
						.size(30.dp)
						.clip(CircleShape)
						.clickable { onRemove() },
					contentAlignment = Alignment.Center
				) {
					Icon(
						imageVector = Icons.Outlined.Clear,
						contentDescription = "Clear",
						modifier = Modifier.size(18.dp)
					)
				}
			}
		} else null
	)
}


