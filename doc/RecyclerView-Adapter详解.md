# RecyclerView Adapter 详解

> 本文档从零开始解释 RecyclerView Adapter 的工作原理，
> 先讲你已经写过的 UserAdapter（只读列表），再讲 TodoAdapter（带交互的列表）。

---

## 一、RecyclerView 为什么需要 Adapter？

RecyclerView 只管两件事：**滚动**和**回收复用**。至于每个列表项里显示什么内容，它完全不管，需要一个"中间人"来负责把数据装进布局里。这个中间人就是 Adapter。

```
数据（List<UserItem>）   Adapter    屏幕（RecyclerView）
  [小明, 18, xxx]    --->  装盘  --->  [   小明   ]
  [小红, 20, xxx]    --->  装盘  --->  [   小红   ]
  [小刚, 22, xxx]    --->  装盘  --->  [   小刚   ]
```

---

## 二、Adapter 的三个必须实现的方法

任何 Adapter 都必须实现这三个方法，RecyclerView 会在合适的时机调用它们。

| 方法 | 什么时候调用 | 做什么 |
|------|------------|--------|
| `onCreateViewHolder` | 需要新的 ViewHolder 时（创建 5-8 次后不再调） | 把 XML 布局变成 View，装进 ViewHolder |
| `onBindViewHolder` | 每次列表项出现在屏幕上时（非常频繁） | 把第 position 条数据填进 ViewHolder 的控件 |
| `getItemCount` | RecyclerView 初始化时 | 告诉它总共有多少条数据 |

---

## 三、ViewHolder 是什么？

ViewHolder 的作用就一个：**缓存控件引用**。

每次 `onBindViewHolder` 被调用时，都需要操作 `tvName`、`tvAge`、`tvEmail` 这些控件。如果不缓存，每次都要 `findViewById` 去找，很慢。所以提前找到、存起来，后面直接用。

可以把它理解为**一个抽屉**，里面放着一个列表项里所有的控件：

```
UserViewHolder（一个抽屉）
  +-- tvName: TextView
  +-- tvAge: TextView
  +-- tvEmail: TextView
```

---

## 四、完整运行流程

假设你有 100 条数据，屏幕只能显示 5 条：

```
1. RecyclerView 问 Adapter："总共有多少条？"
   -> getItemCount() 返回 100

2. RecyclerView 问 Adapter："我需要显示第 0 条，给我一个抽屉"
   -> onCreateViewHolder() 创建 ViewHolder #1
   -> onBindViewHolder() 把第 0 条数据填进去

3. RecyclerView 问 Adapter："我需要显示第 1 条"
   -> onCreateViewHolder() 创建 ViewHolder #2
   -> onBindViewHolder() 把第 1 条数据填进去

4. ...重复到第 4 条，创建了 5 个 ViewHolder

5. 用户往下滚动，第 0 条滚出屏幕
   -> RecyclerView 不销毁 ViewHolder #1，把它交给第 5 条复用
   -> 只调 onBindViewHolder()，把第 5 条数据填进 ViewHolder #1
   -> onCreateViewHolder() 不会被调用（不需要新的抽屉了）

6. 继续滚动...ViewHolder 不断被回收复用，onBindViewHolder 不断被调用
```

这就是为什么叫 RecyclerView -- 因为它**回收** ViewHolder。

---

## 五、UserAdapter 逐行解释（只读列表）

```kotlin
class UserAdapter(private val users: List<UserItem>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>()
```

这行做了两件事：

- `private val users: List<UserItem>` -- 接收数据。Activity 创建 Adapter 时把数据列表传进来
- `: RecyclerView.Adapter<UserAdapter.UserViewHolder>()` -- 表示"我是 RecyclerView 的适配器"。继承它意味着你必须实现三个方法

### 5.1 ViewHolder 定义

```kotlin
class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvName: TextView = itemView.findViewById(R.id.tvName)
    val tvAge: TextView = itemView.findViewById(R.id.tvAge)
    val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
}
```

把 `item_user.xml` 里的三个 TextView 找出来，存成变量。后续在 `onBindViewHolder` 里直接用 `holder.tvName`，不用每次都 `findViewById`。

### 5.2 onCreateViewHolder -- 创建抽屉

```kotlin
override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
    val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.item_user, parent, false)
    return UserViewHolder(view)
}
```

把 XML 布局文件 `item_user.xml` 变成一个真正的 View 对象，然后塞进 ViewHolder 里。

```
item_user.xml（XML 文件，只是"图纸"）
       |
       v  LayoutInflater.inflate()
  View 对象（真正的东西，能显示在屏幕上）
       |
       v  new UserViewHolder(view)
  UserViewHolder（抽屉，里面装着 tvName、tvAge、tvEmail）
```

这个方法**不会频繁调用**。只在 RecyclerView 需要新的 ViewHolder 时才调。滚动时旧的会被复用，不会再调。

### 5.3 onBindViewHolder -- 把数据填进控件

```kotlin
override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
    val user = users[position]
    holder.tvName.text = user.name
    holder.tvAge.text = "年龄: ${user.age}"
    holder.tvEmail.text = "邮箱: ${user.email}"
}
```

从数据列表里取出第 `position` 个数据，填进 ViewHolder 的控件里。

```
position = 0  ->  users[0] = 小明  ->  tvName.text = "小明"
position = 1  ->  users[1] = 小红  ->  tvName.text = "小红"
position = 2  ->  users[2] = 小刚  ->  tvName.text = "小刚"
```

为什么叫"绑定"？因为它把**数据**和**控件**绑在一起。

### 5.4 getItemCount -- 数据总数

```kotlin
override fun getItemCount() = users.size
```

告诉 RecyclerView 总共有多少条数据。RecyclerView 需要知道这个数字来决定滚动范围。

---

## 六、TodoAdapter 相比 UserAdapter 多了什么

TodoAdapter 的骨架（三个必须实现的方法 + ViewHolder）和 UserAdapter 完全一样。它只多了三样东西。

### 6.1 多了两个回调参数

```kotlin
// UserAdapter -- 只接收数据
class UserAdapter(private val users: List<UserItem>)

// TodoAdapter -- 接收数据 + 两个回调函数
class TodoAdapter(
    private var todos: MutableList<TodoItem>,
    private val onToggleDone: (TodoItem) -> Unit,  // 勾选回调
    private val onDelete: (TodoItem) -> Unit        // 删除回调
)
```

`(TodoItem) -> Unit` 是函数类型的变量，拆开看：

```
(TodoItem) -> Unit
     |          |
  参数类型    返回类型（Unit = 没有返回值，相当于 void）
```

意思是："我是一个函数，你给我一个 TodoItem，我什么都不返回（只是执行某个动作）"。

这两个参数就是**两个空槽位**，等 Activity 来填具体逻辑：

```kotlin
// 在 TodoActivity 里创建适配器时，Activity 把具体逻辑"填"进去：
adapter = TodoAdapter(
    todos = mutableListOf(),
    onToggleDone = { todo ->
        // Activity 在这里写：更新数据库 + 刷新列表
        dbHelper.updateTodoDone(todo.id, !todo.isDone)
        loadTodos()
    },
    onDelete = { todo ->
        // Activity 在这里写：删除数据库记录 + 刷新列表
        dbHelper.deleteTodo(todo.id)
        loadTodos()
    }
)
```

**为什么要这样设计？** Adapter 不应该知道数据库的存在。它的职责只是"显示列表"。当用户点了复选框，Adapter 只负责喊一声"嘿，用户勾了这一条"，至于怎么更新数据库、怎么刷新列表，那是 Activity 的事。这叫**各管各的**。

UserAdapter 不需要这样做，因为它显示的数据是只读的，用户不能点、不能删。

### 6.2 onBindViewHolder 里多了交互逻辑

```kotlin
override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
    val todo = todos[position]

    // ---- 你已经会的部分：设置数据 ----
    holder.tvTitle.text = todo.title
    holder.tvTime.text = sdf.format(Date(todo.createdAt))

    // ---- 新增部分1：复选框 ----
    holder.cbDone.setOnCheckedChangeListener(null)  // 先清空旧监听器
    holder.cbDone.isChecked = todo.isDone            // 再设置状态
    holder.cbDone.setOnCheckedChangeListener { _, _ ->  // 最后重新绑定
        onToggleDone(todo)   // 告诉 Activity：用户勾了这条
    }

    // ---- 新增部分2：删除线样式 ----
    if (todo.isDone) {
        holder.tvTitle.paintFlags =
            holder.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG  // 加删除线
        holder.tvTitle.alpha = 0.5f  // 变半透明
    } else {
        holder.tvTitle.paintFlags =
            holder.tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()  // 去掉删除线
        holder.tvTitle.alpha = 1.0f  // 恢复不透明
    }

    // ---- 新增部分3：删除按钮 ----
    holder.btnDelete.setOnClickListener {
        onDelete(todo)   // 告诉 Activity：用户要删这条
    }
}
```

**复选框为什么要先清空再设置？**

因为 RecyclerView 会**复用** ViewHolder。假设列表有 10 条，屏幕只能显示 5 条，RecyclerView 只创建 5 个 ViewHolder。当你滚动时，滚出屏幕的 ViewHolder 会被 reuse 给新数据。如果不清空旧监听器，设置 `isChecked` 时会触发上一条数据的监听器，导致数据错乱。这是 RecyclerView 最常见的坑。

### 6.3 多了 updateData 方法

```kotlin
fun updateData(newTodos: List<TodoItem>) {
    todos.clear()
    todos.addAll(newTodos)
    notifyDataSetChanged()  // 告诉 RecyclerView："数据变了，全部重新显示"
}
```

UserAdapter 不需要这个方法，因为数据是一次性传入的、不会变。TodoAdapter 需要，因为每次增删改后都要刷新列表。

调用时机在 Activity 里：

```kotlin
private fun loadTodos() {
    val todos = dbHelper.getAllTodos()  // 从数据库查最新数据
    adapter.updateData(todos)           // 交给适配器刷新列表
}
```

---

## 七、UserAdapter vs TodoAdapter 对比

| | UserAdapter | TodoAdapter | 为什么 |
|------|-----------|-------------|--------|
| 继承 | `RecyclerView.Adapter` | 一样 | 固定模板 |
| 构造函数 | 只接收数据 | 数据 + 两个回调 | 用户能交互（勾选、删除），需要通知 Activity |
| 数据源 | `val users: List`（不可变） | `var todos: MutableList`（可变） | 数据会变，需要能修改 |
| onBindViewHolder | 只设置文字 | 文字 + 复选框状态 + 删除线样式 + 点击事件 | 列表项更复杂，有交互 |
| 刷新方式 | 无（一次性数据） | `updateData()` + `notifyDataSetChanged()` | 数据会变，需要重新查询后刷新 |
| 回调 | 无 | `onToggleDone` + `onDelete` | Adapter 不管数据库，只通知 Activity |

骨架四步（ViewHolder、onCreate、onBind、getItemCount）一个字都没变。
