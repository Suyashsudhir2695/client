package com.example.change.foodorder.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Model.ComplaintsModel;
import com.example.change.foodorder.R;
import com.example.change.foodorder.ViewHolder.ComplaintsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Complaints extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton fab;
    FirebaseDatabase database;
    DatabaseReference compRef;
    FirebaseRecyclerAdapter<ComplaintsModel,ComplaintsViewHolder> adapter;
    private static final String TAG = "Complaints";
    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setTitle("Complaints");
        recyclerView = findViewById(R.id.listComplaints);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        fab = findViewById(R.id.fabAddComp);
        //Firebase
        database = FirebaseDatabase.getInstance();
        compRef = database.getReference("complaints");


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Complaints.this,NewComplaint.class));
            }
        });

        loadComplaints(Common.currentUser.getEmail());
    }

    private void loadComplaints(String email) {
        FirebaseRecyclerOptions<ComplaintsModel> options = new FirebaseRecyclerOptions.Builder<ComplaintsModel>()
                .setQuery(compRef.orderByChild("email").equalTo(email),ComplaintsModel.class).build();
        Log.i(TAG, "loadComplaints: the info Is " + compRef.orderByChild("email").equalTo(email));

        adapter = new FirebaseRecyclerAdapter<ComplaintsModel, ComplaintsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ComplaintsViewHolder holder, int position, @NonNull ComplaintsModel model) {
                holder.txtComId.setText(model.getId());
                holder.txtCompAbout.setText(model.getAbout());
                holder.txtCompBody.setText(model.getBody());
                holder.txtCompTitle.setText(model.getTitle());
            }

            @NonNull
            @Override
            public ComplaintsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.complaints_layout,viewGroup,false);
                return new ComplaintsViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
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
