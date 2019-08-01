package com.example.change.foodorder.Interface;

import android.support.v7.widget.RecyclerView;

public interface RecyclerSwipeToDeleteListener {
    void onSwipe(RecyclerView.ViewHolder viewHolder,int direction,int position);
}
