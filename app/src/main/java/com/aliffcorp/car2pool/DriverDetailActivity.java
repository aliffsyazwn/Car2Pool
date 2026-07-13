package com.aliffcorp.car2pool;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aliffcorp.car2pool.model.User;
import com.aliffcorp.car2pool.remote.ApiUtils;
import com.aliffcorp.car2pool.remote.UserService;
import com.aliffcorp.car2pool.sharedpref.SharedPrefManager;

public class DriverDetailActivity extends AppCompatActivity {

    private UserService userService;
    private User user;
    private String token;

    TextView tvDriver;
    TextView tvStudID;
    TextView tvModel;
    TextView tvPlate;
    TextView tvLicense;

    private Button btnBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        int userID = intent.getIntExtra("user_id", -1);

        tvDriver = findViewById(R.id.tvDriver);
        tvStudID = findViewById(R.id.tvStudID);
        tvModel = findViewById(R.id.tvModel);
        tvPlate = findViewById(R.id.tvPlate);
        tvLicense = findViewById(R.id.tvLicense);

        btnBack = findViewById(R.id.btnBack);

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        user = spm.getUser();
        token = user.getToken();
        userService = ApiUtils.getUserService();

        if (userID != -1) {
            userService.getUser(token, userID).enqueue(new retrofit2.Callback<User>() {
                @Override
                public void onResponse(retrofit2.Call<User> call, retrofit2.Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User driver = response.body();
                        tvDriver.setText(StringUtils.capitalize(driver.getUsername()));
                        tvStudID.setText(driver.getStudID());
                        tvModel.setText(driver.getCarModel());
                        tvPlate.setText(driver.getPlateNumber());
                        tvLicense.setText(driver.getLicense());
                    } else {
                        tvDriver.setText("Error loading driver");
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<User> call, Throwable t) {
                    tvDriver.setText("Network error");
                }
            });
        }

        btnBack.setOnClickListener(v -> finish());
    }
}