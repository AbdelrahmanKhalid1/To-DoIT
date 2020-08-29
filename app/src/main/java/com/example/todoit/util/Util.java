package com.example.todoit.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;

import com.example.todoit.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Util {

    public static final String USERNAME = "username";
    //    public static final String DATE_FORMAT = "d MMM yyyy";
    public static final int DATE_FORMAT = DateFormat.DEFAULT;
    public static final String TIME_12_FORMAT = "h:mm a";
    public static final String TIME_24_FORMAT = "HH:mm";
    public static final int DEFUALT_CATEOGRY = 0;

    public static String getTime(String format, Calendar calendar) {
        return new SimpleDateFormat(format).format(calendar.getTime());
    }

    public static String getWelcomeMessage() {
        @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat("H").format(Calendar.getInstance().getTime());
        int hour = Integer.parseInt(time);
        if (hour >= 5 && hour <= 11)
            return "Good Morning";
        else if (hour >= 12 && hour <= 16)
            return "Good Afternoon";
        else if (hour >= 17 && hour <= 18)
            return "Good Evening";
        else
            return "Good Night";
    }

    public static String getTimeFormated(Calendar calendar, Context context) {
        return (android.text.format.DateFormat.is24HourFormat(context)) ? Util.getTime(Util.TIME_24_FORMAT, calendar) :
                Util.getTime(Util.TIME_12_FORMAT, calendar);
    }

    public static String getTimeFormated(String[] time, Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        return getTimeFormated(calendar, context);
    }

    public static Category[] getCategory() {
        return new Category[]{
                new Category("No\nCategory", Color.TRANSPARENT, null),
                new Category("Work", R.color.categoryWork, R.drawable.ic_work),
                new Category("Mail", R.color.categoryMail, R.drawable.ic_mail),
                new Category("Game", R.color.categoryGame, R.drawable.ic_game),
                new Category("Life", R.color.categoryLife, R.drawable.ic_baseline_event_note_24),
                new Category("Health", R.color.categoryHealth, R.drawable.ic_favorite),
                new Category("Travel", R.color.categoryTravel, R.drawable.ic_baseline_departure_board_24),
        };
    }
}