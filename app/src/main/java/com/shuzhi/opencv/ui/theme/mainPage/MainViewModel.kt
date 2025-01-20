package com.shuzhi.opencv.ui.theme.mainPage

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 *@author :yinxiaolong
 *@describe : com.shuzhi.opencv.ui.theme.mainPage
 *@date :2025-01-16 19:38
 */
class MainViewModel : ViewModel() {
    var isPermissionGranted  by mutableStateOf(false)
    var imageForCrop :Bitmap ? = null
    val imageCroped :MutableList<Bitmap> = mutableStateListOf()
}