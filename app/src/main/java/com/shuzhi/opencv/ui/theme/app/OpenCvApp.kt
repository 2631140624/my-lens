package com.shuzhi.opencv.ui.theme.app

import android.app.Application
import android.content.Context
import me.pqpo.smartcropperlib.SmartCropper

/**
 *@author :yinxiaolong
 *@describe : com.shuzhi.opencv.ui.theme.app
 *@date :2025-01-18 21:43
 */
class OpenCvApp : Application() {
    companion object{
        var appContext :Context? = null
    }
    override fun onCreate() {
        super.onCreate()
        appContext = this
        SmartCropper.buildImageDetector(this)
    }
}