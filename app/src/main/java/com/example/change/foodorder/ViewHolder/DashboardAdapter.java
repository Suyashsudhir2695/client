package com.example.change.foodorder.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.change.foodorder.Activities.Home;
import com.example.change.foodorder.Interface.ItemClickListener;
import com.example.change.foodorder.R;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardViewHolder> {
    Context context;
    String[] items = {"Order Food","Your Favorites","Cart","Wallet","Your Complaints",
    "Your Orders","Settings","Sign Out"
    };
    int[] images = {
      R.drawable.restaurants_menu,
      R.drawable.favorites,
      R.drawable.cart,
      R.drawable.wallet,
      R.drawable.feedback,
      R.drawable.purchase_history,
      R.drawable.settings,
      R.drawable.sign_out
    };

    public DashboardAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public DashboardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layoutdash,viewGroup,false);

        return new DashboardViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull DashboardViewHolder holder, int i) {
        holder.textDashNav.setText(items[i]);
        holder.imgDashNav.setImageResource(images[i]);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Toast.makeText(context, "" + items[position], Toast.LENGTH_SHORT).show();
                switch (position){
                    case 0:
                        context.startActivity(new Intent(context, Home.class));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.length;
    }
}
