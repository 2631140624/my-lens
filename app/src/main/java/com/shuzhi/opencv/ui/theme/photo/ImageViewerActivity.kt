package com.shuzhi.opencv.ui.theme.photo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.shuzhi.opencv.ui.theme.OpencvTheme
import com.shuzhi.opencv.ui.theme.app.OpenCvApp
import com.shuzhi.opencv.ui.theme.base.BaseActivity
import com.shuzhi.opencv.ui.theme.mainPage.MainViewModel
import com.shuzhi.opencv.ui.theme.photo.PhotoManager.ImageViewer

/**
 *@author :yinxiaolong
 *@describe : com.shuzhi.opencv.ui.theme.photo
 *@date :2025-01-21 22:48
 */
class ImageViewerActivity :BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val index = intent.getIntExtra("index",0)
        setContent {
            OpencvTheme {
                ImageViewer(OpenCvApp.sharedViewModel!!.imageCroped,index){
                    finish()
                }
            }
        }
    }
}