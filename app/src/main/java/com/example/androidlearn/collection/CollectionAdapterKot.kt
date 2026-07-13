package com.example.androidlearn.collection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlearn.R

/**
 * 收藏列表适配器（Kotlin 版）
 */
class CollectionAdapterKot(
    private var items: List<CollectionItemKot>,
    private val onMoreClick: (CollectionItemKot) -> Unit
) : RecyclerView.Adapter<CollectionAdapterKot.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val tvNote: TextView = itemView.findViewById(R.id.tvNote)
        val btnMore: Button = itemView.findViewById(R.id.btnMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvTitle.text = item.title
        holder.tvCategory.text = item.category
        holder.tvNote.text = item.note

        // 评分（先清空监听器，防止复用时误触发）
        holder.ratingBar.setOnRatingBarChangeListener(null)
        holder.ratingBar.rating = item.rating

        // 根据分类设置封面占位图
        holder.ivCover.setImageResource(
            when (item.category) {
                "电影" -> android.R.drawable.ic_media_play
                "书籍" -> android.R.drawable.ic_menu_edit
                "音乐" -> android.R.drawable.ic_media_ff
                else -> android.R.drawable.ic_menu_gallery
            }
        )

        // "更多"按钮点击
        holder.btnMore.setOnClickListener { onMoreClick(item) }
    }

    override fun getItemCount() = items.size

    /**
     * 更新数据并刷新列表
     */
    fun updateData(newItems: List<CollectionItemKot>) {
        items = newItems
        notifyDataSetChanged()
    }
}
