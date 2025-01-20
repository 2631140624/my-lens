package com.shuzhi.opencv.ui.theme.mainPage

import androidx.camera.core.ImageCapture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.shuzhi.opencv.ui.theme.navgation.Screen

/**
 *@author :yinxiaolong
 *@describe : com.shuzhi.opencv.ui.theme.mainPage
 *@date :2025-01-19 10:39
 */
@Composable
fun HomeScreen(
    appVm: MainViewModel,
    navController: NavController,
    imageCapture: ImageCapture,
    onTakePhotoClick: () -> Unit,
    homeScreenViewModel: HomeScreenViewModel
) {
    Box(contentAlignment = Alignment.BottomCenter){
        Text(text = "Home Screen")
        CameraPreview({

        },imageCapture)

        Row(horizontalArrangement = Arrangement.SpaceAround) {
            Button(onClick = {
                onTakePhotoClick()
//            navController.navigate(Screen.CropImagePage.route)
            }) {
                Text("take photo and Go to Crop page")
            }
            Button(onClick = {
                navController.navigate(Screen.SelectedImagePage.route)
                //todo goto 已选择的图片页面
            }) {
                Text("${appVm.imageCroped.size}")
            }
        }
    }
}