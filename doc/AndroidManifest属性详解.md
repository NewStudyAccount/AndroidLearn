# AndroidManifest.xml 属性详解

> 本文档基于本项目 [AndroidManifest.xml](../app/src/main/AndroidManifest.xml) 逐行逐属性解析。
> Manifest 是 Android 应用的"身份证"，系统通过它认识你的应用：包含什么组件、需要什么权限、如何启动。

---

## 一、文件总体结构

```
manifest
├── uses-permission × N        ← 申请权限
└── application                ← 应用全局配置
    ├── (全局属性: icon/label/theme/backup...)
    ├── activity × N           ← 注册 Activity
    │   └── intent-filter      ← 启动规则
    ├── service × N            ← 注册 Service
    ├── receiver × N           ← 注册 BroadcastReceiver（静态）
    │   └── intent-filter      ← 监听的 action
    └── provider × N           ← 注册 ContentProvider
```

整个 Manifest 本质是回答系统三个问题：

| 问题 | 由谁回答 |
|------|----------|
| **这个 App 需要什么能力？** | `<uses-permission>` |
| **这个 App 长什么样、叫什么名？** | `<application>` 的全局属性 |
| **这个 App 包含哪些组件、怎么启动它们？** | `<activity>`/`<service>`/`<receiver>`/`<provider>` 及其 `name`/`exported`/`intent-filter` |

---

## 二、文件头部（根节点）

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
```

| 属性 | 含义 |
|------|------|
| `<?xml ...?>` | XML 声明，指定版本 1.0、编码 UTF-8 |
| `xmlns:android` | 声明 `android` 命名空间，所有 `android:xxx` 属性都来自 Android 框架定义的 schema |
| `xmlns:tools` | 声明 `tools` 命名空间，用于只在编译期生效的属性（如 `tools:replace`、`tools:targetApi`），不会打包进 APK |

> **根节点 `<manifest>`** 是整个清单的容器，包名实际由 `build.gradle.kts` 中的 `namespace = "com.example.androidlearn"` 决定，所以这里不再写 `package` 属性（AGP 8.x 已废弃该写法）。

---

## 三、权限声明 `<uses-permission>`

```xml
<uses-permission android:name="android.permission.READ_CONTACTS" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

| 属性 | 含义 |
|------|------|
| `<uses-permission>` | 向系统申请一个权限 |
| `android:name` | 权限的完整常量名，必须以 `android.permission.` 开头（系统权限） |

### 权限分类

```
Android 权限体系
│
├── 普通权限（Normal Permissions）
│   ├── 安装时自动授予，无需弹窗
│   ├── 例：RECEIVE_BOOT_COMPLETED、INTERNET、VIBRATE
│   └── 处理：Manifest 声明即可
│
├── 危险权限（Dangerous Permissions）
│   ├── 涉及用户隐私，必须运行时动态申请
│   ├── 例：READ_CONTACTS、ACCESS_FINE_LOCATION、CAMERA
│   └── 处理：Manifest 声明 + 代码中 requestPermissions()
│
└── 签名权限（Signature Permissions）
    ├── 仅相同签名应用可获取
    └── 例：BIND_DEVICE_ADMIN
```

> **判断"需要什么权限"的途径**：①官方 API 文档的 "Required Permissions" ②Logcat 报错信息 ③Android Studio Lint 检查 ④[Manifest.permission](https://developer.android.com/reference/android/Manifest.permission) 常量页。
> 核心原则：**权限跟着 API 走，不跟着功能走**。

---

## 四、`<application>` 节点 —— 应用全局配置

这是整个应用的配置容器，所有四大组件都必须声明在它内部。

```xml
<application
    android:allowBackup="true"
    android:dataExtractionRules="@xml/data_extraction_rules"
    android:fullBackupContent="@xml/backup_rules"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:roundIcon="@mipmap/ic_launcher_round"
    android:supportsRtl="true"
    android:theme="@style/Theme.AndroidLearn">
```

| 属性 | 值 | 含义 |
|------|-----|------|
| `allowBackup` | `true` | 允许系统备份应用数据（adb backup / 云备份），默认 true |
| `dataExtractionRules` | `@xml/data_extraction_rules` | **Android 12+** 的备份/迁移规则文件，精确控制哪些数据可备份或迁移到新设备 |
| `fullBackupContent` | `@xml/backup_rules` | **Android 12 以前**的备份规则文件（旧机制，仍保留兼容） |
| `icon` | `@mipmap/ic_launcher` | 应用图标（启动器显示），引用 `res/mipmap-*/ic_launcher.webp` |
| `label` | `@string/app_name` | 应用显示名称，引用 `strings.xml` 中的 `app_name="AndroidLearn"` |
| `roundIcon` | `@mipmap/ic_launcher_round` | 圆形图标（用于 Pixel 等圆形图标启动器） |
| `supportsRtl` | `true` | 支持从右到左布局（阿拉伯语、希伯来语等 RTL 语言） |
| `theme` | `@style/Theme.AndroidLearn` | 应用全局主题，引用 `res/values/themes.xml` |

> 备份相关有两个属性（`dataExtractionRules` + `fullBackupContent`）是因为 Google 在 Android 12 重构了备份机制，新机制用前者，旧版本回退到后者。

### 其他常用 `<application>` 属性（本项目未用）

| 属性 | 含义 |
|------|------|
| `android:name` | 自定义 Application 类全路径（用于全局初始化，如 CrashHandler、SDK 初始化） |
| `android:debuggable` | 是否可调试（发布版本必须为 false） |
| `android:largeHeap` | 是否申请大堆内存（不推荐使用，应优化内存） |
| `android:hardwareAccelerated` | 是否开启硬件加速（默认 true） |
| `android:networkSecurityConfig` | 网络安全配置（如允许明文 HTTP） |

---

## 五、`<activity>` 节点 —— 注册 Activity

### 5.1 主 Activity（启动入口）

```xml
<activity
    android:name=".MainActivity"
    android:exported="true"
    android:windowSoftInputMode="adjustResize">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

| 属性 | 值 | 含义 |
|------|-----|------|
| `android:name` | `.MainActivity` | Activity 类名，`.` 表示相对于 `namespace` 包路径，等价于 `com.example.androidlearn.MainActivity` |
| `android:exported` | `true` | **是否可被其他应用启动**。主 Activity 必须为 true，否则桌面启动器无法打开你的 App |
| `android:windowSoftInputMode` | `adjustResize` | 软键盘弹出时的窗口行为：`adjustResize` 表示压缩布局给键盘腾空间（另一选项是 `adjustPan` 平移） |

**`<intent-filter>` 子节点**（决定如何被启动）：

| 子节点 | 值 | 含义 |
|--------|-----|------|
| `<action android:name="android.intent.action.MAIN" />` | `MAIN` | 表示这是应用的"主入口"动作 |
| `<category android:name="android.intent.category.LAUNCHER" />` | `LAUNCHER` | 表示应在桌面启动器中显示图标 |

> **`MAIN` + `LAUNCHER` 的组合是固定写法**，缺一个都不会在桌面显示图标。这两个一起告诉系统："点桌面图标时启动这个 Activity"。

### 5.2 内部演示 Activity

```xml
<activity
    android:name=".activity.ActivityLifecycleDemo"
    android:exported="false" />
```

| 属性 | 值 | 含义 |
|------|-----|------|
| `android:name` | `.activity.ActivityLifecycleDemo` | 子包下的类，路径相对于 `namespace` |
| `android:exported` | `false` | **仅应用内部可启动**，其他应用无法通过 Intent 拉起，安全隔离 |

> 这四个是内部演示页，不对外暴露，所以 `exported=false`。Android 12+ 强制要求所有组件显式声明 `exported`，不写会编译失败。

### 5.3 Activity 其他常用属性（本项目未用）

| 属性 | 含义 |
|------|------|
| `android:launchMode` | 启动模式：`standard`/`singleTop`/`singleTask`/`singleInstance` |
| `android:screenOrientation` | 屏幕方向：`portrait`/`landscape`/`sensor` |
| `android:configChanges` | 配置变化时自行处理（不重建 Activity），如 `orientation|screenSize` |
| `android:parentActivityName` | 声明父 Activity，用于 ActionBar 返回箭头 |
| `android:theme` | 单独覆盖应用主题 |
| `android:noHistory` | `true` 表示离开后自动 finish |

---

## 六、`<service>` 节点 —— 注册 Service

```xml
<service
    android:name=".service.MyService"
    android:exported="false" />
```

| 属性 | 值 | 含义 |
|------|-----|------|
| `android:name` | `.service.MyService` | Service 类全路径 |
| `android:exported` | `false` | 仅应用内部可启动/绑定，外部应用无法访问 |

> 如果想让其他应用绑定你的 Service（如提供 SDK 服务），需设 `exported=true` 并配合 `<intent-filter>`。本项目是教学场景，不对外暴露。

### Service 其他常用属性（本项目未用）

| 属性 | 含义 |
|------|------|
| `android:foregroundServiceType` | 前台服务类型（Android 10+ 强制），如 `location`/`camera`/`microphone` |
| `android:permission` | 启动/绑定此 Service 所需的权限 |
| `android:process` | 运行在独立进程（如 `:remote`） |

---

## 七、`<receiver>` 节点 —— 静态注册广播接收器

```xml
<receiver
    android:name=".receiver.MyReceiver"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
    </intent-filter>
</receiver>
```

| 属性 | 值 | 含义 |
|------|-----|------|
| `android:name` | `.receiver.MyReceiver` | 接收器类全路径 |
| `android:exported` | `true` | **必须为 true**，因为 `BOOT_COMPLETED` 是系统广播，发送方是系统进程（非本应用），不导出则收不到 |
| `<intent-filter>` | — | 声明此接收器监听哪些广播 action |
| `<action android:name="android.intent.action.BOOT_COMPLETED" />` | — | 监听"开机完成"系统广播 |

> **注意对比**：静态注册接收系统广播时 `exported` 必须为 `true`（系统是外部发送方）；而代码中动态注册的接收器（如本项目 `MainActivity` 中的）可用 `RECEIVER_NOT_EXPORTED`（仅接收本应用广播），二者场景不同。

### 动态注册 vs 静态注册

| 方式 | 位置 | 特点 |
|------|------|------|
| 静态注册 | AndroidManifest.xml | 应用未启动也能接收（Android 8.0+ 对隐式广播有限制） |
| 动态注册 | 代码中 `registerReceiver()` | 跟随组件生命周期，灵活可控 |

---

## 八、`<provider>` 节点 —— 注册 ContentProvider

```xml
<provider
    android:name=".provider.UserContentProvider"
    android:authorities="com.example.androidlearn.provider"
    android:exported="true" />
```

| 属性 | 值 | 含义 |
|------|-----|------|
| `android:name` | `.provider.UserContentProvider` | Provider 类全路径 |
| `android:authorities` | `com.example.androidlearn.provider` | **URI 的唯一标识（主机名）**，全局唯一，其他应用通过 `content://com.example.androidlearn.provider/users` 访问 |
| `android:exported` | `true` | 允许其他应用访问（教学演示目的） |

> **`authorities` 是 Provider 最关键的属性**，相当于它的"域名"。两个应用的 authorities 不能重复，否则第二个无法安装。如果要对外暴露真实数据，还应配合 `android:readPermission` / `android:writePermission` 做权限管控。

### Provider 其他常用属性（本项目未用）

| 属性 | 含义 |
|------|------|
| `android:readPermission` | 查询此 Provider 所需的权限 |
| `android:writePermission` | 修改此 Provider 所需的权限 |
| `android:permission` | 读写统一权限（等价于同时设置上两个） |
| `android:grantUriPermissions` | 是否允许临时授权访问（如通过 Intent 传 URI） |
| `android:process` | 运行在独立进程 |
| `android:enabled` | 是否启用 |

---

## 九、核心属性横向对比

### 9.1 `android:exported` —— 最常见、最易出错

| 组件 | 本项目取值 | 原因 |
|------|-----------|------|
| MainActivity | `true` | 桌面启动器要能拉起 |
| 演示 Activity（4个） | `false` | 仅内部跳转 |
| MyService / MyBindService | `false` | 仅内部启停 |
| MyReceiver（静态） | `true` | 要接收系统广播 |
| UserContentProvider | `true` | 教学演示对外暴露 |

**决策口诀**：组件是否需要被**本应用之外**的组件（系统、其他应用）访问？需要 → `true`，不需要 → `false`。Android 12+ 必须显式声明，否则编译报错。

### 9.2 `android:name` 的写法

| 写法 | 含义 | 示例 |
|------|------|------|
| `.MainActivity` | 相对 namespace | `com.example.androidlearn.MainActivity` |
| `.activity.DataPassDemo` | 子包路径 | `com.example.androidlearn.activity.DataPassDemo` |
| `com.example.androidlearn.MainActivity` | 完整类名 | 等价于第一种 |

### 9.3 `android:icon` vs `android:roundIcon`

| 属性 | 用途 |
|------|------|
| `icon` | 默认图标（方形/自适应） |
| `roundIcon` | 圆形图标，仅支持圆形图标的启动器（如旧版 Pixel）使用 |

### 9.4 备份相关两个属性

| 属性 | 适用版本 | 用途 |
|------|----------|------|
| `fullBackupContent` | Android 12 以前 | 旧版备份规则 |
| `dataExtractionRules` | Android 12+ | 新版备份/迁移规则 |

两者同时声明是为了向前向后兼容。

---

## 十、intent-filter 常用 action / category

### 常用 action

| Action 常量 | 含义 |
|-------------|------|
| `android.intent.action.MAIN` | 应用主入口 |
| `android.intent.action.VIEW` | 查看数据（URL、文件等） |
| `android.intent.action.SEND` | 分享数据 |
| `android.intent.action.DIAL` | 拨号 |
| `android.intent.action.BOOT_COMPLETED` | 开机完成（系统广播） |
| `android.intent.action.BATTERY_LOW` | 电量低（系统广播） |

### 常用 category

| Category 常量 | 含义 |
|---------------|------|
| `android.intent.category.LAUNCHER` | 在桌面启动器显示图标 |
| `android.intent.category.DEFAULT` | 默认类别（隐式 Intent 默认匹配项） |
| `android.intent.category.BROWSABLE` | 可从浏览器跳转 |
| `android.intent.category.APP_BROWSER` | 浏览器应用 |

---

## 十一、资源引用语法

Manifest 中大量出现 `@xxx/yyy` 形式的引用，统一规则如下：

| 语法 | 含义 | 示例 |
|------|------|------|
| `@string/xxx` | 引用字符串资源 | `@string/app_name` |
| `@mipmap/xxx` | 引用图片资源（密度相关） | `@mipmap/ic_launcher` |
| `@style/xxx` | 引用样式资源 | `@style/Theme.AndroidLearn` |
| `@xml/xxx` | 引用 XML 资源 | `@xml/backup_rules` |
| `@drawable/xxx` | 引用 drawable 资源 | `@drawable/ic_logo` |
| `@color/xxx` | 引用颜色资源 | `@color/black` |
| `@+id/xxx` | 新增一个 ID（多用于布局） | `@+id/btn_start` |
| `@id/xxx` | 引用已存在的 ID | `@id/btn_start` |

> 资源引用会经过编译期校验，引用不存在的资源会编译失败，这是 Android 资源系统的安全保障。

---

## 十二、参考链接

| 资料 | 链接 |
|------|------|
| 官方 Manifest 文档 | https://developer.android.com/guide/topics/manifest/manifest-intro |
| `<application>` 标签 | https://developer.android.com/guide/topics/manifest/application-element |
| `<activity>` 标签 | https://developer.android.com/guide/topics/manifest/activity-element |
| `<intent-filter>` 标签 | https://developer.android.com/guide/topics/manifest/intent-filter-element |
| 权限列表 | https://developer.android.com/reference/android/Manifest.permission |

---

## 十三、配套文档索引

| 文档 | 用途 |
|------|------|
| [项目代码阅读指南.md](./项目代码阅读指南.md) | 如何阅读本项目源码 |
| [四大组件学习教程.md](./四大组件学习教程.md) | 四大组件概念详解 |
| [四大组件快速参考.md](./四大组件快速参考.md) | 四大组件 API 速查 |
| [Android开发学习文档.md](./Android开发学习文档.md) | Android 开发整体路线 |
| [Gradle学习文档.md](./Gradle学习文档.md) | Gradle 构建配置 |
| **AndroidManifest属性详解.md（本文）** | **Manifest 各属性含义解析** |