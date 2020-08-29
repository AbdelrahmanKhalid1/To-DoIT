package com.example.todoit.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "task_table")
public class Task implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String desc;
    private String date;
    private String time;
    private int category;

    public Task(String title, String desc, String date, String time, int category) {
        this.title = title;
        this.desc = desc;
        this.date = date;
        this.time = time;
        this.category = category;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getCategory() {
        return category;
    }

    public String printValues() {
        return "Title: " + title +
                "\nDesc: " + desc +
                "\nDay: " + date +
                "\nTime: " + time;
    }
}
