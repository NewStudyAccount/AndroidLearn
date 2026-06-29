package com.example.androidlearn.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.androidlearn.R

/**
 * Activity 生命周期演示
 *
 * 运行此 Activity，观察 Logcat 中的生命周期日志：
 * - 打开 Activity: onCreate → onStart → onResume
 * - 按 Home 键: onPause → onStop
 * - 返回 Activity: onRestart → onStart → onResume
 * - 按返回键: onPause → onStop → onDestroy
 */
class ActivityLifecycleDemo : AppCompatActivity() {

    companion object {
        private const val TAG = "LifecycleDemo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lifecycle_demo)
        Log.d(TAG, "===== onCreate: Activity 创建 =====")
        Log.d(TAG, "这是初始化的地方：加载布局、绑定数据、初始化控件")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "===== onStart: Activity 可见 =====")
        Log.d(TAG, "Activity 变为可见，但用户还不能交互")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "===== onResume: Activity 可交互 =====")
        Log.d(TAG, "Activity 进入前台，可以与用户交互")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "===== onPause: Activity 失去焦点 =====")
        Log.d(TAG, "Activity 失去焦点（如弹出对话框、跳转到其他页面）")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "===== onStop: Activity 不可见 =====")
        Log.d(TAG, "Activity 完全不可见")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "===== onDestroy: Activity 销毁 =====")
        Log.d(TAG, "Activity 被销毁，释放资源")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "===== onRestart: Activity 重启 =====")
        Log.d(TAG, "Activity 从停止状态重新启动")
    }
}
