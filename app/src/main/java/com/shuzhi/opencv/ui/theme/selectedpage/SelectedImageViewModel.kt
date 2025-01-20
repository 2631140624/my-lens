package com.shuzhi.opencv.ui.theme.selectedpage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 *@author :yinxiaolong
 *@describe : com.shuzhi.opencv.ui.theme.selectedpage
 *@date :2025-01-20 14:31
 */
class SelectedImageViewModel : ViewModel(){
    var showDeleteDialog  by mutableStateOf(false)

}