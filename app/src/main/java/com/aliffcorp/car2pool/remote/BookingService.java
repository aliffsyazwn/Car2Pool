package com.aliffcorp.car2pool.remote;

import com.aliffcorp.car2pool.model.Booking;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Header;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.Path;

public interface BookingService {

    @GET("bookings")
    Call<List<Booking>> getBookings(@Header("api-key") String api_key);

    @GET("bookings/{booking_id}")
    Call<Booking> getBooking(@Header("api-key") String api_key, @Path("booking_id") int bookingId);

    @DELETE("bookings/{booking_id}")
    Call<Void> cancelBooking(@Header("api-key") String api_key, @Path("booking_id") int bookingId);

    @FormUrlEncoded
    @POST("bookings")
    Call<Booking> addBooking(@Header("api-key") String api_key, @Field("user_id") int user_id, @Field("ride_id") int ride_id);

    @PUT("bookings/{booking_id}")
    Call<Booking> updateBooking(@Header("api-key") String api_key, @Path("booking_id") int bookingId, @Body Booking updatedBooking);
}
