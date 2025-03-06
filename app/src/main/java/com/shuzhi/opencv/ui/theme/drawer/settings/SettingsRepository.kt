package com.shuzhi.opencv.ui.theme.drawer.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    // 读取数据
    val darkModeFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.DARK_MODE] ?: false
        }

    // 写入数据
    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[PreferencesKeys.DARK_MODE] = enabled
        }
    }


    val notificationFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS] ?: true
        }

    suspend fun setNotification(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[PreferencesKeys.NOTIFICATIONS] = enabled
        }
    }

    val googleMlkitDocumentScannerFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.GOOGLE_MLKIT_DOCUMENT_SCANNER] ?: true
        }

    suspend fun setGoogleMlkitDocumentScanner(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[PreferencesKeys.GOOGLE_MLKIT_DOCUMENT_SCANNER] = enabled
        }
    }
}