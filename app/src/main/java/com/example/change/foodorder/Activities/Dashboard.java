package com.example.change.foodorder.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.change.foodorder.R;
import com.example.change.foodorder.ViewHolder.DashboardAdapter;

public class Dashboard extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    DashboardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        recyclerView = findViewById(R.id.listNav);
        linearLayoutManager = new LinearLayoutManager(this);
        adapter = new DashboardAdapter(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }
}
