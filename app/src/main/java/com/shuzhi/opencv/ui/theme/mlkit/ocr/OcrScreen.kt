package com.shuzhi.opencv.ui.theme.mlkit.ocr

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.mlkit.vision.text.Text
import com.shuzhi.opencv.R
import com.shuzhi.opencv.ui.theme.app.OpenCvApp
import com.shuzhi.opencv.ui.theme.util.UiState



@Composable
fun OCrScreenWithScaffold(index :Int) {

    Scaffold(
        topBar = {
            Row(modifier = Modifier.fillMaxWidth()) {
            Text("OCR 文本识别", modifier = Modifier.padding(16.dp).align(Alignment.CenterVertically))
        }
        },
        content = {paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                OcrScreen(index)
            }
        },
    )
}
@Composable
fun OcrScreen(
    index: Int,
    viewModel: OcrViewModel = hiltViewModel()
) {
    var indexIn by remember {  mutableIntStateOf(index) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    if (indexIn>=0) {
        viewModel.bitmapFromLastPage = OpenCvApp.sharedViewModel!!.imageCroped[indexIn]
    }
    // 图片选择器
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        imageUri = uri
        indexIn =-1
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 图片预览
            Crossfade(
                targetState = imageUri,
                animationSpec = tween(1000),
                label = "ImageTransition"
            ) { targetUri ->
                AsyncImage(
                    model = if (indexIn>=0)  viewModel.bitmapFromLastPage  else targetUri,
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clickable {
                            launcher.launch("image/*")
                            /* 重新选择图片 */
                        },
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.icon_upload),
                    error = painterResource(R.drawable.icon_upload)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (imageUri != null||indexIn >= 0) {

                // 识别按钮
                Button(onClick = {
                    var bitmap :Bitmap?= null
                    bitmap = if (imageUri == null) {
                        viewModel.bitmapFromLastPage
                    }else{
                        context.loadBitmap(imageUri!!)
                    }
                    bitmap?.let { viewModel.processImage(it) }
                }) {
                    Text("开始识别")
                }
            }

        val state = viewModel.ocrState.value
        // 状态显示
        AnimatedContent(targetState = state) { state ->
            when {
                state.isLoading() -> CircularProgressIndicator()
                state.isSuccess() -> SuccessContent(state)
                state.isError() -> Text((state as UiState.Error).message)
            }
        }
    }


//    if (imageUri == null) {
//        Button(
//            onClick = { launcher.launch("image/*") },
//            //  modifier = Modifier.align(Alignment.BottomCenter)
//        ) {
//            Text("选择图片")
//        }
//    }
}

@Composable
fun SuccessContent(state: UiState<Text>) {
    val clipboardManager = LocalClipboardManager.current
    var showDialog by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
        //.horizontalScroll(rememberScrollState())
    ) {
        LazyColumn {
            item {
                CopyableTextWithButton(
                    showDialog = showDialog,
                    text = ((state as UiState.Success<Text>).data.text),
                    onCancelDialog = { showDialog = false }
                )
            }
        }
        if ((state as UiState.Success<Text>).data.text.isEmpty() or (state as UiState.Success<Text>).data.text.isBlank()) {
            Text("未识别到文本", modifier = Modifier.align(Alignment.Center))
            return@Box
        }
        // 自定义浮动复制按钮
        IconButton(
            onClick = {
                clipboardManager.setText(AnnotatedString(((state as UiState.Success<Text>).data.text)))
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
                .background(Color.White, CircleShape)
                .padding(10.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ThumbUp,
                contentDescription = "复制"
            )
        }

    }
}


@Composable
fun CopyableTextWithButton(showDialog: Boolean, text: String, onCancelDialog: () -> Unit) {


    Box(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()) {
        SelectionContainer {
            Text(
                text = text,
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyMedium.copy(
                    background = Color.LightGray.copy(alpha = 0.2f)
                ),
                modifier = Modifier.padding(8.dp)
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { onCancelDialog.invoke() },
                title = { Text("复制成功") },
                text = { Text("内容已复制到剪贴板") },
                confirmButton = {
                    TextButton(onClick = { onCancelDialog.invoke() }) {
                        Text("确定")
                    }
                }
            )
        }
    }
}

@Deprecated("不合适")
@Composable
fun TextBoxOverlay(
    boxes: List<BoundingBoxInfo>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.fillMaxSize()) {
        boxes.forEach { boxInfo ->
//            // 先绘制矩形框
//            drawRect(
//                color = when (boxInfo.type) {
//                    "block" -> Color.Red.copy(alpha = 0.3f)
//                    "line" -> Color.Green.copy(alpha = 0.3f)
//                    else -> Color.Blue.copy(alpha = 0.3f)
//                },
//                topLeft = Offset(boxInfo.rect.left, boxInfo.rect.top),
//                size = Size(boxInfo.rect.width, boxInfo.rect.height),
//                style = Fill
//            )

            // 再绘制文字
            val textLayoutResult = textMeasurer.measure(
                text = AnnotatedString(boxInfo.text),
                style = when (boxInfo.type) {
                    "block" -> TextStyle(
                        color = Color.White,
                        fontSize = 12.sp,
                        background = Color.Red
                    )

                    "line" -> TextStyle(
                        color = Color.White,
                        fontSize = 10.sp,
                        background = Color.Green
                    )

                    else -> TextStyle(
                        color = Color.White,
                        fontSize = 8.sp,
                        background = Color.Blue
                    )
                },
                maxLines = 1,
                overflow = TextOverflow.Visible
            )

            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x = boxInfo.rect.left, // 添加左边距
                    y = boxInfo.rect.top    // 添加顶部边距
                )
            )
        }
    }
}


// 扩展函数：Uri 转 Bitmap
fun Context.loadBitmap(uri: Uri): Bitmap? {
    return contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it)
    }
}