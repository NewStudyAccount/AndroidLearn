package com.example.androidlearn

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidlearn.activity.ActivityLifecycleDemo
import com.example.androidlearn.activity.DataPassDemo
import com.example.androidlearn.activity.RecyclerDemoActivity
import com.example.androidlearn.receiver.MyReceiver
import com.example.androidlearn.service.MyBindService
import com.example.androidlearn.service.MyService

/**
 * 主 Activity - 四大组件综合演示
 *
 * 本 Activity 演示如何使用 Android 四大组件：
 * 1. Activity - 生命周期、页面跳转
 * 2. Service - 后台服务
 * 3. BroadcastReceiver - 广播接收
 * 4. ContentProvider - 内容提供
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    // ========== Service 相关变量 ==========
    private var myBindService: MyBindService? = null
    private var isBound = false

    // 服务连接回调
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "===== onServiceConnected: 服务已连接 =====")
            val binder = service as MyBindService.LocalBinder
            myBindService = binder.getService()
            isBound = true
            updateServiceStatus("BindService 已连接")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "===== onServiceDisconnected: 服务已断开 =====")
            myBindService = null
            isBound = false
            updateServiceStatus("BindService 已断开")
        }
    }

    // ========== BroadcastReceiver 相关变量 ==========
    private lateinit var myReceiver: MyReceiver
    private var isReceiverRegistered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "===== onCreate: MainActivity 创建 =====")

        setupUI()
    }

    /**
     * 初始化 UI 组件
     */
    private fun setupUI() {
        val tvStatus = findViewById<TextView>(R.id.tvStatus)

        // ========== Activity 演示 ==========

        // 1. Activity 生命周期演示
        findViewById<Button>(R.id.btnLifecycle).setOnClickListener {
            Log.d(TAG, "点击: 打开生命周期演示")
            val intent = Intent(this, ActivityLifecycleDemo::class.java)
            startActivity(intent)
        }

        // 2. 数据传递演示
        findViewById<Button>(R.id.btnDataPass).setOnClickListener {
            Log.d(TAG, "点击: 打开数据传递演示")
            val intent = Intent(this, DataPassDemo::class.java)
            startActivity(intent)
        }

        // ========== Service 演示 ==========

        // 3. 启动 Service (startService)
        findViewById<Button>(R.id.btnStartService).setOnClickListener {
            Log.d(TAG, "点击: 启动 MyService")
            val intent = Intent(this, MyService::class.java)
            startService(intent)
            Toast.makeText(this, "MyService 已启动", Toast.LENGTH_SHORT).show()
            updateServiceStatus("MyService 运行中...")
        }

        // 4. 停止 Service
        findViewById<Button>(R.id.btnStopService).setOnClickListener {
            Log.d(TAG, "点击: 停止 MyService")
            val intent = Intent(this, MyService::class.java)
            stopService(intent)
            Toast.makeText(this, "MyService 已停止", Toast.LENGTH_SHORT).show()
            updateServiceStatus("MyService 已停止")
        }

        // 5. 绑定 Service (bindService)
        findViewById<Button>(R.id.btnBindService).setOnClickListener {
            Log.d(TAG, "点击: 绑定 BindService")
            if (!isBound) {
                val intent = Intent(this, MyBindService::class.java)
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
                Toast.makeText(this, "正在绑定 BindService...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "BindService 已经绑定", Toast.LENGTH_SHORT).show()
            }
        }

        // 6. 调用 Service 方法
        findViewById<Button>(R.id.btnCallService).setOnClickListener {
            if (isBound) {
                val data = myBindService?.getData()
                val count = myBindService?.getCount()
                Toast.makeText(this, "数据: $data\n计数: $count", Toast.LENGTH_LONG).show()
                Log.d(TAG, "调用 Service 方法: data=$data, count=$count")
            } else {
                Toast.makeText(this, "请先绑定 BindService", Toast.LENGTH_SHORT).show()
            }
        }

        // 7. 解绑 Service
        findViewById<Button>(R.id.btnUnbindService).setOnClickListener {
            Log.d(TAG, "点击: 解绑 BindService")
            if (isBound) {
                unbindService(serviceConnection)
                isBound = false
                myBindService = null
                Toast.makeText(this, "BindService 已解绑", Toast.LENGTH_SHORT).show()
                updateServiceStatus("BindService 已解绑")
            } else {
                Toast.makeText(this, "BindService 未绑定", Toast.LENGTH_SHORT).show()
            }
        }

        // ========== BroadcastReceiver 演示 ==========

        // 8. 注册广播接收器
        findViewById<Button>(R.id.btnRegisterReceiver).setOnClickListener {
            Log.d(TAG, "点击: 注册广播接收器")
            if (!isReceiverRegistered) {
                myReceiver = MyReceiver()
                val filter = IntentFilter().apply {
                    addAction(Intent.ACTION_BATTERY_LOW)
                    addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
                    addAction(MyReceiver.ACTION_CUSTOM)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    registerReceiver(myReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
                } else {
                    registerReceiver(myReceiver, filter)
                }
                isReceiverRegistered = true
                Toast.makeText(this, "广播接收器已注册", Toast.LENGTH_SHORT).show()
                updateReceiverStatus("广播接收器已注册")
            } else {
                Toast.makeText(this, "广播接收器已注册", Toast.LENGTH_SHORT).show()
            }
        }

        // 9. 发送自定义广播
        findViewById<Button>(R.id.btnSendBroadcast).setOnClickListener {
            Log.d(TAG, "点击: 发送自定义广播")
            val intent = Intent(MyReceiver.ACTION_CUSTOM).apply {
                putExtra("message", "Hello from MainActivity!")
                setPackage(packageName)  // Android 8.0+ 需要指定包名
            }
            sendBroadcast(intent)
            Toast.makeText(this, "广播已发送", Toast.LENGTH_SHORT).show()
        }

        // 10. 取消注册广播接收器
        findViewById<Button>(R.id.btnUnregisterReceiver).setOnClickListener {
            Log.d(TAG, "点击: 取消注册广播接收器")
            if (isReceiverRegistered) {
                unregisterReceiver(myReceiver)
                isReceiverRegistered = false
                Toast.makeText(this, "广播接收器已取消注册", Toast.LENGTH_SHORT).show()
                updateReceiverStatus("广播接收器未注册")
            } else {
                Toast.makeText(this, "广播接收器未注册", Toast.LENGTH_SHORT).show()
            }
        }

        // ========== ContentProvider 演示 ==========

        // 11. 查询 ContentProvider
        findViewById<Button>(R.id.btnQueryProvider).setOnClickListener {
            Log.d(TAG, "点击: 查询 ContentProvider")
            queryContentProvider()
        }

        // 12. RecyclerView 综合演示
        findViewById<Button>(R.id.btnRecyclerDemo).setOnClickListener {
            Log.d(TAG, "点击: 打开 RecyclerView 演示")
            val intent = Intent(this, RecyclerDemoActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * 查询 ContentProvider 数据
     */
    private fun queryContentProvider() {
        val cursor = contentResolver.query(
            com.example.androidlearn.provider.UserContentProvider.CONTENT_URI,
            null,  // 所有列
            null,  // 没有 WHERE 条件
            null,  // 没有参数
            null   // 默认排序
        )

        val sb = StringBuilder()
        sb.appendLine("=== ContentProvider 查询结果 ===")

        cursor?.use {
            while (it.moveToNext()) {
                val idIndex = it.getColumnIndex("_id")
                val nameIndex = it.getColumnIndex("name")
                val ageIndex = it.getColumnIndex("age")
                val emailIndex = it.getColumnIndex("email")

                val id = it.getInt(idIndex)
                val name = it.getString(nameIndex)
                val age = it.getInt(ageIndex)
                val email = it.getString(emailIndex)

                sb.appendLine("ID: $id, 姓名: $name, 年龄: $age, 邮箱: $email")
            }
        }

        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        tvStatus.text = sb.toString()
        Log.d(TAG, "ContentProvider 查询完成")
    }

    /**
     * 更新服务状态显示
     */
    private fun updateServiceStatus(status: String) {
        val tvServiceStatus = findViewById<TextView>(R.id.tvServiceStatus)
        tvServiceStatus.text = "Service 状态: $status"
    }

    /**
     * 更新广播状态显示
     */
    private fun updateReceiverStatus(status: String) {
        val tvReceiverStatus = findViewById<TextView>(R.id.tvReceiverStatus)
        tvReceiverStatus.text = "Receiver 状态: $status"
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "===== onDestroy: MainActivity 销毁 =====")

        // 清理资源：解绑服务
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }

        // 清理资源：取消注册广播接收器
        if (isReceiverRegistered) {
            unregisterReceiver(myReceiver)
            isReceiverRegistered = false
        }
    }
}
