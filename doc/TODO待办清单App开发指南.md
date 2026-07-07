# TODO 待办清单 App 开发指南

> 本文档指导你在现有 AndroidLearn 项目中，**只用已学技术**构建 TODO 待办清单 App。
> 不需要添加任何新依赖，不需要修改 gradle 配置。
>
> 核心技术：SQLiteOpenHelper + RecyclerView.Adapter + Activity
> 这三样你已经全部学过了，现在把它们组合起来做一个完整功能。

---

## 一、你将学到什么

| 知识点 | 说明 | 你在项目中的已有经验 |
|-------|------|-------------------|
| SQLiteOpenHelper | 本地数据库 | `database/UserDatabaseHelper.kt` 已经用过 |
| RecyclerView.Adapter | 列表适配器 | `activity/UserAdapter.kt` 已经写过 |
| Activity 页面交互 | 点击事件、页面跳转 | `MainActivity.kt` 每天都在用 |
| CRUD 综合运用 | 增删改查一体化 | 这是本教程的新挑战 |

**你不需要学任何新东西**，只是把已学的知识串起来。

---

## 二、整体架构

```
用户操作：输入待办 -> 点击添加 -> 列表显示 -> 勾选完成 / 左滑删除

TodoActivity（页面）
    |--- etTodoInput + btnAdd      （输入区域）
    |--- RecyclerView               （列表区域）
    |       |--- TodoAdapter         （适配器）
    |               |--- TodoItem    （数据类）
    |
    |--- TodoDatabaseHelper          （数据库）
            |--- todos 表             （存储待办数据）
```

一共需要创建 **6 个新文件**，不修改任何已有文件的 gradle 配置。

---

## 三、步骤一：创建数据模型（TodoItem）

### 3.1 新建文件

在 `app/src/main/java/com/example/androidlearn/todo/` 包下新建 `TodoItem.kt`。

（如果没有 `todo` 包，右键 `androidlearn` -> New -> Package -> 输入 `todo`）

### 3.2 写什么

```kotlin
package com.example.androidlearn.todo

/**
 * 待办事项数据类
 *
 * 对应数据库中的 todos 表，每一条记录就是一个待办事项。
 * 和你之前写的 UserItem 类似，只是一个普通的 data class。
 */
data class TodoItem(
    val id: Int = 0,            // 数据库自增主键，默认 0（插入时由数据库赋值）
    val title: String,          // 待办内容
    val isDone: Boolean = false, // 是否完成，默认未完成
    val createdAt: Long = System.currentTimeMillis()  // 创建时间戳
)
```

### 3.3 与你之前的 UserItem 对比

```kotlin
// 你之前写的 UserItem（在 UserAdapter.kt 中）
data class UserItem(val id: Int, val name: String, val age: Int, val email: String)

// 现在写的 TodoItem
data class TodoItem(val id: Int, val title: String, val isDone: Boolean, val createdAt: Long)
```

结构完全一样，只是字段不同。`isDone` 用来记录待办是否完成，`createdAt` 用来按时间排序。

---

## 四、步骤二：创建数据库帮助类（TodoDatabaseHelper）

### 4.1 新建文件

在 `todo` 包下新建 `TodoDatabaseHelper.kt`。

### 4.2 写什么

```kotlin
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
class TodoDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

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
```

### 4.3 与你之前的 UserDatabaseHelper 对比

| | UserDatabaseHelper | TodoDatabaseHelper |
|---|---|---|
| 建表 | `onCreate` 中建表 | 一样 |
| 插入 | `insertSampleData` 写死示例数据 | `insertTodo` 由用户输入 |
| 查询 | 无（只在 ContentProvider 中查） | `getAllTodos` 返回列表 |
| 更新 | 无 | `updateTodoDone` 修改完成状态 |
| 删除 | 无 | `deleteTodo` 删除指定记录 |

**新知识：`ContentValues`**
- 这是 Android 提供的键值对容器，专门用于数据库插入和更新
- 类似 `Bundle`，但只用于数据库操作
- `put("列名", 值)` 设置要插入/更新的数据

**新知识：`cursor.getColumnIndexOrThrow("列名")`**
- 你之前用的是 `cursor.getColumnIndex("列名")`，找不到时返回 -1
- `getColumnIndexOrThrow` 找不到时直接抛异常，更安全

---

## 五、步骤三：创建页面布局

### 5.1 新建文件

在 `app/src/main/res/layout/` 下新建 `activity_todo.xml`。

### 5.2 设计思路

页面从上到下：

```
+-----------------------------+
|  [输入框___________] [添加]   |  <- 输入区域
+-----------------------------+
|                             |
|  RecyclerView                |  <- 待办列表
|   O  买菜做饭          14:30  |
|   O  写作业            14:25  |
|   X  ~~跑步~~          14:00  |
|                             |
+-----------------------------+
```

### 5.3 写什么

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <!-- 标题 -->
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="待办清单"
        android:textSize="24sp"
        android:textStyle="bold"
        android:layout_marginBottom="16dp" />

    <!-- 输入区域 -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">

        <EditText
            android:id="@+id/etTodoInput"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:hint="输入新的待办事项..."
            android:inputType="text"
            android:maxLines="1" />

        <Button
            android:id="@+id/btnAdd"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="添加"
            android:layout_marginStart="8dp" />
    </LinearLayout>

    <!-- 待办列表 -->
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rvTodos"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:layout_marginTop="16dp" />

</LinearLayout>
```

### 5.4 要点

- 和你之前写的 `activity_recycler_demo.xml` 结构类似
- 多了一个输入区域（水平 LinearLayout 包含 EditText + Button）
- `layout_weight="1"` 让 RecyclerView 占据剩余空间

---

## 六、步骤四：创建列表项布局

### 6.1 新建文件

在 `layout` 下新建 `item_todo.xml`。

### 6.2 写什么

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:gravity="center_vertical"
    android:padding="12dp"
    android:layout_marginVertical="4dp"
    android:background="#FFF5F5F5">

    <!-- 复选框：勾选表示已完成 -->
    <CheckBox
        android:id="@+id/cbDone"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

    <!-- 待办标题 -->
    <TextView
        android:id="@+id/tvTitle"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:textSize="16sp"
        android:layout_marginStart="8dp" />

    <!-- 创建时间 -->
    <TextView
        android:id="@+id/tvTime"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="12sp"
        android:textColor="#FF999999" />

    <!-- 删除按钮 -->
    <Button
        android:id="@+id/btnDelete"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="删除"
        android:textSize="12sp"
        android:layout_marginStart="8dp" />

</LinearLayout>
```

### 6.3 与你之前 item_user.xml 的对比

| | item_user.xml | item_todo.xml |
|---|---|---|
| 显示内容 | 姓名 + 年龄 + 邮箱 | 复选框 + 标题 + 时间 + 删除按钮 |
| 交互 | 无 | 复选框可点击、删除按钮可点击 |
| 布局 | 垂直排列 | 水平排列 |

---

## 七、步骤五：创建适配器（TodoAdapter）

### 7.1 新建文件

在 `todo` 包下新建 `TodoAdapter.kt`。

### 7.2 写什么

```kotlin
package com.example.androidlearn.todo

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlearn.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 待办事项适配器
 *
 * 和你之前写的 UserAdapter 结构完全一样，
 * 只是多了两个交互：复选框点击 和 删除按钮点击。
 *
 * @param todos 待办列表数据
 * @param onToggleDone 勾选/取消勾选时的回调（传给 Activity 处理数据库更新）
 * @param onDelete 点击删除时的回调（传给 Activity 处理数据库删除）
 */
class TodoAdapter(
    private var todos: MutableList<TodoItem>,
    private val onToggleDone: (TodoEntity) -> Unit,
    private val onDelete: (TodoEntity) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbDone: CheckBox = itemView.findViewById(R.id.cbDone)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todos[position]

        // 设置标题
        holder.tvTitle.text = todo.title

        // 设置时间（格式化时间戳为 "HH:mm" 格式）
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        holder.tvTime.text = sdf.format(Date(todo.createdAt))

        // 设置复选框状态（先清空监听器，防止复用时误触发）
        holder.cbDone.setOnCheckedChangeListener(null)
        holder.cbDone.isChecked = todo.isDone

        // 已完成的样式：删除线 + 半透明
        if (todo.isDone) {
            holder.tvTitle.paintFlags =
                holder.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvTitle.alpha = 0.5f
        } else {
            holder.tvTitle.paintFlags =
                holder.tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.tvTitle.alpha = 1.0f
        }

        // 复选框点击 -> 通知 Activity 更新数据库
        holder.cbDone.setOnCheckedChangeListener { _, _ ->
            onToggleDone(todo)
        }

        // 删除按钮点击 -> 通知 Activity 删除记录
        holder.btnDelete.setOnClickListener {
            onDelete(todo)
        }
    }

    override fun getItemCount() = todos.size

    /**
     * 更新数据并刷新列表
     * 每次数据库操作后，Activity 调用这个方法传入新数据
     */
    fun updateData(newTodos: List<TodoItem>) {
        todos.clear()
        todos.addAll(newTodos)
        notifyDataSetChanged()  // 通知 RecyclerView 整个列表需要刷新
    }
}
```

### 7.3 与你之前的 UserAdapter 对比

| | UserAdapter | TodoAdapter |
|---|---|---|
| 继承 | `RecyclerView.Adapter` | 一样 |
| 数据源 | `val users: List<UserItem>`（不可变） | `var todos: MutableList<TodoItem>`（可变） |
| 交互 | 无 | 复选框 + 删除按钮 |
| 刷新 | 无（一次性数据） | `updateData()` + `notifyDataSetChanged()` |
| 回调 | 无 | `onToggleDone` + `onDelete` |

**新知识：回调函数（Lambda）**

```kotlin
// 这是构造函数的两个参数，类型是"接收 TodoItem -> 返回 Unit（无返回值）"
private val onToggleDone: (TodoItem) -> Unit,
private val onDelete: (TodoItem) -> Unit
```

意思是：Adapter 不自己处理数据库操作，而是"告诉"Activity 用户做了什么，由 Activity 来决定怎么处理。这叫做**回调模式**。

**新知识：`notifyDataSetChanged()`**

告诉 RecyclerView "数据变了，请重新显示整个列表"。简单粗暴，对于学习项目够用。

**新知识：`Paint.STRIKE_THRU_TEXT_FLAG`**

给文字加删除线的标志位。`or` 操作添加删除线，`and inv()` 操作移除删除线。这是位运算，不用深究，记住用法就行。

---

## 八、步骤六：创建 TodoActivity

### 8.1 新建文件

在 `todo` 包下新建 `TodoActivity.kt`。

### 8.2 写什么

```kotlin
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

/**
 * TODO 待办清单主页面
 *
 * 把所有组件组装在一起：
 * - 数据库：TodoDatabaseHelper
 * - 列表：RecyclerView + TodoAdapter
 * - 交互：添加、勾选、删除
 */
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
```

### 8.3 整体流程

```
用户点击"添加"按钮
    -> 获取输入框文字
    -> 调用 dbHelper.insertTodo(title) 写入数据库
    -> 调用 loadTodos() 从数据库重新查询
    -> adapter.updateData(todos) 刷新列表显示

用户勾选复选框
    -> 回调 onToggleDone(todo)
    -> 调用 dbHelper.updateTodoDone(id, !isDone) 更新数据库
    -> 调用 loadTodos() 刷新列表

用户点击"删除"
    -> 弹出 AlertDialog 确认
    -> 调用 dbHelper.deleteTodo(id) 删除记录
    -> 调用 loadTodos() 刷新列表
```

**每个操作的模式都一样：操作数据库 -> 重新查询 -> 刷新列表。**

### 8.4 与你之前的 RecyclerDemoActivity 对比

| | RecyclerDemoActivity | TodoActivity |
|---|---|---|
| 数据来源 | ContentProvider 查询 | SQLiteOpenHelper 直接查询 |
| 数据操作 | 只读 | 增删改查全套 |
| 列表交互 | 无 | 复选框 + 删除按钮 |
| 刷新方式 | 一次性加载 | 每次操作后重新查询刷新 |

---

## 九、接入主应用

### 9.1 注册 Activity

打开 `app/src/main/AndroidManifest.xml`，在现有 Activity 注册区域添加：

```xml
<!-- TODO 待办清单 -->
<activity
    android:name=".todo.TodoActivity"
    android:exported="false" />
```

### 9.2 添加入口按钮

打开 `app/src/main/res/layout/activity_main.xml`，在 `btnRecyclerDemo` 按钮下方添加：

```xml
<Button
    android:id="@+id/btnTodo"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="TODO 待办清单"
    android:layout_marginTop="4dp" />
```

### 9.3 添加按钮点击事件

打开 `MainActivity.kt`，在 `setupUI()` 方法中添加：

```kotlin
// 13. TODO 待办清单
findViewById<Button>(R.id.btnTodo).setOnClickListener {
    Log.d(TAG, "点击: 打开 TODO 待办清单")
    startActivity(Intent(this, com.example.androidlearn.todo.TodoActivity::class.java))
}
```

### 9.4 清理遗留问题

你的 `AndroidManifest.xml` 中有两个空壳 Activity（`ToDoActivity` 和 `LoginActivity`），没有对应代码文件。建议删除以避免编译警告：

```xml
<!-- 删除这两段 -->
<activity android:name=".activity.ToDoActivity" ... />
<activity android:name=".ui.login.LoginActivity" ... />
```

---

## 十、验证构建

完成后在 Android Studio 中 Build -> Make Project。

如果遇到编译错误，按以下顺序检查：

1. `todo` 包是否存在
2. 6 个文件是否都在正确位置
3. `R` 的 import 是否正确（`import com.example.androidlearn.R`）
4. AndroidManifest.xml 中 Activity 是否注册

---

## 十一、预期效果

1. **添加**：输入内容 -> 点击"添加" -> 列表顶部出现新项
2. **完成**：点击复选框 -> 文字变灰出现删除线
3. **删除**：点击"删除"按钮 -> 弹出确认框 -> 确认后消失
4. **持久化**：关闭 App 重新打开，数据仍在（SQLite 存储在本地）

---

## 十二、6 个文件总览

| 文件 | 位置 | 职责 |
|------|------|------|
| `TodoItem.kt` | `todo/` | 数据模型 |
| `TodoDatabaseHelper.kt` | `todo/` | 数据库操作（增删改查） |
| `activity_todo.xml` | `res/layout/` | 页面布局 |
| `item_todo.xml` | `res/layout/` | 列表项布局 |
| `TodoAdapter.kt` | `todo/` | 列表适配器 |
| `TodoActivity.kt` | `todo/` | 页面逻辑（组装所有组件） |

---

## 十三、遇到问题怎么办

| 问题 | 原因 | 解决 |
|------|------|------|
| 点添加没反应 | 输入框为空 | 检查 `title.isNotEmpty()` 判断 |
| 列表不显示 | RecyclerView 没设置 LayoutManager | 确认 `rvTodos.layoutManager = LinearLayoutManager(this)` |
| 复选框勾选后列表乱跳 | 复用时监听器被触发 | 确认 `setOnCheckedChangeListener(null)` 在 `isChecked` 之前 |
| 数据不持久化 | 数据库名写错或没调正确的写入方法 | 检查 `DATABASE_NAME` 和 `insertTodo` |
| 编译报红找不到 R | 包路径不对 | 检查 `import com.example.androidlearn.R` |

---

## 十四、完成后的下一步

这个 TODO App 用的是"直接在主线程操作数据库"的方式，对学习项目完全够用。当你学了更多技术后，可以升级：

| 升级方向 | 新技术 | 改进效果 |
|---------|--------|---------|
| Room 数据库 | Room + KSP | 告别手写 SQL，用注解自动生成 |
| ViewModel | ViewModel + LiveData | 数据变化自动刷新，不用手动 loadTodos() |
| 协程 | lifecycleScope + suspend | 数据库操作移到后台线程，UI 更流畅 |
| 滑动删除 | ItemTouchHelper | 左滑删除，比按钮更现代 |

但这些都是后话。先把当前版本做出来跑通，再考虑升级。
