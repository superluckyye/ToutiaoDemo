package com.example.toutiaodemo

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.text.style.TextAlign
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

// 假设 NewsArticle 数据结构如下，以供参考
// data class NewsArticle(
//     val title: String? = null,
//     val date: String? = null,
//     val author_name: String? = null,
//     val url: String? = null,
//     val thumbnail_pic_s: String? = null
// )

// --------------------------------------------------------------------------------------
// 主屏幕 Composable
// --------------------------------------------------------------------------------------
@Composable
fun HomeScreen(modifier: Modifier = Modifier, viewModel: HomeViewModel = viewModel()) {

    val articles by viewModel.sortedArticles.collectAsState()
    val isRefreshing by viewModel.isLoading.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    Column(modifier = modifier.fillMaxSize()) {

        TopBar()

        // 使用 SwipeRefresh 替代 PullRefresh
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.refreshArticles() }, // 下拉时调用刷新逻辑
            modifier = Modifier.fillMaxSize()
        ) {

            if (isRefreshing && articles.isEmpty()) {
                LoadingView()
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(articles) { index, item ->

                        // 根据有无图片选择卡片类型
                        if (item.thumbnail_pic_s.isNullOrEmpty()) {
                            TextNewsCard(item)
                        } else {
                            BigImageNewsCard(item)
                        }

                        // 🔥 自动触发加载更多 (下滑到最后一条新闻时触发)
                        // 当列表末尾、有更多数据且当前不在加载中时，触发加载更多
                        if (index == articles.lastIndex && hasMore && !isLoadingMore) {
                            SideEffect {
                                viewModel.loadMore() // 调用 ViewModel 的加载更多函数
                            }
                        }
                    }

                    item {
                        // 底部指示器
                        when {
                            isLoadingMore -> LoadingMoreView()
                            !hasMore && articles.isNotEmpty() -> NoMoreView()
                        }
                    }
                }
            }
        } // End of SwipeRefresh
    }
}

// --------------------------------------------------------------------------------------
// UI 组件 (MyScreen 增加了退出登录逻辑)
// --------------------------------------------------------------------------------------

// 顶部栏
@Composable
fun TopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFD84D3C))
            .padding(bottom = 8.dp)
    ) {
        // 第一行：时间/天气/AI问答
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "14° 北京 多云\n空气优",
                color = Color.White,
                fontSize = 12.sp,
                lineHeight = 14.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "AI问答",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x33FFFFFF))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // 第二行：搜索框 (简化)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(36.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Gray,
                modifier = Modifier.padding(start = 12.dp)
            )
            Text(
                text = "学习总书记重要论述 | 乌克兰重...",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, end = 12.dp)
            )
        }

        // 第三行：频道导航 (简化)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("关注", "推荐", "热榜", "新时代", "小说", "视频").forEach { title ->
                Text(
                    text = title,
                    color = if (title == "推荐") Color.White else Color(0x99FFFFFF), // 高亮“推荐”
                    fontSize = 16.sp
                )
            }
        }
    }
}

// 文本新闻卡片 (无图)
@Composable
fun TextNewsCard(article: NewsArticle) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)
            .clickable {
                val intent = Intent(context, NewsDetailActivity::class.java).apply {
                    putExtra("url", article.url ?: "")
                }
                context.startActivity(intent)
            }
    ) {
        Text(article.title ?: "无标题", fontSize = 16.sp, lineHeight = 24.sp)
        Spacer(Modifier.height(6.dp))
        Text("${article.author_name ?: ""}  ${article.date ?: ""}",
            fontSize = 12.sp, color = Color.Gray)
        Divider(modifier = Modifier.padding(top = 8.dp), color = Color(0xFFEEEEEE), thickness = 0.5.dp)
    }
}

// 大图新闻卡片 (有图)
@Composable
fun BigImageNewsCard(article: NewsArticle) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)
            .clickable {
                val intent = Intent(context, NewsDetailActivity::class.java).apply {
                    putExtra("url", article.url ?: "")
                }
                context.startActivity(intent)
            }
    ) {
        Text(article.title ?: "无标题", fontSize = 16.sp, lineHeight = 24.sp)
        Spacer(Modifier.height(6.dp))

        AsyncImage(
            model = article.thumbnail_pic_s,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(6.dp))

        Text("${article.author_name ?: ""}  ${article.date ?: ""}",
            fontSize = 12.sp, color = Color.Gray)
        Divider(modifier = Modifier.padding(top = 8.dp), color = Color(0xFFEEEEEE), thickness = 0.5.dp)
    }
}

// 底部 loading/占位符
@Composable
fun LoadingMoreView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
    }
}

@Composable
fun NoMoreView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("已经到底啦", fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

// 我的页面 (增加了退出登录逻辑)
@Composable
fun MyScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current // 获取 Context 用于导航

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("我的页面", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 32.dp))

        Surface(shape = RoundedCornerShape(50), modifier = Modifier.size(80.dp), color = Color.Gray) {}
        Text("用户昵称", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 8.dp))

        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            onClick = {},
        ) {
            Text("浏览历史", modifier = Modifier.padding(16.dp))
        }
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            onClick = {},
        ) {
            Text("关于头条", modifier = Modifier.padding(16.dp))
        }

        // 🔥 退出登录按钮 - 导航到 LoginActivity 并清除堆栈
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).padding(top = 16.dp),
            onClick = {
                val intent = Intent(context, LoginActivity::class.java)
                // 确保用户无法通过返回键回到主页
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            },
        ) {
            Text("退出登录", modifier = Modifier.padding(16.dp), color = Color.Red, textAlign = TextAlign.Center)
        }
    }
}