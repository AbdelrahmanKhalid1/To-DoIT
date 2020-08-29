package com.example.todoit.util;

import android.view.View;

public interface OnRecyclerItemClickListener {

    interface OnItemClickListener{
        void onItemClick(int position, View view);
    }

    interface OnItemLongClickListener{
        void onItemLongClick(int position, View view);
    }

}
