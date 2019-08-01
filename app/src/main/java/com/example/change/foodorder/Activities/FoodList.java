package com.example.change.foodorder.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.change.foodorder.Common.Common;

import com.example.change.foodorder.Databases.Database;
import com.example.change.foodorder.Interface.ItemClickListener;
import com.example.change.foodorder.Model.Favorites;
import com.example.change.foodorder.Model.Food;
import com.example.change.foodorder.Model.Order;
import com.example.change.foodorder.Model.Rating;
import com.example.change.foodorder.R;
import com.example.change.foodorder.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;
import java.util.List;

import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_SCALE;

public class FoodList extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList,ratingTable;
    String categoryId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    //Search

    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestions = new ArrayList<>();
    MaterialSearchBar searchBar;
    Database localDB;

    SwipeRefreshLayout swipeRefreshLayout;
    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.logo);
        actionBar.setTitle("Items");
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutFoodList);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getIntent() != null)
                    categoryId = getIntent().getStringExtra("CategoryId");

                if (!categoryId.isEmpty() && categoryId != null) {
                    if (Common.isConnected(getBaseContext()))
                        loadFoodList(categoryId);
                    else {
                        Toast.makeText(getBaseContext(), "Couldn't Connect to Internet! " +
                                "Make Sure you have an active internet Connection", Toast.LENGTH_SHORT).show();
                        return;

                    }
                }

            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (getIntent() != null)
                    categoryId = getIntent().getStringExtra("CategoryId");

                if (!categoryId.isEmpty() && categoryId != null) {
                    if (Common.isConnected(getBaseContext()))
                        loadFoodList(categoryId);
                    else {
                        Toast.makeText(FoodList.this, "Couldn't Connect to Internet! " +
                                "Make Sure you have an active internet Connection", Toast.LENGTH_SHORT).show();
                        return;

                    }
                }
                searchBar = findViewById(R.id.searchBar);
                searchBar.setHint("Search Simply Food");
                //searchBar.setSpeechMode(false);
                loadSuggest();

                searchBar.setLastSuggestions(suggestions);
                searchBar.setCardViewElevation(10);
                searchBar.addTextChangeListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        List<String> suggest = new ArrayList<>();
                        for (String search : suggestions) {
                            if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                                suggest.add(search);


                        }
                        searchBar.setLastSuggestions(suggest);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                    @Override
                    public void onSearchStateChanged(boolean enabled) {
                        if (!enabled)
                            recyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onSearchConfirmed(CharSequence text) {
                        searchStart(text);
                    }

                    @Override
                    public void onButtonClicked(int buttonCode) {

                    }
                });

            }
        });


        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");
        ratingTable = database.getReference("Ratings");

        recyclerView = findViewById(R.id.recyclerFood);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        localDB = new Database(this);


    }

    private void searchStart(CharSequence text) {
        FirebaseRecyclerOptions<Food> sOptions = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(foodList.orderByChild("name").equalTo(text.toString()), Food.class).build();
        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(sOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder viewHolder, int position, @NonNull Food model) {
                PushDownAnim.setPushDownAnimTo(viewHolder.foodAddToCart,viewHolder.foodFav)
                        . setScale( MODE_SCALE ,
                                PushDownAnim.DEFAULT_PUSH_SCALE)
                        .setDurationPush( PushDownAnim.DEFAULT_PUSH_DURATION )
                        .setDurationRelease( PushDownAnim.DEFAULT_RELEASE_DURATION )
                        .setInterpolatorPush( PushDownAnim.DEFAULT_INTERPOLATOR )
                        .setInterpolatorRelease( PushDownAnim.DEFAULT_INTERPOLATOR );
                viewHolder.foodPrice.setText(model.getPrice());
                viewHolder.foodName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.foodImage);


                Query rating = ratingTable.orderByChild("foodId").equalTo(adapter.getRef(position).getKey());

                rating.addValueEventListener(new ValueEventListener() {
                    int count = 0, sum = 0;

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShop : dataSnapshot.getChildren()) {
                            Rating item = postSnapShop.getValue(Rating.class);
                            sum += Integer.parseInt(item.getRateValue());
                            count++;

                        }
                        if (count != 0) {
                            float avg = (float) (sum / count);
                            viewHolder.textRating.setText(String.valueOf(avg));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetails = new Intent(FoodList.this, FoodDetails.class);
                        foodDetails.putExtra("FoodId", searchAdapter.getRef(position).getKey());
                        startActivity(foodDetails);



                    }
                });



            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(itemView);
            }
        };
        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        foodList.orderByChild("menuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Food item = postSnapshot.getValue(Food.class);
                    suggestions.add(item.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadFoodList(String categoryId) {
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(foodList.orderByChild("menuId").equalTo(categoryId), Food.class).build();
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder viewHolder, final int position, @NonNull final Food model) {
                viewHolder.foodName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.foodImage);
                viewHolder.foodPrice.setText(model.getPrice());

                Query rating = ratingTable.orderByChild("foodId").equalTo(adapter.getRef(position).getKey());

                rating.addValueEventListener(new ValueEventListener() {
                    int count = 0, sum = 0;

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShop : dataSnapshot.getChildren()) {
                            Rating item = postSnapShop.getValue(Rating.class);
                            sum += Integer.parseInt(item.getRateValue());
                            count++;

                        }
                        if (count != 0) {
                            float avg = (float) (sum / count);
                            viewHolder.textRating.setText(String.valueOf(avg));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                    viewHolder.foodAddToCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean existingFoodYes = new Database(getBaseContext()).ifFoodExists(adapter.getRef(position).getKey(), Common.currentUser.getEmail());

                            if (!existingFoodYes){


                            new Database(getBaseContext()).addToCart(new Order(
                                            Common.currentUser.getEmail(),
                                            adapter.getRef(position).getKey(),
                                            model.getName(),
                                            "1",
                                            model.getPrice(),
                                            model.getDiscount(),
                                            model.getImage()

                                    )


                            );

                        }
                            else {
                                new Database(getBaseContext()).incCart(Common.currentUser.getEmail(),adapter.getRef(position).getKey());
                            }
                            Toast.makeText(FoodList.this, model.getName() + " has been added", Toast.LENGTH_SHORT).show();
                        }


                    });





                if (localDB.isFavorites(adapter.getRef(position).getKey(),Common.currentUser.getEmail()))
                    viewHolder.foodFav.setImageResource(R.drawable.favorites);

                viewHolder.foodFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Favorites favorites = new Favorites();
                        favorites.setFoodId(adapter.getRef(position).getKey());
                        favorites.setFoodName(model.getName());
                        favorites.setFoodPrice(model.getPrice());
                        favorites.setFoodDesc(model.getDescription());
                        favorites.setFoodDiscount(model.getDiscount());
                        favorites.setFoodImage(model.getImage());
                        favorites.setFoodMenuId(model.getMenuId());
                        favorites.setUserEmail(Common.currentUser.getEmail());

                        Log.i("Position Inside OnClick", adapter.getRef(position).getKey());
                        if (!localDB.isFavorites(adapter.getRef(position).getKey(),Common.currentUser.getEmail())) {
                            localDB.addFavorites(favorites);
                            viewHolder.foodFav.setImageResource(R.drawable.favorites);
                            Toast.makeText(FoodList.this, model.getName() + " was added to favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            localDB.removeFavorites(adapter.getRef(position).getKey(),Common.currentUser.getEmail());
                            viewHolder.foodFav.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                            Toast.makeText(FoodList.this, model.getName() + " was removed from favorites", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
//


                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetails = new Intent(FoodList.this, FoodDetails.class);
                        foodDetails.putExtra("FoodId", adapter.getRef(position).getKey());
                        startActivity(foodDetails);
                    }
                });


            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

    }

   /* @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_food_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refreshFoodList) {
            loadFoodList(categoryId);
        }
        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
