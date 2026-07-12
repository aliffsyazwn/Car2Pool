package com.aliffcorp.car2pool.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

import com.aliffcorp.car2pool.model.Ride;

import java.util.List;

public interface RideService {

    @GET("rides")
    Call<List<Ride>> getAllRides(@Header("api-key") String api_key);

    @GET("rides/{id}")
    Call<Ride> getRides(@Header("api-key") String api_key, @Path("id") int id);
}