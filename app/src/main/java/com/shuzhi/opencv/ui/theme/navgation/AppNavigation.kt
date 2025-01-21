package com.shuzhi.opencv.ui.theme.navgation

import androidx.camera.core.ImageCapture
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandIn
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.shuzhi.opencv.ui.theme.croppage.CropScreen
import com.shuzhi.opencv.ui.theme.croppage.CropScreenViewModel
import com.shuzhi.opencv.ui.theme.filter.OpencvFilter
import com.shuzhi.opencv.ui.theme.mainPage.HomeScreen
import com.shuzhi.opencv.ui.theme.mainPage.HomeScreenViewModel
import com.shuzhi.opencv.ui.theme.mainPage.MainViewModel
import com.shuzhi.opencv.ui.theme.mainPage.rotateBitmap
import com.shuzhi.opencv.ui.theme.pdf.PdfManager
import com.shuzhi.opencv.ui.theme.selectedpage.SelectedImageScreen
import com.shuzhi.opencv.ui.theme.selectedpage.SelectedImageViewModel
import org.opencv.imgproc.Imgproc

/**
 *@author :yinxiaolong
 *@describe : com.shuzhi.opencv.ui.theme.Navgation  app 路由管理
 *@date :2025-01-19 10:24
 */

// app UI 入口
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavgation(
    //param for app
    navController : NavHostController,
    appVm :MainViewModel,
    //param for Main
    imageCapture: ImageCapture,
    onTakePhotoClickd:()->Unit
    //param for others
){
    AnimatedNavHost (navController = navController, startDestination = Screen.Main.route){
        composable(
            Screen.Main.route,
            exitTransition = {
                slideOutVertically()
            }) {
            val  homeScreenViewModel : HomeScreenViewModel = viewModel()
            HomeScreen(appVm,navController,imageCapture,onTakePhotoClickd,homeScreenViewModel)
        }
        composable(
           "${Screen.CropImagePage.route}/{index}",
            arguments = listOf(navArgument("index") { type = NavType.IntType }),
            enterTransition= {expandIn()}
            ) {
            val index = it.arguments?.getInt("index") ?: -1
            //compose 提供的composable 生命周期相关的viewModel支持
            //https://developer.android.com/codelabs/basic-android-kotlin-compose-viewmodel-and-state?hl=zh-cn#5
            val  cropScreenViewModel :CropScreenViewModel= viewModel()
            //从选择页面进来重新 裁切
            if (navController.previousBackStackEntry?.destination?.route == Screen.SelectedImagePage.route){
                appVm.imageForCrop = appVm.imageCroped[index]
            }
            CropScreen(appVm,navController,cropScreenViewModel,index)
        }
        composable(Screen.SelectedImagePage.route) {
            val vm :SelectedImageViewModel = viewModel()
            SelectedImageScreen(appVm,vm,{key:String,index:Int->
                when(key){
                    "添加" ->{
                        navController.popBackStack()
                    }
                    "筛选器" ->{
                        appVm.imageCroped[index] = OpencvFilter.filter1(appVm.imageCroped[index], Imgproc.COLORMAP_AUTUMN)
                    }
                    "裁切" ->{
                        navController.navigate("${Screen.CropImagePage.route}/$index")
                    }
                    "旋转" ->{
                        appVm.imageCroped[index] = appVm.imageCroped[index].rotateBitmap(90)
                    }
                    "重新排序" ->{

                    }
                    "删除" ->{
                        vm.showDeleteDialog = true

                    }

                }
            }, onDelete = {

            }, onGeneratePDF = {
                PdfManager.saveBitmapsAsPdf(appVm.imageCroped,"xiaolong")
            })
        }


        composable(Screen.PDFResultPage.route) {

        }
    }

}

// Screen.kt
sealed class Screen(val route: String) {
    object Main: Screen("main_screen")
    object CropImagePage: Screen("Crop_Image_screen")
    object SelectedImagePage :Screen("selected_image_screen")
    object PDFResultPage :Screen("pdf_result_page")
}