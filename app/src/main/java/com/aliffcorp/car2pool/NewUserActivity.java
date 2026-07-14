package com.aliffcorp.car2pool;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;

public class NewUserActivity extends AppCompatActivity {

    private EditText txtEmail, txtPassword, txtConfirmPassword;
    private Button btnRegister;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_user);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        userService = ApiUtils.getUserService();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();
                String confirmPassword = txtConfirmPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(NewUserActivity.this, "Email and Password are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(NewUserActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                register(email, password);
            }
        });

        findViewById(R.id.tvBackToLogin).setOnClickListener(v -> finish());
    }

    private void register(String email, String password) {
        userService.registerUser(email, password).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(NewUserActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to login
                } else {
                    try (ResponseBody errorBody = response.errorBody()) {
                        if (errorBody != null) {
                            String errorJson = errorBody.string();
                            Log.e("NewUserActivity", "Error Body: " + errorJson);
                            
                            try {
                                FailLogin failResponse = new Gson().fromJson(errorJson, FailLogin.class);
                                if (failResponse != null && failResponse.getError() != null) {
                                    Toast.makeText(NewUserActivity.this, failResponse.getError().getMessage(), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(NewUserActivity.this, "Error: " + errorJson, Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(NewUserActivity.this, "Error: " + errorJson, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(NewUserActivity.this, "Error: " + response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("NewUserActivity", "Error reading error body", e);
                        Toast.makeText(NewUserActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("NewUserActivity", "Error: " + t.getMessage());
                Toast.makeText(NewUserActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}