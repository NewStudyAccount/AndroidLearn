package com.example.androidlearn.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * 自定义广播接收器示例
 *
 * 演示如何接收系统广播和自定义广播
 */
class MyReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "MyReceiver"
        const val ACTION_CUSTOM = "com.example.androidlearn.CUSTOM_ACTION"
    }

    /**
     * 接收到广播时调用
     *
     * 注意：
     * - 这个方法在主线程中执行，不能执行耗时操作
     * - 如果需要执行耗时操作，应该启动 Service
     * - Android 8.0+ 对隐式广播有限制，推荐使用显式广播
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        Log.d(TAG, "===== onReceive: 收到广播 =====")
        Log.d(TAG, "Action: $action")

        when (action) {
            // 开机完成广播
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d(TAG, "系统启动完成")
                Toast.makeText(context, "系统启动完成", Toast.LENGTH_SHORT).show()
            }

            // 电量低广播
            Intent.ACTION_BATTERY_LOW -> {
                Log.d(TAG, "电量低")
                Toast.makeText(context, "电量低，请充电！", Toast.LENGTH_SHORT).show()
            }

            // 飞行模式变化广播
            Intent.ACTION_AIRPLANE_MODE_CHANGED -> {
                val isOn = intent.getBooleanExtra("state", false)
                Log.d(TAG, "飞行模式: ${if (isOn) "开启" else "关闭"}")
                Toast.makeText(
                    context,
                    "飞行模式: ${if (isOn) "开启" else "关闭"}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // 自定义广播
            ACTION_CUSTOM -> {
                val message = intent.getStringExtra("message") ?: ""
                Log.d(TAG, "收到自定义广播: $message")
                Toast.makeText(context, "收到: $message", Toast.LENGTH_SHORT).show()
            }

            else -> {
                Log.d(TAG, "未知广播: $action")
            }
        }
    }
}
