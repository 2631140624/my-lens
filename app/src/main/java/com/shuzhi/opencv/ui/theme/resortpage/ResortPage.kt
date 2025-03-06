package com.shuzhi.opencv.ui.theme.resortpage

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 *@author :yinxiaolong
 *@describe : com.shuzhi.opencv.ui.theme.resortpage
 *@date :2025-01-21 12:54
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraggablePhotoGrid(photoList: SnapshotStateList<Bitmap>, onReorder: (List<Bitmap>) -> Unit) {
    //var photoList by remember { mutableStateOf(photos) }
    var draggingIndex by remember { mutableStateOf(-1) }
    var dragOffset by remember { mutableStateOf(Offset(0f, 0f)) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            //.padding(8.dp)
            .background(Color.Black)
    ) {
        items(photoList.size) { index ->
            val isDragging = index == draggingIndex

            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(4.dp)
                    .then(
                        if (isDragging) Modifier
                            .graphicsLayer {
                                scaleX = 1.1f
                                scaleY = 1.1f
                                translationX = dragOffset.x
                                translationY = dragOffset.y
                                alpha = 0.7f
                            }
                        else Modifier
                    )
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { offset ->
                                if (photoList.size>1) {
                                    draggingIndex = index
                                }
                            },
                            onDrag = { change, offset ->
                                if (draggingIndex == -1) return@detectDragGesturesAfterLongPress
                                dragOffset = Offset(dragOffset.x + offset.x, dragOffset.y + offset.y)
                                change.consume()

                                // 这里可以添加排序的逻辑，更新图片顺序
                                val targetIndex = calculateTargetIndex(
                                    photoList.size,
                                    draggingIndex,
                                    dragOffset
                                )
                                if (targetIndex != draggingIndex && targetIndex in photoList.indices) {
                                    photoList.swap(draggingIndex, targetIndex)
                                    draggingIndex = targetIndex
                                    dragOffset = Offset(0f, 0f)
                                }
                            },
                            onDragEnd = {
                                draggingIndex = -1
                                dragOffset = Offset(0f, 0f)  // 恢复偏移量
                                onReorder(photoList)
                            },
                            onDragCancel = {
                                draggingIndex = -1
                                dragOffset = Offset(0f, 0f)
                            }
                        )
                    }
            ) {
                Box(contentAlignment = Alignment.BottomCenter) {
                    Image(
                        painter = BitmapPainter(photoList[index].asImageBitmap()),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        text = "${index+1}",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Cursive,
                        modifier = Modifier.padding(4.dp).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp)).padding(2.dp)
                    )

                }
            }
        }
    }
}

fun calculateTargetIndex(total: Int, currentIndex: Int, offset: Offset): Int {
    // 根据拖拽的方向和距离计算目标索引
    val offsetX = offset.x.toInt()
    val offsetY = offset.y.toInt()
    val columnCount = 3 // 假设一行展示3列
    val row = currentIndex / columnCount
    val column = currentIndex % columnCount
    val newRow = (row + offsetY / 200).coerceIn(0, total / columnCount)
    val newColumn = (column + offsetX / 200).coerceIn(0, columnCount - 1)
    return newRow * columnCount + newColumn
}

fun <T> SnapshotStateList<T>.swap(fromIndex: Int, toIndex: Int) {
    val temp = this[fromIndex]
    this[fromIndex] = this[toIndex]
    this[toIndex] = temp
}
