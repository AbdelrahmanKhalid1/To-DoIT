package com.example.todoit.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.todoit.data.entity.Task;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface TaskDao {

    @Insert
    Completable insert(Task task);
    @Update
    Completable update(Task task);
    @Delete
    Completable delete(Task task);
    @Delete
    Completable deleteList(List<Task> tasks);
    @Query("SELECT * FROM task_table WHERE date = :date ORDER BY time ASC")
    Single<List<Task>> getAllTodayTasks(String date);
    @Query("SELECT * FROM task_table WHERE id = :id")
    Single<Task> getTask(int id);
    @Query("SELECT MAX(id) FROM task_table")
    Single<Integer> getMaxId();

}
