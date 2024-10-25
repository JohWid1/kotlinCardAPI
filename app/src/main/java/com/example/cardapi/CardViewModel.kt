package com.example.cardapi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.Call

class CardViewModel : ViewModel() {
    var card by mutableStateOf<Card?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun fetchRandomCard(apiCall: suspend () -> Card?) {
        isLoading = true
        viewModelScope.launch {
            try {
                card = apiCall()
            } catch (e: Exception) {
                card = null
            } finally {
                isLoading = false
            }
        }
    }
}

