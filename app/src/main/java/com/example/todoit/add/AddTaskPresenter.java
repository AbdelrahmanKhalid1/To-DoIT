package com.example.todoit.add;

import android.util.Log;

import com.example.todoit.data.entity.Task;
import com.example.todoit.data.dao.TaskDao;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddTaskPresenter {
    private static final String TAG = "AddTaskPresenter";
    AddTaskView view;
    TaskDao taskDao;

    public AddTaskPresenter(AddTaskView view, TaskDao taskDao) {
        this.view = view;
        this.taskDao = taskDao;
    }

    public void updateTask(Task task) {
        taskDao.update(task)
                .subscribeOn(Schedulers.computation())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        view.setAlarm();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void addTask(final Task task) {
        taskDao.insert(task)
                .subscribeOn(Schedulers.computation())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        getMaxId(task);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError addTask: " + e.getMessage());
                    }
                });

    }

    private void getMaxId(Task task) {
        taskDao.getMaxId()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        view.onAddSuccess(integer);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError getMaxId: " + e.getMessage());
                    }
                });
    }

    public void getTaskById(int id){
        taskDao.getTask(id)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Task>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Task task) {
                        view.setTask(task);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    public interface AddTaskView {
        void setAlarm();
        void onAddSuccess(int taskId);
        void setTask(Task task);
    }
}
