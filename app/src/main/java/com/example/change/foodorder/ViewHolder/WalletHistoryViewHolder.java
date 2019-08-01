package com.example.change.foodorder.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.change.foodorder.R;

public class WalletHistoryViewHolder extends RecyclerView.ViewHolder {
    public TextView tvWalletHistoryTime,tvWalletHistoryTrans;
    public WalletHistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        tvWalletHistoryTime = itemView.findViewById(R.id.tvWalletHistoryTime);
        tvWalletHistoryTrans = itemView.findViewById(R.id.tvWalletHistoryTrans);


    }
}
