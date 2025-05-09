package com.shuzhi.opencv.ui.theme.pdf.pdfpreview.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.content.FileProvider
import com.shuzhi.opencv.ui.theme.pdf.pdfpreview.model.DocumentItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface DocumentRepository {
    suspend fun loadLocalDocuments(): List<DocumentItem>
}
@Singleton
class DocumentRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher
) : DocumentRepository {

    override suspend fun loadLocalDocuments(): List<DocumentItem> = withContext(ioDispatcher) {
        val pdfDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

        val outputFile = File(pdfDir, "dragonLens")

        outputFile.listFiles { file ->
            file.isFile && file.extension.equals("pdf", true)
        }?.mapNotNull { file ->
            try {
                Log.d("DocumentRepositoryImpl", "Loading document: ${file.name}")
                DocumentItem(
                    uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    ),
                    fileName = file.nameWithoutExtension,
                    timestamp = file.lastModified(),
                    thumbnail = generateThumbnail(file)
                )
            } catch (e: Exception) {
                println("Error loading document: ${e.message}")
                null
            }
        }?.sortedByDescending { it.timestamp } ?: emptyList()
    }

    private fun generateThumbnail(file: File): Bitmap? {
        return try {
            val renderer = PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
            val page = renderer.openPage(0)

            val bitmap = Bitmap.createBitmap(
                page.width / 2,
                page.height / 2,
                Bitmap.Config.ARGB_8888
            ).apply {
                page.render(this, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            }

            page.close()
            renderer.close()
            bitmap
        } catch (e: Exception) {
            println("Error generating thumbnail: ${e.message}")
            null
        }
    }
}