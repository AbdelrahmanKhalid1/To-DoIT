package com.example.todoit.main;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.todoit.notification.receiver.AlarmReceiver;
import com.example.todoit.util.DatePickerFragment;
import com.example.todoit.add.AddTaskActivity;
import com.example.todoit.R;
import com.example.todoit.data.TodoItDatabase;
import com.example.todoit.util.Keys;
import com.example.todoit.util.OnRecyclerItemClickListener;
import com.example.todoit.util.Util;
import com.example.todoit.data.entity.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements EnterNameDialog.EnterNameDialogListener,
        OnRecyclerItemClickListener.OnItemClickListener, OnRecyclerItemClickListener.OnItemLongClickListener, TaskPresenter.TaskView, DatePickerDialog.OnDateSetListener {
    private static final String TAG = "MainActivity";
    private TextView mUsername, mDate;
    private RecyclerView mRecycler;
    private TaskAdapter mAdapter;
    private TaskPresenter mPresenter;
    private Calendar mCalendar;
    private ActionMode mActionMode;
    private List<Task> tasks;
    private Set<Integer> selectedTasks;
    public static final int REQUEST_CODE_ADD_TASK = 2;
    public static final int REQUEST_CODE_UPDATE_TASK = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = findViewById(R.id.textView_username);
        mDate = findViewById(R.id.textView_date);
        mDate.setText(DateFormat.getDateInstance(DateFormat.DEFAULT).format(Calendar.getInstance().getTime()));
        mRecycler = findViewById(R.id.recycler_todo);
        mCalendar = Calendar.getInstance();
        selectedTasks = new HashSet<>();

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        TodoItDatabase db = TodoItDatabase.getInstance(getApplication());
        mPresenter = new TaskPresenter(this, db.taskDao());
        setTextUsername();
        buildRecycler();
    }

    private void setTextUsername(){
        SharedPreferences settings = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        String username = settings.getString("username", "");
        if(username == null || username.isEmpty()){
            EnterNameDialog dialog = new EnterNameDialog();
            dialog.show(getSupportFragmentManager(), "enter name dialog");
        }
    }

    @Override
    public void onEnterUserName(String username) {
        mUsername.setText(username);
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE).edit();
        editor.putString("username", username).apply();
    }

    private void buildRecycler() {
        mAdapter = new TaskAdapter(this, this);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setHasFixedSize(true);
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        mPresenter.getTasksFromDatabae(mDate.getText().toString());
        TextView textViewWelcome = findViewById(R.id.textView_welcome);
        textViewWelcome.setText(Util.getWelcomeMessage());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, AddTaskActivity.class), REQUEST_CODE_ADD_TASK);
            }
        });

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_date) {
            openDatePicker();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //region showTasks
    @Override
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        mAdapter.setTasks(tasks);
    }

    @Override
    public void showMessage(String message) {
        Log.d(TAG, "showMessage: " + message);
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void successfullDeleted(List<Task> tasks) {
        this.tasks = tasks;
        mAdapter.setTasks(tasks);
        selectedTasks.clear();
    }
    //endregion

    @Override
    public void onItemClick(int position, View view) {
        if (mActionMode != null) {
            view.setSelected(!view.isSelected());
            if (!selectedTasks.add(position))
                selectedTasks.remove(position);
            return;
        }
        Intent intent = new Intent(this, AddTaskActivity.class);
        intent.putExtra(Keys.ADD_TASK, tasks.get(position));
        startActivityForResult(intent, REQUEST_CODE_UPDATE_TASK);
    }

    @Override
    public void onItemLongClick(int position, View view) {
        if (mActionMode == null) {
            mActionMode = startActionMode(actionModeCallback);
        }
        if (!selectedTasks.add(position))
            selectedTasks.remove(position);
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            selectedTasks.clear();
            actionMode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            actionMode.setTitle("Choose Option");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_done_all:
                    markAllDone();
                    actionMode.finish();
                    return true;
                case R.id.action_delete:
                    mPresenter.deleteTasks(selectedTasks, tasks);
                    actionMode.finish();
                    return true;
                default:
                    mAdapter.setTasks(tasks);
                    selectedTasks.clear();
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            //TODO search for a better way
            buildRecycler();
            mAdapter.setTasks(tasks);
            mActionMode = null;
        }
    };

    private void markAllDone() {
        for (int x : selectedTasks) {
            Task task = tasks.get(x);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, task.getId(),
                    new Intent(this, AlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
        selectedTasks.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mPresenter.getTasksFromDatabae(mDate.getText().toString());
            switch (requestCode) {
                case REQUEST_CODE_ADD_TASK:
                    Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                    return;
                case REQUEST_CODE_UPDATE_TASK:
                    Toast.makeText(this, "Updated Succesfully", Toast.LENGTH_SHORT).show();
                    return;
                default:
            }
        }
    }

    //region Date Setter
    private void openDatePicker() {
        DialogFragment datePicker = new DatePickerFragment(mCalendar);
        datePicker.show(getSupportFragmentManager(), "date picker");
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        mCalendar.set(Calendar.YEAR, i);
        mCalendar.set(Calendar.MONTH, i1);
        mCalendar.set(Calendar.DAY_OF_MONTH, i2);
        String date = DateFormat.getDateInstance(Util.DATE_FORMAT).format(mCalendar.getTime());
        mDate.setText(date);
        mPresenter.getTasksFromDatabae(date);
    }
    //endregion
}