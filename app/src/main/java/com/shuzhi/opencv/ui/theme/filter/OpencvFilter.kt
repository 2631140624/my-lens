package com.shuzhi.opencv.ui.theme.filter

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

/**
 *@author :yinxiaolong
 *@describe : com.shuzhi.opencv.ui.theme.filter
 *@date :2025-01-20 19:02
 */
object OpencvFilter {

    fun filter1(bitmap: Bitmap,style:Int) :Bitmap{
        val matImage = Mat()
        Utils.bitmapToMat(bitmap, matImage)

        // 转换为灰度图
        val grayMat = Mat()
        Imgproc.cvtColor(matImage, grayMat, Imgproc.COLOR_BGR2GRAY)

        // 创建一个空的输出 Mat
        val colorMat = Mat()

        // 应用 COLORMAP_JET 色彩图
        Imgproc.applyColorMap(grayMat, colorMat, style)

        // 将处理后的 Mat 转回 Bitmap
        val colorBitmap = Bitmap.createBitmap(colorMat.cols(), colorMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(colorMat, colorBitmap)

        return colorBitmap
    }

    fun filter2(bitmap: Bitmap):Bitmap{
        // CLAHE 算法
        val matImage = Mat()
        Utils.bitmapToMat(bitmap, matImage)

        // 将图像转换为灰度图像
        val grayMat = Mat()
        Imgproc.cvtColor(matImage, grayMat, Imgproc.COLOR_BGR2GRAY)

        // 创建 CLAHE 对象
        val clahe = Imgproc.createCLAHE()

        // 设置 CLAHE 参数
        clahe.clipLimit = 3.0   // 限制对比度
       // clahe.tileGridSize = Size(8.0, 8.0)   // 设置图像分块的大小

        // 应用 CLAHE 增强图像
        val enhancedMat = Mat()
        clahe.apply(grayMat, enhancedMat)

        // 将增强后的图像转换为 Bitmap
        val enhancedBitmap = Bitmap.createBitmap(enhancedMat.cols(), enhancedMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(enhancedMat, enhancedBitmap)

        return  enhancedBitmap
    }
}