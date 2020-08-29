package com.example.todoit.main;

import com.example.todoit.data.entity.Task;
import com.example.todoit.data.dao.TaskDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TaskPresenter {
    private static final String TAG = "TaskPresenter";
    private final TaskView view;
    private TaskDao taskDao;

    public TaskPresenter(TaskView view, TaskDao taskDao) {
        this.view = view;
        this.taskDao = taskDao;
    }

    public void getTasksFromDatabae(String date) {
        taskDao.getAllTodayTasks(date)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Task>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Task> tasks) {
                        view.setTasks(tasks);
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.showMessage(e.getMessage());
                    }
                });
    }

    public void deleteTasks(Set<Integer> position, final List<Task> tasks){
        List<Task> deleteTasks = new ArrayList<>();
        for(int x : position){
            deleteTasks.add(tasks.get(x));
            tasks.remove(x);
        }
        taskDao.deleteList(deleteTasks)
                .subscribeOn(Schedulers.computation())
                .subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                view.successfullDeleted(tasks);
            }

            @Override
            public void onError(Throwable e) {
                view.showMessage(e.getMessage());
            }
        });
    }


    public interface TaskView {
        void setTasks(List<Task> tasks);
        void showMessage(String message);
        void successfullDeleted(List<Task> tasks);
    }
}
