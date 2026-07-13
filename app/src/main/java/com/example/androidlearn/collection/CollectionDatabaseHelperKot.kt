package com.example.androidlearn.collection

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * 收藏数据库帮助类（Kotlin 版）
 * 参考 TodoDatabaseHelper 的结构
 */
class CollectionDatabaseHelperKot(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    companion object {
        private const val TAG = "CollectionDBHelperKot"
        const val DATABASE_NAME = "collection_kot.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "collections"

        const val COL_ID = "_id"
        const val COL_TITLE = "title"
        const val COL_CATEGORY = "category"
        const val COL_RATING = "rating"
        const val COL_NOTE = "note"
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "创建数据库")
        val sql = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT NOT NULL,
                $COL_CATEGORY TEXT NOT NULL,
                $COL_RATING REAL DEFAULT 0,
                $COL_NOTE TEXT
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
     * 插入一条收藏
     * @return 新插入行的 ID，失败返回 -1
     */
    fun insertCollection(title: String, category: String, rating: Float, note: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TITLE, title)
            put(COL_CATEGORY, category)
            put(COL_RATING, rating)
            put(COL_NOTE, note)
        }
        val id = db.insert(TABLE_NAME, null, values)
        Log.d(TAG, "插入收藏: $title, ID=$id")
        return id
    }

    /**
     * 查询所有收藏，按 ID 倒序
     */
    fun getAllCollections(): List<CollectionItemKot> {
        return queryCollections(null, null)
    }

    /**
     * 按分类筛选收藏
     */
    fun getCollectionsByCategory(category: String): List<CollectionItemKot> {
        return queryCollections("$COL_CATEGORY = ?", arrayOf(category))
    }

    /**
     * 通用查询方法
     */
    private fun queryCollections(selection: String?, selectionArgs: Array<String>?): List<CollectionItemKot> {
        val list = mutableListOf<CollectionItemKot>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,           // 所有列
            selection,      // WHERE
            selectionArgs,  // WHERE 参数
            null,           // GROUP BY
            null,           // HAVING
            "$COL_ID DESC"  // 按 ID 倒序
        )

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(COL_ID))
                val title = it.getString(it.getColumnIndexOrThrow(COL_TITLE))
                val category = it.getString(it.getColumnIndexOrThrow(COL_CATEGORY))
                val rating = it.getFloat(it.getColumnIndexOrThrow(COL_RATING))
                val note = it.getString(it.getColumnIndexOrThrow(COL_NOTE))
                list.add(CollectionItemKot(id, title, category, rating, note))
            }
        }

        Log.d(TAG, "查询到 ${list.size} 条收藏")
        return list
    }

    /**
     * 根据 ID 删除一条收藏
     */
    fun deleteCollection(id: Int) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COL_ID = ?", arrayOf(id.toString()))
        Log.d(TAG, "删除收藏 ID=$id")
    }
}
