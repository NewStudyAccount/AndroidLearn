# Android 开发学习文档

## 学习路线图

```
┌─────────────────────────────────────────────────────────────────┐
│                    Android 开发学习路线                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  第一阶段：基础准备                                               │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐                        │
│  │  Java/   │ │  Android │ │  Gradle  │                        │
│  │  Kotlin  │ │  Studio  │ │  基础    │                        │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘                        │
│       └─────────────┼────────────┘                              │
│                     ▼                                           │
│  第二阶段：四大组件                                               │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐          │
│  │ Activity │ │ Service  │ │Broadcast │ │ Content  │          │
│  │  活动    │ │  服务    │ │ Receiver │ │ Provider │          │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘          │
│       └─────────────┼────────────┼────────────┘                │
│                     ▼                                           │
│  第三阶段：UI 开发                                                │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐          │
│  │ 布局     │ │ 常用控件 │ │ 列表     │ │ Fragment │          │
│  │ Layout  │ │ Widgets  │ │ RecyclerView│ │ 碎片    │          │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘          │
│       └─────────────┼────────────┼────────────┘                │
│                     ▼                                           │
│  第四阶段：进阶技能                                               │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐          │
│  │ 网络请求 │ │ 数据存储 │ │ Jetpack  │ │ 第三方库 │          │
│  │ Retrofit │ │ Room/SP  │ │ Compose  │ │ Glide等  │          │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘          │
│       └─────────────┼────────────┼────────────┘                │
│                     ▼                                           │
│  第五阶段：实战项目                                               │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐                        │
│  │ 新闻App  │ │ 天气App  │ │ 完整项目 │                        │
│  └──────────┘ └──────────┘ └──────────┘                        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 第一阶段：基础准备（1-2 周）

### 1. 学习 Kotlin（推荐）或 Java

**为什么选 Kotlin？**
- Google 官方推荐的 Android 开发语言
- 语法更简洁，代码量比 Java 少 30-50%
- 空安全，减少崩溃
- 现在 95% 以上的 Android 新项目都用 Kotlin

**Kotlin 核心知识点：**

```kotlin
// 1. 变量与类型
val name: String = "小明"      // 不可变（推荐）
var age: Int = 18              // 可变
val list = listOf(1, 2, 3)     // 类型推断

// 2. 函数
fun greet(name: String): String {
    return "你好, $name"
}
// 简写
fun greet(name: String) = "你好, $name"

// 3. 空安全
var text: String? = null       // 可空类型
text?.length                   // 安全调用
text?.length ?: 0              // Elvis 操作符

// 4. 数据类
data class User(val name: String, val age: Int)

// 5. Lambda
val double = { x: Int -> x * 2 }
```

### 2. 安装 Android Studio

- 下载地址：https://developer.android.com/studio
- 包含 Gradle 构建工具、布局编辑器等
- 首次启动会自动下载 Gradle、依赖库等

### 3. 了解项目结构

```
MyApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/myapp/    ← Kotlin 代码
│   │   │   ├── res/                        ← 资源文件
│   │   │   │   ├── layout/                ← 布局 XML
│   │   │   │   ├── values/                ← 颜色、字符串、样式
│   │   │   │   ├── drawable/              ← 图片资源
│   │   │   │   └── mipmap/                ← 应用图标
│   │   │   └── AndroidManifest.xml        ← 应用配置
│   │   └── test/                           ← 单元测试
│   └── build.gradle.kts                    ← 模块构建配置
├── build.gradle.kts                        ← 项目构建配置
└── gradle/                                 ← Gradle 包装器
```

---

## 第二阶段：四大组件（2-3 周）

### 1. Activity（活动）— 最重要！

Activity 是用户看到的每一个屏幕。

**Activity 生命周期：**

```
            onCreate() ← 首次创建
                 │
            onStart()  ← 变为可见
                 │
           onResume()  ← 获取焦点
                 │
        ┌──运行中────┐
        │ 用户交互中 │
        └─────┬─────┘
              │
          onPause()  ← 失去焦点
              │
          onStop()   ← 不可见
              │
        onDestroy()  ← 销毁
```

**第一个 Activity 示例：**

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // 设置布局

        // 获取按钮并设置点击事件
        val button = findViewById<Button>(R.id.myButton)
        button.setOnClickListener {
            Toast.makeText(this, "你点击了按钮！", Toast.LENGTH_SHORT).show()
        }
    }
}
```

**页面跳转：**

```kotlin
// 从 MainActivity 跳转到 SecondActivity
val intent = Intent(this, SecondActivity::class.java)
intent.putExtra("name", "小明")  // 传递数据
startActivity(intent)
```

### 2. Service（服务）— 后台运行

用于执行后台任务（播放音乐、下载文件等）。

### 3. BroadcastReceiver（广播接收器）— 接收系统消息

监听系统事件（网络变化、电量变化等）。

### 4. ContentProvider（内容提供器）— 数据共享

跨应用共享数据（通讯录、媒体库等）。

> 💡 **初学者建议：先重点掌握 Activity，其他三个了解即可。**

---

## 第三阶段：UI 开发（2-3 周）

### 1. 常用布局

| 布局类型 | 名称 | 说明 |
|---------|------|------|
| 线性布局 | LinearLayout | 水平或垂直排列子控件 |
| 约束布局 | ConstraintLayout（推荐！） | 通过约束关系定位，性能最好 |
| 帧布局 | FrameLayout | 子控件层叠显示 |
| 相对布局 | RelativeLayout | 相对定位，逐渐被 ConstraintLayout 取代 |

### 2. 常用控件

```xml
<!-- activity_main.xml -->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <!-- 文本显示 -->
    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="你好，Android！"
        android:textSize="24sp" />

    <!-- 输入框 -->
    <EditText
        android:id="@+id/editText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="请输入用户名" />

    <!-- 按钮 -->
    <Button
        android:id="@+id/button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="点击我" />

    <!-- 图片 -->
    <ImageView
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:src="@drawable/ic_launcher" />

</LinearLayout>
```

### 3. RecyclerView（列表）— 超级重要！

90% 的 App 都有列表，RecyclerView 是最常用的列表控件。

```
┌──────────────────────────┐
│  ┌────────────────────┐  │
│  │ 项目 1              │  │  ← ViewHolder
│  └────────────────────┘  │
│  ┌────────────────────┐  │
│  │ 项目 2              │  │  ← ViewHolder
│  └────────────────────┘  │
│  ┌────────────────────┐  │
│  │ 项目 3              │  │  ← ViewHolder
│  └────────────────────┘  │
│  ┌────────────────────┐  │
│  │ 项目 4              │  │  ← ViewHolder（复用）
│  └────────────────────┘  │
│         ...              │
└──────────────────────────┘
```

---

## 第四阶段：进阶技能（3-4 周）

### 1. 网络请求 — Retrofit

```kotlin
// 定义 API 接口
interface ApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Int): User

    @POST("users")
    suspend fun createUser(@Body user: User): User
}

// 使用
val user = RetrofitClient.api.getUser(1)
```

### 2. 数据存储

| 方式 | 适用场景 |
|------|---------|
| SharedPreferences | 简单键值对（设置、配置） |
| Room 数据库 | 结构化数据（用户、订单） |
| 文件存储 | 大文件、图片缓存 |

### 3. Jetpack Compose — 现代 UI（推荐学习）

```kotlin
@Composable
fun Greeting(name: String) {
    Text(
        text = "你好, $name!",
        fontSize = 24.sp,
        color = Color.Blue
    )
}

@Composable
fun MyScreen() {
    Column {
        Greeting("小明")
        Button(onClick = { /* 点击事件 */ }) {
            Text("点击我")
        }
    }
}
```

### 4. 常用第三方库

```kotlin
// build.gradle.kts
dependencies {
    // 网络请求
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // JSON 解析
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // 图片加载
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // 异步处理
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Jetpack 组件
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
}
```

---

## 第五阶段：实战项目（4-6 周）

### 推荐练手项目

| 项目 | 功能 | 技术 | 时间 |
|------|------|------|------|
| TODO 待办清单 App（入门） | 增删改查、本地存储 | Activity + Room + RecyclerView | 1 周 |
| 天气 App（进阶） | 城市搜索、天气展示、未来预报 | 网络请求 + JSON + UI 动画 | 2 周 |
| 新闻阅读 App（综合） | 分类浏览、详情阅读、收藏、分享 | ViewPager + 网络 + 数据库 + MVVM | 3 周 |

---

## 学习资源推荐

| 类型 | 资源 | 说明 |
|------|------|------|
| 官方文档 | developer.android.com | 最权威，有中文 |
| 官方教程 | Android Basics in Kotlin | Google 免费课程 |
| B 站 | 搜索 "Android 开发" | 中文视频教程多 |
| 书籍 | 《第一行代码》郭霖 | 入门经典 |
| 书籍 | 《Kotlin 编程实战》 | Kotlin 深入 |

---

## 学习时间规划

总计约 3-4 个月（每天 2-3 小时）

```
第 1-2 周  ──→  Kotlin 基础 + 环境搭建
第 3-4 周  ──→  Activity + UI 基础
第 5-6 周  ──→  RecyclerView + Fragment
第 7-8 周  ──→  网络 + 数据存储
第 9-10 周 ──→  Jetpack + 架构模式
第 11-16 周 ──→  实战项目
```

---

## 给小白的建议

1. **先学 Kotlin，再学 Android** — 语言基础打牢
2. **多写代码，少看视频** — 看 10 遍不如写 1 遍
3. **从模仿开始** — 先照着教程写，再自己改
4. **善用官方文档** — 学会查文档比背 API 更重要
5. **不要贪多** — 先掌握核心，再扩展
