package com.aliffcorp.car2pool;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.aliffcorp.car2pool.model.User;
import com.aliffcorp.car2pool.StringUtils;
import com.aliffcorp.car2pool.sharedpref.SharedPrefManager;

public class ViewProfileActivity extends AppCompatActivity {

    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvRole;

    private Button btnUpdateProfile;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);

        // Initialize Views
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvRole = findViewById(R.id.tvRole);

        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnLogout = findViewById(R.id.btnLogout);

        // Check Login
        SharedPrefManager spm = new SharedPrefManager(this);

        if (!spm.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Display user information
        User user = spm.getUser();

        tvUsername.setText(StringUtils.capitalize(user.getUsername()));
        tvEmail.setText(user.getEmail());
        tvRole.setText(StringUtils.capitalize(user.getRole()));

        // Update Profile
        btnUpdateProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ViewProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
        });

        // Logout
        btnLogout.setOnClickListener(v -> {
            spm.logout();

            Intent intent = new Intent(ViewProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}