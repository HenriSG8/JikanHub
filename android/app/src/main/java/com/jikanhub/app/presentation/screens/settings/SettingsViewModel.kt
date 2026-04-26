package com.jikanhub.app.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jikanhub.app.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    val notificationSoundUri: StateFlow<String?> = tokenManager.notificationSoundUri
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveNotificationSound(uri: String) {
        viewModelScope.launch {
            tokenManager.setNotificationSound(uri)
        }
    }
}
