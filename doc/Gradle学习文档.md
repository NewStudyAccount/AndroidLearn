# Gradle 学习文档

## Gradle 是否需要单独安装？

### 简短回答：不需要！

Android Studio **自带 Gradle**，不需要你手动安装。

---

## Gradle 的工作方式

```
你安装 Android Studio 时：
┌──────────────────────┐
│   Android Studio     │
│   ┌──────────────┐   │
│   │ Gradle Wrapper│   │  ← 自带，不需要额外安装
│   └──────┬───────┘   │
└──────────┼───────────┘
           │
           ▼
创建项目时自动下载对应版本：
┌──────────────────────┐
│ ~/.gradle/wrapper/   │
│   dists/             │
│     gradle-8.x/      │  ← 自动下载，约 100-200 MB
└──────────────────────┘
```

---

## Gradle 在项目中的结构

```
MyApp/
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar        ← 包装器（很小，几 KB）
│       └── gradle-wrapper.properties ← 指定 Gradle 版本
├── gradlew                           ← Linux/Mac 构建脚本
├── gradlew.bat                       ← Windows 构建脚本
└── build.gradle.kts                  ← 构建配置文件
```

---

## 关键文件说明

### gradle-wrapper.properties

这个文件告诉项目使用哪个版本的 Gradle：

```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.4-bin.zip
```

### build.gradle.kts（项目级）

```kotlin
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}
```

### build.gradle.kts（模块级）

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.myapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
}
```

---

## 常见问题速查表

| 问题 | 答案 |
|------|------|
| 需要单独安装 Gradle 吗？ | ❌ 不需要 |
| Gradle 从哪来？ | Android Studio 自带 Wrapper |
| 首次创建项目会怎样？ | 自动下载对应版本的 Gradle |
| 下载到哪里？ | `~/.gradle/wrapper/dists/` 目录 |
| 占多大空间？ | 约 100-200 MB/版本 |
| 需要配置环境变量吗？ | ❌ 不需要 |

---

## Gradle vs Gradle Wrapper

| | Gradle | Gradle Wrapper |
|---|--------|----------------|
| 是什么 | 构建工具本体 | 一个脚本，自动下载正确版本的 Gradle |
| 需要安装？ | 可以手动装 | 不需要，项目自带 |
| 推荐？ | 一般不需要 | ✅ 推荐使用 Wrapper |

---

## 首次下载慢的解决方案

在国内下载 Gradle 比较慢，以下是解决方法：

### 方法一：使用镜像源（推荐）

修改 `gradle-wrapper.properties`，把官方地址换成腾讯镜像：

```properties
# 原始地址
distributionUrl=https\://services.gradle.org/distributions/gradle-8.4-bin.zip

# 腾讯镜像（速度快）
distributionUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-8.4-bin.zip
```

### 方法二：手动下载

1. 去 Gradle 官网下载对应版本：https://gradle.org/releases/
2. 放到 `~/.gradle/wrapper/dists/gradle-8.4-bin/` 对应的随机目录下
3. 重新打开项目

### 方法三：配置全局代理

在 Android Studio 中配置 HTTP 代理，加速下载。

---

## Gradle 常用命令

```bash
# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK
./gradlew assembleRelease

# 清理构建产物
./gradlew clean

# 运行单元测试
./gradlew test

# 查看项目依赖
./gradlew dependencies

# 生成依赖报告
./gradlew buildEnvironment
```

> 💡 Windows 用户使用 `gradlew.bat` 代替 `./gradlew`

---

## Gradle 配置优化

### 1. 开启构建缓存（gradle.properties）

```properties
# 开启构建缓存
org.gradle.caching=true

# 开启并行构建
org.gradle.parallel=true

# 增加 JVM 内存
org.gradle.jvmargs=-Xmx2048m

# 开启配置缓存（Gradle 8.0+）
org.gradle.configuration-cache=true
```

### 2. 使用国内 Maven 镜像（settings.gradle.kts）

```kotlin
pluginManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        google()
        mavenCentral()
    }
}
```

---

## 总结

> **你什么都不用做！** 只要安装好 Android Studio，创建项目时它会自动处理 Gradle 的一切。唯一需要注意的是首次下载可能比较慢，可以换成国内镜像加速。
