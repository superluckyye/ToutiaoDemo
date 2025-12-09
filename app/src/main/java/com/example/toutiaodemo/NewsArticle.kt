package com.example.toutiaodemo

data class NewsArticle(
    val title: String?,
    val content: String?,  // Assuming articles have content
    val date: String?,
    val author_name: String?,
    val thumbnail_pic_s: String?,
    val thumbnail_pic_s02: String?,
    val thumbnail_pic_s03: String?,
    val url: String?
)
