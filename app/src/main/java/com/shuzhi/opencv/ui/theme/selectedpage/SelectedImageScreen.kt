package com.shuzhi.opencv.ui.theme.selectedpage

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.ValueElement
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.shuzhi.opencv.R
import com.shuzhi.opencv.ui.theme.app.OpenCvApp
import com.shuzhi.opencv.ui.theme.mainPage.MainViewModel

/**
 *@author :yinxiaolong
 *@describe : com.shuzhi.opencv.ui.theme.selectedpage
 *@date :2025-01-19 12:30
 */
val toolMap = mutableMapOf<String, Int>().apply {
    this["添加"] = R.drawable.icon_upload
    this["筛选器"] = R.drawable.filter
    this["裁切"] = R.drawable.crop
    this["旋转"] = R.drawable.rotate
    this["重新排序"] = R.drawable.rank
    this["删除"] = R.drawable.delete
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectedImageScreen(mainViewModel: MainViewModel,vm:SelectedImageViewModel,onToolClicked:(String,Int)->Unit,onDelete:()->Unit,onGeneratePDF:(Int)->Unit) {

    // 创建一个PagerState，控制ViewPager的状态
    val pagerState = rememberPagerState(pageCount = {
        mainViewModel.imageCroped.size
    })
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().background(color = Color.Black)
    ) {
        Text(
            "${pagerState.currentPage + 1}/${pagerState.pageCount}",
            modifier = Modifier.padding(top = 10.dp),
            fontStyle = FontStyle.Italic,
            color = Color.White,
            fontSize =15.sp
        )
        // 使用 HorizontalPager 显示一组页面
        HorizontalPager(
            state = pagerState, // 设置 Pager 的状态
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f),
            contentPadding = PaddingValues(horizontal = 50.dp)
        ) { page ->
            val imgScale by animateFloatAsState(
                targetValue = if (pagerState.currentPage == page) 1f else 0.8f,
                animationSpec = tween(300), label = "imgScale"
            )
            val bitmap = mainViewModel.imageCroped[page]
            // 为每个页面设置内容
            Image(
                painter = BitmapPainter(bitmap.asImageBitmap()),
                contentDescription = "Page $page",
                modifier = Modifier.wrapContentSize().scale(imgScale)
            )
        }
        FlowRow(modifier = Modifier.background(Color.White).fillMaxHeight()) {
            toolMap.toList().forEach {
                Column(horizontalAlignment = Alignment.CenterHorizontally,modifier = Modifier.wrapContentSize().padding(5.dp).align(Alignment.CenterVertically)) {
                    Image(
                        painter = painterResource(it.second),
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                onToolClicked(it.first,pagerState.currentPage)
                            },
                        contentDescription = null)
                    Text(
                        text = it.first,
                        fontStyle = FontStyle.Italic,)
                }
            }

            Button(onClick = {
                onGeneratePDF(pagerState.currentPage)
                //todo 到导出页面
            }) {
                Text("导出")
            }

        }

    }
    if (vm.showDeleteDialog) {
        DeleteDialog(
            onDisMiss = {
                vm.showDeleteDialog = false
            },
            onConform = {
                mainViewModel.imageCroped.removeAt(pagerState.currentPage)
                onDelete()
                Toast.makeText(OpenCvApp.appContext,"删除成功",Toast.LENGTH_SHORT).show()
                vm.showDeleteDialog = false
            },
            onCancel = {
                vm.showDeleteDialog = false
            },
        )
    }

}

@Composable
fun DeleteDialog(onDisMiss:()->Unit,onConform:()->Unit,onCancel:()->Unit) {

        Dialog(onDismissRequest = { onDisMiss() }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("请确认是否删除", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("删除后将无法恢复！！！")
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.SpaceAround) {
                        TextButton(onClick = { onCancel() }) {
                            Text("cancel")
                        }
                        TextButton(onClick = { onConform() }) {
                            Text("delete")
                        }

                    }
                }
            }

    }
}
