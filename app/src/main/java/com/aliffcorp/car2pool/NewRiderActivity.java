package com.aliffcorp.car2pool;

import android.content.Intent;
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
import com.aliffcorp.car2pool.sharedpref.SharedPrefManager;
import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewRiderActivity extends AppCompatActivity {

    private EditText txtEmail;
    private EditText txtUsername;
    private EditText txtPassword;
    private EditText txtID;

    private Button btnNewRider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_rider);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get view objects references
        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtID = findViewById(R.id.txtID);
        btnNewRider = findViewById(R.id.btnNewRider);

        btnNewRider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewRider(v);
            }
        });
    }


    /**
     * Called when Add User button is clicked
     * @param view
     */
    public void addNewRider(View view) {
        String api_key = "3e9d870b-3335-4b02-bd4b-e0ea3263d173";
        String username = txtUsername.getText().toString();
        String email = txtEmail.getText().toString();
        String password = md5(txtPassword.getText().toString());
        String token = "00000000-00000-0000-0000-000000000000";
        String lease = "1970-01-01 00:00:00";
        String role = "rider";
        int is_active =  1 ;
        String secret = "206b2dbe-ecc9-490b-b81b-83767288bc5e";
        String studId = txtID.getText().toString();

        if (validateRegister(username, password, email, studId)) {
            doNewRider(api_key, email, username, password, token, lease, role, is_active, secret, studId);
        }
    }

    private void doNewRider(String api_key, String email, String username, String password, String token,
    String lease, String role, int is_active, String secret ,String studId) {
        UserService userService = ApiUtils.getUserService();
        Call<User> call;

        call = userService.addRider(api_key, email, username, password, token, lease, role, is_active, secret, studId);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();

                    if (user != null && user.getToken() != null) {
                        displayToast("Register successful");

                        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
                        spm.storeUser(user);

                        finish();
                        Intent intent = new Intent(NewRiderActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        displayToast("Register error");
                    }
                } else {
                    String errorResp;
                    try {
                        errorResp = response.errorBody().string();
                        FailLogin e = new Gson().fromJson(errorResp, FailLogin.class);
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


private boolean validateRegister(String username, String password, String email, String studId) {
    if (username == null || username.trim().isEmpty()) {
        txtUsername.setError("Username or Email is required");
        txtUsername.requestFocus();
        return false;
    }

    if (password == null || password.trim().isEmpty()) {
        txtPassword.setError("Password is required");
        txtPassword.requestFocus();
        return false;
    }

    if (email == null || email.trim().isEmpty() || !email.contains("@")) {
        txtEmail.setError("Enter the correct email");
        txtEmail.requestFocus();
        return false;
    }

    if (studId == null || studId.trim().isEmpty()) {
        txtID.setError("Student ID is required");
        txtID.requestFocus();
        return false;
    }
    return true;
}

    public void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String h = Integer.toHexString(0xFF & b);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}