package com.aliffcorp.car2pool;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.aliffcorp.car2pool.model.User;
import com.aliffcorp.car2pool.remote.ApiUtils;
import com.aliffcorp.car2pool.remote.UserService;
import com.aliffcorp.car2pool.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewProfileActivity extends AppCompatActivity {

    private TextView tvUsername;
    private TextView tvFullName;
    private TextView tvHeaderFullName;
    private TextView tvEmail;
    private TextView tvRole;
    private TextView tvStudID;
    private TextView tvModel;
    private TextView tvPlate;
    private TextView tvLicense;

    private CardView cardVehicleInfo;

    private Button btnUpdateProfile;
    private Button btnLogout;

    private SharedPrefManager spm;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);

        spm = new SharedPrefManager(this);

        if (!spm.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        //==========================
        // TextViews
        //==========================

        tvUsername = findViewById(R.id.tvUsername);
        tvFullName = findViewById(R.id.tvFullName);
        tvHeaderFullName = findViewById(R.id.tvHeaderFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvRole = findViewById(R.id.tvRole);
        tvStudID = findViewById(R.id.tvStudID);

        tvModel = findViewById(R.id.tvModel);
        tvPlate = findViewById(R.id.tvPlate);
        tvLicense = findViewById(R.id.tvLicense);

        cardVehicleInfo = findViewById(R.id.cardVehicleInfo);

        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnLogout = findViewById(R.id.btnLogout);

        User user = spm.getUser();

        //==========================
        // Navigation
        //==========================

        View riderNav = findViewById(R.id.footerCard);
        View driverNav = findViewById(R.id.dFooterCard);

        if ("driver".equalsIgnoreCase(user.getRole())) {

            riderNav.setVisibility(View.GONE);
            driverNav.setVisibility(View.VISIBLE);

            CardView cardHome = driverNav.findViewById(R.id.cardHome);
            CardView cardUpdateRide = driverNav.findViewById(R.id.cardUpdateRide);
            CardView cardCreateRide = driverNav.findViewById(R.id.cardCreateRide);
            CardView cardProfile = driverNav.findViewById(R.id.cardProfile);

            cardHome.setOnClickListener(v ->
                    startActivity(new Intent(this, DriverMainActivity.class)));

            cardUpdateRide.setOnClickListener(v ->
                    startActivity(new Intent(this, DriverRideListActivity.class)));

            cardCreateRide.setOnClickListener(v ->
                    startActivity(new Intent(this, CreateRideActivity.class)));

            cardProfile.setOnClickListener(v -> {
                // Current Page
            });

        } else {

            riderNav.setVisibility(View.VISIBLE);
            driverNav.setVisibility(View.GONE);

            CardView cardHome = riderNav.findViewById(R.id.cardHome);
            CardView cardSearchRide = riderNav.findViewById(R.id.cardSearchRide);
            CardView cardBooking = riderNav.findViewById(R.id.cardBooking);
            CardView cardProfile = riderNav.findViewById(R.id.cardProfile);

            cardHome.setOnClickListener(v ->
                    startActivity(new Intent(this, MainActivity.class)));

            cardSearchRide.setOnClickListener(v ->
                    startActivity(new Intent(this, RideListActivity.class)));

            cardBooking.setOnClickListener(v ->
                    startActivity(new Intent(this, BookingList.class)));

            cardProfile.setOnClickListener(v -> {
                // Current Page
            });
        }

        displayUserData(user);

        fetchLatestUserData();

        btnUpdateProfile.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ViewProfileActivity.this,
                    UpdateProfileActivity.class);

            intent.putExtra("user_id", user.getId());

            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {

            spm.logout();

            Intent intent = new Intent(
                    ViewProfileActivity.this,
                    LoginActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (spm != null && spm.isLoggedIn()) {
            displayUserData(spm.getUser());
            fetchLatestUserData();
        }
    }

    private void fetchLatestUserData() {

        userService = ApiUtils.getUserService();

        User currentUser = spm.getUser();

        userService.getUser(
                        currentUser.getToken(),
                        currentUser.getId())
                .enqueue(new Callback<User>() {

                    @Override
                    public void onResponse(Call<User> call,
                                           Response<User> response) {

                        if (response.isSuccessful() &&
                                response.body() != null) {

                            User updatedUser = response.body();

                            if (updatedUser.getToken() == null) {
                                updatedUser.setToken(currentUser.getToken());
                            }

                            spm.storeUser(updatedUser);

                            displayUserData(updatedUser);

                        } else if (response.code() == 401) {

                            Toast.makeText(
                                    ViewProfileActivity.this,
                                    "Session expired",
                                    Toast.LENGTH_LONG).show();

                            spm.logout();

                            startActivity(new Intent(
                                    ViewProfileActivity.this,
                                    LoginActivity.class));

                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call,
                                          Throwable t) {

                        Log.e("PROFILE",
                                t.getMessage());
                    }
                });
    }

    private void displayUserData(User user) {

        String name;

        if (user.getFullName() != null &&
                !user.getFullName().isEmpty()) {

            name = user.getFullName();

        } else {

            name = user.getUsername();
        }

        tvHeaderFullName.setText(StringUtils.capitalize(name));
        tvUsername.setText(user.getUsername());
        tvFullName.setText(user.getFullName());
        tvEmail.setText(user.getEmail());
        tvRole.setText(StringUtils.capitalize(user.getRole()));
        tvStudID.setText(user.getStudID());

        tvModel.setText(user.getCarModel());
        tvPlate.setText(user.getPlateNumber());
        tvLicense.setText(user.getLicense());

        // Vehicle Card

        if ("driver".equalsIgnoreCase(user.getRole())) {

            cardVehicleInfo.setVisibility(View.VISIBLE);

        } else {

            cardVehicleInfo.setVisibility(View.GONE);
        }
    }
}