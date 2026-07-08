package com.example.androidlearn.todo

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlearn.R
import java.text.SimpleDateFormat
import java.util.*


/**
 * 待办事项适配器
 *
 * 和你之前写的 UserAdapter 结构完全一样，
 * 只是多了两个交互：复选框点击 和 删除按钮点击。
 *
 * @param todos 待办列表数据
 * @param onToggleDone 勾选/取消勾选时的回调（传给 Activity 处理数据库更新）
 * @param onDelete 点击删除时的回调（传给 Activity 处理数据库删除）
 */
class TodoAdapter(
    private var todos: MutableList<TodoItem>,
    private val onToggleDone: (TodoItem) -> Unit,
    private val onDelete: (TodoItem) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbDone: CheckBox = itemView.findViewById(R.id.cbDone)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todos[position]

        // 设置标题
        holder.tvTitle.text = todo.title

        // 设置时间（格式化时间戳为 "HH:mm" 格式）
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        holder.tvTime.text = sdf.format(Date(todo.createdAt))

        // 设置复选框状态（先清空监听器，防止复用时误触发）
        holder.cbDone.setOnCheckedChangeListener(null)
        holder.cbDone.isChecked = todo.isDone

        // 已完成的样式：删除线 + 半透明
        if (todo.isDone) {
            holder.tvTitle.paintFlags =
                holder.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvTitle.alpha = 0.5f
        } else {
            holder.tvTitle.paintFlags =
                holder.tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.tvTitle.alpha = 1.0f
        }

        // 复选框点击 -> 通知 Activity 更新数据库
        holder.cbDone.setOnCheckedChangeListener { _, _ ->
            onToggleDone(todo)
        }

        // 删除按钮点击 -> 通知 Activity 删除记录
        holder.btnDelete.setOnClickListener {
            onDelete(todo)
        }
    }

    override fun getItemCount() = todos.size

    /**
     * 更新数据并刷新列表
     * 每次数据库操作后，Activity 调用这个方法传入新数据
     */
    fun updateData(newTodos: List<TodoItem>) {
        todos.clear()
        todos.addAll(newTodos)
        notifyDataSetChanged()  // 通知 RecyclerView 整个列表需要刷新
    }


}