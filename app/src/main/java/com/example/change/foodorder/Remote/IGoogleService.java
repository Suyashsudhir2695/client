package com.example.change.foodorder.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface IGoogleService {

    @GET
    Call<String> getAddressName(@Url String url);

    @GET("maps/api/geocode/json")
    Call<String> getLocationFromAddress(@Query("address") String address,@Query("sensor") String sensor,@Query("key") String key);

    @GET("maps/api/geocode/json")
    Call<String> getLocationFromLatLng(@Query("latlng") String address,@Query("sensor") String sensor,@Query("key") String key);


    @GET("maps/api/directions/json")
    Call<String> getDirections(@Query("origin") String origin, @Query("destination") String destination, @Query("sensor") String sensor, @Query("key") String key);
}
