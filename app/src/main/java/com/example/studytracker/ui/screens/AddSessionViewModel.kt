package com.example.studytracker.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studytracker.data.TagsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddSessionViewModel(
	private val tagsRepository: TagsRepository
) : ViewModel() {
 val tagsList : StateFlow<List<String>> = tagsRepository.tags.stateIn(
		viewModelScope, SharingStarted.Lazily, emptyList()
	)

	fun addTags(tag : String) {
		viewModelScope.launch {
			tagsRepository.updateTags(tag)
		}
	}

	fun clearTags() {
		viewModelScope.launch {
			tagsRepository.clearTags()
		}
	}
}

