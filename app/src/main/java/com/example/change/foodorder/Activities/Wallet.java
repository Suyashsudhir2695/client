package com.example.change.foodorder.Activities;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Databases.Database;
import com.example.change.foodorder.Model.Request;
import com.example.change.foodorder.Model.User;
import com.example.change.foodorder.Model.WalletModel;
import com.example.change.foodorder.R;
import com.example.change.foodorder.ViewHolder.WalletHistoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class Wallet extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference userRef,walletRef;
    PaytmPGService pgService;
    PaytmOrder order;
    TextView tvBalance;
    private static final String TAG = "WalletModel";
    FloatingActionButton fabAddBal;
    String orderNumber;
    ExtendedEditText editAddMoney;
    RelativeLayout layout;
    ProgressDialog pdialog;
    FirebaseRecyclerAdapter<WalletModel, WalletHistoryViewHolder> adapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Button btnAddBalLayout;


    //Paytm Credentials
    public static final String MID = "Studen67930786424661";
    public static final String INDUSTRY_TYPE_ID = "Retail";
    public static final String CHANNEL_ID = "WEB";
    public static final String WEBSITE = "APP_STAGING";
    public static final String CALLBACK_URL = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

    ActionBar actionBar;
    String id ;
    String date;
    TextView txtNoWalletHist;
    ImageView imgNoWalletHist;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setTitle("Wallet");
        pdialog = new ProgressDialog(Wallet.this);
        pdialog.setMessage("Processing Your Payment");

        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("user");
        walletRef = database.getReference("wallet");
        //walletRef.limitToLast(2);
        txtNoWalletHist = findViewById(R.id.txtNoWalletHist);
        imgNoWalletHist = findViewById(R.id.imgNoWalletHist);
        btnAddBalLayout = findViewById(R.id.btnAddBalLayout);

        pgService = PaytmPGService.getStagingService();
        tvBalance = findViewById(R.id.txtWalletBalance);
        recyclerView = findViewById(R.id.listWalletHistory);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        id = String.valueOf(System.currentTimeMillis());
        Log.i(TAG, "onCreate: The Identifier is " + id);

        tvBalance.setText(Common.currentUser.getBalance());
        float bal = Float.parseFloat(Common.currentUser.getBalance());
        float ab = 10000;
        float fa = bal + ab;
        date = DateFormat.getDateInstance().format(new Date());

        Log.i(TAG,  String.valueOf(fa));
        //fabAddBal = findViewById(R.id.fabAddWalletBal);

        layout = findViewById(R.id.layoutWallet);

        btnAddBalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddBalanceAlert();
            }
        });
        //if (walletRef.child(Common.currentUser.getEmail().replace(".","_")).child(id).ex)

        walletRef.orderByChild("email").equalTo(Common.currentUser.getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            txtNoWalletHist.setVisibility(View.GONE);
                            imgNoWalletHist.setVisibility(View.GONE);


                            loadWalletHistory(Common.currentUser.getEmail());
                    }

                    else {
                            txtNoWalletHist.setVisibility(View.VISIBLE);
                            imgNoWalletHist.setVisibility(View.VISIBLE);



                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void loadWalletHistory(String email) {
        FirebaseRecyclerOptions<WalletModel> options = new FirebaseRecyclerOptions.Builder<WalletModel>()
                .setQuery(walletRef.orderByChild("email").equalTo(email),WalletModel.class).build();
        Log.i(TAG, "loadWalletHistory: The Info is " +walletRef.child(email.replace(".","_")).getKey());

        adapter = new FirebaseRecyclerAdapter<WalletModel, WalletHistoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull WalletHistoryViewHolder holder, int position, @NonNull WalletModel model) {
                holder.tvWalletHistoryTime.setText(model.getTime());
                holder.tvWalletHistoryTrans.setText(model.getType());
                Log.i(TAG, "onBindViewHolder: The Time is " + model.getTime());
                Log.i(TAG, "onBindViewHolder: The Type is " + model.getType());
            }

            @NonNull
            @Override
            public WalletHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                Log.i(TAG, "onCreateViewHolder1: View Created");
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wallet_history_layout,viewGroup,false);
                Log.i(TAG, "onCreateViewHolder2: View Created");
                return new WalletHistoryViewHolder(view);

            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);




    }

    private void showAddBalanceAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(Wallet.this);
        alert.setIcon(R.drawable.ic_add_black_24dp);
        alert.setTitle("Add Money");
        alert.setIcon(R.drawable.add_money);

        View view = this.getLayoutInflater().inflate(R.layout.add_balance_layout,null);
          editAddMoney = view.findViewById(R.id.editWalletBal);
        Button btnHundred = view.findViewById(R.id.btnWalletHundred);
        Button btnFive = view.findViewById(R.id.btnWalletFive);
        Button btnThousand = view.findViewById(R.id.btnWalletThousand);
        //Button btnAddBalLayout = view.findViewById(R.id.btnAddBalLayout);
        alert.setView(view);


        btnFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAddMoney.setText("");
                editAddMoney.setText("500");
            }
        });
        btnHundred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAddMoney.setText("");
                editAddMoney.setText("100");
            }
        });
        btnThousand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAddMoney.setText("");
                editAddMoney.setText("1000");
            }
        });





        alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pdialog.show();
                generateChecksum();


            }
        });
        alert.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });





        alert.show();



    }

    private void generateChecksum() {



        orderNumber = String.valueOf(System.currentTimeMillis());



        //total = txtTotalPrice.getText().toString().replace("₹","");

        String url = "https://antinomical-attachm.000webhostapp.com/checksum.php";
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put( "MID" , MID);
// Key in your staging and production MID available in your dashboard
        paramMap.put( "ORDER_ID" , orderNumber);
        paramMap.put( "CUST_ID" , Common.currentUser.getEmail());
        //paramMap.put( "MOBILE_NO" , "7777777777");
        // paramMap.put( "EMAIL" , "username@emailprovider.com");
        paramMap.put( "CHANNEL_ID" , CHANNEL_ID);
        paramMap.put( "TXN_AMOUNT" , editAddMoney.getText().toString());
        paramMap.put( "WEBSITE" , WEBSITE);
// This is the staging value. Production value is available in your dashboard
        paramMap.put( "INDUSTRY_TYPE_ID" , INDUSTRY_TYPE_ID);
// This is the staging value. Production value is available in your dashboard
        paramMap.put( "CALLBACK_URL", CALLBACK_URL);


        JSONObject object = new JSONObject(paramMap);
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, object, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "onResponse: "+ response.optString("CHECKSUMHASH" ));
                startTrans(response.optString("CHECKSUMHASH"));



            }

        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: " + error.getMessage());

            }

        });

        Volley.newRequestQueue(this).add(request);




    }

    private void startTrans(String checksumhash) {
        //total = txtTotalPrice.getText().toString().replace("₹","");

        pgService = PaytmPGService.getStagingService();
        Map<String, String> paramMap = new HashMap<String,String>();
        paramMap.put( "MID" , MID);
// Key in your staging and production MID available in your dashboard
        paramMap.put( "ORDER_ID" , orderNumber);
        paramMap.put( "CUST_ID" , Common.currentUser.getEmail());
        //paramMap.put( "MOBILE_NO" , "7777777777");
        // paramMap.put( "EMAIL" , Common.currentUser.getEmail());
        paramMap.put( "CHANNEL_ID" , CHANNEL_ID);
        paramMap.put( "TXN_AMOUNT" , editAddMoney.getText().toString());
        paramMap.put( "WEBSITE" , WEBSITE);
// This is the staging value. Production value is available in your dashboard
        paramMap.put( "INDUSTRY_TYPE_ID" , INDUSTRY_TYPE_ID);
// This is the staging value. Production value is available in your dashboard
        paramMap.put( "CALLBACK_URL", CALLBACK_URL);
        paramMap.put( "CHECKSUMHASH" , checksumhash);
        order = new PaytmOrder((HashMap<String, String>) paramMap);
        pgService.initialize(order,null);
        pdialog.dismiss();
        pgService.startPaymentTransaction(Wallet.this, true, true, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(final Bundle inResponse) {
                float currentBal = Float.parseFloat(Common.currentUser.getBalance());
                float amt = Float.parseFloat(inResponse.getString("TXNAMOUNT"));
                float finalAmt = currentBal + amt;

                Log.i(TAG, "onTransactionResponse: " +inResponse.toString());

                if (inResponse.getString("STATUS").equals("TXN_SUCCESS")){

                    Map<String,Object> map = new HashMap<>();
                    map.put("balance", String.valueOf(finalAmt));
                    userRef.child(Common.currentUser.getEmail().replace(".","_")).updateChildren(map)
                            .addOnCompleteListener(Wallet.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(Wallet.this, "Balance Added", Toast.LENGTH_SHORT).show();


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Wallet.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                    updateWalletHistory(date,"Added ₹ " + amt);








                }
                else {
                    Toast.makeText(Wallet.this, "We're facing issues processing your payment", Toast.LENGTH_SHORT).show();

                }





            }

            @Override
            public void networkNotAvailable() {

            }

            @Override
            public void clientAuthenticationFailed(String inErrorMessage) {

            }

            @Override
            public void someUIErrorOccurred(String inErrorMessage) {

            }

            @Override
            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {

            }

            @Override
            public void onBackPressedCancelTransaction() {
                Snackbar.make(layout,"You Cancelled the transaction",Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE)
                        .setAction("Try Again", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                generateChecksum();
                            }
                        }).show();


            }

            @Override
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {

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
    private void updateWalletHistory(final String time, final String type){

        walletRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                WalletModel model = new WalletModel(Common.currentUser.getEmail(),time,type);
                walletRef.child(id).setValue(model);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
