package com.example.androidlearn.activity

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidlearn.R

/**
 * 接收数据的 Activity
 *
 * 演示如何从 Intent 中获取传递过来的数据
 */
class ReceiveDataActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive_data)

        val tvResult = findViewById<TextView>(R.id.tvResult)
        val sb = StringBuilder()

        // 方式一：接收简单数据
        val name = intent.getStringExtra("name")
        if (name != null) {
            sb.appendLine("=== 接收简单数据 ===")
            sb.appendLine("姓名: $name")
            sb.appendLine("年龄: ${intent.getIntExtra("age", 0)}")
            sb.appendLine("分数: ${intent.getDoubleExtra("score", 0.0)}")
            sb.appendLine("是学生: ${intent.getBooleanExtra("is_student", false)}")
            sb.appendLine()
        }

        // 方式二：接收 Bundle 数据
        val bundle = intent.extras
        if (bundle != null && bundle.containsKey("name")) {
            sb.appendLine("=== 接收 Bundle 数据 ===")
            sb.appendLine("姓名: ${bundle.getString("name")}")
            sb.appendLine("年龄: ${bundle.getInt("age")}")
            sb.appendLine("分数: ${bundle.getDouble("score")}")
            sb.appendLine()
        }

        // 方式三：接收对象
        val user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("user", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<User>("user")
        }
        if (user != null) {
            sb.appendLine("=== 接收对象 ===")
            sb.appendLine("姓名: ${user.name}")
            sb.appendLine("年龄: ${user.age}")
            sb.appendLine("邮箱: ${user.email}")
        }

        tvResult.text = sb.toString()
    }
}