package com.example.change.foodorder.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.change.foodorder.Interface.ItemClickListener;
import com.example.change.foodorder.R;

public class FavoritesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView foodName,foodPrice;
    public ImageView foodImage,foodAddToCart;
    public RelativeLayout backgroundViewFav;
    public LinearLayout foregroundViewFav;

    private ItemClickListener itemClickListener;

    public FavoritesViewHolder(View itemView) {
        super(itemView);
        foodName = itemView.findViewById(R.id.food_name_fav);
        foodImage = itemView.findViewById(R.id.food_image_fav);
        foodAddToCart = itemView.findViewById(R.id.addToCartFav);
        foodPrice = itemView.findViewById(R.id.food_pr_fav);
        backgroundViewFav = itemView.findViewById(R.id.deleteBackgroundFav);
        foregroundViewFav = itemView.findViewById(R.id.foregroundFav);

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
