# Android 开发常见问题与解决记录

> 记录开发过程中遇到的实际问题、原因分析和解决方案。

---

## 一、Gradle Build 失败：AGP 插件无法解析

### 问题现象

```
Plugin [id: 'com.android.application', version: '8.5.2', apply: false] was not found
```

AGP 8.5.2 无法从任何仓库下载。

### 原因

在中国大陆，Google 的 Maven 仓库（`dl.google.com`）被墙，导致依赖下载失败。

### 解决方案

在 `settings.gradle.kts` 中添加阿里云镜像，放在原始仓库之前（优先使用）：

```kotlin
pluginManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/central")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/central")
        google()
        mavenCentral()
    }
}
```

---

## 二、AAR Metadata 版本不兼容（19 个错误）

### 问题现象

```
Dependency 'androidx.core:core-ktx:1.18.0' requires Android Gradle plugin 8.9.1 or higher.
This build currently uses Android Gradle plugin 8.5.2.
```

多个依赖要求更高版本的 AGP。

### 原因

依赖库版本太新，而 AGP 版本太旧，无法满足最低要求。

### 通用解决思路

```
报错信息 → 哪个依赖？ → 要求什么版本？
                            ↓
                  升级 AGP 到满足要求的版本
                            ↓
                  升级 Gradle wrapper（新 AGP 需要新 Gradle）
                            ↓
                  升级 compileSdk（如果报错要求的话）
                            ↓
                  如果某个依赖要求太新（如要 AGP 9.x），
                  考虑降级那个依赖到兼容版本
```

### 本次解决方案

在 `gradle/libs.versions.toml` 中：

| 改动 | 旧版本 | 新版本 | 原因 |
|---|---|---|---|
| AGP | 8.5.2 | 8.9.1 | 满足大多数依赖要求 |
| lifecycle | 2.11.0 | 2.8.7 | 2.11.0 要求 AGP 9.1.0 太激进 |

---

## 三、版本频繁冲突的根因：新旧库混搭

### 问题现象

项目总是在变各种东西的版本，反复出现兼容性问题。

### 原因分析

项目中同时使用了 **旧版 Support Library** 和 **新版 AndroidX** 两套库：

| 类型 | 旧的（废弃） | 新的（应使用） |
|---|---|---|
| Support 库 | `com.android.support:appcompat-v7:28.0.0` | `androidx.appcompat:appcompat:1.6.1` |
| Support 库 | `com.android.support:design:28.0.0` | `com.google.android.material:material:1.10.0` |
| Lifecycle | `android.arch.lifecycle:livedata:1.1.1` | `androidx.lifecycle:lifecycle-livedata-ktx:2.8.7` |
| Lifecycle | `android.arch.lifecycle:viewmodel:1.1.1` | `androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7` |
| ConstraintLayout | `com.android.support.constraint:constraint-layout:2.0.4` | `androidx.constraintlayout:constraintlayout:2.1.4` |

### 解决方案

删除所有旧 Support Library 依赖，只保留 AndroidX 版本：

1. `libs.versions.toml` 删掉旧版本声明和 library 声明
2. `app/build.gradle.kts` 删掉对应的 `implementation` 行
3. 补上 AndroidX 版本的对应库

---

## 四、XML 布局中使用旧包名导致 DataBinding 编译失败

### 问题现象

```
错误: 程序包android.support.constraint不存在
import android.support.constraint.ConstraintLayout;
```

生成的 DataBinding 代码引用了旧包名。

### 原因

布局 XML 文件中使用了旧的 `android.support.constraint.ConstraintLayout`，而不是 AndroidX 的 `androidx.constraintlayout.widget.ConstraintLayout`。

### 解决方案

全局搜索并替换 XML 文件中的旧包名：

```xml
<!-- 旧 -->
<android.support.constraint.ConstraintLayout ...>

<!-- 新 -->
<androidx.constraintlayout.widget.ConstraintLayout ...>
```

---

## 五、ConstraintLayout 中所有元素堆叠在一起

### 问题现象

页面上所有控件叠在左上角，没有按预期排列。

### 原因

ConstraintLayout 的子 View **必须设置约束**才能定位。如果没设约束，所有 View 默认堆在 (0,0)。

另外，`tools:layout_editor_absoluteX/Y` 只是 Android Studio 预览用的，**运行时不起作用**。

### 解决方案

每个子 View 至少设置水平和垂直各一个约束：

```xml
<androidx.constraintlayout.widget.ConstraintLayout ...>

    <!-- 标题：顶部对齐 -->
    <TextView
        android:id="@+id/tvTitle"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- 输入区域：在标题下方 -->
    <LinearLayout
        android:id="@+id/inputArea"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_constraintTop_toBottomOf="@id/tvTitle"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- 列表：填满剩余空间 -->
    <RecyclerView
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintTop_toBottomOf="@id/inputArea"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

关键：`layout_height="0dp"` + 上下约束 = 自动拉伸填满中间空间。

---

## 六、ConstraintLayout 约束与多设备适配

### 问题

加约束的方式，如果是不同类型的设备，岂不是都要重新开发一遍？

### 解答

**不会。** ConstraintLayout 的约束定义的是"相对关系"，不是绝对坐标，天然适配不同屏幕。

```xml
<!-- 这个约束的意思是：按钮在输入框下方 16dp -->
<!-- 不管屏幕多宽多高，按钮始终在输入框下方 16dp -->
<Button
    app:layout_constraintTop_toBottomOf="@id/input"
    android:layout_marginTop="16dp" />
```

只有当**布局结构完全不同时**（如手机单列 vs 平板分栏），才需要使用资源限定符创建多套布局：

```
res/
  layout/activity_main.xml          ← 手机（默认）
  layout-w600dp/activity_main.xml   ← 平板（宽度≥600dp）
```

系统根据屏幕宽度自动选择，不需要在代码里判断设备类型。

---

## 七、item_todo.xml 列表项布局编写

### 问题

item_todo.xml 中使用了 ConstraintLayout 但没有设置约束，且错误使用了 `layout_weight`（这是 LinearLayout 的属性）。

### 布局结构

```
┌──────────────────────────────────────────────┐
│  ☑  待办事项标题                    [删除]    │
│      创建时间                                │
└──────────────────────────────────────────────┘
```

### 正确写法

```xml
<androidx.constraintlayout.widget.ConstraintLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="12dp">

    <!-- 复选框：左侧垂直居中 -->
    <CheckBox
        android:id="@+id/cbDone"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent" />

    <!-- 标题：复选框右侧，占据剩余空间 -->
    <TextView
        android:id="@+id/tvTitle"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_constraintStart_toEndOf="@id/cbDone"
        app:layout_constraintEnd_toStartOf="@id/btnDelete"
        app:layout_constraintTop_toTopOf="parent" />

    <!-- 时间：标题下方 -->
    <TextView
        android:id="@+id/tvTime"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_constraintStart_toEndOf="@id/cbDone"
        app:layout_constraintEnd_toStartOf="@id/btnDelete"
        app:layout_constraintTop_toBottomOf="@id/tvTitle" />

    <!-- 删除按钮：右侧垂直居中 -->
    <Button
        android:id="@+id/btnDelete"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

---

## 八、ToDo 待办清单代码流程解读

### 整体架构：4 个文件各司其职

```
┌─────────────┐     ┌──────────────┐     ┌─────────────────┐     ┌──────────────┐
│  TodoItem   │ ←── │TodoActivity  │ ──→ │TodoDatabaseHelper│     │  TodoAdapter │
│  (数据模型)  │     │  (页面/控制器) │     │   (数据库操作)    │     │  (列表渲染)   │
└─────────────┘     └──────────────┘     └─────────────────┘     └──────────────┘
```

### TodoItem.kt — 数据模型

```kotlin
data class TodoItem(
    val id: Int = 0,            // 数据库自增主键
    val title: String,          // 待办内容
    val isDone: Boolean = false, // 是否完成
    val createdAt: Long = System.currentTimeMillis()  // 创建时间戳
)
```

### TodoDatabaseHelper.kt — 数据库层

提供 4 个方法：

| 方法 | 作用 | 对应 SQL |
|---|---|---|
| `insertTodo(title)` | 新增一条待办 | `INSERT INTO todos ...` |
| `getAllTodos()` | 查询所有待办，按时间倒序 | `SELECT * FROM todos ORDER BY created_at DESC` |
| `updateTodoDone(id, isDone)` | 切换完成状态 | `UPDATE todos SET is_done=? WHERE _id=?` |
| `deleteTodo(id)` | 删除一条待办 | `DELETE FROM todos WHERE _id=?` |

### TodoAdapter.kt — RecyclerView 适配器

把 `List<TodoItem>` 数据渲染成屏幕上的列表项。不直接操作数据库，通过回调通知 Activity。

### TodoActivity.kt — 页面控制器

```
onCreate
  ├─ 1. dbHelper = TodoDatabaseHelper(this)    ← 初始化数据库
  ├─ 2. setupUI()                              ← 初始化界面
  │     ├─ 创建 Adapter，传入 onToggleDone 和 onDelete 回调
  │     ├─ RecyclerView 设置 LayoutManager + Adapter
  │     └─ btnAdd 点击：写入数据库 → 清空输入框 → 刷新列表
  └─ 3. loadTodos()                            ← 首次加载数据
```

核心模式：**每次操作都是 "写数据库 → 重新查询 → 刷新列表"**，保证 UI 和数据库始终同步。

---

## 九、TodoActivity 与 activity_todo.xml 如何关联

### 关联方式

```kotlin
class TodoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)           // ① 加载布局

        val etInput = findViewById<EditText>(R.id.etTodoInput)  // ② 获取控件引用
    }
}
```

**两步：**

| 步骤 | 代码 | 作用 |
|---|---|---|
| ① 加载布局 | `setContentView(R.layout.activity_todo)` | 把 XML 膨胀成 View 树，显示到屏幕上 |
| ② 获取控件引用 | `findViewById<EditText>(R.id.xxx)` | 通过 id 找到布局里的具体控件 |

### R 类是什么

编译时 Android 自动生成的资源 ID 类：

```java
public final class R {
    public static final class layout {
        public static final int activity_todo = 0x7f0c0001;
    }
    public static final class id {
        public static final int etTodoInput = 0x7f080001;
        public static final int btnAdd = 0x7f080002;
    }
}
```

`setContentView` 和 `findViewById` 靠这些 ID 来找到对应的资源。

---

## 十、item_todo.xml 如何被加载

### 加载时机

item_todo.xml 不是 Activity 直接加载的，而是 **RecyclerView 通过 Adapter 加载的**。

```kotlin
// TodoAdapter.kt
override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
    val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.item_todo, parent, false)  // 这里加载 item_todo.xml
    return TodoViewHolder(view)
}
```

### 加载流程

```
loadTodos()
  └─ adapter.updateData(todos)    ← 数据变化，触发 RecyclerView 工作
       └─ RecyclerView 判断屏幕能显示几条（假设 5 条）
            ├─ 调用 onCreateViewHolder 5 次 → 加载 5 个 item_todo.xml
            └─ 调用 onBindViewHolder 5 次  → 把数据填入每个 View
```

### 复用机制

RecyclerView 只创建屏幕可见数量 + 少量备用的 ViewHolder，滑动时复用旧的，只重新绑定数据。100 条数据和 10000 条数据的内存占用差不多。

### 两个 XML 的对比

| XML | 谁加载 | 什么时候 |
|---|---|---|
| `activity_todo.xml` | `setContentView()` | Activity 创建时加载 1 次 |
| `item_todo.xml` | `LayoutInflater.inflate()` | Adapter 的 `onCreateViewHolder` 中加载，每个列表项 1 次，之后复用 |

---

## 十一、Activity 和 Adapter 是否有创建模板

### Android Studio 内置模板

右键包名 → New → Activity，会自动生成 Activity + XML。但 RecyclerView + Adapter 没有内置模板。

### View Binding 替代 findViewById

开启 View Binding 后，自动生成 Binding 类，不用手写 `findViewById`：

```kotlin
// app/build.gradle.kts
buildFeatures {
    viewBinding = true
}

// 使用方式
class TodoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTodoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etTodoInput.text.clear()    // 直接访问控件
        binding.btnAdd.setOnClickListener { ... }
    }
}
```

自动生成规则：`activity_todo.xml` → `ActivityTodoBinding`（下划线转驼峰 + Binding）。

### 标准模板结构

新建"列表 + 增删改查"页面，固定 4 个文件：

```
feature/
├── XxxItem.kt              ← 数据类
├── XxxDatabaseHelper.kt    ← 数据库操作
├── XxxAdapter.kt           ← RecyclerView 适配器
└── XxxActivity.kt          ← 页面控制器
```

---

## 十二、页面布局的确认与编写方法

### 第 1 步：先画草图

在纸上画出页面结构，标注各区域的排列方式和关系。

### 第 2 步：选择布局容器

```
能用 LinearLayout 搞定 → 用 LinearLayout
搞不定（需要对齐、重叠等） → 用 ConstraintLayout
列表数据 → RecyclerView
需要滚动 → 外层套 ScrollView
```

| 场景 | 布局 |
|---|---|
| 一列按钮依次排列 | LinearLayout(vertical) |
| 一行图标均匀分布 | LinearLayout(horizontal) |
| 标题在左、按钮在右 | ConstraintLayout |
| 元素重叠 | FrameLayout 或 ConstraintLayout |
| 列表数据 | RecyclerView + item 布局 |
| 长页面需要滚动 | ScrollView + LinearLayout |

### 第 3 步：从上往下逐个添加控件

先放结构，再调细节。

### 第 4 步：设置约束或权重

**ConstraintLayout：** 声明"我在谁的哪个方向"

```xml
app:layout_constraintTop_toBottomOf="@id/tvTitle"
```

**LinearLayout：** 用 `layout_weight` 分配剩余空间

```xml
android:layout_height="0dp"
android:layout_weight="1"
```

### 常用属性速查

```
宽高：
  match_parent  → 和父容器一样大
  wrap_content  → 刚好包住内容
  0dp + constraint/weight → 由约束或权重决定

对齐：
  layout_gravity → 自己在父容器中的位置
  gravity        → 自己内部内容的位置

常见值：
  start / end / center / top / bottom
  center_horizontal / center_vertical
```
