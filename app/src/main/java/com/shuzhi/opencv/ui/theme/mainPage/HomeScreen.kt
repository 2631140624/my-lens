package com.shuzhi.opencv.ui.theme.mainPage

import androidx.camera.core.ImageCapture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.shuzhi.opencv.ui.theme.navgation.Screen
import com.shuzhi.opencv.ui.theme.photo.PhotoManager.PickPhoto
import com.shuzhi.opencv.ui.theme.util.LocalToastHostState
import kotlinx.coroutines.launch

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
    val toastHostState = LocalToastHostState.current
    val scope = rememberCoroutineScope()
    Box(contentAlignment = Alignment.BottomCenter){
        Text(text = "Home Screen")
        CameraPreview({

        },imageCapture)

        Row(horizontalArrangement = Arrangement.SpaceAround) {
            PickPhoto(){
                appVm.imageCroped.addAll(it)
            }

            Button(onClick = {
                onTakePhotoClick()
//            navController.navigate(Screen.CropImagePage.route)
            }) {
                Text("take photo")
            }
            Button(onClick = {
                if (appVm.imageCroped.isNotEmpty()) {
                    navController.navigate(Screen.SelectedImagePage.route)
                }else{
                    scope.launch {
                        toastHostState.showToast(message = "请先选择或拍摄图片")
                    }
                }
                //todo goto 已选择的图片页面
            }) {
                Row {
                    Text("${appVm.imageCroped.size}")
                    Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null)
                }

            }
        }
    }
}