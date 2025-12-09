package com.example.toutiaodemo

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView // 👈 关键导入
import androidx.compose.foundation.layout.fillMaxSize // 👈 导入 fillMaxSize

class NewsDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra("url") ?: ""

        setContent {
            // 调用 Composable 函数来显示 WebView
            NewsDetailScreen(url = url)
        }
    }
}

@Composable
fun NewsDetailScreen(url: String) {
    // 使用 AndroidView 来托管和管理传统的 WebView
    AndroidView(
        modifier = Modifier.fillMaxSize(), // 让 WebView 填充整个屏幕
        factory = { context ->
            // 在 factory 块中创建并配置 WebView 实例
            WebView(context).apply {
                // 启用 JavaScript（通常是加载网页所必需的）
                settings.javaScriptEnabled = true

                // 设置 WebViewClient，确保链接在当前 WebView 中打开
                webViewClient = WebViewClient()
            }
        },
        update = { webView ->
            // 在 update 块中加载 URL。当 url 变化时，这里会被重新调用。
            if (url.isNotEmpty()) {
                webView.loadUrl(url)
            }
        }
    )
}