package com.example.change.foodorder.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.change.foodorder.Interface.ItemClickListener;
import com.example.change.foodorder.R;

public class DashboardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ItemClickListener itemClickListener;
    public TextView textDashNav;
    public ImageView imgDashNav;
    public DashboardViewHolder(@NonNull View itemView) {
        super(itemView);
        textDashNav = itemView.findViewById(R.id.textDashNav);
        imgDashNav = itemView.findViewById(R.id.imgDashNav);
        itemView.setOnClickListener(this);
    }
    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);



    }
}
