package com.shuzhi.opencv.ui.theme.selectedpage

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.ValueElement
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shuzhi.opencv.R
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
fun SelectedImageScreen(mainViewModel: MainViewModel,onToolClicked:(String,Int)->Unit) {

    // 创建一个PagerState，控制ViewPager的状态
    val pagerState = rememberPagerState(pageCount = {
        mainViewModel.imageCroped.size
    })
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "${pagerState.currentPage + 1}/${pagerState.pageCount}",
            modifier = Modifier.padding(top = 10.dp)
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
        FlowRow() {
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
                    Text("${it.first}")
                }
            }

            Button(onClick = {
                //todo 到导出页面
            }) {
                Text("导出")
            }

        }

    }

}