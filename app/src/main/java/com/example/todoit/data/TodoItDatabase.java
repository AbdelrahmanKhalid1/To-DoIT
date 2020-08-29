package com.example.todoit.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.todoit.R;
import com.example.todoit.data.entity.Task;
import com.example.todoit.data.dao.TaskDao;

@Database(entities = Task.class, version = 1)
public abstract class TodoItDatabase extends RoomDatabase {

    private static TodoItDatabase instance;
    public abstract TaskDao taskDao();

    public static synchronized TodoItDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    TodoItDatabase.class, context.getResources().getString(R.string.app_name))
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
