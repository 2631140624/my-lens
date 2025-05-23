package com.shuzhi.opencv.ui.theme.pdf

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.leancloud.LCFile
import cn.leancloud.LCUser
import com.shuzhi.opencv.ui.theme.app.OpenCvApp
import com.shuzhi.opencv.ui.theme.pdf.pdfpreview.model.PdfRecord
import com.shuzhi.opencv.ui.theme.pdf.pdfpreview.model.PdfRecord.Companion.DATE
import com.shuzhi.opencv.ui.theme.pdf.pdfpreview.model.PdfRecord.Companion.PDF_Preview_Image
import com.shuzhi.opencv.ui.theme.pdf.pdfpreview.model.PdfRecord.Companion.PDF_URL
import com.shuzhi.opencv.ui.theme.pdf.pdfpreview.model.PdfRecord.Companion.USER_ID
import com.shuzhi.opencv.ui.theme.pdf.pdfpreview.model.PdfRecord.Companion.NAME
import com.shuzhi.opencv.ui.theme.pdf.pdfpreview.repository.DocumentRepositoryImpl
import com.shuzhi.opencv.ui.theme.util.LocalToastHostState
import com.shuzhi.opencv.ui.theme.util.ToastDefaults
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 *@author :yinxiaolong
 *@describe : com.shuzhi.opencv.ui.theme.pdf 导出页面
 *@date :2025-01-20 14:58
 */
// ExportScreen.kt
@Composable
fun ExportScreen(
    viewModel: ExportViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    // 处理权限请求结果
    val permissionCheck = remember { mutableStateOf(false) }
    LaunchedEffect(permissionCheck.value) {
        if (permissionCheck.value) {
            viewModel.exportDocument()
        }
    }
    val toastHostState = LocalToastHostState.current
    // 显示Snackbar
    uiState.exportResult?.let { result ->
        val message = result.message ?: if (result.success) "导出成功" else "导出失败"
        LaunchedEffect(result) {
            withContext(Dispatchers.Main) {
                toastHostState.showToast(message)
            }
        }
    }
   val permissionLauncher: ActivityResultLauncher<String> = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        if (isGranted) {
            viewModel.exportDocument()
        } else {
//            viewModel.updateExportResult(
//                ExportResult.Error("需要存储权限")
//            )
        }
    }

    Scaffold(
        topBar = { /* 顶部导航栏 */ },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                content = { Text("保存") },
                onClick = {
                    scope.launch {
                        viewModel.prepareExport()
                        viewModel.exportDocument()
                    }
                }
            )
        },
        modifier = Modifier.padding(horizontal = 8.dp)
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = uiState.fileName,
                onValueChange = viewModel::updateFileName,
                label = { Text("文件标题") }
            )

            CheckboxWithLabel(
                checked = uiState.saveToPrivateStorage,
                onCheckedChange = viewModel::updatePrivateStorage
            ) { Text("保存到应用私有存储") }
            CheckboxWithLabel(
                checked = uiState.saveToExternalStorage,
                onCheckedChange = viewModel::updateExternalStorage
            ) { Text("保存到公共文档目录") }
            CheckboxWithLabel(
                checked = uiState.uploadToCloud,
                onCheckedChange = viewModel::updateUploadToCloud
            ) { Text("上传到云端") }

            Spacer(modifier = Modifier.height(40.dp))
            if (viewModel.isLoading) {
                Row(Modifier.fillMaxWidth(),horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

// ExportViewModel.kt
class ExportViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(ExportUIState())
    val uiState: StateFlow<ExportUIState> = _uiState
    companion object{
        const val TAG = "ExportViewModel"
    }

    var isLoading  by mutableStateOf(false)
    fun updateFileName(name: String) {
        _uiState.update { it.copy(fileName = name) }
    }

    fun prepareExport() {
        if (_uiState.value.fileName.isBlank()) {
            _uiState.update { it.copy(fileName = generateDefaultFileName()) }
        }
    }

    fun needExternalStoragePermission(): Boolean {
        return _uiState.value.saveToExternalStorage &&
                !hasExternalStoragePermission()
    }

    fun exportDocument() = viewModelScope.launch (Dispatchers.IO){
        val result = try {
            isLoading =true
            val filePaths = mutableListOf<String>()

            Log.d(TAG,"private ${_uiState.value.saveToPrivateStorage}")
            Log.d(TAG,"external ${_uiState.value.saveToExternalStorage}")
            if (_uiState.value.saveToPrivateStorage) {
                val path = saveToPrivateDir()
                filePaths += path
            }
            if (_uiState.value.uploadToCloud) {

                Log.d(TAG, "uploadToCloud start")
                uploadToCould()

            }
            Log.d(TAG,filePaths.toString())
            if (_uiState.value.saveToExternalStorage) {
//                if (!hasExternalStoragePermission()) {
//                    _uiState.update { it.copy(
//                        exportResult = ExportResult.Error("需要存储权限")
//                    )}
//                    return@launch
//                }
                val path = saveToPublicDir()
                filePaths += path
            }
            Log.d(TAG,filePaths.toString())
            isLoading =false
            ExportResult.Success("文件保存到：${filePaths.joinToString()}")

        } catch (e: Exception) {
            isLoading = false
            Log.e(TAG,e.message?:"null")
            ExportResult.Error(e.message)
        }

        _uiState.update { it.copy(exportResult = result) }
    }

    private fun generateDefaultFileName(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return "${dateFormat.format(Date())}_MyScannerApp"
    }

    private fun saveToPrivateDir(): String {
        val dir = OpenCvApp.appContext!!.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = PdfManager.saveBitmapsAsPdf(OpenCvApp.sharedViewModel!!.imageCroped,_uiState.value.fileName,dir!!)
        return file?.absolutePath?:""
    }

    private fun uploadToCould(){
        val dir = OpenCvApp.appContext!!.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = PdfManager.saveBitmapsAsPdf(OpenCvApp.sharedViewModel!!.imageCroped,_uiState.value.fileName,dir!!,true)

        val pdfFile = LCFile(_uiState.value.fileName+".pdf",file)
        //先把封面上传
        val pngFile = LCFile(_uiState.value.fileName+".png",
            DocumentRepositoryImpl.generateThumbnail(file!!)?.bitmapToBytes() ?: ByteArray(0)
        )
        pngFile.save()
        pdfFile.save()
        //保存pdf 元数据
        val pdfRecord = PdfRecord().apply {
            put(USER_ID, LCUser.currentUser().username)
            put(PDF_Preview_Image, pngFile.url)
            put(DATE,file.lastModified())
            put(NAME, _uiState.value.fileName+".pdf")
            put(PDF_URL, pdfFile.url)

        }
        pdfRecord.save()
        //最后删除文件
        file.delete()
    }

    fun Bitmap.bitmapToBytes(format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): ByteArray {
        val outputStream = ByteArrayOutputStream()
        this.compress(format, 100, outputStream) // 压缩质量 100 表示不压缩
        return outputStream.toByteArray()
    }
    private fun linkFileToUser(file: LCFile) {
        val user = LCUser.currentUser()
        user.add("files", file) // 添加至用户字段
        user.saveInBackground().subscribe()
    }
    private fun saveToPublicDir(): String {
        val dir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS
        )
        val file = PdfManager.saveBitmapsAsPdf(OpenCvApp.sharedViewModel!!.imageCroped,_uiState.value.fileName,dir)
       // PdfGenerator.generatePdf(file, OpenCvApp.sharedViewModel!!.imageCroped)
        return file?.absolutePath?:""
    }

    private fun hasExternalStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            OpenCvApp.appContext!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun updatePrivateStorage(b: Boolean) {
        _uiState.update { it.copy(saveToPrivateStorage = b) } }

    fun updateExternalStorage(b: Boolean) {
        _uiState.update { it.copy(saveToExternalStorage = b) }
    }
    fun updateUploadToCloud(b: Boolean) {
        _uiState.update { it.copy(uploadToCloud = b) }
    }
}


// PdfGenerator.kt
object PdfGenerator {
    fun generatePdf(outputFile: File, pages: List<Bitmap>) {
        PdfDocument().also{ pdfDocument ->
            pages.forEach { bitmap ->
                val pageInfo = PdfDocument.PageInfo.Builder(
                    bitmap.width,
                    bitmap.height,
                    pages.indexOf(bitmap)
                ).create()

                val page = pdfDocument.startPage(pageInfo)
                page.canvas.drawBitmap(bitmap, 0f, 0f, null)
                pdfDocument.finishPage(page)
            }

            FileOutputStream(outputFile).use { fos ->
                pdfDocument.writeTo(fos)
            }
            pdfDocument.close()
        }
    }
}

// 数据类
data class ExportUIState(
    val fileName: String = "",
    val saveToPrivateStorage: Boolean = true,
    val saveToExternalStorage: Boolean = true,
    val uploadToCloud: Boolean = true,
    val exportResult: ExportResult? = null
)

sealed class ExportResult(
    val success: Boolean,
    val message: String?
) {
    class Success(message: String?) : ExportResult(true, message)
    class Error(message: String?) : ExportResult(false, message)
}

@Composable
fun CheckboxWithLabel(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .clickable(
                enabled = enabled,
                onClick = { onCheckedChange(!checked) }
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null, // 点击事件由父Row处理
            enabled = enabled,
            modifier = Modifier.padding(end = 8.dp)
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            label()
        }
    }
}
