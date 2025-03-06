package com.shuzhi.opencv.ui.theme.photo

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import java.io.InputStream
import kotlin.math.PI
import kotlin.math.abs

/**
 *@author :yinxiaolong
 *@describe : com.shuzhi.opencv.ui.theme.photo
 *@date :2025-01-21 14:38
 */
object PhotoManager {

    @Composable
    fun PickPhoto(onPicked:(List<Bitmap>)->Unit){
        val context = LocalContext.current

        // 创建一个用于处理图像选择的 ActivityResultLauncher
        val getImage = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
               onPicked(uris.map { uriToBitmap(context.contentResolver,it)!! })
        }
        Button(onClick = {
            getImage.launch("image/*")
        }) {
            Text("相册")
        }
    }
    public fun uriToBitmap(contentResolver: ContentResolver, imageUri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Log.e("URI to Bitmap", "Failed to decode image", e)
            null
        }
    }
    @Composable
    fun ImageViewer(images:List<Bitmap>,index :Int,onExit:()->Unit) {

        // Pager state 用于控制当前页面
        val pagerState = rememberPagerState(initialPage = index, pageCount ={ images.size})
        var bgAlpha by remember { mutableStateOf(1f) }
        // 显示预览界面
        Box(modifier = Modifier.fillMaxSize().background(Color.Transparent.copy(alpha =bgAlpha ))) {
            // 图片滑动展示
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize().nestedScroll( rememberNestedScrollInteropConnection() )
            ) { page ->
                val painter = BitmapPainter(images[page].asImageBitmap())
                // 处理图片缩放的 state
                var scale by remember { mutableStateOf(1f) }
                var offset by remember { mutableStateOf(Offset(0f, 0f)) }
                val height = LocalConfiguration.current.screenHeightDp
                var dragDistance by remember { mutableStateOf(0f) }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {


                            detectTransformGesturesUnConsume (true,{ _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(0.5f, 3f)
                                // 更新偏移量
                                offset = Offset(
                                    offset.x + pan.x,
                                    offset.y + pan.y
                                )
                                bgAlpha = 1- offset.y/height.toFloat()
                            }, onCancel = {
                                    scale = 1f
                                    offset = Offset(0f,0f)
                                    bgAlpha = 1f
                                }
                            )
                            detectVerticalDragGestures { _, dragAmount ->
                                // 记录滑动的距离
                                dragDistance += dragAmount

                                // 判断快速滑动或者滑动超过屏幕1/3
                                if (dragDistance > height / 3 ) {
                                    // 滑动超过 1/3 或者速度非常快，则关闭图片预览
                                    onExit()
                                }
                            }


                        }
                        .padding(20.dp)
                ) {
                    Image(
                        painter = painter,
                        contentDescription = "Image $page",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxSize()
                            .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                           // translationX = offset.x,
                            translationY = offset.y
                        )
                    )
                }
            }

            // 图片关闭按钮
            IconButton(
                onClick = { /* 执行关闭操作 */ },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "Close"
                )
            }
        }
    }

    /**
     * 解决detectTransformGestures 会消费事件 的问题
     * https://stackoverflow.com/questions/71156016/how-to-use-detecttransformgestures-but-not-consuming-all-pointer-event
     *
     */
    suspend fun PointerInputScope.detectTransformGesturesUnConsume(
        panZoomLock: Boolean = false,
        onGesture: (centroid: Offset, pan: Offset, zoom: Float, rotation: Float) -> Unit,
        onCancel: (() -> Unit)? = null
    ) {
        awaitEachGesture {
            var rotation = 0f
            var zoom = 1f
            var pan = Offset.Zero
            var pastTouchSlop = false
            val touchSlop = viewConfiguration.touchSlop
            var lockedToPanZoom = false
            
            awaitFirstDown(requireUnconsumed = false)
            do {
                val event = awaitPointerEvent()

                val canceled = event.changes.fastAny { it.isConsumed }
                if (event.changes[0].changedToUp()){
                    if (onCancel != null) {
                        onCancel()
                    }
                }
                if (!canceled) {
                    val zoomChange = event.calculateZoom()
                    val rotationChange = event.calculateRotation()
                    val panChange = event.calculatePan()

                    if (!pastTouchSlop) {
                        zoom *= zoomChange
                        rotation += rotationChange
                        pan += panChange

                        val centroidSize = event.calculateCentroidSize(useCurrent = false)
                        val zoomMotion = abs(1 - zoom) * centroidSize
                        val rotationMotion = abs(rotation * PI.toFloat() * centroidSize / 180f)
                        val panMotion = pan.getDistance()

                        if (zoomMotion > touchSlop ||
                            rotationMotion > touchSlop ||
                            panMotion > touchSlop
                        ) {
                            pastTouchSlop = true
                            lockedToPanZoom = panZoomLock && rotationMotion < touchSlop
                        }
                    }

                    if (pastTouchSlop) {
                        val centroid = event.calculateCentroid(useCurrent = false)
                        val effectiveRotation = if (lockedToPanZoom) 0f else rotationChange
                        if (effectiveRotation != 0f ||
                            zoomChange != 1f ||
                            panChange != Offset.Zero
                        ) {
                            onGesture(centroid, panChange, zoomChange, effectiveRotation)
                        }
//                        event.changes.fastForEach {
//                            if (it.positionChanged()) {
//                                it.consume()
//                            }
//                        }
                    }
                }
            } while (!canceled && event.changes.fastAny { it.pressed })
        }
    }
}