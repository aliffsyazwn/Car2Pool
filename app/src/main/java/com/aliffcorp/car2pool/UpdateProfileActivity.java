package com.aliffcorp.car2pool;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aliffcorp.car2pool.model.User;
import com.aliffcorp.car2pool.remote.ApiUtils;
import com.aliffcorp.car2pool.remote.UserService;
import com.aliffcorp.car2pool.sharedpref.SharedPrefManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UpdateProfileActivity extends AppCompatActivity {

    private View footerCard;
    private View dFooterCard;
    private EditText etUsername;
    private EditText etFullName;
    private EditText etEmail;
    private EditText etStudID;
    private EditText etModel;
    private EditText etPlate;
    private EditText etLicense;
    private TextView tvModelLabel;
    private TextView tvPlateLabel;
    private TextView tvLicenseLabel;
    private RadioGroup rgRole;
    private RadioButton rbRider, rbDriver;
    private View dividerCar, layoutCarDetails;
    private User user;
    private SharedPrefManager spm;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        int id = intent.getIntExtra("user_id", -1);

        // Initialize Views
        etUsername = findViewById(R.id.etUsername);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etStudID = findViewById(R.id.etStudID);
        etModel = findViewById(R.id.etModel);
        etPlate = findViewById(R.id.etPlate);
        etLicense = findViewById(R.id.etLicense);
        tvModelLabel = findViewById(R.id.tvModelLabel);
        tvPlateLabel = findViewById(R.id.tvPlateLabel);
        tvLicenseLabel = findViewById(R.id.tvLicenseLabel);

        rgRole = findViewById(R.id.rgRole);
        rbRider = findViewById(R.id.rbRider);
        rbDriver = findViewById(R.id.rbDriver);
        dividerCar = findViewById(R.id.dividerCar);
        layoutCarDetails = findViewById(R.id.layoutCarDetails);
        footerCard = findViewById(R.id.footerCard);
        dFooterCard = findViewById(R.id.dFooterCard);

        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDriver) {
                showDriverFields(true);
            } else {
                showDriverFields(false);
            }
        });

        spm = new SharedPrefManager(getApplicationContext());
        setupBottomNavigation();
        User currentUser = spm.getUser();
        userService = ApiUtils.getUserService();

        if (id != -1 && currentUser != null) {
            userService.getUser(currentUser.getToken(), id).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    // for debug purpose
                    Log.d("MyApp:", "Update Form Populate Response: " + response.raw().toString());

                    if (response.code() == 200) {
                        // server return success
                        user = response.body();

                        if (user != null) {
                            etFullName.setText(user.getFullName());
                            etUsername.setText(user.getUsername());
                            etEmail.setText(user.getEmail());
                            etStudID.setText(user.getStudID());
                            etModel.setText(user.getCarModel());
                            etPlate.setText(user.getPlateNumber());
                            etLicense.setText(user.getLicense());

                            // Handle visibility based on role
                            if ("driver".equalsIgnoreCase(user.getRole())) {
                                rbDriver.setChecked(true);
                                showDriverFields(true);
                            } else {
                                rbRider.setChecked(true);
                                showDriverFields(false);
                            }
                        }
                    } else if (response.code() == 401) {
                        // unauthorized error. invalid token, ask user to relogin
                        Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                        clearSessionAndRedirect();
                    } else {
                        // server return other error
                        Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                        Log.e("MyApp: ", response.toString());
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Error connecting", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void clearSessionAndRedirect() {
        // clear the shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // terminate this Activity
        finish();

        // forward to Login Page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void doUpdateProfile(View view) {
        if (user == null) {
            Toast.makeText(this, "User data not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = etUsername.getText().toString();
        String fullName = etFullName.getText().toString();
        String email = etEmail.getText().toString();
        String studID = etStudID.getText().toString();
        String carModel = etModel.getText().toString();
        String plateNumber = etPlate.getText().toString();
        String license = etLicense.getText().toString();
        String role = rbDriver.isChecked() ? "driver" : "rider";

        Log.d("MyApp:", "Updating User info: " + user.toString());

        user.setUsername(username);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setStudID(studID);
        user.setCarModel(carModel);
        user.setPlateNumber(plateNumber);
        user.setLicense(license);
        user.setRole(role);

        Call<User> call = userService.updateUser(spm.getUser().getToken(), user.getId(), email,
                username, studID, carModel, plateNumber, license, fullName, role);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    User updatedUser = response.body();
                    if (updatedUser != null) {
                        // If the response doesn't include the token, keep the old one
                        if (updatedUser.getToken() == null) {
                            updatedUser.setToken(spm.getUser().getToken());
                        }
                        spm.storeUser(updatedUser);
                        displayUpdateSuccess(updatedUser.getUsername() + " updated successfully.");
                        clearSessionAndRedirect();
                    }
                } else if (response.code() == 401) {
                    // unauthorized error
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    // other error
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
                Log.d("MyApp:", "Error: " + (t.getCause() != null ? t.getCause().getMessage() : t.getMessage()));
            }
        });
    }

    public void displayUpdateSuccess(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // end this activity
                        finish();
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Displaying an alert dialog with a single button
     * @param message - message to be displayed
     */
    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showDriverFields(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        dividerCar.setVisibility(visibility);
        layoutCarDetails.setVisibility(visibility);
        tvLicenseLabel.setVisibility(visibility);
        etLicense.setVisibility(visibility);

        // Sub-elements of layoutCarDetails are handled by their parent visibility,
        // but we ensure labels are consistent
        tvModelLabel.setVisibility(visibility);
        tvPlateLabel.setVisibility(visibility);
    }
    private void setupBottomNavigation() {

        User currentUser = spm.getUser();

        if (currentUser == null) return;

        // ===========================
        // Show correct navigation
        // ===========================

        if ("driver".equalsIgnoreCase(currentUser.getRole())) {

            footerCard.setVisibility(View.GONE);
            dFooterCard.setVisibility(View.VISIBLE);

            findViewById(R.id.cardHome).setOnClickListener(v -> {
                startActivity(new Intent(this, DriverMainActivity.class));
                finish();
            });

            findViewById(R.id.cardUpdateRide).setOnClickListener(v -> {
                startActivity(new Intent(this, DriverRideListActivity.class));
                finish();
            });

            findViewById(R.id.cardCreateRide).setOnClickListener(v -> {
                startActivity(new Intent(this, CreateRideActivity.class));
                finish();
            });

            findViewById(R.id.cardProfile).setOnClickListener(v -> {
                // Current page
            });

        } else {

            dFooterCard.setVisibility(View.GONE);
            footerCard.setVisibility(View.VISIBLE);

            findViewById(R.id.cardSearchRide).setOnClickListener(v -> {
                startActivity(new Intent(this, RideListActivity.class));
                finish();
            });

            findViewById(R.id.cardBooking).setOnClickListener(v -> {
                startActivity(new Intent(this, BookingList.class));
                finish();
            });

            findViewById(R.id.cardProfile).setOnClickListener(v -> {
                // Current page
            });
        }
    }
}
