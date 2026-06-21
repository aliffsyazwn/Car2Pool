package com.aliffcorp.car2pool.remote;

public class ApiUtils {

    // REST API server URL
    public static final String BASE_URL = "https://aptitude.my/2024794891/api/";

    // return UserService instance
    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }

}
