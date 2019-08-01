package com.example.change.foodorder.Activities;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Model.ComplaintsModel;
import com.example.change.foodorder.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class NewComplaint extends AppCompatActivity {
    ExtendedEditText editCompTitle, editCompAbout,editCompBody;
    Button btnSubmitComp;
    FirebaseDatabase database;
    DatabaseReference compRef;
    RelativeLayout compLayout;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_complaint);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setTitle("New Complaint");
        database = FirebaseDatabase.getInstance();
        compRef = database.getReference("complaints");
        editCompAbout = findViewById(R.id.editComAbout);
        editCompBody = findViewById(R.id.editComBody);
        editCompTitle = findViewById(R.id.editComTitle);
        btnSubmitComp = findViewById(R.id.btnSubmitComp);
        compLayout = findViewById(R.id.layoutComp);

        btnSubmitComp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = UUID.randomUUID().toString().replace("_","").trim().replace("-","").trim();
                final String title = editCompTitle.getText().toString();
                final String about = editCompAbout.getText().toString();
                final String body = editCompBody.getText().toString();

                compRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ComplaintsModel complaintsModel = new ComplaintsModel(id,Common.currentUser.getEmail(),title,about,body);
                        compRef.child(id).setValue(complaintsModel);

                        Snackbar.make(compLayout,"Your Complaint has been recorded",Snackbar.LENGTH_LONG);
                        finish();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });


    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
