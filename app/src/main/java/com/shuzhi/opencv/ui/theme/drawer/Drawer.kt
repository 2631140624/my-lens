package com.shuzhi.opencv.ui.theme.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.leancloud.LCUser
import cn.leancloud.LeanCloud
import com.shuzhi.opencv.ui.theme.app.OpenCvApp
import com.shuzhi.opencv.ui.theme.drawer.settings.SettingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerScreen(
    viewModel: SettingViewModel = hiltViewModel(),
    onGotoOcrPage: () -> Unit,
    onGotoPDFpreviewPage: () -> Unit,
    onGotoCloudPDFpreviewPage: () -> Unit,
    content: @Composable () -> Unit,
    gesturesEnabled: Boolean = true
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // 偏好设置状态（示例使用 rememberSaveable 简单保存）
    var darkThemeEnabled by rememberSaveable { mutableStateOf(false) }
    var notificationEnabled by rememberSaveable { mutableStateOf(true) }

    val googleMlkitDocumentScannerEnabled by viewModel.googleMlkitDocumentScannerFlow.collectAsState()

    ModalNavigationDrawer(
        gesturesEnabled = gesturesEnabled,
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                darkThemeEnabled = darkThemeEnabled,
                onDarkThemeChange = { darkThemeEnabled = it },
                notificationEnabled = notificationEnabled,
                onNotificationChange = { notificationEnabled = it },
                googleMlkitDocumentScannerEnabled = googleMlkitDocumentScannerEnabled ?: false,
                onGoogleMlkitDocumentScannerChange = { viewModel.setGoogleMlkitDocumentScanner(it) },
                onGotoOcrPage = {
                    onGotoOcrPage()
                    scope.launch { drawerState.close() }
                },
                onGotoPDFpreviewPage = {
                    onGotoPDFpreviewPage()
                    scope.launch { drawerState.close() }
                }, onGotoCloudPDFpreviewPage = {
                    onGotoCloudPDFpreviewPage()
                    // 跳转到云端PDF预览页面
                    scope.launch { drawerState.close() }
                },
                onClose = { scope.launch { drawerState.close() } }
            )
        },
        content = {
            content()
        }
    )

}


@Composable
fun DrawerContent(
    darkThemeEnabled: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    notificationEnabled: Boolean,
    onNotificationChange: (Boolean) -> Unit,
    googleMlkitDocumentScannerEnabled: Boolean,
    onGoogleMlkitDocumentScannerChange: (Boolean) -> Unit,
    onGotoOcrPage: () -> Unit,
    onGotoPDFpreviewPage: () -> Unit,
    onGotoCloudPDFpreviewPage: () -> Unit,
    onClose: () -> Unit
) {
    Column(Modifier
        .fillMaxWidth(0.8f)
        .fillMaxHeight()
        .background(Color.White)
        .padding(16.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Text("设置", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, "关闭抽屉")
            }
        }

        Spacer(Modifier.height(24.dp))

        // 暗黑模式开关
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("userName: ${OpenCvApp.sharedViewModel!!.userName}")
            Spacer(Modifier.weight(1f))
//            Switch(
//                checked = darkThemeEnabled,
//                onCheckedChange = onDarkThemeChange
//            )
        }
//
//        // 通知开关
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("接收通知")
//            Spacer(Modifier.weight(1f))
//            Switch(
//                checked = notificationEnabled,
//                onCheckedChange = onNotificationChange
//            )
//        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("启用Google MlKit 文档扫描")
            Spacer(Modifier.weight(1f))
            Switch(
                checked = googleMlkitDocumentScannerEnabled,
                onCheckedChange = onGoogleMlkitDocumentScannerChange
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // 跳转到 OCR 页面
                    onGotoOcrPage()
                }
        ) {
            Text("OCR page")
            Spacer(Modifier.weight(1f))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // 跳转到 OCR 页面
                    onGotoPDFpreviewPage()
                }
        ) {
            Text("本地PDF Preview")
            Spacer(Modifier.weight(1f))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // 跳转到 OCR 页面
                    onGotoCloudPDFpreviewPage()
                }
        ) {
            Text("云端PDF Preview")
            Spacer(Modifier.weight(1f))
        }

        // 可以添加更多设置项...
    }
}