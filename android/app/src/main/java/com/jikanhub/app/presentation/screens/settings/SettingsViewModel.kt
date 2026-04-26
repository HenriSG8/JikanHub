package com.jikanhub.app.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jikanhub.app.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val syncTasks: com.jikanhub.app.domain.usecase.SyncTasksUseCase,
    private val authRepository: com.jikanhub.app.domain.repository.AuthRepository
) : ViewModel() {

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting = _isDeleting.asStateFlow()

    val notificationSoundUri: StateFlow<String?> = tokenManager.notificationSoundUri
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveNotificationSound(uri: String) {
        viewModelScope.launch {
            tokenManager.setNotificationSound(uri)
        }
    }

    fun manualSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            syncTasks(forceForeground = true)
            _isSyncing.value = false
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isDeleting.value = true
            val result = authRepository.deleteAccount()
            if (result.isSuccess) {
                onSuccess()
            }
            _isDeleting.value = false
        }
    }
}
