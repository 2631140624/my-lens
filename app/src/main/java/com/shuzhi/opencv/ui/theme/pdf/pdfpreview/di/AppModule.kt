package com.shuzhi.opencv.ui.theme.pdf.pdfpreview.di

import android.content.Context
import com.shuzhi.opencv.ui.theme.pdf.pdfpreview.repository.DocumentRepository
import com.shuzhi.opencv.ui.theme.pdf.pdfpreview.repository.DocumentRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

// di/AppModule.kt
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideDocumentRepository(
        @ApplicationContext context: Context,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): DocumentRepository = DocumentRepositoryImpl(context, dispatcher)
}

// di/Qualifiers.kt
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class IoDispatcher