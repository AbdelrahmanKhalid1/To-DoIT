package com.example.todoit.notification.receiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.todoit.R;
import com.example.todoit.add.AddTaskActivity;
import com.example.todoit.notification.ToDoIT;
import com.example.todoit.util.Keys;
import com.example.todoit.util.Util;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra(Keys.TASK_ID, -1);
        String title = intent.getStringExtra(Keys.TASK_TITLE);
        long time = intent.getLongExtra(Keys.TASK_TIME, -1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        //To open Activity
        Intent activityIntent = new Intent(context, AddTaskActivity.class);
        activityIntent.putExtra(Keys.TASK_ID, notificationId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId,
                activityIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        //To Snooze
        Intent snoozeIntent = new Intent(context, SnoozeReceiver.class);
        snoozeIntent.putExtra(Keys.SNOOZE_ID, notificationId);
        snoozeIntent.putExtra(Keys.SNOOZE_TITLE, title);
        snoozeIntent.putExtra(Keys.SNOOZE_time, calendar.getTimeInMillis());
        PendingIntent pendingSnoozeIntent = PendingIntent.getBroadcast(context, notificationId,
                snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification builder = new NotificationCompat.Builder(context, ToDoIT.ALARM_CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_task_launcher)
                .setContentTitle(title)
                .setContentText("Today at " + Util.getTimeFormated(calendar, context))
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_add_alarm, "Snooze",  pendingSnoozeIntent)
//                .setSound(alarmSound)
                .setColor(Color.argb(1, 111, 81, 225))
                .build();

        notificationManager.notify(notificationId, builder);
    }
}
