package com.example.toutiaodemo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
// 移除了 KeyboardOptions 和 KeyboardType 导入以确保编译通过
// 如果您想重新添加键盘配置，请在解决依赖问题后，自行取消注释并重新添加：
// import androidx.compose.ui.text.input.KeyboardOptions
// import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LoginScreen()
            }
        }
    }
}

private const val PRESET_USERNAME = "yyy"
private const val PRESET_PASSWORD = "yeyiyang"

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("头条登录", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(bottom = 40.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("账号") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User Icon") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock Icon") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            // keyboardOptions 参数已被移除
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle password visibility")
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        Button(
            onClick = {
                if (username == PRESET_USERNAME && password == PRESET_PASSWORD) {
                    Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                    (context as? ComponentActivity)?.finish()
                } else {
                    Toast.makeText(context, "账号或密码错误", Toast.LENGTH_SHORT).show()
                }
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("登录", style = MaterialTheme.typography.titleMedium)
        }

        // 🔥 增加注册和忘记密码链接
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "注册新账号",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    Toast.makeText(context, "跳转到注册页面 (Placeholder)", Toast.LENGTH_SHORT).show()
                }
            )
            Text(
                text = "忘记密码",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    Toast.makeText(context, "跳转到找回密码 (Placeholder)", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}