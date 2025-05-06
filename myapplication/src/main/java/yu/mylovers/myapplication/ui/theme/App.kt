package yu.mylovers.myapplication.ui.theme

import android.app.Application
import com.shuzhi.opencv.ui.theme.app.OpenCvApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App:Application() {
    override fun onCreate() {
        super.onCreate()
        OpenCvApp.doOnApp(this)
    }
}