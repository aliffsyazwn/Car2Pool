package com.aliffcorp.car2pool.remote;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import com.aliffcorp.car2pool.model.Rides;

public interface RideService {

    @FormUrlEncoded
    @POST("rides")
    Call<Rides> login(@Field("username") String username, @Field("password") String password);


}