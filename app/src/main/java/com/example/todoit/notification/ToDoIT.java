package com.example.todoit.notification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class ToDoIT extends Application {
    public static final String ALARM_CHANNEL_ID = "channel alarm";
//    public static final String ALARM_CHANNEL_NAME = "Alarm";

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createAlarmChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createAlarmChannel() {
        NotificationChannel channel = new NotificationChannel(
                ALARM_CHANNEL_ID,
                "Alarm",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Alarm Channel to remind user next task");
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
}
