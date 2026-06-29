package com.example.androidlearn.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * 用户数据库帮助类
 *
 * 用于创建和管理 SQLite 数据库
 * 继承 SQLiteOpenHelper 简化数据库操作
 */
class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val TAG = "UserDBHelper"
        const val DATABASE_NAME = "users.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "users"

        // 创建表的 SQL 语句
        private const val SQL_CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                age INTEGER,
                email TEXT
            )
        """

        // 删除表的 SQL 语句
        private const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

    /**
     * 首次创建数据库时调用
     * 在这里创建表结构
     */
    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "===== onCreate: 创建数据库 =====")
        db.execSQL(SQL_CREATE_TABLE)
        Log.d(TAG, "表 $TABLE_NAME 创建成功")

        // 插入一些示例数据
        insertSampleData(db)
    }

    /**
     * 数据库版本升级时调用
     * 在这里执行表结构变更
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "===== onUpgrade: 数据库升级 ($oldVersion -> $newVersion) =====")

        // 简单处理：删除旧表，创建新表
        // 实际项目中应该使用 ALTER TABLE 进行增量更新
        db.execSQL(SQL_DROP_TABLE)
        onCreate(db)
    }

    /**
     * 插入示例数据
     */
    private fun insertSampleData(db: SQLiteDatabase) {
        Log.d(TAG, "插入示例数据")

        val sampleUsers = listOf(
            "('小明', 18, 'xiaoming@example.com')",
            "('小红', 20, 'xiaohong@example.com')",
            "('小刚', 22, 'xiaogang@example.com')",
            "('小丽', 19, 'xiaoli@example.com')",
            "('小华', 21, 'xiaohua@example.com')"
        )

        sampleUsers.forEach { values ->
            db.execSQL("INSERT INTO $TABLE_NAME (name, age, email) VALUES $values")
        }

        Log.d(TAG, "示例数据插入完成")
    }
}
