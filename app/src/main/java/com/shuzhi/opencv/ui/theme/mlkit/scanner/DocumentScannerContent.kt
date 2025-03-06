package com.shuzhi.opencv.ui.theme.mlkit.scanner

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.shuzhi.opencv.R
import com.shuzhi.opencv.ui.theme.mainPage.MainViewModel
import com.shuzhi.opencv.ui.theme.mlkit.scanner.icon.DocumentScanner
import com.shuzhi.opencv.ui.theme.photo.PhotoManager
import com.shuzhi.opencv.ui.theme.photo.PhotoManager.uriToBitmap
import com.shuzhi.opencv.ui.theme.util.LocalToastHostState
import kotlinx.coroutines.launch

@Composable
fun DocumentScannerContent(
    appVM:MainViewModel,
    component: DocumentScannerViewmodel = hiltViewModel(),
    onGotoDetail:()->Unit
) {
    val toastHostState = LocalToastHostState.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val documentScanner = rememberDocumentScanner { scanResult ->
        //对扫描结果处理
        component.parseScanResult(scanResult)
        appVM.imageCroped.addAll(scanResult.imageUris.map { uriToBitmap(context.contentResolver,it)!!} )
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Box(modifier = Modifier.clickable {
            runCatching {
                scope.launch {
                    toastHostState.showToast("开始扫描" ?: "Error")
                }
                documentScanner.scan()
            }.onFailure {
                scope.launch {
                    toastHostState.showToast("111" ?: "Error")
                }
            }
        }) {
            Icon(
                imageVector = Icons.TwoTone.DocumentScanner,
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )
        }
        if (appVM.imageCroped.size>0){
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = stringResource(R.string.continue_to_add_pic),
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(
                    onClick = {
                        onGotoDetail()
                    }
                ) {
                    Box(
                        contentAlignment = Alignment.TopEnd,
                        modifier = Modifier.size(44.dp)
                        ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            modifier = Modifier.size(44.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = appVM.imageCroped.size.toString(),
                            modifier = Modifier.padding(4.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                }
            }
        }else {
            Text(
                text = stringResource(R.string.click_to_start_scanning),
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }


}
