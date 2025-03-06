package com.shuzhi.opencv.ui.theme.app

import android.app.Application
import android.content.Context
import com.shuzhi.opencv.ui.theme.mainPage.MainViewModel
import dagger.hilt.android.HiltAndroidApp
import me.pqpo.smartcropperlib.SmartCropper
//import org.opencv.android.OpenCVLoader

/**
 *@author :yinxiaolong
 *@describe : com.shuzhi.opencv.ui.theme.app
 *@date :2025-01-18 21:43
 */
@HiltAndroidApp
class OpenCvApp : Application() {
    companion object{
        var appContext :Context? = null
        var sharedViewModel : MainViewModel? =null
    }
    override fun onCreate() {
        super.onCreate()
        appContext = this
        //图像检测
        SmartCropper.buildImageDetector(this)
        //opencv
     //   val isOpenCVLoaderInit = OpenCVLoader.initLocal()
    }
}