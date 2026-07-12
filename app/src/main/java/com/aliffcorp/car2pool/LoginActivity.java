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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtUsername = findViewById(R.id.etUserName);
        edtPassword = findViewById(R.id.etPassword);
    }

    public void loginClicked(View view) {

        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (validateLogin(username, password)) {
            doLogin(username, password);
        }
    }

    private void doLogin(String username, String password) {

        UserService userService = ApiUtils.getUserService();
        Call<User> call;

        if (username.contains("@")) {
            call = userService.loginEmail(username, password);
        } else {
            call = userService.login(username, password);
        }

        call.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful()) {

                    User user = response.body();

                    if (user != null && user.getToken() != null) {

                        SharedPrefManager spm =
                                new SharedPrefManager(getApplicationContext());

                        spm.storeUser(user);

                        // Debug
                        Log.d("LOGIN", "ID = " + user.getId());
                        Log.d("LOGIN", "Username = " + user.getUsername());
                        Log.d("LOGIN", "Role = " + user.getRole());

                        Toast.makeText(
                                LoginActivity.this,
                                "Login Successful",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent;

                        if (user.getRole() != null &&
                                user.getRole().equalsIgnoreCase("driver")) {

                            Log.d("LOGIN", "Open DriverMainActivity");

                            intent = new Intent(
                                    LoginActivity.this,
                                    DriverMainActivity.class
                            );

                        } else {

                            Log.d("LOGIN", "Open MainActivity");

                            intent = new Intent(
                                    LoginActivity.this,
                                    MainActivity.class
                            );
                        }

                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(
                                LoginActivity.this,
                                "Login Failed",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                } else {

                    try {

                        String errorResp = response.errorBody().string();

                        FailLogin e =
                                new Gson().fromJson(errorResp, FailLogin.class);

                        Toast.makeText(
                                LoginActivity.this,
                                e.getError().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    } catch (Exception e) {

                        Log.e("LOGIN", e.toString());

                        Toast.makeText(
                                LoginActivity.this,
                                "Error",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                Toast.makeText(
                        LoginActivity.this,
                        "Error connecting to server",
                        Toast.LENGTH_LONG
                ).show();

                Log.e("LOGIN", t.toString());
            }
        });
    }

    private boolean validateLogin(String username, String password) {

        if (username.isEmpty()) {
            edtUsername.setError("Username or Email is required");
            edtUsername.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Password is required");
            edtPassword.requestFocus();
            return false;
        }

        return true;
    }

    public void newRiderClicked(View view) {

        Intent intent = new Intent(
                LoginActivity.this,
                NewRiderActivity.class
        );

        startActivity(intent);
    }
}