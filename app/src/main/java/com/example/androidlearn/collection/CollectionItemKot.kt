package com.example.androidlearn.collection

/**
 * 收藏条目数据模型（Kotlin 版）
 */
data class CollectionItemKot(
    val id: Int = 0,
    val title: String,
    val category: String,   // 电影 / 书籍 / 音乐
    val rating: Float = 0f,
    val note: String = ""
)
