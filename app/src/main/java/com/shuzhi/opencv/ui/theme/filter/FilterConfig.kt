package com.shuzhi.opencv.ui.theme.filter

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.shuzhi.opencv.ui.theme.app.OpenCvApp
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBoxBlurFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBulgeDistortionFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGaussianBlurFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGlassSphereFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageRGBDilationFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSharpenFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSwirlFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageThresholdEdgeDetectionFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageToonFilter
import kotlinx.coroutines.launch

// 1. 定义滤镜数据类
data class FilterConfig(
    val name: String,
    val filter: GPUImageFilter?
)

data class FilterItem(
    val bitmap: Bitmap,
    val filterIndex: Int
)

// 2. 创建ViewModel管理状态
class FilterViewModel : ViewModel() {
    // 每一个图片需要存储对应的filter 图片
    var filterBitmap = mutableStateMapOf<Bitmap,FilterItem?>()
    
    // 滤镜配置列表
    val filters = listOf(
        FilterConfig("原图", null),
        FilterConfig("边缘检测", GPUImageThresholdEdgeDetectionFilter()),
        FilterConfig("高斯模糊", GPUImageGaussianBlurFilter(5f)),
        FilterConfig("锐化", GPUImageSharpenFilter(2f)),
        FilterConfig("灰度", GPUImageGrayscaleFilter()),
        FilterConfig("提亮", GPUImageBrightnessFilter(0.5f)),
        FilterConfig("对比度", GPUImageContrastFilter(2.0f)),
        FilterConfig("卡通", GPUImageToonFilter()),
        FilterConfig("鱼眼", GPUImageBulgeDistortionFilter()),
        FilterConfig("漩涡扭曲", GPUImageSwirlFilter()),
        FilterConfig("玻璃效果", GPUImageGlassSphereFilter())

        // 添加更多滤镜...
    )

}

// 3. 主页面
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FilterScreen(viewModel: FilterViewModel) {
    val pagerState = rememberPagerState { OpenCvApp.sharedViewModel!!.imageCroped.size }
    val  coroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部图片预览区域
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) { page ->
            Box(modifier = Modifier.clip(RoundedCornerShape(12.dp))) {
                val originBitmap = OpenCvApp.sharedViewModel!!.imageCroped[page]
                Image(
                    bitmap = if (viewModel.filterBitmap[originBitmap]== null) originBitmap.asImageBitmap() else viewModel.filterBitmap[originBitmap]!!.bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier,
                   // contentScale = ContentScale.Crop
                )
            }
        }

        // 底部控制区域
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 滤镜选择
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                itemsIndexed(viewModel.filters) { index, filter ->
                    val currentBitmap = OpenCvApp.sharedViewModel!!.imageCroped[pagerState.currentPage]
                    FilterItem(
                        filter = filter,
                        isSelected = index == (viewModel.filterBitmap[currentBitmap]?.filterIndex ?: 0) ,
                        onClick = {
                            val filterConfig= viewModel.filters[index]
                            //todo 做 更新滤镜操作
                            viewModel.filterBitmap[currentBitmap] = FilterItem(doFilter(currentBitmap,filterConfig.filter),index)
/*                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                            */
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 应用按钮
            Button(
                onClick = {
                    OpenCvApp.sharedViewModel!!.imageCroped.forEachIndexed { index, bitmap ->
                        val filterItem = viewModel.filterBitmap[bitmap]
                        if (filterItem != null) {
                            // 应用滤镜
                            val filteredBitmap = filterItem.bitmap
                            OpenCvApp.sharedViewModel!!.imageCroped[index] = filteredBitmap
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("应用滤镜")
            }
        }
    }
}
fun doFilter(originBitmap: Bitmap,filter:GPUImageFilter?) : Bitmap{
    if (filter == null) {
        return originBitmap
    }
    val gpuImage = GPUImage(OpenCvApp.appContext)
    gpuImage.setImage(originBitmap)
    gpuImage.setFilter(filter)
    return gpuImage.bitmapWithFilterApplied
}
// 4. 滤镜选择项组件
@Composable
private fun FilterItem(
    filter: FilterConfig,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(80.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .border(
                    width = 2.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(4.dp)
                .background(Color.LightGray, RoundedCornerShape(6.dp))
        )

        Text(
            text = filter.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}