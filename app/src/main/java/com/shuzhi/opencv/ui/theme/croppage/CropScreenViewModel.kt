package com.shuzhi.opencv.ui.theme.croppage

import android.util.Log
import androidx.lifecycle.ViewModel
import me.pqpo.smartcropperlib.view.CropImageView
import kotlin.random.Random

/**
 *@author :yinxiaolong
 *@describe : com.shuzhi.opencv.ui.theme.croppage
 *@date :2025-01-19 10:56
 */
class CropScreenViewModel :ViewModel() {
    companion object{
        const val TAG = "CropScreenViewModel"
    }

    var cropImageView :CropImageView? = null
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG,"CropScreenViewModel onCleared")
    }
}