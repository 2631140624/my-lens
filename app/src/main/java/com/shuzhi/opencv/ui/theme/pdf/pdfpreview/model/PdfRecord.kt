package com.shuzhi.opencv.ui.theme.pdf.pdfpreview.model

import cn.leancloud.LCObject

class PdfRecord : LCObject("PdfRecord") {
    companion object {
        val USER_ID = "userId"
        val NAME = "name"
        val DATE = "date"
        val PDF_Preview_Image = "pdfPreviewImage"
        val PDF_URL = "pdfUrl"
    }
}