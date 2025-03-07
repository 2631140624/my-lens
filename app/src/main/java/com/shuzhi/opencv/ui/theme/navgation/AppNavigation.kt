package com.shuzhi.opencv.ui.theme.navgation

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.window.BackEvent
import androidx.activity.BackEventCompat
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.camera.core.ImageCapture
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.expandIn
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.shuzhi.opencv.ui.theme.app.OpenCvApp
import com.shuzhi.opencv.ui.theme.base.BaseActivity
import com.shuzhi.opencv.ui.theme.croppage.CropScreen
import com.shuzhi.opencv.ui.theme.croppage.CropScreenViewModel
import com.shuzhi.opencv.ui.theme.drawer.DrawerScreen
import com.shuzhi.opencv.ui.theme.drawer.settings.SettingViewModel

import com.shuzhi.opencv.ui.theme.mainPage.HomeScreen
import com.shuzhi.opencv.ui.theme.mainPage.HomeScreenViewModel
import com.shuzhi.opencv.ui.theme.mainPage.MainActivity
import com.shuzhi.opencv.ui.theme.mainPage.MainViewModel
import com.shuzhi.opencv.ui.theme.mainPage.rotateBitmap
import com.shuzhi.opencv.ui.theme.mlkit.ocr.OcrScreen
import com.shuzhi.opencv.ui.theme.mlkit.scanner.DocumentScannerContent
import com.shuzhi.opencv.ui.theme.mlkit.scanner.rememberDocumentScanner
import com.shuzhi.opencv.ui.theme.pdf.PdfManager
import com.shuzhi.opencv.ui.theme.photo.ImageViewerActivity
import com.shuzhi.opencv.ui.theme.photo.PhotoManager.ImageViewer
import com.shuzhi.opencv.ui.theme.resortpage.DraggablePhotoGrid
import com.shuzhi.opencv.ui.theme.selectedpage.SelectedImageScreen
import com.shuzhi.opencv.ui.theme.selectedpage.SelectedImageViewModel
import com.shuzhi.opencv.ui.theme.util.ToastHost
import kotlinx.coroutines.launch


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
//    val predictiveBackProgress = remember { Animatable(0f) }
//
//    val scope = rememberCoroutineScope()
//    // 监听返回手势进度
//    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
//    val backCallback = remember {
//        object : OnBackPressedCallback(true) {
//            override fun handleOnBackStarted(backEvent: BackEventCompat) {
//                scope.launch {
//                    // 开始返回手势
//                    predictiveBackProgress.snapTo(0f)
//                }
//            }
//
//            override fun handleOnBackProgressed(backEvent: BackEventCompat) {
//                scope.launch {
//                    // 更新手势进度 (0f~1f)
//                    predictiveBackProgress.snapTo(backEvent.progress)
//                }
//            }
//
//            override fun handleOnBackPressed() {
//                // 完成返回操作
//                navController.popBackStack()
//                scope.launch {
//                    predictiveBackProgress.animateTo(1f)
//                }
//            }
//
//            override fun handleOnBackCancelled() {
//                // 取消返回
//                scope.launch {
//                    predictiveBackProgress.animateTo(0f)
//                }
//            }
//        }
//    }
//
//    LaunchedEffect(backDispatcher) {
//        backDispatcher?.addCallback(backCallback)
//    }
    AnimatedNavHost (
        navController = navController,
        startDestination = Screen.Main.route,
//        modifier = Modifier.graphicsLayer {
//            translationX = predictiveBackProgress.value * 300 // 横向滑动效果
//            alpha = 1f - predictiveBackProgress.value * 0.5f
//        }
    ){
        composable(
            Screen.Main.route,
            exitTransition = {
                slideOutVertically()
            }) {
            val  homeScreenViewModel : HomeScreenViewModel = viewModel()
            val  settingViewModel : SettingViewModel = hiltViewModel()
            AnimatedContent(settingViewModel.googleMlkitDocumentScannerFlow.collectAsStateWithLifecycle().value ) { it ->
                when(it){
                    true ->{
                        DocumentScannerContent(appVm, onGotoDetail = {
                        navController.navigate(Screen.SelectedImagePage.route)
                    })}
                    false ->{
                        HomeScreen(appVm,navController,imageCapture,onTakePhotoClickd,homeScreenViewModel)
                    }
                    null ->{
                        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                    }
                }

            }
//            if (settingViewModel.googleMlkitDocumentScannerFlow.collectAsStateWithLifecycle().value) {
//                //如果开启了Mlkit文档扫描功能
//
//                return@composable
//            }
//            HomeScreen(appVm,navController,imageCapture,onTakePhotoClickd,homeScreenViewModel)
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
                       // appVm.imageCroped[index] = OpencvFilter.filter1(appVm.imageCroped[index], Imgproc.COLORMAP_AUTUMN)
                    }
                    "裁切" ->{
                        navController.navigate("${Screen.CropImagePage.route}/$index")
                    }
                    "旋转" ->{
                        appVm.imageCroped[index] = appVm.imageCroped[index].rotateBitmap(90)
                    }
                    "重新排序" ->{
                        navController.navigate(Screen.ResortPage.route)
                    }
                    "删除" ->{
                        vm.showDeleteDialog = true

                    }

                }
            }, onDelete = {

            }, onGeneratePDF = {
                PdfManager.saveBitmapsAsPdf(appVm.imageCroped,"xiaolong")
            }, toImagePreview = {
                BaseActivity.currentActivity.get()!!.startActivity(Intent(BaseActivity.currentActivity.get(),ImageViewerActivity::class.java).apply {
                    putExtra("index",it)
                    addFlags(FLAG_ACTIVITY_NEW_TASK)
                })
                //navController.navigate("${Screen.ImagePreview.route}/$it")
            })
        }


        composable(Screen.PDFResultPage.route) {

        }
        composable(Screen.ResortPage.route) {
            DraggablePhotoGrid(appVm.imageCroped){

            }

        }
        composable(
            "${Screen.ImagePreview.route}/{index}",
            arguments = listOf(navArgument("index") { type = NavType.IntType }),){
            val index = it.arguments?.getInt("index") ?: 0
            ImageViewer(appVm.imageCroped,index){

            }
        }
        composable(Screen.OcrPage.route) {
            OcrScreen()
        }

    }

}

// Screen.kt
sealed class Screen(val route: String) {
    object Main: Screen("main_screen")
    object CropImagePage: Screen("Crop_Image_screen")
    object SelectedImagePage :Screen("selected_image_screen")
    object PDFResultPage :Screen("pdf_result_page")

    object ResortPage :Screen("ResortPage")

    object ImagePreview : Screen("image_preview")
    object OcrPage :Screen("ocr_page")
}