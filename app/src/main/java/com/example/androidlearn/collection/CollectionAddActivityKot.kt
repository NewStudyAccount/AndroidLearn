package com.example.androidlearn.collection

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidlearn.R

class CollectionAddActivityKot : AppCompatActivity() {

    companion object {
        private const val TAG = "CollectionAddKot"
    }

    private lateinit var dbHelper: CollectionDatabaseHelperKot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_collection_add)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 初始化数据库
        dbHelper = CollectionDatabaseHelperKot(this)

        // 初始化 UI
        setupUI()
    }

    private fun setupUI() {
        val etTitle = findViewById<EditText>(R.id.etTitle)
        val rgCategory = findViewById<RadioGroup>(R.id.rgCategory)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val etNote = findViewById<EditText>(R.id.etNote)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            // 1. 读取表单数据
            val title = etTitle.text.toString().trim()
            val category = getCategoryFromRadioGroup(rgCategory)
            val rating = ratingBar.rating
            val note = etNote.text.toString().trim()

            // 2. 校验
            if (title.isEmpty()) {
                Toast.makeText(this, "请输入标题", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (category == null) {
                Toast.makeText(this, "请选择分类", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. 保存到数据库
            val id = dbHelper.insertCollection(title, category, rating, note)
            Log.d(TAG, "保存收藏: $title, ID=$id")

            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()

            // 4. 关闭页面，返回列表
            finish()
        }
    }

    /**
     * 从 RadioGroup 获取选中的分类文本
     */
    private fun getCategoryFromRadioGroup(rg: RadioGroup): String? {
        val checkedId = rg.checkedRadioButtonId
        if (checkedId == -1) return null
        return findViewById<android.widget.RadioButton>(checkedId).text.toString()
    }
}
