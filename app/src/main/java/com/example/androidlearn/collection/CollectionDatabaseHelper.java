package com.example.androidlearn.collection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 收藏数据库帮助类
 * 参考 TodoDatabaseHelper 的结构，增加按分类筛选的查询
 */
public class CollectionDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "CollectionDBHelper";
    private static final String DATABASE_NAME = "collection.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "collections";

    // 列名常量
    public static final String COL_ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_CATEGORY = "category";
    public static final String COL_RATING = "rating";
    public static final String COL_NOTE = "note";

    public CollectionDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "创建数据库");
        String sql = "CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT NOT NULL, "
                + COL_CATEGORY + " TEXT NOT NULL, "
                + COL_RATING + " REAL DEFAULT 0, "
                + COL_NOTE + " TEXT"
                + ")";
        db.execSQL(sql);
        Log.d(TAG, "表 " + TABLE_NAME + " 创建成功");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // ========== 增删改查方法 ==========

    /**
     * 插入一条收藏
     * @return 新插入行的 ID，失败返回 -1
     */
    public long insertCollection(String title, String category, float rating, String note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, title);
        values.put(COL_CATEGORY, category);
        values.put(COL_RATING, rating);
        values.put(COL_NOTE, note);
        long id = db.insert(TABLE_NAME, null, values);
        Log.d(TAG, "插入收藏: " + title + ", ID=" + id);
        return id;
    }

    /**
     * 查询所有收藏，按 ID 倒序（最新的在前面）
     */
    public List<CollectionItem> getAllCollections() {
        return queryCollections(null, null);
    }

    /**
     * 按分类筛选收藏
     */
    public List<CollectionItem> getCollectionsByCategory(String category) {
        return queryCollections(COL_CATEGORY + " = ?", new String[]{category});
    }

    /**
     * 通用查询方法
     */
    private List<CollectionItem> queryCollections(String selection, String[] selectionArgs) {
        List<CollectionItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                null,           // 所有列
                selection,      // WHERE
                selectionArgs,  // WHERE 参数
                null,           // GROUP BY
                null,           // HAVING
                COL_ID + " DESC" // 按 ID 倒序
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY));
                float rating = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_RATING));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTE));
                list.add(new CollectionItem(id, title, category, rating, note));
            }
            cursor.close();
        }

        Log.d(TAG, "查询到 " + list.size() + " 条收藏");
        return list;
    }

    /**
     * 根据 ID 删除一条收藏
     */
    public void deleteCollection(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(id)});
        Log.d(TAG, "删除收藏 ID=" + id);
    }
}
