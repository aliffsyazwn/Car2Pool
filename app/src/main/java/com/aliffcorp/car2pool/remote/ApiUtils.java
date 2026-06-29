package com.aliffcorp.car2pool.remote;

public class ApiUtils {

    // REST API server URL
    public static final String BASE_URL = "https://aptitude.my/car2pool/api/";

    // return UserService instance
    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }

    public static RideService getRideService() {
        return RetrofitClient.getClient(BASE_URL).create(RideService.class);
    }

}
