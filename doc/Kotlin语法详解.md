# Kotlin 语法详解

## 目录

- [1. 基础语法](#1-基础语法)
- [2. 变量与类型](#2-变量与类型)
- [3. 函数](#3-函数)
- [4. 控制流](#4-控制流)
- [5. 类与对象](#5-类与对象)
- [6. 空安全](#6-空安全)
- [7. 集合操作](#7-集合操作)
- [8. Lambda 与高阶函数](#8-lambda-与高阶函数)
- [9. 扩展函数](#9-扩展函数)
- [10. 数据类与密封类](#10-数据类与密封类)
- [11. 协程基础](#11-协程基础)
- [12. Android 中常用的 Kotlin 特性](#12-android-中常用的-kotlin-特性)

---

## 1. 基础语法

### 第一个 Kotlin 程序

```kotlin
fun main() {
    println("你好，Kotlin！")
}
```

### 代码特点

```kotlin
// 1. 不需要分号
val name = "小明"    // ✅ 不需要分号
val name = "小明";   // ✅ 也可以加分号，但没必要

// 2. 类型推断
val age = 18              // 自动推断为 Int
val name = "小明"         // 自动推断为 String
val price = 9.99          // 自动推断为 Double

// 3. 字符串模板
val name = "小明"
println("你好, $name")                    // 输出: 你好, 小明
println("名字长度: ${name.length}")        // 输出: 名字长度: 2
println("1 + 1 = ${1 + 1}")              // 输出: 1 + 1 = 2
```

---

## 2. 变量与类型

### val 与 var

```kotlin
// val = 不可变（推荐！类似 Java 的 final）
val name = "小明"
name = "小红"    // ❌ 编译错误！val 不能重新赋值

// var = 可变
var age = 18
age = 20         // ✅ 可以修改
```

> 💡 **原则：优先使用 val，只在需要修改时使用 var**

### 基本数据类型

```kotlin
// 整数类型
val a: Byte = 127           // 8位，-128 ~ 127
val b: Short = 32767        // 16位
val c: Int = 2147483647     // 32位（常用）
val d: Long = 9999999999L   // 64位

// 浮点类型
val e: Float = 3.14f        // 32位
val f: Double = 3.14159     // 64位（常用）

// 字符和布尔
val g: Char = 'A'
val h: Boolean = true

// 字符串
val i: String = "Hello"
```

### 类型转换

```kotlin
// Kotlin 不支持隐式转换，必须显式转换
val x: Int = 10
val y: Long = x.toLong()     // ✅ 显式转换
val z: Long = x              // ❌ 编译错误！

// 常用转换方法
val a = "123".toInt()         // String → Int
val b = 123.toDouble()        // Int → Double
val c = 3.14.toInt()          // Double → Int（截断小数）
```

---

## 3. 函数

### 基本函数

```kotlin
// 标准写法
fun add(a: Int, b: Int): Int {
    return a + b
}

// 单表达式函数（简写）
fun add(a: Int, b: Int) = a + b

// 无返回值
fun greet(name: String): Unit {    // Unit 可以省略
    println("你好, $name")
}

fun greet(name: String) {
    println("你好, $name")
}
```

### 默认参数和命名参数

```kotlin
// 默认参数
fun greet(name: String, greeting: String = "你好") {
    println("$greeting, $name!")
}

greet("小明")                // 输出: 你好, 小明!
greet("小明", "早上好")      // 输出: 早上好, 小明!

// 命名参数（调用时指定参数名，顺序可以随意）
greet(greeting = "晚上好", name = "小明")
```

### 可变参数

```kotlin
fun sum(vararg numbers: Int): Int {
    return numbers.sum()
}

sum(1, 2, 3)           // 6
sum(1, 2, 3, 4, 5)     // 15
```

---

## 4. 控制流

### if 表达式

```kotlin
// Kotlin 中 if 是表达式，有返回值
val max = if (a > b) a else b

// 多行写法
val result = if (score >= 90) {
    "优秀"
} else if (score >= 80) {
    "良好"
} else if (score >= 60) {
    "及格"
} else {
    "不及格"
}
```

### when 表达式（替代 switch）

```kotlin
// 基本用法
val level = when (score) {
    in 90..100 -> "优秀"
    in 80..89  -> "良好"
    in 60..79  -> "及格"
    else       -> "不及格"
}

// 匹配多种类型
fun describe(obj: Any): String = when (obj) {
    is Int    -> "整数: $obj"
    is String -> "字符串: $obj"
    is List<*> -> "列表，长度 ${obj.size}"
    else      -> "未知类型"
}

// 无参数 when
when {
    temperature > 35 -> println("太热了")
    temperature < 0  -> println("太冷了")
    else             -> println("温度适宜")
}
```

### 循环

```kotlin
// for 循环
for (i in 1..5) {
    println(i)    // 1, 2, 3, 4, 5
}

for (i in 1 until 5) {
    println(i)    // 1, 2, 3, 4（不包含5）
}

for (i in 10 downTo 1 step 2) {
    println(i)    // 10, 8, 6, 4, 2
}

// 遍历集合
val fruits = listOf("苹果", "香蕉", "橙子")
for (fruit in fruits) {
    println(fruit)
}

// 带索引遍历
for ((index, fruit) in fruits.withIndex()) {
    println("$index: $fruit")
}

// while 循环
var count = 0
while (count < 5) {
    println(count)
    count++
}
```

---

## 5. 类与对象

### 基本类

```kotlin
class Person(val name: String, var age: Int) {
    // 方法
    fun introduce() {
        println("我叫$name，今年$age 岁")
    }
}

// 使用
val person = Person("小明", 18)
person.introduce()    // 输出: 我叫小明，今年18 岁
person.age = 20       // ✅ var 可以修改
// person.name = ""   // ❌ val 不能修改
```

### 构造函数

```kotlin
// 主构造函数（在类头声明）
class User(val name: String, val age: Int)

// 次构造函数
class User(val name: String) {
    var age: Int = 0

    constructor(name: String, age: Int) : this(name) {
        this.age = age
    }
}

// init 块（初始化代码）
class User(val name: String, val age: Int) {
    init {
        require(age > 0) { "年龄必须大于0" }
        println("创建了用户: $name")
    }
}
```

### 继承

```kotlin
// 父类需要 open 关键字才能被继承
open class Animal(val name: String) {
    open fun sound() {    // open 才能被重写
        println("...")
    }
}

class Dog(name: String) : Animal(name) {
    override fun sound() {
        println("汪汪！")
    }
}

class Cat(name: String) : Animal(name) {
    override fun sound() {
        println("喵喵！")
    }
}
```

### 接口

```kotlin
interface Drawable {
    fun draw()    // 抽象方法
    fun color(): String = "黑色"    // 默认实现
}

class Circle : Drawable {
    override fun draw() {
        println("画一个圆")
    }
}

// 实现多个接口
interface Clickable {
    fun onClick()
}

interface Focusable {
    fun onFocus()
}

class Button : Clickable, Focusable {
    override fun onClick() = println("点击了按钮")
    override fun onFocus() = println("按钮获得焦点")
}
```

---

## 6. 空安全

Kotlin 最重要的特性之一！编译时就防止空指针异常。

```kotlin
// 默认情况下，变量不能为 null
var name: String = "小明"
name = null    // ❌ 编译错误！

// 可空类型（加 ?）
var name: String? = "小明"
name = null    // ✅ 可以

// 安全调用操作符 ?.
val length: Int? = name?.length    // 如果 name 为 null，返回 null

// 链式安全调用
val city: String? = user?.address?.city

// Elvis 操作符 ?:（如果左边为 null，使用右边的值）
val length: Int = name?.length ?: 0
val city: String = user?.address?.city ?: "未知城市"

// 非空断言 !!（慎用！如果为 null 会抛异常）
val length: Int = name!!.length    // 如果 name 为 null，崩溃！

// 安全类型转换 as?
val obj: Any = "Hello"
val str: String? = obj as? String    // 转换失败返回 null，不会崩溃
```

### 实际应用示例

```kotlin
// Android 中获取 EditText 的文本
val text: String = editText.text?.toString() ?: ""

// 获取 Intent 传来的数据
val name: String = intent.getStringExtra("name") ?: "默认值"

// 获取 View
val view: View? = findViewById(R.id.myView)
view?.visibility = View.VISIBLE
```

---

## 7. 集合操作

### 创建集合

```kotlin
// List（不可变）
val list = listOf(1, 2, 3, 4, 5)

// MutableList（可变）
val mutableList = mutableListOf(1, 2, 3)
mutableList.add(4)
mutableList.remove(1)

// Map
val map = mapOf("name" to "小明", "age" to 18)
val mutableMap = mutableMapOf<String, Any>()
mutableMap["name"] = "小明"

// Set
val set = setOf(1, 2, 3, 3)    // 自动去重
```

### 常用操作（超级重要！）

```kotlin
val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// map - 转换每个元素
val doubled = numbers.map { it * 2 }
// 结果: [2, 4, 6, 8, 10, 12, 14, 16, 18, 20]

// filter - 过滤
val evens = numbers.filter { it % 2 == 0 }
// 结果: [2, 4, 6, 8, 10]

// find - 找到第一个满足条件的
val first = numbers.find { it > 5 }
// 结果: 6

// any / all / none
numbers.any { it > 5 }     // true（存在大于5的）
numbers.all { it > 0 }     // true（全部大于0）
numbers.none { it < 0 }    // true（没有小于0的）

// reduce - 累积操作
val sum = numbers.reduce { acc, i -> acc + i }
// 结果: 55

// fold - 带初始值的累积
val sum = numbers.fold(100) { acc, i -> acc + i }
// 结果: 155

// sorted / sortedBy
val sorted = numbers.sorted()
val sortedDesc = numbers.sortedDescending()

// groupBy - 分组
val grouped = numbers.groupBy { if (it % 2 == 0) "偶数" else "奇数" }
// 结果: {奇数=[1,3,5,7,9], 偶数=[2,4,6,8,10]}

// flatMap - 打平嵌套
val nested = listOf(listOf(1, 2), listOf(3, 4), listOf(5))
val flat = nested.flatMap { it }
// 结果: [1, 2, 3, 4, 5]
```

### Android 中的实际应用

```kotlin
// 用户列表筛选
val activeUsers = users.filter { it.isActive }
val names = activeUsers.map { it.name }

// 计算总价
val totalPrice = cartItems.fold(0.0) { acc, item -> acc + item.price * item.quantity }

// 按类型分组
val grouped = products.groupBy { it.category }
```

---

## 8. Lambda 与高阶函数

### Lambda 表达式

```kotlin
// 基本语法
val sum = { a: Int, b: Int -> a + b }
sum(1, 2)    // 3

// 单参数时可以用 it
val doubled = listOf(1, 2, 3).map { it * 2 }

// 作为函数参数
button.setOnClickListener { view ->
    println("按钮被点击了")
}

// 无参数 Lambda
val task = { println("执行任务") }
task()
```

### 高阶函数

```kotlin
// 函数作为参数
fun operate(a: Int, b: Int, operation: (Int, Int) -> Int): Int {
    return operation(a, b)
}

operate(1, 2) { x, y -> x + y }    // 3
operate(1, 2) { x, y -> x * y }    // 2

// 函数作为返回值
fun getCalculator(type: String): (Int, Int) -> Int {
    return when (type) {
        "add" -> { a, b -> a + b }
        "mul" -> { a, b -> a * b }
        else  -> { a, b -> a - b }
    }
}

val calc = getCalculator("add")
calc(1, 2)    // 3
```

### 常用高阶函数

```kotlin
// let - 对象操作，返回最后一行
val result = name?.let {
    println("名字是: $it")
    it.length
}

// apply - 对象配置，返回对象本身
val textView = TextView(context).apply {
    text = "你好"
    textSize = 16f
    setTextColor(Color.BLACK)
}

// run - 对象操作，返回最后一行
val length = "Hello".run {
    println("字符串是: $this")
    this.length
}

// with - 类似 run
val description = with(user) {
    "$name, $age 岁, 来自 $city"
}

// also - 附加操作，返回对象本身
val numbers = mutableListOf(1, 2, 3).also {
    println("原始列表: $it")
    it.add(4)
}
```

---

## 9. 扩展函数

给现有类添加新功能，不需要继承。

```kotlin
// 给 String 添加扩展函数
fun String.isEmail(): Boolean {
    return this.matches(Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"))
}

"test@gmail.com".isEmail()    // true
"hello".isEmail()              // false

// 给 Int 添加扩展函数
fun Int.isEven(): Boolean = this % 2 == 0

4.isEven()    // true
5.isEven()    // false

// Android 中常用的扩展函数
fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

// 使用
myButton.show()
myButton.hide()
```

---

## 10. 数据类与密封类

### 数据类（data class）

```kotlin
// 自动生成 equals, hashCode, toString, copy 等方法
data class User(
    val name: String,
    val age: Int,
    val email: String
)

val user1 = User("小明", 18, "test@gmail.com")
val user2 = User("小明", 18, "test@gmail.com")

user1 == user2          // true（自动比较内容）
user1.toString()        // User(name=小明, age=18, email=test@gmail.com)

// copy - 复制并修改部分属性
val user3 = user1.copy(age = 20, email = "new@gmail.com")

// 解构
val (name, age, email) = user1
println("$name, $age, $email")
```

### 密封类（sealed class）

```kotlin
// 限制子类的继承层次
sealed class Result {
    data class Success(val data: String) : Result()
    data class Error(val message: String) : Result()
    object Loading : Result()
}

// 使用 when 时，编译器会检查是否覆盖所有情况
fun handleResult(result: Result) {
    when (result) {
        is Result.Success -> println("成功: ${result.data}")
        is Result.Error   -> println("错误: ${result.message}")
        Result.Loading    -> println("加载中...")
        // 不需要 else，编译器知道所有情况都覆盖了
    }
}
```

---

## 11. 协程基础

协程是 Kotlin 处理异步操作的方式，比线程更轻量。

```kotlin
// 基本协程
import kotlinx.coroutines.*

fun main() = runBlocking {
    launch {
        delay(1000)    // 挂起1秒，不阻塞线程
        println("任务1完成")
    }

    launch {
        delay(500)
        println("任务2完成")
    }

    println("主线程继续执行")
}

// 输出:
// 主线程继续执行
// 任务2完成
// 任务1完成
```

### 在 Android 中使用协程

```kotlin
// ViewModel 中使用
class MyViewModel : ViewModel() {
    fun loadData() {
        viewModelScope.launch {
            // 主线程
            val data = withContext(Dispatchers.IO) {
                // IO 线程：网络请求、数据库操作
                repository.fetchData()
            }
            // 回到主线程更新 UI
            _uiState.value = data
        }
    }
}

// Activity/Fragment 中使用
class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            // 生命周期感知的协程
            val data = withContext(Dispatchers.IO) {
                repository.fetchData()
            }
            textView.text = data
        }
    }
}
```

---

## 12. Android 中常用的 Kotlin 特性

### 1. 属性委托

```kotlin
// 延迟初始化
val database: Database by lazy {
    Database.connect("...")
}

// 可观察属性
var name: String by Delegates.observable("初始值") { _, old, new ->
    println("从 $old 变为 $new")
}

// SharedPreferences 委托
var token: String by preferences.stringPref("token", "")
```

### 2. 类型别名

```kotlin
typealias OnClickListener = (View) -> Unit
typealias UserMap = Map<String, User>

// 使用
val listener: OnClickListener = { view -> println("点击") }
```

### 3. 范围操作

```kotlin
// 检查数字是否在范围内
val score = 85
if (score in 60..100) {
    println("及格")
}

// 检查集合是否包含元素
if (name in listOf("小明", "小红", "小刚")) {
    println("找到")
}
```

---

## Kotlin 速查表

| 场景 | Java 写法 | Kotlin 写法 |
|------|----------|-------------|
| 声明变量 | `String name = "hi"` | `val name = "hi"` |
| 空检查 | `if (s != null)` | `s?.length` |
| 三元运算 | `a > b ? a : b` | `if (a > b) a else b` |
| 字符串拼接 | `"hi " + name` | `"hi $name"` |
| 遍历 | `for(int i=0; i<n; i++)` | `for (i in 0 until n)` |
| 匿名函数 | `new Runnable(){}` | `Runnable { }` |
| 单例 | 私有构造+静态实例 | `object Singleton` |
| Bean 类 | 手写 getter/setter | `data class` |
| 空安全 | 到处 null 检查 | `?.` 和 `?:` |

---

## 学习建议

1. **边学边练** — 每学一个知识点，在 Kotlin Playground (play.kotlinlang.org) 上练习
2. **对比 Java** — 如果有 Java 基础，对比着学更快
3. **多用集合操作** — map/filter/reduce 是 Kotlin 的精华
4. **拥抱空安全** — 习惯用 `?.` 和 `?:`，避免 `!!`
5. **少用 var** — 尽量用 val，代码更安全
