package com.example.change.foodorder.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Interface.ItemClickListener;
import com.example.change.foodorder.Model.Request;
import com.example.change.foodorder.R;
import com.example.change.foodorder.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class OrderStatus extends AppCompatActivity {
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> qadapter;
    ImageView imageOrderEmpty;
    TextView textOrderEmpty;

    FirebaseDatabase database;
    DatabaseReference requests;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.logo);
        actionBar.setTitle("Orders");
        imageOrderEmpty = findViewById(R.id.imageOrderEmpty);
        textOrderEmpty = findViewById(R.id.textOrderEmpty);

        //FireBase Init

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");
        // loadOrders(Common.currentUser.getEmail());

        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);



        requests.orderByChild("email").equalTo(Common.currentUser.getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            loadOrders(Common.currentUser.getEmail());
                        }
                        else {
                            imageOrderEmpty.setImageResource(R.drawable.purchase_history);
                            textOrderEmpty.setText("Make a new delicious order and come back!");

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });








    }

    private void loadOrders( String email) {





            final Query query = requests.orderByChild("email").equalTo(email);

            FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                    .setQuery(requests.orderByChild("email").equalTo(email), Request.class).build();
            qadapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, final int position, @NonNull Request model) {
                    viewHolder.txtOrderId.setText(qadapter.getRef(position).getKey());
                    viewHolder.txtOrderStatus.setText(Common.codeToStatus(model.getStatus()));
                    viewHolder.txtOrderPhone.setText(model.getEmail());
                    viewHolder.txtOrderAddress.setText(model.getAddress());
                    viewHolder.orderCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(OrderStatus.this);
                            builder.setTitle("Cancel Order");
                            builder.setMessage("Are You Sure You Want to Cancel This Order?");
                            builder.setIcon(R.drawable.ic_delete_white_24dp);
                            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (qadapter.getItem(position).getStatus().equals("0") || qadapter.getItem(position).getStatus().equals("1")) {
                                        cancelOrder(qadapter.getRef(position).getKey());
                                    } else {
                                        Toast.makeText(OrderStatus.this, "You Can\'t Cancel this Order Because It's Been Already " + Common.codeToStatus(qadapter.getItem(position).getStatus()), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            builder.setNegativeButton("Wait", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.show();
                        }
                    });
                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Common.currentKey = qadapter.getRef(position).getKey();
                            startActivity(new Intent(OrderStatus.this,TrackOrders.class));


                        }
                    });


                }


                @NonNull
                @Override
                public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.order_status_layout, parent, false);


                    return new OrderViewHolder(itemView);
                }
            };
            qadapter.startListening();
            recyclerView.setAdapter(qadapter);

    }

    private void cancelOrder(final String key) {
        requests.child(key)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(OrderStatus.this, new StringBuilder("Order ")
                        .append(key)
                        .append(" has been canceled!"),Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.getMessage();
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
