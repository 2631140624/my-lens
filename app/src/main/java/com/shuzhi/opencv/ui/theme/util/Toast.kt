package com.shuzhi.opencv.ui.theme.util

import android.widget.Toast
import com.shuzhi.opencv.ui.theme.app.OpenCvApp

/**
 *@author :yinxiaolong
 *@describe : com.shuzhi.opencv.ui.theme.util
 *@date :2025-01-20 15:04
 */
fun showToast(msg:String){
    Toast.makeText(OpenCvApp.appContext,msg,Toast.LENGTH_SHORT).show()
}