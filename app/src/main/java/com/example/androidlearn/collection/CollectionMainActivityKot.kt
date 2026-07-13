package com.example.androidlearn.collection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlearn.R

class CollectionMainActivityKot : AppCompatActivity() {

    //Kotlin中用于定义与类关联的单例对象，类似于Java中的static成员
    companion object {
        private const val TAG = "CollectionMainKot"
    }

    private lateinit var dbHelper: CollectionDatabaseHelperKot
    private lateinit var adapter: CollectionAdapterKot

    // 当前筛选分类，null 表示"全部"
    private var currentFilter: String? = null

    // 分类按钮引用
    private lateinit var btnAll: Button
    private lateinit var btnFilm: Button
    private lateinit var btnBook: Button
    private lateinit var btnMusic: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_collection_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. 初始化数据库
        dbHelper = CollectionDatabaseHelperKot(this)

        // 2. 初始化 UI
        setupUI()

        // 3. 加载数据
        loadCollections()
    }

    override fun onResume() {
        super.onResume()
        loadCollections()
    }

    private fun setupUI() {
        val rvCollection = findViewById<RecyclerView>(R.id.rvCollection)
        val btnAdd = findViewById<Button>(R.id.btnAdd)

        btnAll = findViewById(R.id.btnAll)
        btnFilm = findViewById(R.id.btnFilm)
        btnBook = findViewById(R.id.btnBook)
        btnMusic = findViewById(R.id.btnMusic)

        // 创建适配器
        adapter = CollectionAdapterKot(
            items = emptyList(),
            onMoreClick = { item -> showMoreDialog(item) }
        )

        // 设置 RecyclerView
        rvCollection.layoutManager = LinearLayoutManager(this)
        rvCollection.adapter = adapter

        // "添加"按钮 -> 跳转到添加页面
        btnAdd.setOnClickListener {
            startActivity(Intent(this, CollectionAddActivityKot::class.java))
        }

        // 分类筛选按钮
        btnAll.setOnClickListener { filterBy(null, btnAll) }
        btnFilm.setOnClickListener { filterBy("电影", btnFilm) }
        btnBook.setOnClickListener { filterBy("书籍", btnBook) }
        btnMusic.setOnClickListener { filterBy("音乐", btnMusic) }

        // 默认高亮"全部"
        updateTabStyle(btnAll)
    }

    /**
     * 按分类筛选并刷新列表
     */
    private fun filterBy(category: String?, activeBtn: Button) {
        currentFilter = category
        updateTabStyle(activeBtn)
        loadCollections()
        Log.d(TAG, "筛选分类: ${category ?: "全部"}")
    }

    /**
     * 更新分类按钮的高亮状态
     */
    private fun updateTabStyle(activeBtn: Button) {
        btnAll.isEnabled = activeBtn != btnAll
        btnFilm.isEnabled = activeBtn != btnFilm
        btnBook.isEnabled = activeBtn != btnBook
        btnMusic.isEnabled = activeBtn != btnMusic
    }

    /**
     * "更多"按钮弹窗：查看详情 / 删除
     */
    private fun showMoreDialog(item: CollectionItemKot) {
        AlertDialog.Builder(this)
            .setTitle(item.title)
            .setMessage("分类: ${item.category}\n评分: ${item.rating}\n\n备注: ${item.note}")
            .setPositiveButton("删除") { _, _ ->
                dbHelper.deleteCollection(item.id)
                loadCollections()
                Log.d(TAG, "删除收藏: ${item.title}")
            }
            .setNegativeButton("关闭", null)
            .show()
    }

    /**
     * 从数据库加载收藏列表
     */
    private fun loadCollections() {
        val list = if (currentFilter == null) {
            dbHelper.getAllCollections()
        } else {
            dbHelper.getCollectionsByCategory(currentFilter!!)
        }
        adapter.updateData(list)
        Log.d(TAG, "加载了 ${list.size} 条收藏")
    }
}
