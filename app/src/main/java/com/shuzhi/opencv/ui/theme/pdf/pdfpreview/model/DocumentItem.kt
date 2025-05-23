package com.shuzhi.opencv.ui.theme.pdf.pdfpreview.model

import android.graphics.Bitmap
import android.net.Uri

data class DocumentItem(
    val uri: Uri,
    val fileName: String,
    val timestamp: Long,
    val thumbnail: Bitmap? = null,
    val thumbnailUri: String? = null
)