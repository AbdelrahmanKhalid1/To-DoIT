package com.example.todoit.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoit.R;
import com.example.todoit.util.Category;
import com.example.todoit.data.entity.Task;
import com.example.todoit.util.OnRecyclerItemClickListener;
import com.example.todoit.util.Util;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ToDoView> {

    private List<Task> tasks = new ArrayList<>();
    private OnRecyclerItemClickListener.OnItemClickListener listener;
    private OnRecyclerItemClickListener.OnItemLongClickListener listenerLong;
    private Category[] categories;

    public TaskAdapter(OnRecyclerItemClickListener.OnItemClickListener listener, OnRecyclerItemClickListener.OnItemLongClickListener listenerLong){
        categories = Util.getCategory();
        this.listener = listener;
        this.listenerLong = listenerLong;
    }

    @NonNull
    @Override
    public ToDoView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task, parent, false);
        return new ToDoView(view, listener, listenerLong);
    }

    @Override
    public void onBindViewHolder(@NonNull final ToDoView holder, int position) {
        Task task = tasks.get(position);
        Context context = holder.itemView.getContext();
        Category category = categories[task.getCategory()];

        holder.title.setText(task.getTitle());
//        holder.time.setText(task.getTime());
        holder.time.setText(Util.getTimeFormated(task.getTime().split(":"), context));
        if(task.getCategory() == 0)
            holder.icon.setBackgroundColor(categories[task.getCategory()].getBgColor());
        else {
            holder.icon.setBackgroundColor(context.getResources().getColor(category.getBgColor()));
            holder.icon.setImageDrawable(context.getResources().getDrawable(category.getIcon(),null));
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    //Todo long click implementation
    static class ToDoView extends RecyclerView.ViewHolder {
        private TextView title, time;
        private ImageView icon;

        public ToDoView(@NonNull View itemView, final OnRecyclerItemClickListener.OnItemClickListener listener,
                        final OnRecyclerItemClickListener.OnItemLongClickListener listenerLong) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            time = itemView.findViewById(R.id.item_time);
            icon = itemView.findViewById(R.id.item_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                        listener.onItemClick(getAdapterPosition(), view);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                        view.setSelected(true);
                        listenerLong.onItemLongClick(getAdapterPosition(), view);
                    }
                    return true;
                }
            });
        }
    }

}
