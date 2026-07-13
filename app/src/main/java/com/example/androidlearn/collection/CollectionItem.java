package com.example.androidlearn.collection;

/**
 * 收藏条目数据模型
 * 对应 item_collection.xml 中展示的字段
 */
public class CollectionItem {
    private int id;
    private String title;
    private String category;   // 电影 / 书籍 / 音乐
    private float rating;      // 评分 0~5
    private String note;       // 备注

    public CollectionItem() {
    }

    public CollectionItem(int id, String title, String category, float rating, String note) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.rating = rating;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
