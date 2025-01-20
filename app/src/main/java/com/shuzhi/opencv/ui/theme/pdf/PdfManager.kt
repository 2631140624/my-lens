package com.shuzhi.opencv.ui.theme.pdf

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import com.shuzhi.opencv.ui.theme.util.showToast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 *@author :yinxiaolong
 *@describe : com.shuzhi.opencv.ui.theme.pdf
 *@date :2025-01-20 14:54
 */
object PdfManager {
    fun saveBitmapsAsPdf(bitmaps: List<Bitmap>, outputFileName: String) {
        // 获取 Documents 目录路径
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val appFolder = File(documentsDir,"dragonLens")
        if (!appFolder.exists()) {
            appFolder.mkdirs()  // 确保目录存在
        }

        // 创建输出文件
        val outputFile = File("$appFolder", "$outputFileName.pdf")
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(outputFile)

            // 创建 PdfDocument 对象
            val pdfDocument = PdfDocument()

            // 遍历 Bitmap 列表并将每张图片绘制为 PDF 页面
            for (bitmap in bitmaps) {
                // 创建一个页面
                val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, pdfDocument.pages.size + 1).create()
                val page = pdfDocument.startPage(pageInfo)

                // 在页面上绘制 Bitmap 图片
                val canvas: Canvas = page.canvas
                val paint = Paint()
                canvas.drawBitmap(bitmap, 0f, 0f, paint)

                // 完成页面
                pdfDocument.finishPage(page)
            }

            // 写入文件
            pdfDocument.writeTo(fileOutputStream)

            // 关闭 PdfDocument
            pdfDocument.close()
            showToast("PDF文件已保存到: ${outputFile.absolutePath}")
            Log.d("PDF", "PDF文件已保存到: ${outputFile.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
            showToast("保存 PDF 失败: ${e.message}")
            Log.e("PDF", "保存 PDF 失败: ${e.message}")
        } finally {
            fileOutputStream?.close()
        }
    }
}