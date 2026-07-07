package com.example.androidlearn.todo


/**
 * 待办事项数据类
 *
 * 对应数据库中的 todos 表，每一条记录就是一个待办事项。
 * 和你之前写的 UserItem 类似，只是一个普通的 data class。
 */

data class TodoItem(
    val id: Int = 0,            // 数据库自增主键，默认 0（插入时由数据库赋值）
    val title: String,          // 待办内容
    val isDone: Boolean = false, // 是否完成，默认未完成
    val createdAt: Long = System.currentTimeMillis()  // 创建时间戳
)
