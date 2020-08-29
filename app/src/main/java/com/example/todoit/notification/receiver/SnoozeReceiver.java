package com.example.todoit.notification.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

import com.example.todoit.util.Keys;

import java.util.Calendar;

public class SnoozeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int taskId = intent.getIntExtra(Keys.SNOOZE_ID, -1);
        String title = intent.getStringExtra(Keys.SNOOZE_TITLE);
        long time = intent.getLongExtra(Keys.SNOOZE_time, -1) + 10 * 60000; //10 * 1 min in millisecs
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(taskId); //To cancel notification (close)

        Intent intentActivity = new Intent(context, AlarmReceiver.class);
        intentActivity.putExtra(Keys.TASK_ID, taskId);
        intentActivity.putExtra(Keys.TASK_TITLE, title);
        intentActivity.putExtra(Keys.TASK_TIME, time);

        PendingIntent pendingActivityIntent = PendingIntent.getBroadcast(context, taskId,
                intentActivity, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingActivityIntent);

//            Toast.makeText(context, "Time Before = " + time +
//                    "Time After = " + Util.getTimeFormated(calendar, context), Toast.LENGTH_LONG).show();
    }
}
