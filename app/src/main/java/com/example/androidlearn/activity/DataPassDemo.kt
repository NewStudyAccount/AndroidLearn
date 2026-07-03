package com.example.androidlearn.activity

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.androidlearn.R
import kotlinx.parcelize.Parcelize

/**
 * Activity 数据传递演示
 *
 * 演示内容：
 * 1. 使用 Intent 传递简单数据
 * 2. 使用 Bundle 传递数据
 * 3. 传递对象（Parcelable）
 * 4. 获取 Activity 返回结果
 */
class DataPassDemo : AppCompatActivity() {

    // 注册结果回调
    private val pickUserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val userName = result.data?.getStringExtra("selected_user") ?: ""
            Toast.makeText(this, "选择了用户: $userName", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_pass_demo)

        // 方式一：传递简单数据
        findViewById<Button>(R.id.btnPassSimple).setOnClickListener {
            val intent = Intent(this, ReceiveDataActivity::class.java)

            // 使用 putExtra 传递各种类型数据
            intent.putExtra("name", "小明")
            intent.putExtra("age", 18)
            intent.putExtra("score", 95.5)
            intent.putExtra("is_student", true)

            startActivity(intent)
        }

        // 方式二：使用 Bundle 传递数据
        findViewById<Button>(R.id.btnPassBundle).setOnClickListener {
            val intent = Intent(this, ReceiveDataActivity::class.java)

            val bundle = Bundle().apply {
                putString("name", "小红")
                putInt("age", 20)
                putDouble("score", 88.0)
            }
            intent.putExtras(bundle)

            startActivity(intent)
        }

        // 方式三：传递对象
        findViewById<Button>(R.id.btnPassObject).setOnClickListener {
            val user = User("小明", 18, "xiaoming@example.com")
            val intent = Intent(this, ReceiveDataActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }

        // 方式四：获取返回结果
        findViewById<Button>(R.id.btnGetResult).setOnClickListener {
            val intent = Intent(this, PickUserActivity::class.java)
            pickUserLauncher.launch(intent)
        }
    }
}

/**
 * 数据类，实现 Parcelable 接口以支持 Intent 传递
 *
 * 使用 @Parcelize 注解自动生成 Parcelable 实现代码
 * 需要在 build.gradle.kts 中添加 kotlin-parcelize 插件
 */
@Parcelize
data class User(
    val name: String,
    val age: Int,
    val email: String
) : Parcelable