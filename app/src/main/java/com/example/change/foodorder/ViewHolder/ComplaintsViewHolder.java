package com.example.change.foodorder.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.change.foodorder.Interface.ItemClickListener;
import com.example.change.foodorder.R;

public class ComplaintsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

public TextView txtComId,txtCompAbout,txtCompTitle,txtCompBody;
private ItemClickListener itemClickListener;
    public ComplaintsViewHolder(@NonNull View itemView) {
        super(itemView);
        txtComId = itemView.findViewById(R.id.txtCompId);
        txtCompAbout = itemView.findViewById(R.id.txtCompAbout);
        txtCompBody = itemView.findViewById(R.id.txtCompBody);
        txtCompTitle = itemView.findViewById(R.id.txtCompTitle);

        itemView.setOnClickListener(this);

    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
