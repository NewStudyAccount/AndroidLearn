package com.example.androidlearn.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast

/**
 * 后台服务示例
 *
 * 演示 startService 方式启动服务
 *
 * 特点：
 * - 服务启动后独立运行，即使启动它的 Activity 被销毁
 * - 需要手动调用 stopService() 或 stopSelf() 停止服务
 * - 适用于执行长时间运行的后台任务（如下载、音乐播放）
 */
class MyService : Service() {

    companion object {
        private const val TAG = "MyService"
    }

    /**
     * 绑定服务时调用
     * 使用 startService 方式启动时返回 null
     */
    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind")
        return null
    }

    /**
     * 服务首次创建时调用（只调用一次）
     * 适合进行初始化操作
     */
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "===== onCreate: 服务创建 =====")
        Toast.makeText(this, "服务创建", Toast.LENGTH_SHORT).show()
    }

    /**
     * 每次通过 startService() 启动服务时调用
     *
     * @param intent 启动服务时传入的 Intent
     * @param flags 额外标志
     * @param startId 服务的唯一标识
     * @return 返回值决定服务被杀死后的重启策略
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "===== onStartCommand: 服务启动 (startId=$startId) =====")

        // 模拟后台任务
        Thread {
            for (i in 1..10) {
                Log.d(TAG, "后台任务执行中: $i/10")
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    Log.e(TAG, "任务被中断", e)
                    return@Thread
                }
            }
            Log.d(TAG, "后台任务完成！")
            // 任务完成后停止服务
            stopSelf()
        }.start()

        // 返回值说明：
        // START_STICKY: 服务被杀死后，系统会尝试重新创建服务，但不保留 Intent
        // START_NOT_STICKY: 服务被杀死后，不会自动重启
        // START_REDELIVER_INTENT: 服务被杀死后，会重新创建并传递最后一个 Intent
        return START_STICKY
    }

    /**
     * 服务销毁时调用
     * 在这里释放资源
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "===== onDestroy: 服务销毁 =====")
        Toast.makeText(this, "服务销毁", Toast.LENGTH_SHORT).show()
    }
}
