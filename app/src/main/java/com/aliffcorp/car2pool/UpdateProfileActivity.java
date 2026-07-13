package com.aliffcorp.car2pool;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etStudID;
    private EditText etModel;
    private EditText etPlate;
    private EditText etLicense;
    private TextView tvModelLabel;
    private TextView tvPlateLabel;
    private TextView tvLicenseLabel;
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
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etStudID = findViewById(R.id.etStudID);
        etModel = findViewById(R.id.etModel);
        etPlate = findViewById(R.id.etPlate);
        etLicense = findViewById(R.id.etLicense);

        tvModelLabel = findViewById(R.id.tvModelLabel);
        tvPlateLabel = findViewById(R.id.tvPlateLabel);
        tvLicenseLabel = findViewById(R.id.tvLicenseLabel);

        spm = new SharedPrefManager(getApplicationContext());
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
                            etUsername.setText(user.getUsername());
                            etEmail.setText(user.getEmail());
                            etPassword.setText(""); // Keep password empty for security
                            etStudID.setText(user.getStudID());
                            etModel.setText(user.getCarModel());
                            etPlate.setText(user.getPlateNumber());
                            etLicense.setText(user.getLicense());

                            // Handle visibility based on role
                            if ("driver".equalsIgnoreCase(user.getRole())) {
                                etModel.setVisibility(View.VISIBLE);
                                tvModelLabel.setVisibility(View.VISIBLE);
                                etPlate.setVisibility(View.VISIBLE);
                                tvPlateLabel.setVisibility(View.VISIBLE);
                                etLicense.setVisibility(View.VISIBLE);
                                tvLicenseLabel.setVisibility(View.VISIBLE);
                            } else {
                                etModel.setVisibility(View.GONE);
                                tvModelLabel.setVisibility(View.GONE);
                                etPlate.setVisibility(View.GONE);
                                tvPlateLabel.setVisibility(View.GONE);
                                etLicense.setVisibility(View.GONE);
                                tvLicenseLabel.setVisibility(View.GONE);
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
        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String studID = etStudID.getText().toString();
        String carModel = etModel.getText().toString();
        String plateNumber = etPlate.getText().toString();
        String license = etLicense.getText().toString();

        if (user == null) {
            Toast.makeText(this, "User data not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("MyApp:", "Updating User info: " + user.toString());

        Call<User> call = userService.updateUser(spm.getUser().getToken(), user.getId(), email, username, password,
                studID, carModel, plateNumber, license);

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
}
