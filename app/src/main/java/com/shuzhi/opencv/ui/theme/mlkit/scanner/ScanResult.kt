package com.shuzhi.opencv.ui.theme.mlkit.scanner

import android.net.Uri

data class ScanResult(
    val imageUris: List<Uri> = emptyList(),
    val pdfUri: Uri? = null
)