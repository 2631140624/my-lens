package com.shuzhi.opencv.ui.theme.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shuzhi.opencv.ui.theme.util.LocalToastHostState
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onRegisterClick:()->Unit,
                onLoginSuccess:()->Unit,
                viewModel: AuthViewModel = viewModel()) {
    val state = viewModel.state.value

    Column(Modifier.padding(16.dp).fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("文档扫描与文字识别系统", fontSize = 28.sp)
        Spacer(modifier = Modifier.height(36.dp))
        TextField(
            value = state.username,
            onValueChange = viewModel::onUsernameChange,
            label = { Text("用户名") }
        )
        TextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("密码") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(26.dp))
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {

            Button(
                onClick = viewModel::login,
                enabled = state.username.isNotBlank() && state.password.isNotBlank()
            ) {
                Text("登录")
            }

            Button(
                onClick = { onRegisterClick() },
                enabled = true
            ) {
                Text("注册")
            }
        }
        val toastHostState = LocalToastHostState.current
        val c = rememberCoroutineScope()
        if (state.isLoggedIn){
            c.launch {
                toastHostState.showToast("登录成功")
            }
            onLoginSuccess()
        }
        state.error?.let {
            Text(it, color = Color.Red)
        }
    }
}