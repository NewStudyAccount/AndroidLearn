package com.example.androidlearn.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidlearn.R

/**
 * 选择用户 Activity
 *
 * 演示如何返回数据给上一个 Activity
 */
class PickUserActivity : AppCompatActivity() {

    private val users = listOf("小明", "小红", "小刚", "小丽", "小华")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_user)

        val listView = findViewById<ListView>(R.id.listView)

        // 设置列表数据
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, users)
        listView.adapter = adapter

        // 点击用户返回结果
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedUser = users[position]

            // 创建返回的 Intent
            val resultIntent = Intent().apply {
                putExtra("selected_user", selectedUser)
            }

            // 设置结果并关闭
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        // 取消按钮
        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}
