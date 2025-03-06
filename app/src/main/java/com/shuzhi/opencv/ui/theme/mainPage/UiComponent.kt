package com.shuzhi.opencv.ui.theme.mainPage

import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.shuzhi.opencv.ui.theme.croppage.CropScreenViewModel
import com.shuzhi.opencv.ui.theme.photo.PhotoManager.detectTransformGesturesUnConsume
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
    var currentZoom by remember { mutableFloatStateOf(1f) }
    var camera by remember { mutableStateOf<Camera?>(null) }
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
                   camera = cameraProvider.bindToLifecycle(
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
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {

            detectTransformGesturesUnConsume (onGesture = { _, _, zoom, _ ->
                val newZoom = currentZoom * zoom
                // 限制缩放范围
                val clampedZoom = newZoom.coerceIn(1f, 3f)

                camera?.cameraControl?.setZoomRatio(clampedZoom)
                currentZoom = clampedZoom
            })
        }
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
    }, modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f).padding(horizontal = 10.dp))

}
