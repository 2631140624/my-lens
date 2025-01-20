package com.shuzhi.opencv.ui.theme.mainPage

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.shuzhi.opencv.ui.theme.croppage.CropScreenViewModel
import me.pqpo.smartcropperlib.view.CropImageView

/**
 *@author :yinxiaolong
 *@describe : com.shuzhi.opencv.ui.theme.mainPage
 *@date :2025-01-16 19:20
 */
@Composable
fun CameraPreview(onImageCaptured: (ImageProxy) -> Unit,imageCapture: ImageCapture) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            val executor = ContextCompat.getMainExecutor(ctx)
            var frame = 0
            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also { imageAnalysis ->
                    imageAnalysis.setAnalyzer(executor) {
                        //todo 图片框选分析
                        if (frame/60>1) {
                            onImageCaptured(it)
                            frame = 0
                        }else{
                            frame++
                        }
                        it.close()
                    }
                }
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
               // val imageCapture = ImageCapture.Builder().build()


                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture,
                        imageAnalyzer
                    )
                } catch (exc: Exception) {
                    exc.printStackTrace()
                }
            }, executor)

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}


@Composable
fun ComposeCropImageView(vm: CropScreenViewModel, appVm: MainViewModel) {
    AndroidView(factory = { ctx ->
        val cropImageView = CropImageView(ctx).apply {
            this.setImageToCrop(appVm.imageForCrop)
        }
        vm.cropImageView = cropImageView
        cropImageView
    }, modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp))

}
