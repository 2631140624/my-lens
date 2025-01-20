package com.shuzhi.opencv.ui.theme.mainPage

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.shuzhi.opencv.ui.theme.OpencvTheme
import com.shuzhi.opencv.ui.theme.navgation.AppNavgation
import com.shuzhi.opencv.ui.theme.navgation.Screen
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.utils.Converters
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale


class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    private var imageCapture: ImageCapture = ImageCapture.Builder().build()

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    val vm by viewModels<MainViewModel>()
    lateinit var  processedImage :MutableState<Bitmap>

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.isPermissionGranted = allPermissionsGranted()
        val isOpenCVLoaderInit = OpenCVLoader.initLocal()
//        Log.d("Main","$isOpenCVLoaderInit")
//
//        val img = loadImageFromAssets("img.png",assets)
//
//        val outimg = Mat()
//        Imgproc.GaussianBlur(img, outimg, Size(311.0, 311.0), 0.0)
        setContent {
            OpencvTheme {
                val navController = rememberAnimatedNavController()
                AppNavgation(
                    navController = navController,
                    appVm = vm,
                    imageCapture = imageCapture,
                    onTakePhotoClickd = {
                        takePictureForBitmap(this, imageCapture, onBitmapCaptured = {
                            vm.imageForCrop =it
                            navController.navigate(Screen.CropImagePage.route+"/-1")
                        })
                    },

                )
            }
        }
    }


//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//
//                    var canProcessedImagebeUse by remember {
//                        mutableStateOf(false)
//                    }
//                    if (vm.isPermissionGranted) {
//                        Box(modifier = Modifier.fillMaxSize()){
//                            CameraPreview( {
//                                Log.d("AAA","111")
//                                cpsCropImgView?.setImageToCrop(it.toBitmap())
//                                canProcessedImagebeUse = true
//
//                            }, imageCapture)
//                            if (canProcessedImagebeUse) {
//                                ComposeCropImageView(cropScreenViewModel)
//                            }
//                            Box(modifier = Modifier.align(Alignment.Center).size(50.dp).background(color = Color.Red).clickable {
//                                takePhoto()
//                            }){
//
//                            }
//                        }
//                    } else {
//                        ActivityCompat.requestPermissions(
//                            this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
//                        )
//                    }
////                    MatImage(outimg)
////                    Greeting("Android  isopencvInit:$isOpenCVLoaderInit")
//                }


    fun takePhotoAndSave() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.CHINA)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    Toast.makeText(baseContext, exc.message, Toast.LENGTH_SHORT).show()
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }


    fun takePictureForBitmap(context: Context, imageCapture: ImageCapture, onBitmapCaptured: (Bitmap) -> Unit) {
        val executor = ContextCompat.getMainExecutor(context)

        imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                // 将 ImageProxy 转换为 Bitmap
                val bitmap = imageProxy.toBitmap()

                // 回调返回 Bitmap
                onBitmapCaptured(bitmap.rotateBitmap(90))

                // 关闭 ImageProxy
                imageProxy.close()
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraX", "Capture failed", exception)
            }
        })
    }

    fun Bitmap.rotateBitmap(rotationDegrees: Int): Bitmap {
        if (rotationDegrees == 0) return this
        val matrix = Matrix().apply {
            postRotate(rotationDegrees.toFloat())
        }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            vm.isPermissionGranted = allPermissionsGranted()
//            for (i in permissions.indices) {
//                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                    Log.d("Permission", "${permissions[i]} granted")
//                    // 处理权限被授予的情况
//                } else {
//                    Log.d("Permission", "${permissions[i]} denied")
//                    // 处理权限被拒绝的情况
//                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
//                        // 用户勾选了 "不再询问"
//                        Log.d("Permission", "${permissions[i]} 不再询问")
//                    }
//                }
//            }
        }
    }

}


@Composable
fun MatImage(mat: Mat) {

    // 使用 Image 显示 Bitmap
    Image(
        painter = BitmapPainter(matToBitmap(mat).asImageBitmap()),
        contentDescription = "Processed Image",
        // modifier = Modifier.fillMaxSize()
    )
}


fun matToBitmap(mat: Mat): Bitmap {
    val bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(mat, bitmap)
    return bitmap
}

/**
 * 从 assets 加载图片并转换为 OpenCV 的 Mat 对象
 */
private fun loadImageFromAssets(fileName: String, assetManager: AssetManager): Mat? {
    return try {
        // 从 assets 打开文件输入流
        val inputStream = assetManager.open(fileName)

        // 将输入流解码为 Bitmap
        val bitmap = BitmapFactory.decodeStream(inputStream)

        // 转换 Bitmap 为 OpenCV 的 Mat
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)
        mat // 返回 Mat 对象
    } catch (e: IOException) {
        e.printStackTrace()
        null // 返回 null 表示加载失败
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OpencvTheme {
        Greeting("Android")
    }
}
// 边缘检测与裁剪
private fun detectAndCropDocument(src: Mat): Mat {
    val gray = Mat()
    val blurred = Mat()
    val edges = Mat()

    // 转灰度
    Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY)

    // 高斯模糊
    Imgproc.GaussianBlur(gray, blurred, Size(5.0, 5.0), 0.0)

    // 边缘检测
    Imgproc.Canny(blurred, edges, 50.0, 150.0)

    // 找轮廓
    val contours: List<MatOfPoint> = ArrayList()
    Imgproc.findContours(edges, contours, Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE)

    // 按面积排序，取最大轮廓
    contours.sortedByDescending { Imgproc.contourArea(it) }
    val largestContour = contours.firstOrNull() ?: return src

    // 近似多边形
    val approxCurve = MatOfPoint2f()
    val largestContour2f = MatOfPoint2f(*largestContour.toArray())
    Imgproc.approxPolyDP(
        largestContour2f,
        approxCurve,
        0.02 * Imgproc.arcLength(largestContour2f, true),
        true
    )

    // 检查是否为四边形
    if (approxCurve.total() != 4L) {
        return src
    }

    // 获取四边形点
    val points = approxCurve.toArray().toList()
    val sortedPoints = sortPoints(points)

    // 目标尺寸
    val outputSize = Size(500.0, 700.0)
    val dstPoints = listOf(
        Point(0.0, 0.0),
        Point(outputSize.width - 1, 0.0),
        Point(outputSize.width - 1, outputSize.height - 1),
        Point(0.0, outputSize.height - 1)
    )

    // 透视变换
    val perspectiveTransform = Imgproc.getPerspectiveTransform(Converters.vector_Point2f_to_Mat(sortedPoints), Converters.vector_Point2f_to_Mat(dstPoints))
    val cropped = Mat()
    Imgproc.warpPerspective(src, cropped, perspectiveTransform, outputSize)

    return cropped
}

// 对点进行排序
private fun sortPoints(points: List<Point>): List<Point> {
    val sorted = points.sortedBy { it.y }
    val top = sorted.take(2).sortedBy { it.x }
    val bottom = sorted.takeLast(2).sortedBy { it.x }
    return listOf(top[0], top[1], bottom[1], bottom[0])
}