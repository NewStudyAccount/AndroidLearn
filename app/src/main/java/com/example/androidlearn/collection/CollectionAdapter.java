package com.example.androidlearn.collection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidlearn.R;

import java.util.List;

/**
 * 收藏列表适配器
 * 参考 TodoAdapter 的结构，展示收藏条目信息
 */
public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {

    private List<CollectionItem> items;
    private final OnItemClickListener listener;

    /**
     * 点击回调接口
     */
    public interface OnItemClickListener {
        /** 点击"更多"按钮 */
        void onMoreClick(CollectionItem item);
    }

    public CollectionAdapter(List<CollectionItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collection, parent, false);
        return new CollectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
        CollectionItem item = items.get(position);

        // 设置标题
        holder.tvTitle.setText(item.getTitle());

        // 设置分类
        holder.tvCategory.setText(item.getCategory());

        // 设置评分（先清空监听器，防止复用时误触发）
        holder.ratingBar.setOnRatingBarChangeListener(null);
        holder.ratingBar.setRating(item.getRating());

        // 设置备注
        holder.tvNote.setText(item.getNote());

        // 根据分类设置封面占位图
        switch (item.getCategory()) {
            case "电影":
                holder.ivCover.setImageResource(android.R.drawable.ic_media_play);
                break;
            case "书籍":
                holder.ivCover.setImageResource(android.R.drawable.ic_menu_edit);
                break;
            case "音乐":
                holder.ivCover.setImageResource(android.R.drawable.ic_media_ff);
                break;
            default:
                holder.ivCover.setImageResource(android.R.drawable.ic_menu_gallery);
                break;
        }

        // "更多"按钮点击 -> 通知 Activity 处理
        holder.btnMore.setOnClickListener(v -> listener.onMoreClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * 更新数据并刷新列表
     */
    public void updateData(List<CollectionItem> newItems) {
        items = newItems;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder：持有 item_collection.xml 中的控件引用
     */
    static class CollectionViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;
        TextView tvCategory;
        RatingBar ratingBar;
        TextView tvNote;
        Button btnMore;

        CollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.ivCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvNote = itemView.findViewById(R.id.tvNote);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}
