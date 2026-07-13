package com.example.androidlearn.collection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidlearn.R;

import java.util.List;

public class CollectionMainActivity extends AppCompatActivity {

    private static final String TAG = "CollectionMain";

    private CollectionDatabaseHelper dbHelper;
    private CollectionAdapter adapter;

    // 当前筛选分类，null 表示"全部"
    private String currentFilter = null;

    // 分类按钮引用，用于高亮切换
    private Button btnAll, btnFilm, btnBook, btnMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_collection_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. 初始化数据库
        dbHelper = new CollectionDatabaseHelper(this);

        // 2. 初始化 UI
        setupUI();

        // 3. 加载数据
        loadCollections();
    }

    /**
     * 从 AddActivity 返回后刷新列表
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadCollections();
    }

    private void setupUI() {
        RecyclerView rvCollection = findViewById(R.id.rvCollection);
        Button btnAdd = findViewById(R.id.btnAdd);

        // 分类按钮
        btnAll = findViewById(R.id.btnAll);
        btnFilm = findViewById(R.id.btnFilm);
        btnBook = findViewById(R.id.btnBook);
        btnMusic = findViewById(R.id.btnMusic);

        // 创建适配器
        adapter = new CollectionAdapter(
                new java.util.ArrayList<>(),
                item -> showMoreDialog(item)
        );

        // 设置 RecyclerView
        rvCollection.setLayoutManager(new LinearLayoutManager(this));
        rvCollection.setAdapter(adapter);

        // "添加"按钮 -> 跳转到添加页面
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, CollectionAddActivity.class);
            startActivity(intent);
        });

        // 分类筛选按钮
        btnAll.setOnClickListener(v -> filterBy(null, btnAll));
        btnFilm.setOnClickListener(v -> filterBy("电影", btnFilm));
        btnBook.setOnClickListener(v -> filterBy("书籍", btnBook));
        btnMusic.setOnClickListener(v -> filterBy("音乐", btnMusic));

        // 默认高亮"全部"
        updateTabStyle(btnAll);
    }

    /**
     * 按分类筛选并刷新列表
     */
    private void filterBy(String category, Button activeBtn) {
        currentFilter = category;
        updateTabStyle(activeBtn);
        loadCollections();
        Log.d(TAG, "筛选分类: " + (category == null ? "全部" : category));
    }

    /**
     * 更新分类按钮的高亮状态
     * 选中的按钮设为不可点击（视觉上表示选中），其余恢复可点击
     */
    private void updateTabStyle(Button activeBtn) {
        btnAll.setEnabled(activeBtn != btnAll);
        btnFilm.setEnabled(activeBtn != btnFilm);
        btnBook.setEnabled(activeBtn != btnBook);
        btnMusic.setEnabled(activeBtn != btnMusic);
    }

    /**
     * "更多"按钮弹窗：查看详情 / 删除
     */
    private void showMoreDialog(CollectionItem item) {
        new AlertDialog.Builder(this)
                .setTitle(item.getTitle())
                .setMessage("分类: " + item.getCategory()
                        + "\n评分: " + item.getRating()
                        + "\n\n备注: " + item.getNote())
                .setPositiveButton("删除", (dialog, which) -> {
                    dbHelper.deleteCollection(item.getId());
                    loadCollections();
                    Log.d(TAG, "删除收藏: " + item.getTitle());
                })
                .setNegativeButton("关闭", null)
                .show();
    }

    /**
     * 从数据库加载收藏列表
     */
    private void loadCollections() {
        List<CollectionItem> list;
        if (currentFilter == null) {
            list = dbHelper.getAllCollections();
        } else {
            list = dbHelper.getCollectionsByCategory(currentFilter);
        }
        adapter.updateData(list);
        Log.d(TAG, "加载了 " + list.size() + " 条收藏");
    }
}
