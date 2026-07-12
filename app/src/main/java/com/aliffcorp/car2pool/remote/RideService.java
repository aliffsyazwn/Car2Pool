package com.aliffcorp.car2pool.remote;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

import com.aliffcorp.car2pool.model.Ride;

import java.util.List;

public interface RideService {

    @GET("rides")
    Call<List<Ride>> getAllRides(@Header("api-key") String api_key);

    @GET("rides/{id}")
    Call<Ride> getRides(@Header("api-key") String api_key, @Path("id") int id);

    @FormUrlEncoded
    @POST("rides")
    Call<Ride> createRide(
            @Header("api-key") String api_key,
            @Field("driver_id") int driverId,
            @Field("origin") String origin,
            @Field("destination") String destination,
            @Field("departure_time") String departureTime,
            @Field("fSeat") int fSeat,
            @Field("rSeat") int rSeat,
            @Field("mSeat") int mSeat,
            @Field("lSeat") int lSeat
    );

    @FormUrlEncoded
    @POST("rides/{id}")
    Call<Ride> updateSeat(@Header ("api-key") String apiKey, @Path("id") int ride_id,
                          @Field("driver_id") int driver_id,
                          @Field("origin") String origin,
                          @Field("destination") String destination,
                          @Field("departure_time") String departureTime,
                          @Field("fSeat") int fSeat,
                          @Field("rSeat") int rSeat,
                          @Field("mSeat") int mSeat,
                          @Field("lSeat") int lSeat);
}