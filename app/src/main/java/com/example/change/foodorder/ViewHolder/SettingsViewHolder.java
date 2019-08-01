package com.example.change.foodorder.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.example.change.foodorder.Interface.ItemClickListener;
import com.example.change.foodorder.R;

public class SettingsViewHolder extends RecyclerView.ViewHolder  {

    public TextView textNameSettings,textPasswordSettings,textPhoneSettings,textHomeSettings,textTwoStepSettings;
    public  Switch  switchTwoStep;
    private ItemClickListener itemClickListener;
    public SettingsViewHolder(@NonNull View itemView) {
        super(itemView);
        textNameSettings = itemView.findViewById(R.id.textNameSettings);
        textPasswordSettings = itemView.findViewById(R.id.textPasswordSettings);
        textPhoneSettings = itemView.findViewById(R.id.textPhoneSettings);
        textHomeSettings = itemView.findViewById(R.id.textHomeSettings);
        textTwoStepSettings = itemView.findViewById(R.id.textTwoStepSettings);
        switchTwoStep = itemView.findViewById(R.id.switchTwoStep);





    }


}
