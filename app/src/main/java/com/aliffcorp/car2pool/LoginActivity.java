package com.aliffcorp.car2pool;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aliffcorp.car2pool.model.FailLogin;
import com.aliffcorp.car2pool.model.User;
import com.aliffcorp.car2pool.remote.ApiUtils;
import com.aliffcorp.car2pool.remote.UserService;
import com.aliffcorp.car2pool.sharedpref.SharedPrefManager;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top,
                    systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get references to form elements
        edtUsername = findViewById(R.id.etUserName);
        edtPassword = findViewById(R.id.etPassword);
    }

    /**
     * Login button action handler
     */
    public void loginClicked(View view) {

        // Get username/email and password
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Validate form
        if (validateLogin(username, password)) {

            // Login using REST API
            doLogin(username, password);

        }

    }

    /**
     * Call REST API to login
     */
    private void doLogin(String username, String password) {

        // Get UserService instance
        UserService userService = ApiUtils.getUserService();

        Call<User> call;

        if (username.contains("@")) {
            call = userService.loginEmail(username, password);
        } else {
            call = userService.login(username, password);
        }

        // Execute REST API
        call.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful()) {

                    User user = response.body();

                    if (user != null && user.getToken() != null) {

                        displayToast("Login Successful");

                        // Save user
                        SharedPrefManager spm =
                                new SharedPrefManager(getApplicationContext());

                        spm.storeUser(user);

                        // Go to MainActivity
                        finish();

                        Intent intent =
                                new Intent(LoginActivity.this,
                                        MainActivity.class);

                        startActivity(intent);

                    } else {

                        displayToast("Login Error");

                    }

                } else {

                    try {

                        String errorResp = response.errorBody().string();

                        FailLogin e =
                                new Gson().fromJson(errorResp,
                                        FailLogin.class);

                        displayToast(e.getError().getMessage());

                    } catch (Exception e) {

                        Log.e("MyApp", e.toString());

                        displayToast("Error");

                    }

                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                displayToast("Error connecting to server.");
                displayToast(t.getMessage());

                Log.e("MyApp", t.toString());

            }
        });
    }

    /**
     * Validate login
     */
    private boolean validateLogin(String username, String password) {

        if (username == null || username.trim().isEmpty()) {

            edtUsername.setError("Email is required");
            edtUsername.requestFocus();

            return false;
        }

        if (password == null || password.trim().isEmpty()) {

            edtPassword.setError("Password is required");
            edtPassword.requestFocus();

            return false;
        }

        return true;
    }

    /**
     * Display Toast
     */
    public void displayToast(String message) {

        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG).show();

    }
}