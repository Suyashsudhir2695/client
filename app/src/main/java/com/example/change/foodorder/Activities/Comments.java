package com.example.change.foodorder.Activities;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.change.foodorder.Model.Rating;
import com.example.change.foodorder.R;
import com.example.change.foodorder.ViewHolder.CommentsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Comments extends AppCompatActivity {
    RecyclerView recycle_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference ratingTable;
    FirebaseRecyclerAdapter<Rating,CommentsViewHolder> adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String foodId = "";
    ActionBar actionBar;
    TextView txtNoComments;
    ImageView imgNoComments;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.logo);
        actionBar.setTitle("Comments");

        //Views
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutComment);
        recycle_menu = findViewById(R.id.recyclerComments);
        recycle_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycle_menu.setLayoutManager(layoutManager);
        txtNoComments = findViewById(R.id.txtNoComments);
        imgNoComments = findViewById(R.id.imgNoComments);

        //Firebase
        database = FirebaseDatabase.getInstance();
        ratingTable = database.getReference("Ratings");
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);







        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                ratingTable.orderByChild("foodId").equalTo(getIntent().getStringExtra("FoodId"))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    txtNoComments.setVisibility(View.GONE);
                                    imgNoComments.setVisibility(View.GONE);
                                    if (getIntent() !=null)
                                        foodId = getIntent().getStringExtra("FoodId");


                                    Query query = ratingTable.orderByChild("foodId").equalTo(foodId);
                                    FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                                            .setQuery(query,Rating.class).build();

                                    adapter = new FirebaseRecyclerAdapter<Rating, CommentsViewHolder>(options) {
                                        @Override
                                        protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull Rating model) {
                                            holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                                            holder.txtComment.setText(model.getComment());
                                            holder.txtUserPhoneComment.setText(model.getUserName());


                                        }

                                        @NonNull
                                        @Override
                                        public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                                            View view = inflater.inflate(R.layout.show_food_comment_layout,parent,false);
                                            return new CommentsViewHolder(view);
                                        }
                                    };
                                    adapter.startListening();
                                    recycle_menu.setAdapter(adapter);
                                    mSwipeRefreshLayout.setRefreshing(false);





                                }
                                else {
                                    txtNoComments.setVisibility(View.VISIBLE);
                                    imgNoComments.setVisibility(View.VISIBLE);


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });






            }
        });
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
               // mSwipeRefreshLayout.setRefreshing(true);

                ratingTable.orderByChild("foodId").equalTo(getIntent().getStringExtra("FoodId"))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    txtNoComments.setVisibility(View.GONE);
                                    imgNoComments.setVisibility(View.GONE);
                                    if (getIntent() !=null)
                                        foodId = getIntent().getStringExtra("FoodId");


                                    Query query = ratingTable.orderByChild("foodId").equalTo(foodId);
                                    FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                                            .setQuery(query,Rating.class).build();

                                    adapter = new FirebaseRecyclerAdapter<Rating, CommentsViewHolder>(options) {
                                        @Override
                                        protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull Rating model) {
                                            holder.ratingBar.setRating(Float.parseFloat(model.getRateValue()));
                                            holder.txtComment.setText(model.getComment());
                                            holder.txtUserPhoneComment.setText(model.getUserName());


                                        }

                                        @NonNull
                                        @Override
                                        public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                                            View view = inflater.inflate(R.layout.show_food_comment_layout,parent,false);
                                            return new CommentsViewHolder(view);
                                        }
                                    };
                                    adapter.startListening();
                                    recycle_menu.setAdapter(adapter);
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }
                                else {
                                    txtNoComments.setVisibility(View.VISIBLE);
                                    imgNoComments.setVisibility(View.VISIBLE);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



            }
        });

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
