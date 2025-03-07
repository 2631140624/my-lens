package com.shuzhi.opencv.ui.theme.mlkit.ocr

import android.graphics.Bitmap
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognizer
import com.shuzhi.opencv.ui.theme.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface OcrRepository {
    suspend fun recognizeText(bitmap: Bitmap): UiState<Text>
}

class OcrRepositoryImpl @Inject constructor(
    private val textRecognizer: TextRecognizer
) : OcrRepository {

    override suspend fun recognizeText(bitmap: Bitmap): UiState<Text> = withContext(Dispatchers.IO) {
        return@withContext try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val result = textRecognizer.process(image).await()
            UiState.Success(result)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Unknown error")
        }
    }
}

// 扩展函数：将 Task 转为协程
private suspend fun <T> Task<T>.await(): T {
    return suspendCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(task.result)
            } else {
                continuation.resumeWithException(task.exception ?: Exception("Unknown error"))
            }
        }
    }
}