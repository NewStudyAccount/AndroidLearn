# Kotlin 语法练习

## 项目创建步骤

### 1. 创建 Android 项目

```
打开 Android Studio
         │
         ▼
点击 "New Project"
         │
         ▼
选择模板: Phone and Tablet → Empty Views Activity
         │
         ▼
配置项目:
  Name:           MyFirstApp
  Package name:   com.example.myfirstapp
  Save location:  D:\MyFirstApp
  Language:       Kotlin
  Minimum SDK:    API 24 (Android 7.0)
  Build config:   Kotlin DSL
         │
         ▼
点击 "Finish"，等待 Gradle 同步完成
```

### 2. 项目结构

```
MyFirstApp/
└── app/
    └── src/
        └── main/
            └── java/
                └── com/example/myfirstapp/
                    └── MainActivity.kt    ← 主代码文件
            └── res/
                └── layout/
                    └── activity_main.xml  ← 布局文件
```

---

## 练习代码

### MainActivity.kt

```kotlin
package com.example.myfirstapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 获取布局中的 TextView
        val textView = findViewById<TextView>(R.id.textView)

        // 构建练习结果
        val result = StringBuilder()

        // ========================================
        // 练习 1: 变量与类型
        // ========================================
        result.appendLine("=== 练习 1: 变量与类型 ===")

        val name = "小明"           // 不可变变量
        var age = 18               // 可变变量
        val price = 9.99           // Double 类型
        val isStudent = true       // Boolean 类型

        result.appendLine("名字: $name")
        result.appendLine("年龄: $age")
        result.appendLine("价格: $price")
        result.appendLine("是否学生: $isStudent")

        age = 20  // var 可以修改
        result.appendLine("修改后年龄: $age")
        result.appendLine()

        // ========================================
        // 练习 2: 字符串模板
        // ========================================
        result.appendLine("=== 练习 2: 字符串模板 ===")

        val firstName = "张"
        val lastName = "三"
        result.appendLine("全名: $firstName$lastName")
        result.appendLine("名字长度: ${firstName.length + lastName.length}")
        result.appendLine("1 + 1 = ${1 + 1}")
        result.appendLine()

        // ========================================
        // 练习 3: 函数
        // ========================================
        result.appendLine("=== 练习 3: 函数 ===")

        fun add(a: Int, b: Int): Int {
            return a + b
        }

        // 单表达式函数
        fun multiply(a: Int, b: Int) = a * b

        // 默认参数
        fun greet(name: String, greeting: String = "你好") {
            result.appendLine("$greeting, $name!")
        }

        result.appendLine("add(3, 5) = ${add(3, 5)}")
        result.appendLine("multiply(4, 6) = ${multiply(4, 6)}")
        greet("小明")
        greet("小红", "早上好")
        result.appendLine()

        // ========================================
        // 练习 4: 条件表达式
        // ========================================
        result.appendLine("=== 练习 4: 条件表达式 ===")

        val score = 85

        // if 表达式
        val level = if (score >= 90) {
            "优秀"
        } else if (score >= 80) {
            "良好"
        } else if (score >= 60) {
            "及格"
        } else {
            "不及格"
        }
        result.appendLine("分数 $score → 等级: $level")

        // when 表达式
        val day = 3
        val dayName = when (day) {
            1 -> "星期一"
            2 -> "星期二"
            3 -> "星期三"
            4 -> "星期四"
            5 -> "星期五"
            6, 7 -> "周末"
            else -> "未知"
        }
        result.appendLine("第 $day 天 → $dayName")
        result.appendLine()

        // ========================================
        // 练习 5: 循环
        // ========================================
        result.appendLine("=== 练习 5: 循环 ===")

        // for 循环
        result.appendLine("1 到 5:")
        for (i in 1..5) {
            result.append("  $i")
        }
        result.appendLine()

        // 带索引遍历
        val fruits = listOf("苹果", "香蕉", "橙子")
        result.appendLine("水果列表:")
        for ((index, fruit) in fruits.withIndex()) {
            result.appendLine("  $index: $fruit")
        }
        result.appendLine()

        // ========================================
        // 练习 6: 集合操作（重点！）
        // ========================================
        result.appendLine("=== 练习 6: 集合操作 ===")

        val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

        // filter - 过滤
        val evens = numbers.filter { it % 2 == 0 }
        result.appendLine("偶数: $evens")

        // map - 转换
        val doubled = numbers.map { it * 2 }
        result.appendLine("翻倍: $doubled")

        // reduce - 累积
        val sum = numbers.reduce { acc, i -> acc + i }
        result.appendLine("总和: $sum")

        // 链式操作
        val result2 = numbers
            .filter { it > 5 }        // 大于5的
            .map { it * it }          // 平方
            .sorted()                 // 排序
        result.appendLine("大于5的数的平方(排序): $result2")
        result.appendLine()

        // ========================================
        // 练习 7: 类与对象
        // ========================================
        result.appendLine("=== 练习 7: 类与对象 ===")

        data class User(val name: String, val age: Int)

        val user1 = User("小明", 18)
        val user2 = User("小红", 20)
        val user3 = user1.copy(age = 19)  // copy 修改部分属性

        result.appendLine("user1: $user1")
        result.appendLine("user2: $user2")
        result.appendLine("user3: $user3")
        result.appendLine("user1 == user3: ${user1 == user3}")
        result.appendLine()

        // ========================================
        // 练习 8: 空安全
        // ========================================
        result.appendLine("=== 练习 8: 空安全 ===")

        var nullableName: String? = null

        // 安全调用
        result.appendLine("null 的长度: ${nullableName?.length}")

        // Elvis 操作符
        result.appendLine("null 的长度(默认0): ${nullableName?.length ?: 0}")

        nullableName = "Kotlin"
        result.appendLine("赋值后长度: ${nullableName?.length}")
        result.appendLine()

        // ========================================
        // 练习 9: Lambda
        // ========================================
        result.appendLine("=== 练习 9: Lambda ===")

        // Lambda 表达式
        val square = { x: Int -> x * x }
        result.appendLine("5 的平方: ${square(5)}")

        // 作为参数传递
        fun operate(a: Int, b: Int, op: (Int, Int) -> Int): Int {
            return op(a, b)
        }

        result.appendLine("3 + 4 = ${operate(3, 4) { a, b -> a + b }}")
        result.appendLine("3 * 4 = ${operate(3, 4) { a, b -> a * b }}")
        result.appendLine()

        // ========================================
        // 显示所有结果
        // ========================================
        textView.text = result.toString()
    }
}
```

---

### activity_main.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp"
    android:background="#F5F5F5">

    <TextView
        android:id="@+id/textView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="16sp"
        android:textColor="#333333"
        android:lineSpacingExtra="4dp"
        android:fontFamily="monospace" />

</ScrollView>
```

---

## 运行方式

### 方式 1: 使用真机（推荐）

```
1. 手机开启开发者模式
   设置 → 关于手机 → 连续点击版本号 7 次

2. 开启 USB 调试
   设置 → 开发者选项 → USB 调试 ✅

3. 用数据线连接手机到电脑

4. 手机上点击 "允许 USB 调试"

5. Android Studio 顶部选择你的手机

6. 点击绿色三角 ▶ 运行
```

### 方式 2: 使用模拟器

```
1. 点击工具栏 "Device Manager"
2. 点击 "Create Virtual Device"
3. 选择 Pixel 6 → Next
4. 下载系统镜像 API 34 → Next → Finish
5. 点击 ▶ 启动模拟器
6. 点击绿色三角 ▶ 运行
```

---

## 运行效果

成功后手机上会显示：

```
=== 练习 1: 变量与类型 ===
名字: 小明
年龄: 18
价格: 9.99
是否学生: true
修改后年龄: 20

=== 练习 2: 字符串模板 ===
全名: 张三
名字长度: 2
1 + 1 = 2

=== 练习 3: 函数 ===
add(3, 5) = 8
multiply(4, 6) = 24
你好, 小明!
早上好, 小红!

=== 练习 4: 条件表达式 ===
分数 85 → 等级: 良好
第 3 天 → 星期三

=== 练习 5: 循环 ===
1 到 5:
  1  2  3  4  5
水果列表:
  0: 苹果
  1: 香蕉
  2: 橙子

=== 练习 6: 集合操作 ===
偶数: [2, 4, 6, 8, 10]
翻倍: [2, 4, 6, 8, 10, 12, 14, 16, 18, 20]
总和: 55
大于5的数的平方(排序): [36, 49, 64, 81, 100]

=== 练习 7: 类与对象 ===
user1: User(name=小明, age=18)
user2: User(name=小红, age=20)
user3: User(name=小明, age=19)
user1 == user3: false

=== 练习 8: 空安全 ===
null 的长度: null
null 的长度(默认0): 0
赋值后长度: 6

=== 练习 9: Lambda ===
5 的平方: 25
3 + 4 = 7
3 * 4 = 12
```

---

## 练习内容总结

| 练习 | 内容 | 知识点 |
|------|------|--------|
| 练习 1 | 变量与类型 | val/var, Int, Double, Boolean, String |
| 练习 2 | 字符串模板 | $变量, ${表达式} |
| 练习 3 | 函数 | fun, 返回值, 默认参数, 单表达式 |
| 练习 4 | 条件表达式 | if 表达式, when 表达式 |
| 练习 5 | 循环 | for..in, withIndex |
| 练习 6 | 集合操作 | filter, map, reduce, 链式调用 |
| 练习 7 | 类与对象 | data class, copy, == |
| 练习 8 | 空安全 | ?, ?., ?: |
| 练习 9 | Lambda | {}, 高阶函数 |

---

## 下一步练习建议

```
1. 修改变量值，观察输出变化

2. 添加新的函数，如：
   fun max(a: Int, b: Int) = if (a > b) a else b

3. 尝试新的集合操作，如：
   numbers.filter { it > 3 }.map { it * 10 }

4. 创建新的数据类，如：
   data class Product(val name: String, val price: Double)

5. 尝试 when 表达式的不同用法
```
