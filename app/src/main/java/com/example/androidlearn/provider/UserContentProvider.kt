package com.example.androidlearn.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.Log
import com.example.androidlearn.database.UserDatabaseHelper

/**
 * 自定义 ContentProvider 示例
 *
 * 演示如何创建 ContentProvider 在应用间共享数据
 *
 * ContentProvider 是 Android 中用于跨应用共享数据的标准方式
 * 通过 URI 来标识数据，提供 CRUD 操作
 */
class UserContentProvider : ContentProvider() {

    companion object {
        private const val TAG = "UserContentProvider"
        const val AUTHORITY = "com.example.androidlearn.provider"
        const val TABLE_NAME = "users"

        // URI 匹配码
        private const val USERS = 100      // 匹配所有用户
        private const val USER_ID = 101    // 匹配单个用户（带 ID）

        // 内容 URI
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")

        // 创建 URI 匹配器
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            // content://com.example.androidlearn.provider/users
            addURI(AUTHORITY, TABLE_NAME, USERS)
            // content://com.example.androidlearn.provider/users/#
            addURI(AUTHORITY, "$TABLE_NAME/#", USER_ID)
        }
    }

    private lateinit var dbHelper: UserDatabaseHelper

    /**
     * ContentProvider 创建时调用（应用启动时）
     * 适合进行初始化操作
     */
    override fun onCreate(): Boolean {
        Log.d(TAG, "===== onCreate: ContentProvider 创建 =====")
        val ctx = context ?: return false
        dbHelper = UserDatabaseHelper(ctx)
        return true
    }

    /**
     * 查询数据
     *
     * @param uri 内容 URI
     * @param projection 要返回的列（null 表示所有列）
     * @param selection WHERE 子句
     * @param selectionArgs WHERE 子句的参数
     * @param sortOrder 排序方式
     * @return 查询结果的 Cursor
     */
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Log.d(TAG, "===== query: 查询数据 =====")
        Log.d(TAG, "URI: $uri")

        val db = dbHelper.readableDatabase
        val cursor: Cursor

        when (uriMatcher.match(uri)) {
            USERS -> {
                // 查询所有用户
                cursor = db.query(
                    TABLE_NAME,
                    projection as? Array<String>,
                    selection,
                    selectionArgs as? Array<String>,
                    null,
                    null,
                    sortOrder
                )
            }
            USER_ID -> {
                // 查询单个用户（根据 ID）
                val id = ContentUris.parseId(uri)
                cursor = db.query(
                    TABLE_NAME,
                    projection as? Array<String>,
                    "_id = ?",
                    arrayOf(id.toString()),
                    null,
                    null,
                    sortOrder
                )
            }
            else -> throw IllegalArgumentException("未知 URI: $uri")
        }

        // 设置通知 URI，当数据变化时通知观察者
        context?.contentResolver?.let { cr ->
            cursor.setNotificationUri(cr, uri)
        }
        return cursor
    }

    /**
     * 获取 MIME 类型
     */
    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            USERS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.$TABLE_NAME"
            USER_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.$TABLE_NAME"
            else -> throw IllegalArgumentException("未知 URI: $uri")
        }
    }

    /**
     * 插入数据
     *
     * @param uri 内容 URI
     * @param values 要插入的数据
     * @return 新插入行的 URI
     */
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.d(TAG, "===== insert: 插入数据 =====")

        val db = dbHelper.writableDatabase

        return when (uriMatcher.match(uri)) {
            USERS -> {
                val id = db.insert(TABLE_NAME, null, values)
                if (id != -1L) {
                    val newUri = ContentUris.withAppendedId(CONTENT_URI, id)
                    // 通知数据变化
                    context?.contentResolver?.notifyChange(newUri, null)
                    Log.d(TAG, "插入成功，ID: $id")
                    newUri
                } else {
                    Log.e(TAG, "插入失败")
                    null
                }
            }
            else -> throw IllegalArgumentException("未知 URI: $uri")
        }
    }

    /**
     * 更新数据
     *
     * @param uri 内容 URI
     * @param values 要更新的数据
     * @param selection WHERE 子句
     * @param selectionArgs WHERE 子句的参数
     * @return 更新的行数
     */
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        Log.d(TAG, "===== update: 更新数据 =====")

        val db = dbHelper.writableDatabase
        val count: Int

        when (uriMatcher.match(uri)) {
            USERS -> {
                count = db.update(TABLE_NAME, values, selection, selectionArgs as? Array<String>)
            }
            USER_ID -> {
                val id = ContentUris.parseId(uri)
                count = db.update(
                    TABLE_NAME,
                    values,
                    "_id = ?",
                    arrayOf(id.toString())
                )
            }
            else -> throw IllegalArgumentException("未知 URI: $uri")
        }

        if (count > 0) {
            context?.contentResolver?.notifyChange(uri, null)
            Log.d(TAG, "更新了 $count 行")
        }
        return count
    }

    /**
     * 删除数据
     *
     * @param uri 内容 URI
     * @param selection WHERE 子句
     * @param selectionArgs WHERE 子句的参数
     * @return 删除的行数
     */
    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        Log.d(TAG, "===== delete: 删除数据 =====")

        val db = dbHelper.writableDatabase
        val count: Int

        when (uriMatcher.match(uri)) {
            USERS -> {
                count = db.delete(TABLE_NAME, selection, selectionArgs as? Array<String>)
            }
            USER_ID -> {
                val id = ContentUris.parseId(uri)
                count = db.delete(TABLE_NAME, "_id = ?", arrayOf(id.toString()))
            }
            else -> throw IllegalArgumentException("未知 URI: $uri")
        }

        if (count > 0) {
            context?.contentResolver?.notifyChange(uri, null)
            Log.d(TAG, "删除了 $count 行")
        }
        return count
    }
}