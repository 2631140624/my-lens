package com.shuzhi.opencv.ui.theme.app

import android.app.Application
import android.content.Context
import cn.leancloud.LeanCloud
import cn.leancloud.core.LeanService
import com.shuzhi.opencv.ui.theme.mainPage.MainViewModel
import dagger.hilt.android.HiltAndroidApp
import me.pqpo.smartcropperlib.SmartCropper
//import org.opencv.android.OpenCVLoader

/**
 *@author :yinxiaolong
 *@describe : com.shuzhi.opencv.ui.theme.app
 *@date :2025-01-18 21:43
 */
//@HiltAndroidApp
class OpenCvApp : Application() {
    companion object{
        var appContext :Context? = null
        var sharedViewModel : MainViewModel? =null

        //sdk启动前核心加载 在其他模块调用
        public fun doOnApp(context: Context){
            appContext = context
            //图像检测
            SmartCropper.buildImageDetector(context)
            LeanCloud.initialize(
                context,
                "93SYdQVOtKPYltllBEpdhV6z-gzGzoHsz",
                "QlqutzgYXcW0vUx3knFxTgeZ",
                "https://93sydqvo.lc-cn-n1-shared.com")
        }
    }
    override fun onCreate() {
        super.onCreate()
        appContext = this
        //图像检测
        SmartCropper.buildImageDetector(this)
        //opencv
     //   val isOpenCVLoaderInit = OpenCVLoader.initLocal()
//        LeanCloud.setServer(LeanService.API,"https://93sydqvo.lc-cn-n1-shared.com")
        LeanCloud.initialize(
            this,
            "93SYdQVOtKPYltllBEpdhV6z-gzGzoHsz",
            "QlqutzgYXcW0vUx3knFxTgeZ",
            "https://93sydqvo.lc-cn-n1-shared.com")
    }

}