package com.ekosoftware.tmdb.presentation

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class ErrorEventsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {


}

