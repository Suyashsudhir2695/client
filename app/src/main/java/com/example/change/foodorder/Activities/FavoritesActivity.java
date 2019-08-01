package com.example.change.foodorder.Activities;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Databases.Database;
import com.example.change.foodorder.Helpers.RecyclerSwipeToDeleteHelper;
import com.example.change.foodorder.Interface.RecyclerSwipeToDeleteListener;
import com.example.change.foodorder.Model.Favorites;
import com.example.change.foodorder.R;
import com.example.change.foodorder.ViewHolder.FavoritesAdapter;
import com.example.change.foodorder.ViewHolder.FavoritesViewHolder;

public class FavoritesActivity extends AppCompatActivity implements RecyclerSwipeToDeleteListener {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RelativeLayout relativeLayout;
    FavoritesAdapter adapter;
    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.logo);


        actionBar.setTitle("Favorites");

        recyclerView = findViewById(R.id.recyclerFav);

        relativeLayout = findViewById(R.id.rootLayoutFav);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper.SimpleCallback callback = new RecyclerSwipeToDeleteHelper(0,ItemTouchHelper.LEFT,FavoritesActivity.this);

        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
        adapter = new FavoritesAdapter(FavoritesActivity.this,new Database(this).getFavorites(Common.currentUser.getEmail()));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof FavoritesViewHolder)
        {

            String name = ((FavoritesAdapter)recyclerView.getAdapter()).getFav(position).getFoodName();

            final Favorites deleteItem = ((FavoritesAdapter)recyclerView.getAdapter()).getFav(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();
            adapter.removeItem(deleteIndex);

            new Database(getBaseContext()).removeFavorites(deleteItem.getFoodId(), Common.currentUser.getEmail());

            Snackbar snackbar = Snackbar.make(relativeLayout,name + " Has Been Removed From Favorites",Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.WHITE);
            snackbar.setAction("Add Again", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.restoreItem(deleteItem,deleteIndex);
                    new Database(getBaseContext()).addFavorites(deleteItem);

                }
            });
            snackbar.show();


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
