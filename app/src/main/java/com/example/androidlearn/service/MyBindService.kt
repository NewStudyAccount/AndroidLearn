package com.example.androidlearn.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

/**
 * 绑定服务示例
 *
 * 演示 bindService 方式启动服务
 *
 * 特点：
 * - 服务与调用者绑定，调用者退出后服务自动停止
 * - 可以进行方法调用，实现双向通信
 * - 适用于需要与 Activity 交互的场景
 */
class MyBindService : Service() {

    companion object {
        private const val TAG = "MyBindService"
    }

    // 创建 Binder 对象，用于与 Activity 通信
    private val binder = LocalBinder()

    /**
     * 本地 Binder 类
     * 用于返回 Service 实例，让 Activity 可以调用 Service 的方法
     */
    inner class LocalBinder : Binder() {
        fun getService(): MyBindService = this@MyBindService
    }

    /**
     * 绑定服务时调用
     * 返回 Binder 对象给调用者
     */
    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "===== onBind: 服务绑定 =====")
        return binder
    }

    /**
     * 所有绑定都解绑后调用
     */
    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "===== onUnbind: 服务解绑 =====")
        // 返回 true 表示下次绑定时调用 onRebind() 而不是 onBind()
        return super.onUnbind(intent)
    }

    /**
     * 重新绑定时调用（当 onUnbind 返回 true 时）
     */
    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(TAG, "===== onRebind: 服务重新绑定 =====")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "===== onCreate: 服务创建 =====")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "===== onDestroy: 服务销毁 =====")
    }

    // ========== 以下是可以被 Activity 调用的方法 ==========

    private var count = 0

    /**
     * 获取当前计数
     */
    fun getCount(): Int {
        count++
        Log.d(TAG, "getCount() 被调用，返回: $count")
        return count
    }

    /**
     * 获取服务数据
     */
    fun getData(): String {
        return "来自 BindService 的数据 - ${System.currentTimeMillis()}"
    }

    /**
     * 执行任务
     */
    fun performTask(taskName: String): String {
        Log.d(TAG, "执行任务: $taskName")
        return "任务 '$taskName' 已完成"
    }
}
