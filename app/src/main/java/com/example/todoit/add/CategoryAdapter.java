package com.example.todoit.add;

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
import com.example.todoit.util.OnRecyclerItemClickListener;
import com.example.todoit.util.Util;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryView> {

    private Category[] categories;
    private OnRecyclerItemClickListener.OnItemClickListener listener;
    private int selectedCategory = Util.DEFUALT_CATEOGRY;

    public CategoryAdapter(Category[] categories, OnRecyclerItemClickListener.OnItemClickListener listener){
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category, parent, false);
        return new CategoryView(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryView holder, int position) {
        Category category = categories[position];
        Context context = holder.itemView.getContext();

        holder.categoryName.setText(category.getTitleStr());
        if(position == 0)
            holder.categoryIcon.setBackgroundColor(category.getBgColor());
        else {
            holder.categoryIcon.setBackgroundColor(context.getResources().getColor(category.getBgColor()));
            holder.categoryIcon.setImageDrawable(context.getResources().getDrawable(category.getIcon(), null));
        }

        if(selectedCategory == position)
            holder.itemView.setSelected(true);
        else
            holder.itemView.setSelected(false);
    }

    @Override
    public int getItemCount() { return categories.length; }

    public int getSelectedCategory() { return selectedCategory; }

    public void setSelectedCategory(int selectedCategory){
        this.selectedCategory = selectedCategory;
        notifyDataSetChanged();
    }

    static class CategoryView extends RecyclerView.ViewHolder{
        private TextView categoryName;
        private ImageView categoryIcon;
        public CategoryView(@NonNull View itemView,
                            final OnRecyclerItemClickListener.OnItemClickListener listener) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.category_icon);
            categoryName = itemView.findViewById(R.id.category_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                        listener.onItemClick(getAdapterPosition(), view);
                }
            });
        }
    }

}
