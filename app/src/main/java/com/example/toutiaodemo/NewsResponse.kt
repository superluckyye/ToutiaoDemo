package com.example.toutiaodemo

data class NewsResponse(
    val reason: String?,
    val result: NewsResult?,
    val error_code: Int
)

data class NewsResult(
    val stat: String?,
    val data: List<NewsArticle>?
)
