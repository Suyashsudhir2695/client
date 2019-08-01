package com.example.change.foodorder.Activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Common.DirectionJsonParser;
import com.example.change.foodorder.Model.Request;
import com.example.change.foodorder.Model.ShippingInfo;
import com.example.change.foodorder.R;
import com.example.change.foodorder.Remote.IGoogleService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackOrders extends FragmentActivity implements OnMapReadyCallback ,ValueEventListener{

    private GoogleMap mMap;
    FirebaseDatabase database;
    DatabaseReference reqRef, shipmentRef;
    Request currentRequest;
    IGoogleService mService;
    Marker supplierMarker;
    Polyline polyline;
    private static final String TAG = "TrackOrders";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_orders);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        database = FirebaseDatabase.getInstance();
        reqRef = database.getReference("Requests");
        shipmentRef = database.getReference("SupplierOrders");
        shipmentRef.addValueEventListener(this);
        mService = Common.getGoogleService();
    }

    @Override
    protected void onStop() {
        shipmentRef.removeEventListener(this);
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        trackLocation();

    }

    private void trackLocation() {
        reqRef.child(Common.currentKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        currentRequest = dataSnapshot.getValue(Request.class);
                        if (currentRequest.getAddress() != null && !currentRequest.getAddress().isEmpty()){
                            mService.getLocationFromAddress(currentRequest.getAddress(),"false","AIzaSyB0o0r0f_b14v10iIaCrSq62mOSNfgdONM")
                                    .enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            try{
                                                Log.i(TAG, "onResponse: responsebody from address"+response.body());

                                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                                String lat = ((JSONArray)jsonObject.get("results"))
                                                        .getJSONObject(0)
                                                        .getJSONObject("geometry")
                                                        .getJSONObject("location")
                                                        .get("lat").toString();


                                                String lng = ((JSONArray)jsonObject.get("results"))
                                                        .getJSONObject(0)
                                                        .getJSONObject("geometry")
                                                        .getJSONObject("location")
                                                        .get("lng").toString();


                                                final LatLng orderLocation = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                                                Log.i(TAG, "onResponse: from order location");

                                                mMap.addMarker(new MarkerOptions().position(orderLocation)
                                                .title("Your Location")
                                                .icon(BitmapDescriptorFactory.defaultMarker()));

                                                shipmentRef.child(Common.currentKey)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                ShippingInfo info = dataSnapshot.getValue(ShippingInfo.class);

                                                                LatLng deliveryLatLng = new LatLng(info.getLat(),info.getLng());
                                                                Log.i(TAG, "onDataChange: LatLng"+deliveryLatLng);

                                                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.add_to_cart);

                                                                bitmap = Common.scaleBitmap(bitmap,70,70);

                                                                if (supplierMarker == null){
                                                                    supplierMarker = mMap.addMarker(new MarkerOptions()
                                                                    .position(deliveryLatLng)
                                                                    .title("Your Order is here. Supplier #"+ info.getOrderId())
                                                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                                                                }
                                                                else {
                                                                    supplierMarker.setPosition(deliveryLatLng);
                                                                }

                                                                CameraPosition position = new CameraPosition.Builder()
                                                                        .target(deliveryLatLng)
                                                                        .bearing(0)
                                                                        .zoom(15.0f)
                                                                        .tilt(45)
                                                                        .build();
                                                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));

                                                                if (polyline != null)
                                                                    polyline.remove();


                                                                mService.getDirections(deliveryLatLng.latitude+","+deliveryLatLng.longitude,
                                                                        orderLocation.latitude+","+orderLocation.longitude
                                                                        ,"false","AIzaSyB95eskfrIUWbvkxqIxnhWWIsjsQpUysUw")
                                                                        .enqueue(new Callback<String>() {
                                                                            @Override
                                                                            public void onResponse(Call<String> call, Response<String> response) {
                                                                                new ParserTask().execute(response.body().toString());

                                                                            }

                                                                            @Override
                                                                            public void onFailure(Call<String> call, Throwable t) {

                                                                            }
                                                                        });



                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                Log.i(TAG, "onCancelled: Inner" + databaseError.getMessage());

                                                            }
                                                        });

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Log.i(TAG, "onResponse: Json Error" + e.getMessage());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {

                                        }
                                    });

                        }
                        else  if (currentRequest.getLatLng() != null && !currentRequest.getLatLng().isEmpty()){
                            mService.getLocationFromLatLng(currentRequest.getLatLng(),"false","AIzaSyB0o0r0f_b14v10iIaCrSq62mOSNfgdONM")
                                    .enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            try{
                                                Log.i(TAG, "onResponse: responsebody"+response.body());
                                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                                String lat  = ((JSONArray) jsonObject.get("results"))
                                                        .getJSONObject(0)
                                                        .getJSONObject("geometry")
                                                        .getJSONObject("location")
                                                        .getJSONObject("lat").toString();



                                                String lng  = ((JSONArray) jsonObject.get("results"))
                                                        .getJSONObject(0)
                                                        .getJSONObject("geometry")
                                                        .getJSONObject("location")
                                                        .getJSONObject("lng").toString();


                                                final LatLng orderLocation = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));

                                                mMap.addMarker(new MarkerOptions().position(orderLocation)
                                                        .title("Your Location")
                                                        .icon(BitmapDescriptorFactory.defaultMarker()));

                                                shipmentRef.child(Common.currentKey)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                ShippingInfo info = dataSnapshot.getValue(ShippingInfo.class);

                                                                LatLng deliveryLatLng = new LatLng(info.getLat(),info.getLng());

                                                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.add_to_cart);

                                                                bitmap = Common.scaleBitmap(bitmap,70,70);

                                                                if (supplierMarker == null){
                                                                    supplierMarker = mMap.addMarker(new MarkerOptions()
                                                                            .position(deliveryLatLng)
                                                                            .title("Your Order is here. Supplier #"+ info.getOrderId())
                                                                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                                                                }
                                                                else {
                                                                    supplierMarker.setPosition(deliveryLatLng);
                                                                }

                                                                CameraPosition position = new CameraPosition.Builder()
                                                                        .target(deliveryLatLng)
                                                                        .bearing(0)
                                                                        .zoom(15.0f)
                                                                        .tilt(45)
                                                                        .build();
                                                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));

                                                                if (polyline != null)
                                                                    polyline.remove();


                                                                mService.getDirections(deliveryLatLng.latitude+","+deliveryLatLng.longitude,
                                                                        currentRequest.getLatLng(),"false","AIzaSyB95eskfrIUWbvkxqIxnhWWIsjsQpUysUw")
                                                                        .enqueue(new Callback<String>() {
                                                                            @Override
                                                                            public void onResponse(Call<String> call, Response<String> response) {
                                                                                new ParserTask().execute(response.body().toString());

                                                                            }

                                                                            @Override
                                                                            public void onFailure(Call<String> call, Throwable t) {

                                                                            }
                                                                        });



                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {

                                        }
                                    });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Log.i(TAG, "onCancelled: " +  databaseError.getMessage());

                    }
                });

    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        trackLocation();

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    private  class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>> {
        ProgressDialog dialog = new ProgressDialog(TrackOrders.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Hang On...");
            dialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject object;
            List<List<HashMap<String,String>>> routes = null;
            try {
                object = new JSONObject(strings[0]);
                DirectionJsonParser parser = new DirectionJsonParser();
                routes = parser.parse(object);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            dialog.dismiss();

            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            for (int i=0;i<lists.size();i++){
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                List<HashMap<String,String>> path = lists.get(i);

                for (int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));

                    LatLng pos = new LatLng(lat,lng);

                    points.add(pos);
                }

                polylineOptions.addAll(points);
                polylineOptions.width(12);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);

            }
           polyline= mMap.addPolyline(polylineOptions);


        }
    }
}
