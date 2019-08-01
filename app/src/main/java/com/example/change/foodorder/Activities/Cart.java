package com.example.change.foodorder.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Databases.Database;
import com.example.change.foodorder.Helpers.RecyclerSwipeToDeleteHelper;
import com.example.change.foodorder.Interface.RecyclerSwipeToDeleteListener;
import com.example.change.foodorder.Model.FBResponse;
import com.example.change.foodorder.Model.Notification;
import com.example.change.foodorder.Model.Order;
import com.example.change.foodorder.Model.Request;
import com.example.change.foodorder.Model.Sender;
import com.example.change.foodorder.Model.Token;
import com.example.change.foodorder.Model.WalletModel;
import com.example.change.foodorder.R;
import com.example.change.foodorder.Remote.APIService;
import com.example.change.foodorder.Remote.IGoogleService;
import com.example.change.foodorder.ViewHolder.CartAdapter;
import com.example.change.foodorder.ViewHolder.CartViewHolder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_SCALE;

public class Cart extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleApiClient.ConnectionCallbacks, RecyclerSwipeToDeleteListener {
    private static final String TAG = "Cart";

    int flag;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference request,userRef,walletRef;
    TextView txtTotalPrice;
    Button btnPlace;
    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;
    EditText editCommentComment, editAddressComment;
    APIService mService;
    Place shippingAddress;
    String address;
    IGoogleService googleService;
    RelativeLayout layout;
    ImageView imageOrderEmpty;
    TextView textOrderEmpty;
    //String total;
    float tAmt;
    String id;

    ProgressDialog progressDialog;

    PaytmPGService service;
    PaytmOrder order;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private final static int LOCATION_REQUEST = 1001;

    private Location lastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL = 1000;
    private static int FASTEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 10;
    ActionBar actionBar;
     String orderNumber;


    //Paytm Credentials
    public static final String MID = "<MID>";
    public static final String INDUSTRY_TYPE_ID = "Retail";
    public static final String CHANNEL_ID = "WAP";
    public static final String WEBSITE = "WEBSTAGING";
    public static final String CALLBACK_URL = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
    String date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.logo);
        orderNumber = String.valueOf(System.currentTimeMillis());


        actionBar.setTitle("Cart");

        mService = Common.getApiService();
        googleService = Common.getGoogleService();
        layout = findViewById(R.id.rootLaoutCart);
        imageOrderEmpty = findViewById(R.id.imageCartEmpty);
        textOrderEmpty = findViewById(R.id.textCartEmpty);
        progressDialog = new ProgressDialog(Cart.this);
        progressDialog.setMessage("Processing Your Payment");



        askPermission();

        displayLocation();
        id = String.valueOf(System.currentTimeMillis());
//
        date = DateFormat.getDateInstance().format(new Date());


        database = FirebaseDatabase.getInstance();
        request = database.getReference("Requests");
        userRef = database.getReference("user");
        walletRef = database.getReference("wallet");

        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        ItemTouchHelper.SimpleCallback callback = new RecyclerSwipeToDeleteHelper(0,ItemTouchHelper.RIGHT,Cart.this);

        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);



        txtTotalPrice = findViewById(R.id.totalt);
        btnPlace = findViewById(R.id.btnPlaceOrder);



        PushDownAnim.setPushDownAnimTo(btnPlace)
                . setScale( MODE_SCALE ,
                        PushDownAnim.DEFAULT_PUSH_SCALE)
                .setDurationPush( PushDownAnim.DEFAULT_PUSH_DURATION )
                .setDurationRelease( PushDownAnim.DEFAULT_RELEASE_DURATION )
                .setInterpolatorPush( PushDownAnim.DEFAULT_INTERPOLATOR )
                .setInterpolatorRelease( PushDownAnim.DEFAULT_INTERPOLATOR );

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cart.size() > 0)
                    showInfoAlert();

                else {


                }
            }
        });

        loadFoodList();
        if (cart.size() < 0 || cart.size() == 0 ){
            imageOrderEmpty.setImageResource(R.drawable.cart_empty);
            textOrderEmpty.setText(R.string.cart_empty);

        }

    }

    private void askPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLocation();
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClent();
                createLocationRequest();
            }
        }
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (lastLocation != null) {
            Log.i(TAG, "displayLocation: "+ "Your Location is " + lastLocation.getLatitude() + "," + lastLocation.getLongitude());
           }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClent();
                        createLocationRequest();

                        displayLocation();

                    }
                }
        }
    }

    protected synchronized void buildGoogleApiClent() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();
    }

    private void requestPermissionLocation() {

        ActivityCompat.requestPermissions(this, new String[]
                {android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST);

    }


    private boolean checkPlayServices() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
               GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(this, "Turn on the Location Service", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }



    private void showInfoAlert(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(Cart.this);
        builder.setTitle("One More Step!");
        builder.setMessage("Enter Shipping Address");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.order_info_shipment_layout, null);
        final PlaceAutocompleteFragment fragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        fragment.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        ((EditText) fragment.getView().findViewById(R.id.place_autocomplete_search_input)).setHint("Enter Shipping Address");
        ((EditText) fragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(14);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("IN")
                .build();
        fragment.setFilter(typeFilter);


        fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                shippingAddress = place;
            }

            @Override
            public void onError(Status status) {
                Log.e("Error Places", status.getStatusMessage());

            }
        });
        editCommentComment = view.findViewById(R.id.editCommentComment);

        final RadioButton shipToHome = view.findViewById(R.id.radioShipToHome);
        final RadioButton shipToCurrent = view.findViewById(R.id.radioShipToCurrent);
        final RadioButton radioNetBank  = view.findViewById(R.id.radioNetBank);
        final RadioButton radioWallet = view.findViewById(R.id.RadioWallet);
        final TextView txtOrderLayoutBalance = view.findViewById(R.id.txtOrderLayoutBalance);
        final TextView txtOrderLayoutInfo = view.findViewById(R.id.txtOrderLayoutInfo);

        txtOrderLayoutBalance.setText("₹" + Common.currentUser.getBalance());
        txtOrderLayoutInfo.setText("You'll be asked to pay ₹" + String.valueOf(Double.parseDouble(txtTotalPrice.getText().toString()) - Double.parseDouble(Common.currentUser.getBalance())));
        txtOrderLayoutBalance.setVisibility(View.GONE);
        txtOrderLayoutInfo.setVisibility(View.GONE);
        shipToHome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                address = Common.currentUser.getAddress();
                ((EditText) fragment.getView().findViewById(R.id.place_autocomplete_search_input)).setText(address);

            }
        });

        shipToCurrent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    googleService.getAddressName(String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=false&key=AIzaSyB0o0r0f_b14v10iIaCrSq62mOSNfgdONM",
                            lastLocation.getLatitude(),lastLocation.getLongitude()))

                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    try {

                                        JSONObject jsonObject = new JSONObject(response.body().toString());

                                        JSONArray array = jsonObject.getJSONArray("results");

                                        JSONObject firObject = array.getJSONObject(0);

                                        address = firObject.getString("formatted_address");

                                        ((EditText) fragment.getView().findViewById(R.id.place_autocomplete_search_input)).setText(address);
                                    }
                                    catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.e(TAG, "onFailure: " + t.getMessage());
                                }
                            });
                }
            }
        });

        radioNetBank.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    flag = 1;
                }
            }
        });
        radioWallet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    double balToBePaid = Double.parseDouble(Common.currentUser.getBalance()) - Double.parseDouble(txtTotalPrice.getText().toString());
                    txtOrderLayoutBalance.setVisibility(View.VISIBLE);
                    if (balToBePaid<=0) {
                        flag = 2;
                        txtOrderLayoutInfo.setVisibility(View.VISIBLE);
                    }

                    else{
                        flag = 3;
                }}

            }
        });


        builder.setView(view);


        builder.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.show();

                if (!shipToCurrent.isChecked() && !shipToHome.isChecked()) {
                    if (shippingAddress!= null)
                        address = shippingAddress.getAddress().toString();
                    else {
                        Toast.makeText(Cart.this, "Select An Delivery Address", Toast.LENGTH_SHORT).show();
                        getFragmentManager().beginTransaction()
                                .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
                        return;
                    }




                }


                if (TextUtils.isEmpty(address)){

                    Toast.makeText(Cart.this, "Select An Delivery Addressaaaa", Toast.LENGTH_SHORT).show();
                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
                    return;
                }
                if (flag == 1){
                    generateChecksum(txtTotalPrice.getText().toString());
                }
                if (flag == 2){
                    double balToBePaid2 = Float.parseFloat(Common.currentUser.getBalance()) - Float.parseFloat(txtTotalPrice.getText().toString());
                    generateChecksum(String.valueOf(balToBePaid2));
                    updateUserBalance("0.00");

                    updateWalletHistory(date,"Paid ₹ " + String.valueOf(balToBePaid2)) ;
                }
                 if (flag ==3){

                     updateUserBalance(String.valueOf(Float.parseFloat(Common.currentUser.getBalance()) - Float.parseFloat(txtTotalPrice.getText().toString())));
                     updateWalletHistory(date,"Paid ₹"+ txtTotalPrice.getText().toString());
                    Request requestsB = new Request(
                            Common.currentUser.getEmail(),
                            Common.currentUser.getPhone(),
                            Common.currentUser.getName(),
                            address,
                            txtTotalPrice.getText().toString(),
                            "0",
                            editCommentComment.getText().toString(),
                            "balance",
                            String.format("%s,%s",shippingAddress.getLatLng().latitude,shippingAddress.getLatLng().longitude),
                            cart

                    );

                    String orderNumber = String.valueOf(System.currentTimeMillis());
                    request.child(orderNumber).setValue(requestsB);
                    new Database(getBaseContext()).emptyCart(Common.currentUser.getEmail());
                    sendNotification(orderNumber);

                    Toast.makeText(Cart.this, "Thank You for Shopping With Us. Your Oder Has Been Placed!", Toast.LENGTH_LONG).show();
                    finish();

                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();

                }


            }


        });
        builder.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getFragmentManager().beginTransaction()
                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
                return;

            }
        });
        builder.show();
    }



    private void updateUserBalance(String s) {

        Map<String,Object> map = new HashMap<>();
        map.put("balance", s);
        userRef.child(Common.currentUser.getEmail().replace(".","_")).updateChildren(map)
                .addOnCompleteListener(Cart.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(Cart.this, "Balance Updated", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onComplete: Balance Updated and Current Balance is " + Common.currentUser.getBalance());


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Cart.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void generateChecksum(final String s) {



        //total = txtTotalPrice.getText().toString().replace("₹","");

        String url = "https://antinomical-attachm.000webhostapp.com/checksum.php";
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put( "MID" , MID);
// Key in your staging and production MID available in your dashboard
        paramMap.put( "ORDER_ID" , Common.ordernumber);
        paramMap.put( "CUST_ID" , Common.currentUser.getEmail());
        //paramMap.put( "MOBILE_NO" , "7777777777");
       // paramMap.put( "EMAIL" , "username@emailprovider.com");
        paramMap.put( "CHANNEL_ID" , CHANNEL_ID);
        paramMap.put( "TXN_AMOUNT" , s);
        paramMap.put( "WEBSITE" , WEBSITE);
// This is the staging value. Production value is available in your dashboard
        paramMap.put( "INDUSTRY_TYPE_ID" , INDUSTRY_TYPE_ID);
// This is the staging value. Production value is available in your dashboard
        paramMap.put( "CALLBACK_URL", CALLBACK_URL);


       JSONObject object = new JSONObject(paramMap);
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, object, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "onResponse: "+ response.optString("CHECKSUMHASH" + "The total amt is " + txtTotalPrice.getText().toString()));
                startTrans(response.optString("CHECKSUMHASH"),s);



            }

        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: " + error.getMessage());

            }

    });

        Volley.newRequestQueue(this).add(request);


       }

    private void startTrans(String checksumhash,String amt) {

        //total = txtTotalPrice.getText().toString().replace("₹","");

         service = PaytmPGService.getStagingService();
        Map<String, String> paramMap = new HashMap<String,String>();
        paramMap.put( "MID" , MID);
// Key in your staging and production MID available in your dashboard
        paramMap.put( "ORDER_ID" , Common.ordernumber);
        paramMap.put( "CUST_ID" , Common.currentUser.getEmail());
        //paramMap.put( "MOBILE_NO" , "7777777777");
       // paramMap.put( "EMAIL" , Common.currentUser.getEmail());
        paramMap.put( "CHANNEL_ID" , CHANNEL_ID);
        paramMap.put( "TXN_AMOUNT" , amt);
        paramMap.put( "WEBSITE" , WEBSITE);
// This is the staging value. Production value is available in your dashboard
        paramMap.put( "INDUSTRY_TYPE_ID" , INDUSTRY_TYPE_ID);
// This is the staging value. Production value is available in your dashboard
        paramMap.put( "CALLBACK_URL", CALLBACK_URL);
        paramMap.put( "CHECKSUMHASH" , checksumhash);
         order = new PaytmOrder((HashMap<String, String>) paramMap);
        service.initialize(order,null);
        progressDialog.dismiss();
        service.startPaymentTransaction(Cart.this, true, true, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(final Bundle inResponse) {
                Log.i(TAG, "onTransactionResponse: " +inResponse.toString());
                Request requests = new Request(
                        Common.currentUser.getEmail(),
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        address,
                        txtTotalPrice.getText().toString(),
                        "0",
                        editCommentComment.getText().toString(),
                        inResponse.getString("STATUS"),
                        String.format("%s,%s",shippingAddress.getLatLng().latitude,shippingAddress.getLatLng().longitude),
                        cart

                );

                if (inResponse.getString("STATUS").equals("TXN_SUCCESS")) {
//                    if ((Float.parseFloat(Common.currentUser.getBalance()) < (Float.parseFloat(txtTotalPrice.getText().toString())))){
//
//
//
//
//
//
//
//                    }

                    request.child(Common.ordernumber).setValue(requests);
                    new Database(getBaseContext()).emptyCart(Common.currentUser.getEmail());
                    sendNotification(Common.ordernumber);

                    Toast.makeText(Cart.this, "Thank You for Shopping With Us. Your Oder Has Been Placed!", Toast.LENGTH_LONG).show();


//
                    finish();

                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();


                }
                else {
                    Snackbar.make(layout,"We're facing issues processing your payment",Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE)
                            .setAction("Try Again", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    generateChecksum(txtTotalPrice.getText().toString());
                                }
                            }).show();
                }

            }

            @Override
            public void networkNotAvailable() {
//                getFragmentManager().beginTransaction()
//                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
//                return;

            }

            @Override
            public void clientAuthenticationFailed(String inErrorMessage) {
//                getFragmentManager().beginTransaction()
//                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
//                return;

            }

            @Override
            public void someUIErrorOccurred(String inErrorMessage) {
//                getFragmentManager().beginTransaction()
//                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
//                return;

            }

            @Override
            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
//                getFragmentManager().beginTransaction()
//                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
//                return;

            }

            @Override
            public void onBackPressedCancelTransaction() {
                Snackbar.make(layout,"You Cancelled the transaction",Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE)
                        .setAction("Try Again", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               generateChecksum(txtTotalPrice.getText().toString());
                            }
                        }).show();
//                getFragmentManager().beginTransaction()
//                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
//                return;


            }

            @Override
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
//                getFragmentManager().beginTransaction()
//                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
//                return;

            }
        });
    }

    private void updateLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,  this);

    }


    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }

    private void sendNotification(final String orderNumber) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("serverToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    Token serverToken = postSnapShot.getValue(Token.class);

                    Notification notification = new Notification("Simply Food", "We have a new order. Order Number #" + orderNumber);
                    Sender content = new Sender(serverToken.getToken(), notification);
                    mService.sendNotification(content)
                            .enqueue(new Callback<FBResponse>() {
                                @Override
                                public void onResponse(Call<FBResponse> call, Response<FBResponse> response) {

                                    if (response.code() == 200) {
                                        if (response.body().success == 1) {

                                            Toast.makeText(Cart.this, "Thank You for Shopping With Us. Your Oder Has Been Placed!", Toast.LENGTH_LONG).show();
                                            finish();
                                        } else {
                                            Toast.makeText(Cart.this, "Error Placing Your Order. Don't Worry, We'll Try Again!", Toast.LENGTH_LONG).show();
                                            Log.i(TAG, "onResponse: " + response.toString());
                                            Log.i(TAG, "onResponse: " + response.body().success);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<FBResponse> call, Throwable t) {

                                    Log.e("Error placing order", t.getMessage());

                                }

                            });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadFoodList() {
        cart = new Database(this).getCarts(Common.currentUser.getEmail());
        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        int tPrice = 0;

        for (Order o : cart)
            tPrice += (Integer.parseInt(o.getPrice())) * (Integer.parseInt(o.getQuantity()));
        Locale locale = new Locale("en", "IN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(String.valueOf(tPrice));


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == Common.DELETE)
            deleteCartItem(item.getOrder());


        return true;
    }

    private void deleteCartItem(int order) {
        cart.remove(order);
        new Database(this).emptyCart(Common.currentUser.getEmail());

        for (Order o : cart)
            new Database(this).addToCart(o);

        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
        loadFoodList();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();;
        updateLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartViewHolder){
            String name = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();

            final Order deleteItem = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());

            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);

            new Database(getBaseContext()).removeFromCart(deleteItem.getProductId(),Common.currentUser.getEmail());
            double total = 0.00;
            List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getEmail());
            for (Order item : orders)
                total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));
            Locale locale = new Locale("en","IN");
            NumberFormat format = NumberFormat.getCurrencyInstance(locale);
            txtTotalPrice.setText(String.valueOf(total));


            Snackbar snackbar = Snackbar.make(layout,name+ " has been removed from cart",Snackbar.LENGTH_LONG);
            snackbar.setAction("Add To Cart", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.restoreItem(deleteItem,deleteIndex);
                    new Database(getBaseContext()).addToCart(deleteItem);
                    double total = 0.00;
                    List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getEmail());
                    for (Order item : orders)
                        total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));
                    Locale locale = new Locale("en","IN");
                    NumberFormat format = NumberFormat.getCurrencyInstance(locale);
                    txtTotalPrice.setText(String.valueOf(total));


                }
            });
            snackbar.setActionTextColor(Color.WHITE);
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
