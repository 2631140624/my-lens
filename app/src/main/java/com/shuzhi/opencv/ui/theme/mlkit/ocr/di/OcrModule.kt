package com.shuzhi.opencv.ui.theme.mlkit.ocr.di



import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.shuzhi.opencv.ui.theme.mlkit.ocr.OcrRepository
import com.shuzhi.opencv.ui.theme.mlkit.ocr.OcrRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OcrModule {

    @Provides
    @Singleton
    fun provideTextRecognizer(): TextRecognizer {
        return TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
    }
}

// 文件名：OcrModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class OcrAbsModule {

    // 绑定接口到实现类（需要 OcrRepositoryImpl 构造函数有 @Inject）
    @Binds
    abstract fun bindOcrRepository(impl: OcrRepositoryImpl): OcrRepository
}

