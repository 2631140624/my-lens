package com.shuzhi.opencv.ui.theme.mlkit.ocr

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.text.Text
import com.shuzhi.opencv.ui.theme.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OcrViewModel @Inject constructor(
    private val ocrRepository: OcrRepository
) : ViewModel() {
    companion object {
        const val TAG = "OcrViewModel"
    }

    private val _ocrState = mutableStateOf<UiState<Text>>(UiState.Idle)
    val ocrState: State<UiState<Text>> = _ocrState

    var bitmapFromLastPage by mutableStateOf<Bitmap?>(null)

    val boxes = mutableListOf<BoundingBoxInfo>()
    fun processImage(bitmap: Bitmap) {
        _ocrState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = ocrRepository.recognizeText(bitmap)) {
                is UiState.Success -> {
                    _ocrState.value = UiState.Success(result.data)
                  //  processText((_ocrState.value as UiState.Success<Text>).data)
                }

                is UiState.Error -> {
                    _ocrState.value = UiState.Error(result.message ?: "Unknown error")
                }

                UiState.Loading -> TODO()
                UiState.Idle -> TODO()
            }
        }
    }

    fun processText(
        result: Text
    ) {
        boxes.clear()
        result.textBlocks.forEach { block ->
            block.boundingBox?.let {
                boxes.add(BoundingBoxInfo(
                    it.toComposeRect(),
                    block.text,  // 添加区块级文本
                    "block"
                ))
            }

//                block.lines.forEach { line ->
//                line.boundingBox?.let {
//                    boxes.add(BoundingBoxInfo(
//                        it.toComposeRect(),
//                        line.text,  // 添加行级文本
//                        "line"
//                    ))
//                }
//
//                    line.elements.forEach { element ->
//                        element.boundingBox?.let {
//                            boxes.add(
//                                BoundingBoxInfo(
//                                    it.toComposeRect(),
//                                    element.text,  // 添加元素级文本
//                                    "element"
//                                )
//                            )
//                        }
//                    }
//                }
            }
        }

        fun processText(result: Text, doOnEveryElement: (String) -> Unit) {
            val resultText = result.text
            Log.d(TAG, resultText)
            for (block in result.textBlocks) {
                val blockText = block.text
                val blockCornerPoints = block.cornerPoints
                val blockFrame = block.boundingBox
                for (line in block.lines) {
                    val lineText = line.text
                    val lineCornerPoints = line.cornerPoints
                    val lineFrame = line.boundingBox
                    for (element in line.elements) {
                        val elementText = element.text
                        val elementCornerPoints = element.cornerPoints
                        val elementFrame = element.boundingBox
                    }
                }
            }
        }
    }





// Android Rect 转 Compose Rect 的扩展函数
fun android.graphics.Rect.toComposeRect(): Rect {
    return Rect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
}


data class BoundingBoxInfo(
    val rect: Rect,
    val text: String,   // 新增文本字段
    val type: String    // "block", "line" 或 "element" 用于区分层级
)