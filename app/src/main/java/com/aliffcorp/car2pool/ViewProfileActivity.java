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
    private TextView tvModelLabel;
    private TextView tvPlateLabel;
    private TextView tvLicenseLabel;

    // Vehicle Information Card
    private CardView cardVehicleInfo;

    private Button btnUpdateProfile;
    private Button btnLogout;

    private SharedPrefManager spm;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);

        // Initialize Views
        tvUsername = findViewById(R.id.tvUsername);
        tvFullName = findViewById(R.id.tvFullName);
        tvHeaderFullName = findViewById(R.id.tvHeaderFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvRole = findViewById(R.id.tvRole);
        tvStudID = findViewById(R.id.tvStudID);

        tvModel = findViewById(R.id.tvModel);
        tvPlate = findViewById(R.id.tvPlate);
        tvLicense = findViewById(R.id.tvLicense);

        tvModelLabel = findViewById(R.id.tvModelLabel);
        tvPlateLabel = findViewById(R.id.tvPlateLabel);
        tvLicenseLabel = findViewById(R.id.tvLicenseLabel);

        cardVehicleInfo = findViewById(R.id.cardVehicleInfo);

        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnLogout = findViewById(R.id.btnLogout);

        spm = new SharedPrefManager(this);

        // Check Login
        if (!spm.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Display cached data first
        displayUserData(spm.getUser());

        // Refresh latest data from server
        fetchLatestUserData();

        // Update Profile
        btnUpdateProfile.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ViewProfileActivity.this,
                    UpdateProfileActivity.class);

            intent.putExtra(
                    "user_id",
                    spm.getUser().getId());

            startActivity(intent);
        });

        // Logout
        btnLogout.setOnClickListener(v -> {

            spm.logout();

            Intent intent = new Intent(
                    ViewProfileActivity.this,
                    LoginActivity.class);

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
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
                currentUser.getId()
        ).enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call,
                                   Response<User> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    User updatedUser = response.body();

                    if (updatedUser.getToken() == null) {
                        updatedUser.setToken(currentUser.getToken());
                    }

                    spm.storeUser(updatedUser);

                    displayUserData(updatedUser);

                } else if (response.code() == 401) {

                    Toast.makeText(
                            ViewProfileActivity.this,
                            "Session expired. Please login again.",
                            Toast.LENGTH_LONG
                    ).show();

                    spm.logout();

                    finish();

                    startActivity(
                            new Intent(
                                    ViewProfileActivity.this,
                                    LoginActivity.class));
                }
            }

            @Override
            public void onFailure(Call<User> call,
                                  Throwable t) {

                Log.e(
                        "ViewProfileActivity",
                        "Error fetching user data : "
                                + t.getMessage());
            }
        });
    }

    private void displayUserData(User user) {

        String nameToShow;

        if (user.getFullName() != null &&
                !user.getFullName().isEmpty()) {

            nameToShow = user.getFullName();

        } else {

            nameToShow = user.getUsername();
        }

        tvHeaderFullName.setText(
                StringUtils.capitalize(nameToShow));

        tvFullName.setText(user.getFullName());
        tvUsername.setText(user.getUsername());
        tvEmail.setText(user.getEmail());
        tvRole.setText(
                StringUtils.capitalize(user.getRole()));
        tvStudID.setText(user.getStudID());

        tvModel.setText(user.getCarModel());
        tvPlate.setText(user.getPlateNumber());
        tvLicense.setText(user.getLicense());

        // ==========================
        // Show Vehicle Card Only for Driver
        // ==========================

        if ("driver".equalsIgnoreCase(user.getRole())) {

            cardVehicleInfo.setVisibility(View.VISIBLE);

        } else {

            cardVehicleInfo.setVisibility(View.GONE);

        }
    }
}