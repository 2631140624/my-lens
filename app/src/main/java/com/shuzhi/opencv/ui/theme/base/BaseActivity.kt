package com.shuzhi.opencv.ui.theme.base

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import java.lang.ref.WeakReference

/**
 *@author :yinxiaolong
 *@describe : com.shuzhi.opencv.ui.theme.base
 *@date :2025-01-21 22:56
 */
abstract class BaseActivity :ComponentActivity() {

    companion object {
        lateinit var currentActivity: WeakReference<Activity>
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        currentActivity = WeakReference(this@BaseActivity)
    }
}