# 练习项目设计：个人收藏夹 App

> 一个从简单到复杂的渐进式练习项目，覆盖布局、数据库、列表、网络请求等核心知识点。

---

## 项目简介

**名称：** 个人收藏夹（MyCollection）

**功能：** 记录你喜欢的电影/书籍/音乐，支持增删改查、分类筛选、网络获取封面图。

**为什么选这个主题：**
- 数据结构简单，适合入门
- 功能可逐步扩展，不会一步到位
- 天然覆盖所有核心知识点
- 最后可以对接真实 API，体验完整开发流程

---

## 阶段一：基础布局 + 静态页面

**目标：** 熟悉布局编写，页面能显示出来

**知识点：**
- LinearLayout / ConstraintLayout 选择与使用
- View 绑定关系（setContentView + findViewById / View Binding）
- 常用属性（宽高、间距、对齐、字号、颜色）

### 任务清单

```
1. 创建项目，配置 Gradle（AGP、依赖、镜像）
2. 设计主页布局 activity_main.xml
   ├─ 顶部：标题栏 + "添加"按钮
   ├─ 中部：分类标签栏（全部/电影/书籍/音乐）
   └─ 底部：列表区域（先用占位 TextView）
3. 设计添加页布局 activity_add.xml
   ├─ 封面图区域（ImageView，先用占位图）
   ├─ 标题输入（EditText）
   ├─ 分类选择（RadioGroup：电影/书籍/音乐）
   ├─ 评分（RatingBar）
   ├─ 备注输入（EditText 多行）
   └─ 保存按钮
4. 设计列表项布局 item_collection.xml
   ├─ 左侧：封面缩略图
   ├─ 中间：标题 + 分类标签 + 评分
   └─ 右侧：更多操作按钮
5. 用 View Binding 替代 findViewById
```

### 预期产出

```
app/src/main/res/layout/
├── activity_main.xml          ← 主页
├── activity_add.xml           ← 添加/编辑页
└── item_collection.xml        ← 列表项
```

---

## 阶段二：数据模型 + SQLite 数据库

**目标：** 数据能持久化存储，增删改查跑通

**知识点：**
- data class 定义数据模型
- SQLiteOpenHelper 创建数据库表
- 增删改查方法封装
- Activity 与数据库交互

### 任务清单

```
1. 创建数据模型 CollectionItem.kt
   data class CollectionItem(
       val id: Int = 0,
       val title: String,          // 标题
       val category: String,       // 分类：movie/book/music
       val rating: Float = 0f,     // 评分 0-5
       val note: String = "",      // 备注
       val coverUrl: String = "",  // 封面地址（阶段四用）
       val createdAt: Long = System.currentTimeMillis()
   )

2. 创建 CollectionDatabaseHelper.kt
   ├─ onCreate：建表
   ├─ insertItem(item)：插入
   ├─ getAllItems()：查询全部
   ├─ getItemsByCategory(category)：按分类查询
   ├─ updateItem(item)：更新
   └─ deleteItem(id)：删除

3. 在主页连接数据库
   ├─ 启动时查询并显示总数
   └─ 验证数据库能正常读写

4. 在添加页实现保存功能
   ├─ 校验输入（标题不能为空）
   ├─ 写入数据库
   └─ 返回主页并刷新
```

### 预期产出

```
app/src/main/java/.../collection/
├── CollectionItem.kt
├── CollectionDatabaseHelper.kt
├── MainActivity.kt
└── AddActivity.kt
```

---

## 阶段三：RecyclerView 列表展示

**目标：** 数据以列表形式展示，支持交互操作

**知识点：**
- RecyclerView + Adapter 模式
- ViewHolder 复用机制
- 回调函数（Lambda）传递事件
- AlertDialog 确认操作
- 列表项布局约束

### 任务清单

```
1. 创建 CollectionAdapter.kt
   ├─ onCreateViewHolder：加载 item_collection.xml
   ├─ onBindViewHolder：绑定数据到控件
   │   ├─ 标题、分类、评分
   │   └─ 封面图（先用占位图）
   ├─ 回调设计：
   │   ├─ onItemClick：点击跳转编辑页
   │   ├─ onItemLongClick：长按弹出删除确认
   │   └─ onCategoryFilter：分类标签切换
   └─ updateData()：刷新列表数据

2. 主页接入 RecyclerView
   ├─ 设置 LinearLayoutManager
   ├─ 设置 Adapter
   └─ 实现 loadItems()：查询 → 更新 Adapter

3. 实现分类筛选
   ├─ 标签栏点击 → 切换查询条件
   ├─ 全部：getAllItems()
   └─ 其他：getItemsByCategory(category)

4. 实现列表项交互
   ├─ 点击 → 跳转编辑页（携带 item id）
   ├─ 长按 → AlertDialog 确认删除
   └─ 删除后自动刷新列表

5. 实现编辑功能
   ├─ AddActivity 复用为编辑页
   ├─ 判断：有 id → 编辑模式，无 id → 新增模式
   └─ 编辑模式：预填充原有数据
```

### 预期产出

```
完整可用的增删改查 + 分类筛选功能
```

---

## 阶段四：网络请求 + 图片加载

**目标：** 对接真实 API，加载网络图片

**知识点：**
- Retrofit 网络请求框架
- Gson JSON 解析
- Glide/Coil 图片加载
- 权限申请（网络权限）
- 异步处理（协程/回调）
- 加载状态处理（Loading/Error/Success）

### 任务清单

```
1. 添加依赖
   ├─ Retrofit + Gson（网络请求 + JSON 解析）
   ├─ OkHttp（HTTP 客户端）
   └─ Coil（图片加载，Kotlin 友好）

2. 封装网络请求层
   ├─ ApiService.kt：定义 API 接口
   │   └─ @GET("search") fun search(@Query("q") keyword: String): Call<SearchResult>
   ├─ RetrofitClient.kt：单例 Retrofit 实例
   └─ SearchResult.kt：响应数据模型

3. 实现搜索功能
   ├─ 主页添加搜索栏
   ├─ 输入关键词 → 调用 API → 显示搜索结果
   ├─ 选择结果 → 自动填充标题、封面等信息
   └─ 加载状态：ProgressBar + 错误提示

4. 封面图加载
   ├─ 列表项：Coil 加载缩略图
   ├─ 添加页：Coil 加载大图
   ├─ 占位图：加载中 / 加载失败
   └─ 缓存机制：内存缓存 + 磁盘缓存

5. 错误处理
   ├─ 网络不可用 → 提示用户
   ├─ 请求超时 → 重试按钮
   └─ 数据解析失败 → 降级处理
```

### 预期产出

```
可以搜索网络数据、加载封面图的完整应用
```

---

## 阶段五（进阶）：架构优化

**目标：** 代码结构更规范，为后续扩展打基础

**知识点：**
- MVVM 架构模式
- ViewModel + LiveData
- Repository 模式分离数据源
- Room 数据库（替代 SQLiteOpenHelper）

### 任务清单

```
1. 引入 ViewModel + LiveData
   ├─ CollectionViewModel.kt
   ├─ 数据变化自动通知 UI 刷新
   └─ 屏幕旋转不丢失数据

2. Repository 模式
   ├─ CollectionRepository.kt
   ├─ 统一管理本地数据库 + 网络请求
   └─ 缓存策略：本地优先，网络更新

3. Room 数据库（替代手写 SQL）
   ├─ @Entity 定义表结构
   ├─ @Dao 定义操作方法
   └─ @Database 定义数据库
```

---

## 项目文件结构（最终）

```
app/src/main/java/.../collection/
├── data/
│   ├── model/
│   │   ├── CollectionItem.kt        ← 数据模型
│   │   └── SearchResult.kt          ← API 响应模型
│   ├── local/
│   │   └── CollectionDatabaseHelper.kt ← 数据库操作
│   ├── remote/
│   │   ├── ApiService.kt            ← API 接口定义
│   │   └── RetrofitClient.kt        ← Retrofit 实例
│   └── repository/
│       └── CollectionRepository.kt  ← 数据仓库（统一入口）
├── ui/
│   ├── main/
│   │   ├── MainActivity.kt          ← 主页
│   │   └── CollectionAdapter.kt     ← 列表适配器
│   ├── add/
│   │   └── AddActivity.kt           ← 添加/编辑页
│   └── search/
│       └── SearchActivity.kt        ← 搜索页（可选）
├── viewmodel/
│   └── CollectionViewModel.kt       ← ViewModel（阶段五）
└── util/
    └── NetworkUtil.kt               ← 网络工具类
```

---

## 各阶段对应练习的知识点

| 阶段 | 练习内容 | 对应之前的问答 |
|---|---|---|
| 一 | 布局编写、View Binding | 问题 5/6/7/12 |
| 二 | 数据模型、SQLite、Activity 流程 | 问题 8/9 |
| 三 | RecyclerView、Adapter、列表交互 | 问题 8/10/11 |
| 四 | 网络请求、JSON 解析、图片加载 | 后续扩展 |
| 五 | MVVM、ViewModel、Repository | 架构优化 |

---

## 推荐的 API（免费，无需注册）

| 用途 | API | 说明 |
|---|---|---|
| 电影信息 | OMDb API (omdbapi.com) | 免费 API Key，搜索电影信息和海报 |
| 书籍信息 | Google Books API | 无需 Key，搜索书籍信息和封面 |
| 图片占位 | picsum.photos | 随机图片，适合开发阶段占位 |

---

## 开始方式

从阶段一开始，完成一个阶段后再进入下一个。每个阶段的代码都可以独立运行和测试。

不需要一次做完所有功能，每完成一步都能看到实际效果，保持学习动力。

---

## 阶段一详细设计：页面布局

### 页面一：主页 (activity_collection_main.xml)

```
┌──────────────────────────────────────────┐
│  顶部标题栏                               │
│  ┌──────────────────────────┐ ┌────────┐ │
│  │ 我的收藏                 │ │  添加   │ │
│  └──────────────────────────┘ └────────┘ │
├──────────────────────────────────────────┤
│  分类标签栏                               │
│  ┌──────┐┌──────┐┌──────┐┌──────┐      │
│  │ 全部 ││ 电影 ││ 书籍 ││ 音乐 │      │
│  └──────┘└──────┘└──────┘└──────┘      │
├──────────────────────────────────────────┤
│                                          │
│  列表区域（RecyclerView）                 │
│  ┌──────────────────────────────────────┐│
│  │ ┌────┐ 标题文字              [更多] │││
│  │ │封面│ [电影] ★★★☆☆               │││
│  │ │    │ 备注预览...                  │││
│  │ └────┘                              │││
│  ├──────────────────────────────────────┤│
│  │ ┌────┐ 标题文字              [更多] │││
│  │ │封面│ [书籍] ★★★★★               │││
│  │ │    │ 备注预览...                  │││
│  │ └────┘                              │││
│  └──────────────────────────────────────┘│
│                                          │
└──────────────────────────────────────────┘
```

**布局建议：**
- 根布局：ConstraintLayout
- 顶部标题栏：TextView + Button，左右对齐
- 分类标签栏：LinearLayout(horizontal)，4 个 Button 平分宽度
- 列表区域：RecyclerView，约束在标签栏下方，填满剩余空间

---

### 页面二：添加/编辑页 (activity_collection_add.xml)

```
┌──────────────────────────────────────────┐
│  ┌──────────┐  标题输入                   │
│  │          │  ┌────────────────────┐    │
│  │  封面图   │  │ 请输入标题          │    │
│  │  (可点击) │  └────────────────────┘    │
│  │          │                            │
│  └──────────┘                            │
│                                          │
│  分类                                     │
│  ┌────────┐┌────────┐┌────────┐         │
│  │ ○ 电影 ││ ○ 书籍 ││ ○ 音乐 │         │
│  └────────┘└────────┘└────────┘         │
│                                          │
│  评分                                     │
│  ★ ★ ★ ☆ ☆                              │
│                                          │
│  备注                                     │
│  ┌────────────────────────────────────┐  │
│  │                                    │  │
│  │                                    │  │
│  │                                    │  │
│  └────────────────────────────────────┘  │
│                                          │
│  ┌────────────────────────────────────┐  │
│  │              保 存                 │  │
│  └────────────────────────────────────┘  │
└──────────────────────────────────────────┘
```

**布局建议：**
- 外层 ScrollView（内容可能超出屏幕）
- 内层 ConstraintLayout
- 封面图：ImageView，固定宽高，左侧
- 标题：TextInputLayout + TextInputEditText，封面右侧
- 分类：RadioGroup(horizontal) + 3 个 RadioButton
- 评分：RatingBar
- 备注：TextInputLayout + TextInputEditText，多行
- 保存：Button，全宽

---

### 页面三：列表项 (item_collection.xml)

```
┌──────────────────────────────────────────────┐
│ ┌──────┐  标题文字                   [更多]  │
│ │      │  [分类]  ★ ★ ★ ☆ ☆                │
│ │ 封面  │  备注预览文字...                    │
│ │      │                                    │
│ └──────┘                                    │
└──────────────────────────────────────────────┘
```

**布局建议：**
- 根布局：ConstraintLayout
- 封面：ImageView，左侧，垂直居中
- 标题：TextView，封面右侧，顶部对齐
- 分类标签：TextView，标题下方左侧，带圆角背景
- 评分：RatingBar，标题下方右侧
- 备注：TextView，分类/评分下方，单行省略
- 更多按钮：ImageButton，右侧垂直居中

---

### 控件 ID 清单

| 页面 | 控件 | ID | 用途 |
|---|---|---|---|
| 主页 | TextView | tvTitle | 标题"我的收藏" |
| 主页 | Button | btnAdd | 跳转添加页 |
| 主页 | Button | btnAll/btnMovie/btnBook/btnMusic | 分类筛选 |
| 主页 | RecyclerView | rvCollection | 列表 |
| 主页 | TextView | tvEmpty | 空状态提示 |
| 添加页 | ImageView | ivCover | 封面图 |
| 添加页 | TextInputEditText | etTitle | 标题输入 |
| 添加页 | RadioGroup | rgCategory | 分类选择 |
| 添加页 | RadioButton | rbMovie/rbBook/rbMusic | 分类选项 |
| 添加页 | RatingBar | ratingBar | 评分 |
| 添加页 | TextInputEditText | etNote | 备注输入 |
| 添加页 | Button | btnSave | 保存 |
| 列表项 | ImageView | ivCover | 封面缩略图 |
| 列表项 | TextView | tvTitle | 标题 |
| 列表项 | TextView | tvCategory | 分类标签 |
| 列表项 | RatingBar | ratingBar | 评分 |
| 列表项 | TextView | tvNote | 备注预览 |
| 列表项 | ImageButton | btnMore | 更多操作 |

---

### ConstraintLayout 约束关系

**主页：**
```
tvTitle    → top:parent, start:parent, end:btnAdd
btnAdd     → top:parent, end:parent, bottom:tvTitle
tabBar     → top:tvTitle底部, start:parent, end:parent
rvCollection → top:tabBar底部, bottom:parent, start/end:parent (填满)
tvEmpty    → 水平垂直居中于 rvCollection
```

**添加页：**
```
ivCover    → top:parent, start:parent
etTitle    → top:ivCover顶部, start:ivCover右侧, end:parent
tvCategoryLabel → top:ivCover底部, start:parent
rgCategory → top:tvCategoryLabel底部, start/end:parent
tvRatingLabel → top:rgCategory底部, start:parent
ratingBar  → top:tvRatingLabel底部, start:parent
tvNoteLabel → top:ratingBar底部, start:parent
etNote     → top:tvNoteLabel底部, start/end:parent
btnSave    → top:etNote底部, start/end:parent
```

**列表项：**
```
ivCover    → start:parent, top/bottom:parent (垂直居中)
tvTitle    → start:ivCover右侧, end:btnMore, top:parent
tvCategory → start:ivCover右侧, top:tvTitle底部
ratingBar  → start:tvCategory右侧, top:tvTitle底部
tvNote     → start:ivCover右侧, end:btnMore, top:tvCategory底部
btnMore    → end:parent, top/bottom:parent (垂直居中)
```
