package com.shuzhi.opencv.ui.theme.drawer.settings

import androidx.appcompat.app.AppCompatDelegate

data class SettingState(val nightMode: Boolean,val notification:Boolean,val googleMlkitDocumentScanner:Boolean) {
    init {
        if (nightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    companion object {
        val Default by lazy {
            SettingState(
                nightMode = false,
                notification = true,
                googleMlkitDocumentScanner = true
            )
        }
    }
}
