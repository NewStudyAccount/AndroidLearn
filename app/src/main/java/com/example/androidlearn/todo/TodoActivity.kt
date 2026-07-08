package com.example.androidlearn.todo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlearn.R

class TodoActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "TodoActivity"
    }

    private lateinit var dbHelper: TodoDatabaseHelper
    private lateinit var adapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)

        // 1. 初始化数据库
        dbHelper = TodoDatabaseHelper(this)

        // 2. 初始化 UI
        setupUI()

        // 3. 加载数据
        loadTodos()
    }

    private fun setupUI() {
        val etInput = findViewById<EditText>(R.id.etTodoInput)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val rvTodos = findViewById<RecyclerView>(R.id.rvTodos)

        // 创建适配器
        adapter = TodoAdapter(
            todos = mutableListOf(),
            onToggleDone = { todo ->
                // 切换完成状态，然后刷新列表
                dbHelper.updateTodoDone(todo.id, !todo.isDone)
                loadTodos()  // 重新查询数据库，刷新列表
            },
            onDelete = { todo ->
                // 弹出确认对话框
                AlertDialog.Builder(this)
                    .setTitle("确认删除")
                    .setMessage("确定删除「${todo.title}」？")
                    .setPositiveButton("删除") { _, _ ->
                        dbHelper.deleteTodo(todo.id)
                        loadTodos()
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
        )

        // 设置 RecyclerView
        rvTodos.layoutManager = LinearLayoutManager(this)
        rvTodos.adapter = adapter

        // 添加按钮点击事件
        btnAdd.setOnClickListener {
            val title = etInput.text.toString().trim()
            if (title.isNotEmpty()) {
                dbHelper.insertTodo(title)
                etInput.text.clear()   // 清空输入框
                loadTodos()            // 刷新列表
                Log.d(TAG, "添加待办: $title")
            } else {
                Toast.makeText(this, "请输入待办内容", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 从数据库加载所有待办，更新列表
     */
    private fun loadTodos() {
        val todos = dbHelper.getAllTodos()
        adapter.updateData(todos)
        Log.d(TAG, "加载了 ${todos.size} 条待办")
    }
}