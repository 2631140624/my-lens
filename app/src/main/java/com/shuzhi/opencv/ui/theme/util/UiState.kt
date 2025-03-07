package com.shuzhi.opencv.ui.theme.util

// 通用状态基类
sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    
    fun isLoading() = this is Loading
    fun isSuccess() = this is Success
    fun isError() = this is Error
}