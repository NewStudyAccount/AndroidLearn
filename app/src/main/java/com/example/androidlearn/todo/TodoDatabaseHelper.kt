package com.example.androidlearn.todo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


/**
 * 待办事项数据库帮助类
 *
 * 和你之前写的 UserDatabaseHelper 结构几乎一样。
 * 区别在于：UserDatabaseHelper 只有查询，这里增加了增删改查全套操作。
 */
class TodoDatabaseHelper(context: Context): SQLiteOpenHelper (
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
){
    companion object {
        private const val TAG = "TodoDBHelper"
        const val DATABASE_NAME = "todo.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "todos"

        // 列名常量，避免手写字符串出错
        const val COL_ID = "_id"
        const val COL_TITLE = "title"
        const val COL_IS_DONE = "is_done"
        const val COL_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "创建数据库")
        val sql = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT NOT NULL,
                $COL_IS_DONE INTEGER DEFAULT 0,
                $COL_CREATED_AT INTEGER
            )
        """.trimIndent()
        db.execSQL(sql)
        Log.d(TAG, "表 $TABLE_NAME 创建成功")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // ========== 增删改查方法 ==========

    /**
     * 插入一条待办
     * @return 新插入行的 ID，失败返回 -1
     */
    fun insertTodo(title: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TITLE, title)
            put(COL_IS_DONE, 0)  // 0 表示未完成
            put(COL_CREATED_AT, System.currentTimeMillis())
        }
        val id = db.insert(TABLE_NAME, null, values)
        Log.d(TAG, "插入待办: $title, ID=$id")
        return id
    }

    /**
     * 查询所有待办，按创建时间倒序（最新的在前面）
     */
    fun getAllTodos(): List<TodoItem> {
        val todos = mutableListOf<TodoItem>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,       // 所有列
            null,       // 没有 WHERE
            null,       // 没有参数
            null,       // 没有 GROUP BY
            null,       // 没有 HAVING
            "$COL_CREATED_AT DESC"  // 按创建时间倒序
        )

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COL_ID))
                val title = it.getString(it.getColumnIndexOrThrow(COL_TITLE))
                val isDone = it.getInt(it.getColumnIndexOrThrow(COL_IS_DONE)) == 1
                val createdAt = it.getLong(it.getColumnIndexOrThrow(COL_CREATED_AT))
                todos.add(TodoItem(id, title, isDone, createdAt))
            }
        }

        Log.d(TAG, "查询到 ${todos.size} 条待办")
        return todos
    }

    /**
     * 更新待办的完成状态
     */
    fun updateTodoDone(id: Int, isDone: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_IS_DONE, if (isDone) 1 else 0)
        }
        db.update(TABLE_NAME, values, "$COL_ID = ?", arrayOf(id.toString()))
        Log.d(TAG, "更新待办 ID=$id, isDone=$isDone")
    }

    /**
     * 根据 ID 删除一条待办
     */
    fun deleteTodo(id: Int) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COL_ID = ?", arrayOf(id.toString()))
        Log.d(TAG, "删除待办 ID=$id")
    }

}