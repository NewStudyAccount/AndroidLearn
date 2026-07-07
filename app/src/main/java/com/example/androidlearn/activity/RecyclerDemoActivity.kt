package com.example.androidlearn.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlearn.R
import com.example.androidlearn.provider.UserContentProvider

/**
 * RecyclerView + ContentProvider 综合演示
 *
 * 展示如何用 RecyclerView 列表显示 ContentProvider 查询到的数据
 * - RecyclerView: Android 最常用的列表控件
 * - ContentProvider: 跨应用数据共享标准方式
 */
class RecyclerDemoActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "RecyclerDemoActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_demo)

        // 1. 获取 RecyclerView 并设置布局管理器
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 2. 从 ContentProvider 查询数据
        val users = loadUsers()

        // 3. 设置适配器
        recyclerView.adapter = UserAdapter(users)

        Log.d(TAG, "加载了 ${users.size} 条用户数据")
    }

    private fun loadUsers(): List<UserItem> {
        val users = mutableListOf<UserItem>()

        val cursor = contentResolver.query(
            UserContentProvider.CONTENT_URI,
            null, null, null, null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow("_id"))
                val name = it.getString(it.getColumnIndexOrThrow("name"))
                val age = it.getInt(it.getColumnIndexOrThrow("age"))
                val email = it.getString(it.getColumnIndexOrThrow("email"))
                users.add(UserItem(id, name, age, email))
            }
        }

        return users
    }
}
