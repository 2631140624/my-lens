package com.shuzhi.opencv.ui.theme.drawer.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile

object SettingManager {

}
//VK
object PreferencesKeys {
    val DARK_MODE = booleanPreferencesKey("dark_mode")
    val NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
    val GOOGLE_MLKIT_DOCUMENT_SCANNER = booleanPreferencesKey("google_mlkit_document_scanner")
}