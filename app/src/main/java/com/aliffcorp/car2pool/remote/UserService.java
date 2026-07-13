package com.aliffcorp.car2pool.remote;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

import com.aliffcorp.car2pool.model.User;

public interface UserService {

    @FormUrlEncoded
    @POST("users/login")
    Call<User> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("users")
    Call<User> addUser(@Header("api-key") String api_key, @Field("email") String email,
                        @Field("username") String username, @Field("password") String password,
                        @Field("token") String token, @Field("lease") String lease,
                        @Field("role") String role, @Field("is_active") int is_active,
                        @Field("secret") String secret, @Field("studID") String studID,
                        @Field("carModel") String carModel, @Field("plateNumber") String plateNumber,
                        @Field("license") String license, @Field("fullName") String fullName);

    @FormUrlEncoded
    @POST("users/login")
    Call<User> loginEmail(@Field("email") String username, @Field("password") String password);

    @GET("users/{id}")
    Call<User> getUser(@Header("api-key") String api_key, @Path("id") int id);

    @FormUrlEncoded
    @POST("users/{id}")
    Call<User> updateUser(@Header("api-key") String api_key, @Path("id") int id,
                          @Field("email") String email, @Field("username") String username,
                          @Field("password") String password, @Field("token") String token,
                          @Field("lease") String lease, @Field("role") String role,
                          @Field("is_active") int is_active, @Field("secret") String secret,
                          @Field("studID") String studID, @Field("carModel") String carModel,
                          @Field("plateNumber") String plateNumber, @Field("license") String license,
                          @Field("fullName") String fullName);

}