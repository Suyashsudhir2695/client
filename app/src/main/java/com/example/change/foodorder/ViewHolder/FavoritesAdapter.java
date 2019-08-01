package com.example.change.foodorder.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Databases.Database;
import com.example.change.foodorder.Activities.FoodDetails;
import com.example.change.foodorder.Interface.ItemClickListener;
import com.example.change.foodorder.Model.Favorites;
import com.example.change.foodorder.Model.Order;
import com.example.change.foodorder.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesViewHolder> {

    private Context context;
    private List<Favorites> favoritesList;

    public FavoritesAdapter(Context context, List<Favorites> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
    }

    @Override
    public FavoritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.favorites_item_layout, parent, false);

        return new FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FavoritesViewHolder viewHolder, final int position) {


        viewHolder.foodName.setText(favoritesList.get(position).getFoodName());
        Picasso.with(context).load(favoritesList.get(position).getFoodImage())
                .into(viewHolder.foodImage);
        viewHolder.foodPrice.setText(favoritesList.get(position).getFoodPrice());



        viewHolder.foodAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean existingFoodYes = new Database(context).ifFoodExists(favoritesList.get(position).getFoodId(), Common.currentUser.getEmail());

                if (!existingFoodYes){


                    new Database(context).addToCart(new Order(
                                    Common.currentUser.getEmail(),
                                   favoritesList.get(position).getFoodId(),
                            favoritesList.get(position).getFoodName(),
                                    "1",
                            favoritesList.get(position).getFoodPrice(),
                            favoritesList.get(position).getFoodDiscount(),
                            favoritesList.get(position).getFoodImage()

                            )


                    );

                }
                else {
                    new Database(context).incCart(Common.currentUser.getEmail(),favoritesList.get(position).getFoodId());
                }
                Toast.makeText(context, favoritesList.get(position).getFoodName() + " has been added", Toast.LENGTH_SHORT).show();
            }


        });







//


        final Favorites local = favoritesList.get(position);
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent foodDetails = new Intent(context, FoodDetails.class);
                foodDetails.putExtra("FoodId",favoritesList.get(position).getFoodId());
                context.startActivity(foodDetails);
            }
        });


    }


    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public void removeItem(int position) {
        favoritesList.remove(position);
        notifyItemRemoved(position);
    }


    public void restoreItem(Favorites item, int position) {
        favoritesList.add(position, item);
        notifyItemInserted(position);
    }

    public Favorites getFav(int pos) {
        return favoritesList.get(pos);
    }
}
