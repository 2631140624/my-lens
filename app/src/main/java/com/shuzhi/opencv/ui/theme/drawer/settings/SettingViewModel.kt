package com.shuzhi.opencv.ui.theme.drawer.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

//@Inject表示SettingViewModel对象是一个可被注入的对象
@HiltViewModel
class SettingViewModel @Inject constructor(
    private val repository: SettingsRepository
)  :ViewModel () {
    val darkModeFlow = repository.darkModeFlow
    val notificationFlow = repository.notificationFlow
    var googleMlkitDocumentScannerFlow = repository.googleMlkitDocumentScannerFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // 防抖动
        initialValue = null
    )

    suspend fun setDarkMode(enabled: Boolean) {
        repository.setDarkMode(enabled)
    }

    suspend fun setNotification(enabled: Boolean) {
        repository.setNotification(enabled)
    }

    fun setGoogleMlkitDocumentScanner(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setGoogleMlkitDocumentScanner(enabled)
        }
    }
}