package com.shuzhi.opencv.ui.theme.pdf

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

import android.os.Environment
import android.util.Log
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.CompressionConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.styledxmlparser.jsoup.nodes.Element
import com.shuzhi.opencv.ui.theme.util.showAndroidToast
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 *@author :yinxiaolong
 *@describe : com.shuzhi.opencv.ui.theme.pdf
 *@date :2025-01-20 14:54
 */
object PdfManager {
    fun saveBitmapsAsPdf(bitmaps: List<Bitmap>, outputFileName: String,documentsDir:File,isForCloud:Boolean = false) :File?{
        // 获取 Documents 目录路径
        val appFolder = File(documentsDir,if (isForCloud)"cache" else "dragonLens")
        if (!appFolder.exists()) {
            appFolder.mkdirs()  // 确保目录存在
        }

        // 创建输出文件
        val outputFile = File("$appFolder", "$outputFileName.pdf")
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(outputFile)

            // 创建 PdfDocument 对象
//            val pdfDocument = android.graphics.pdf.PdfDocument()

            // 使用 iText 的高级压缩
            val writer = PdfWriter(outputFile).apply {
                compressionLevel = CompressionConstants.DEFAULT_COMPRESSION
            }
            val pdfDocument = PdfDocument(writer).apply {
                defaultPageSize = PageSize.A4 // 标准化页面尺寸
            }
            val document = Document(pdfDocument, PageSize.A4)
            // 遍历 Bitmap 列表
            for (bitmap in bitmaps) {
                // 将 Bitmap 转换为 iText 支持的 Image 对象
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream)
                val imageData = ImageDataFactory.create(byteArrayOutputStream.toByteArray())
                val image = Image(imageData)

                // 缩放图片适配页面宽度（保持宽高比）
                image.scaleToFit(PageSize.A4.width , PageSize.A4.height )  // 72f 为页边距
                //image.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER) // 水平居中

                // 添加图片到 PDF
                document.add(image)
               // document.add(Paragraph("\n"))  // 添加换行分隔符
            }
//            // 遍历 Bitmap 列表并将每张图片绘制为 PDF 页面
//            for (bitmap in bitmaps) {
//                // 创建一个页面
//                val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, pdfDocument.pages.size + 1).create()
//                val page = pdfDocument.startPage(pageInfo)
//
//                // 在页面上绘制 Bitmap 图片
//                val canvas: Canvas = page.canvas
//                val paint = Paint()
//                canvas.drawBitmap(bitmap, 0f, 0f, paint)
//
//                // 完成页面
//                pdfDocument.finishPage(page)
//            }
//
//            // 写入文件
//            pdfDocument.writeTo(fileOutputStream)

            // 关闭 PdfDocument
            pdfDocument.close()
            document.close()
            writer.close()
           // showAndroidToast("PDF文件已保存到: ${outputFile.absolutePath}")
            Log.d("PDF", "PDF文件已保存到: ${outputFile.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
            return null
           // showAndroidToast("保存 PDF 失败: ${e.message}")
            Log.e("PDF", "保存 PDF 失败: ${e.message}")
        } finally {
            fileOutputStream?.close()
        }
        return outputFile
    }
}