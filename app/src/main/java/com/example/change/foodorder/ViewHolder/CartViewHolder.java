package com.example.change.foodorder.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Interface.ItemClickListener;
import com.example.change.foodorder.R;


public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener {

    public RelativeLayout backgroundView;
    public LinearLayout foregroundView;

    public TextView text_cart_item_name, text_cart_item_price;
    public ElegantNumberButton cart_item_count;
    public ImageView imgFoodCart;

    private ItemClickListener itemClickListener;

    public CartViewHolder(View itemView) {
        super(itemView);

        text_cart_item_name = itemView.findViewById(R.id.text_cart_item_name);
        text_cart_item_price = itemView.findViewById(R.id.text_cart_item_price);
        cart_item_count = itemView.findViewById(R.id.numberButtonCart);
        imgFoodCart = itemView.findViewById(R.id.foodImageCart);
        backgroundView = itemView.findViewById(R.id.deleteBackground);
        foregroundView = itemView.findViewById(R.id.linearLayoutCart);

        itemView.setOnCreateContextMenuListener(this);
    }

    public void setText_cart_item_name(TextView text_cart_item_name) {
        this.text_cart_item_name = text_cart_item_name;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select an Action");
        menu.add(0,0,getAdapterPosition(), Common.DELETE);


    }
}