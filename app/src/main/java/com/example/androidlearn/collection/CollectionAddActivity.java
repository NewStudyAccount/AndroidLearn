package com.example.androidlearn.collection;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidlearn.R;

public class CollectionAddActivity extends AppCompatActivity {

    private static final String TAG = "CollectionAdd";

    private CollectionDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_collection_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化数据库
        dbHelper = new CollectionDatabaseHelper(this);

        // 初始化 UI
        setupUI();
    }

    private void setupUI() {
        EditText etTitle = findViewById(R.id.etTitle);
        RadioGroup rgCategory = findViewById(R.id.rgCategory);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        EditText etNote = findViewById(R.id.etNote);
        Button btnSave = findViewById(R.id.btnSave);

        // "保存"按钮点击事件
        btnSave.setOnClickListener(v -> {
            // 1. 读取表单数据
            String title = etTitle.getText().toString().trim();
            String category = getCategoryFromRadioGroup(rgCategory);
            float rating = ratingBar.getRating();
            String note = etNote.getText().toString().trim();

            // 2. 校验：标题和分类必填
            if (title.isEmpty()) {
                Toast.makeText(this, "请输入标题", Toast.LENGTH_SHORT).show();
                return;
            }
            if (category == null) {
                Toast.makeText(this, "请选择分类", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. 保存到数据库
            long id = dbHelper.insertCollection(title, category, rating, note);
            Log.d(TAG, "保存收藏: " + title + ", ID=" + id);

            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();

            // 4. 关闭页面，返回列表
            finish();
        });
    }

    /**
     * 从 RadioGroup 获取选中的分类文本
     */
    private String getCategoryFromRadioGroup(RadioGroup rg) {
        int checkedId = rg.getCheckedRadioButtonId();
        if (checkedId == -1) {
            return null;
        }
        return findViewById(checkedId).getTag() != null
                ? findViewById(checkedId).getTag().toString()
                : ((android.widget.RadioButton) findViewById(checkedId)).getText().toString();
    }
}
