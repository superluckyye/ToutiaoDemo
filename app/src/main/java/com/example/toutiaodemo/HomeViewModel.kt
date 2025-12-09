package com.example.toutiaodemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map // 👈 新增导入
import kotlinx.coroutines.flow.stateIn // 👈 新增导入
import kotlinx.coroutines.flow.combine // 👈 新增导入，用于数据流操作

// 假设 NewsArticle, RetrofitClient 等类已在项目中定义

class HomeViewModel : ViewModel() {

    private val apiKey = "07e3a8a42e19dcca5f715eea8eb71fe7"
    private var currentPage = 1

    // 原始新闻列表数据流
    private val _articles = MutableStateFlow<List<NewsArticle>>(emptyList())

    // -------------------------
    //  UI 状态流
    // -------------------------

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading // 用于初始加载和下拉刷新

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore // 用于底部加载更多指示器

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore // 用于判断是否已经加载完所有数据

    // -------------------------
    //  排序后的新闻列表 (提供给 HomeScreen 使用)
    //  实现需求：无图新闻在前，有图新闻在后
    // -------------------------
    val sortedArticles: StateFlow<List<NewsArticle>> = _articles
        .map { list ->
            // 1. 无图新闻 (thumbnail_pic_s 为空或空字符串)
            val noImage = list.filter { it.thumbnail_pic_s.isNullOrEmpty() }
            // 2. 有图新闻 (thumbnail_pic_s 非空)
            val withImage = list.filter { !it.thumbnail_pic_s.isNullOrEmpty() }

            // 合并并返回：无图 + 有图
            noImage + withImage
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // -------------------------
    // ViewModel 初始化自动加载
    // -------------------------
    init {
        loadArticles()
    }

    // -------------------------
    //  初始加载 / 下拉刷新逻辑
    // -------------------------
    fun loadArticles(isRefresh: Boolean = false) {
        if (!isRefresh && currentPage > 1) return // 初始数据只加载一次

        viewModelScope.launch {
            if (isRefresh) {
                // 如果是刷新，设置加载状态
                _isLoading.value = true
                currentPage = 1 // 重置页码
                _hasMore.value = true // 重置“还有更多”状态
            }

            try {
                // 确保在初始加载或刷新时设置加载状态
                if (currentPage == 1) _isLoading.value = true

                val response = RetrofitClient.apiService.getNews(
                    apiKey = apiKey,
                    type = "top",
                    page = currentPage,
                    pageSize = 30
                )

                val list = response.result?.data ?: emptyList()

                // 如果是刷新，覆盖旧列表；否则，追加列表
                if (isRefresh) {
                    _articles.value = list
                } else if (currentPage == 1) {
                    _articles.value = list
                }

                // 只有成功后才增加页码
                if (list.isNotEmpty()) currentPage++

                // 判断是否还有更多数据 (假设 pageSize=30)
                _hasMore.value = list.size >= 30

                Log.d("API", "新闻加载成功，数量 = ${list.size}，当前页 = ${currentPage - 1}")

            } catch (e: Exception) {
                Log.e("API", "加载失败: $e")
                // 如果加载失败且当前列表为空，保持 loading 状态
                if (_articles.value.isEmpty()) _isLoading.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    // -------------------------
    //  下拉刷新入口
    // -------------------------
    fun refreshArticles() {
        // 重置列表，避免在刷新期间显示旧数据
        _articles.value = emptyList()
        loadArticles(isRefresh = true)
    }

    // -------------------------
    //  加载更多逻辑
    // -------------------------
    fun loadMore() {
        if (!_hasMore.value || _isLoadingMore.value || _isLoading.value) return // 检查状态

        viewModelScope.launch {
            try {
                _isLoadingMore.value = true

                val response = RetrofitClient.apiService.getNews(
                    apiKey = apiKey,
                    type = "top",
                    page = currentPage,
                    pageSize = 10
                )

                val newList = response.result?.data ?: emptyList()

                // 追加到原始文章列表
                _articles.value = _articles.value + newList

                // 只有成功获取数据才递增页码
                if (newList.isNotEmpty()) currentPage++

                // 判断是否还有更多数据
                _hasMore.value = newList.size >= 30

                Log.d("API", "加载更多成功，新增数量 = ${newList.size}，当前页 = ${currentPage - 1}")

            } catch (e: Exception) {
                Log.e("API", "加载更多失败: $e")
            } finally {
                _isLoadingMore.value = false
            }
        }
    }
}