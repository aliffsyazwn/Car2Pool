package com.aliffcorp.car2pool.sharedpref;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.aliffcorp.car2pool.model.User;


public class SharedPrefManager {

    //the constants
    private static final String SHARED_PREF_NAME = "bookstoresharedpref";
    private static final String KEY_ID = "keyid";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_TOKEN = "keytoken";
    private static final String KEY_ROLE = "keyrole";
    private static final String KEY_STUDID = "keystudid";
    private static final String KEY_CARMODEL = "keycarmodel";
    private static final String KEY_PLATENUMBER = "keyplatenumber";
    private static final String KEY_LICENSE = "keylicense";

    private final Context mCtx;

    public SharedPrefManager(Context context) {
        mCtx = context;
    }

    /**
     * method to let the user login
     * this method will store the user data in shared preferences
     * @param user
     */
    public void storeUser(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_TOKEN, user.getToken());
        editor.putString(KEY_ROLE, user.getRole());
        editor.putString(KEY_STUDID, user.getStudID());
        editor.putString(KEY_CARMODEL, user.getCarModel());
        editor.putString(KEY_PLATENUMBER, user.getPlateNumber());
        editor.putString(KEY_LICENSE, user.getLicense());
        editor.apply();
    }

    /**
     * this method will checker whether user is already logged in or not.
     * return True if already logged in
     */

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    /**
     * this method will give the information of logged in user, retrieved from SharedPreferences
     */
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        User user = new User();
        user.setId(sharedPreferences.getInt(KEY_ID, -1));
        user.setUsername(sharedPreferences.getString(KEY_USERNAME, null));
        user.setEmail(sharedPreferences.getString(KEY_EMAIL, null));
        user.setToken(sharedPreferences.getString(KEY_TOKEN, null));
        user.setRole(sharedPreferences.getString(KEY_ROLE, null));
        user.setStudID(sharedPreferences.getString(KEY_STUDID, null));
        user.setCarModel(sharedPreferences.getString(KEY_CARMODEL, null));
        user.setPlateNumber(sharedPreferences.getString(KEY_PLATENUMBER, null));
        user.setLicense(sharedPreferences.getString(KEY_LICENSE, null));

        return user;
    }

    /**
     * this method will logout the user. clear the SharedPreferences
     */
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}