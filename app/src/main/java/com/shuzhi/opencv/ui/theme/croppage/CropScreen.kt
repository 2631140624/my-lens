package com.shuzhi.opencv.ui.theme.croppage

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.shuzhi.opencv.ui.theme.app.OpenCvApp
import com.shuzhi.opencv.ui.theme.mainPage.ComposeCropImageView
import com.shuzhi.opencv.ui.theme.mainPage.MainViewModel

/**
 *@author :yinxiaolong
 *@describe : com.shuzhi.opencv.ui.theme.croppage
 *@date :2025-01-19 10:52
 */

@Composable
fun CropScreen(
    appVm: MainViewModel,
    navController: NavHostController,
    viewModel: CropScreenViewModel,
    index: Int
){
    DisposableEffect(Unit) {
        println("CropScreen ON DisposableEffect")
        appVm.isGestureEnable = false
        onDispose {
            println("CropScreen onDispose")
            appVm.isGestureEnable = true
        }
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally
    , modifier = Modifier
            .background(Color.Black)
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(3.dp)) {
        ComposeCropImageView(viewModel, appVm)
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, top = 20.dp)
        ){
            Text("取消", modifier = Modifier.clickable {
                navController.popBackStack()
            }, color = Color.White)
            Text("确定", modifier = Modifier.clickable {
                //确定后加入队列
                appVm.imageForCrop = viewModel.cropImageView?.crop()
                if (index!=-1){
                    appVm.imageCroped[index] = appVm.imageForCrop!!
                    navController.popBackStack()
                    return@clickable
                }
                appVm.imageForCrop?.let {
                    appVm.imageCroped.add(it)
                    navController.popBackStack()
                    Toast.makeText(OpenCvApp.appContext,"添加成功 当前${appVm.imageCroped.size}张图片已经添加",Toast.LENGTH_SHORT).show()
                }
            },color = Color.White)
        }
    }
}