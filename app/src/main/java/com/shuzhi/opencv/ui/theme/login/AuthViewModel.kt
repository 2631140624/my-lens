package com.shuzhi.opencv.ui.theme.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.leancloud.LCUser
import kotlinx.coroutines.launch
import cn.leancloud.LeanCloud
import com.shuzhi.opencv.ui.theme.app.OpenCvApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthViewModel : ViewModel() {
    // 登录/注册状态管理
    data class AuthState(
        val username: String = "",
        val password: String = "",
        val confirmPassword: String = "",  // 新增确认密码字段
        val isLoading: Boolean = false,
        val error: String? = null,
        val isLoggedIn: Boolean = false,
        val isRegistered: Boolean = false
    )

    private val _state = mutableStateOf(AuthState())
    val state: State<AuthState> = _state

    // 处理用户输入
    fun onUsernameChange(username: String) {
        _state.value = _state.value.copy(username = username)
    }

    fun onPasswordChange(password: String) {
        _state.value = _state.value.copy(password = password)
    }
    // 新增确认密码输入处理
    fun onConfirmPasswordChange(confirm: String) {
        _state.value = _state.value.copy(confirmPassword = confirm)
    }

    // 注册逻辑
    fun register() {
        viewModelScope.launch (Dispatchers.Main){
            _state.value = _state.value.copy(isLoading = true)
            try {
                val user = LCUser()
                user.username = _state.value.username
                user.password = _state.value.password
                withContext(Dispatchers.IO) {
                    user.signUp()
                }
                _state.value = _state.value.copy(isRegistered = true, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    // 登录逻辑
    fun login() {
        viewModelScope.launch(Dispatchers.Main) {
            _state.value = _state.value.copy(isLoading = true)
            try {
                withContext(Dispatchers.IO) {
                    LCUser.logIn(_state.value.username, _state.value.password).subscribe { user ->
                        OpenCvApp.sharedViewModel!!.userName =user.username
                    }
                }
                _state.value = _state.value.copy(isLoggedIn = true, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, isLoading = false)
            }
        }
    }
}