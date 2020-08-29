package com.example.todoit.add;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.todoit.data.TodoItDatabase;
import com.example.todoit.notification.receiver.AlarmReceiver;
import com.example.todoit.util.DatePickerFragment;
import com.example.todoit.R;
import com.example.todoit.data.entity.Task;
import com.example.todoit.util.Keys;
import com.example.todoit.util.OnRecyclerItemClickListener;
import com.example.todoit.util.TimePickerFragment;
import com.example.todoit.util.Util;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

//TODO make abstract class and create two childs update and add
public class AddTaskActivity extends AppCompatActivity implements OnRecyclerItemClickListener.OnItemClickListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, AddTaskPresenter.AddTaskView {

    private static final String TAG = "AddTaskActivity";
    private RecyclerView recycler;
    private EditText title, desc, date, time;
    private TextView reminder;
    private Task task;
    private Calendar calendar;
    private CategoryAdapter adapter;
    private boolean isNewTask;
    private AddTaskPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        //region intiliztation
        title = findViewById(R.id.editText_task_title);
        desc = findViewById(R.id.editText_task_desc);
        date = findViewById(R.id.editText_day);
        time = findViewById(R.id.editText_time);
        reminder = findViewById(R.id.textView_reminder);
        recycler = findViewById(R.id.recycler_category);
        calendar = Calendar.getInstance();
        //endregion

        Toolbar toolbar = findViewById(R.id.toolbar_add_task);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TodoItDatabase db = TodoItDatabase.getInstance(this);
        presenter = new AddTaskPresenter(this, db.taskDao());

        buildRecycler();

        if(getIntent().getIntExtra(Keys.TASK_ID, -1) != -1)
            presenter.getTaskById(getIntent().getIntExtra(Keys.TASK_ID, -1)); //if activity is started from notification
        else {
            task = (Task) getIntent().getSerializableExtra(Keys.ADD_TASK); //if activity is started from main activity
            setUI();
        }
    }

    private void buildRecycler() {
        adapter = new CategoryAdapter(Util.getCategory(), this);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recycler.setHasFixedSize(true);
        recycler.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position, View view) {
        adapter.setSelectedCategory(position);
    }

    private void setUI() {
        if (task != null) {
            isNewTask = false;
            title.setText(task.getTitle());
            desc.setText(task.getDesc());
            date.setText(task.getDate());
            String[] timeArr = task.getTime().split(":");
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArr[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(timeArr[1]));

            time.setText(Util.getTimeFormated(calendar, this));
            adapter.setSelectedCategory(task.getCategory());
        } else {
            isNewTask = true;
            date.setText(getString(R.string.today));
            time.setText(Util.getTimeFormated(calendar, this));
        }
        updateTextViewReminder();
    }

    @Override
    protected void onStart() {
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePicker();
            }
        });
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add_task) {
            if (!validateEditText((TextInputLayout) findViewById(R.id.textInput_title)) |
                    !validateEditText((TextInputLayout) findViewById(R.id.textInput_desc))) {
                return true;
            }

            if (isNewTask)
                addNewTask();
            else
                updateTask();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewTask() {
        task = new Task(title.getText().toString(),
                desc.getText().toString(),
                DateFormat.getDateInstance(Util.DATE_FORMAT).format(calendar.getTime()), //change Date format to Default Format
                Util.getTime(Util.TIME_24_FORMAT, calendar), //change Time format to Default Format
                adapter.getSelectedCategory());
        presenter.addTask(task);
    }

    private void updateTask() {
        task.setTitle(title.getText().toString());
        task.setDesc(desc.getText().toString());
        task.setDate(date.getText().toString());
        task.setTime(Util.getTime(Util.TIME_24_FORMAT, calendar));
        task.setCategory(adapter.getSelectedCategory());
        presenter.updateTask(task);
    }

    @Override
    public void setAlarm() {
        Log.d(TAG, "setAlarm: " + task.getId());
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(Keys.TASK_ID, task.getId());
        intent.putExtra(Keys.TASK_TITLE, task.getTitle());
        intent.putExtra(Keys.TASK_TIME, calendar.getTimeInMillis());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, task.getId(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (calendar.after(Calendar.getInstance())) {
            Log.d(TAG, "setAlarm: in alaaaarrrrrmmmmm");
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()/*-30*60000*/, pendingIntent);
        }
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onAddSuccess(int taskId) {
        task.setId(taskId);
        setAlarm();
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
        setUI();
    }

    private boolean validateEditText(TextInputLayout textInputLayout) {
        EditText editText = textInputLayout.getEditText();
        if (editText != null && editText.getText().toString().isEmpty()) {
            textInputLayout.setError(editText.getHint() + "can't be empty");
            return false;
        }
        return true;
    }

    //region DatePickerRegion
    private void openDatePicker() {
        DialogFragment datePicker = new DatePickerFragment(calendar);
        datePicker.show(getSupportFragmentManager(), "date picker");
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        calendar.set(Calendar.YEAR, i);
        calendar.set(Calendar.MONTH, i1);
        calendar.set(Calendar.DAY_OF_MONTH, i2);
        String date = DateFormat.getDateInstance(Util.DATE_FORMAT).format(calendar.getTime());
        this.date.setText(date);
        updateTextViewReminder();
    }
    //endregion

    //region TimePickerRegion
    private void openTimePicker() {
        DialogFragment timePicker = new TimePickerFragment(calendar);
        timePicker.show(getSupportFragmentManager(), "time picker");
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        calendar.set(Calendar.HOUR_OF_DAY, i);
        calendar.set(Calendar.MINUTE, i1);
        calendar.set(Calendar.SECOND, 0);
        String time = Util.getTimeFormated(calendar, this);
        this.time.setText(time);
        updateTextViewReminder();
    }

    //endregion

    private void updateTextViewReminder() {
        long t = calendar.getTimeInMillis();
//        calendar.setTimeInMillis(t - 30*60000); // time - 30 min
        String reminderStr = "Reminder is set For " + DateFormat.getDateInstance(Util.DATE_FORMAT).format(calendar.getTime()) + ", " + time.getText();
//        calendar.setTimeInMillis(t);
        reminder.setText(reminderStr);
    }
}