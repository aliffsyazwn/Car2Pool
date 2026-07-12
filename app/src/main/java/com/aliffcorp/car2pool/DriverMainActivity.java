package com.aliffcorp.car2pool;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aliffcorp.car2pool.model.User;
import com.aliffcorp.car2pool.sharedpref.SharedPrefManager;

public class DriverMainActivity extends AppCompatActivity {

    private TextView tvHello;

    private CardView cardProfile;
    private CardView cardCreateRide;
    private CardView cardUpdateRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top,
                    systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get logged in user
        SharedPrefManager spm = new SharedPrefManager(this);
        User user = spm.getUser();

        // Connect XML
        tvHello = findViewById(R.id.tvHello);

        cardProfile = findViewById(R.id.cardProfile);
        cardCreateRide = findViewById(R.id.cardCreateRide);
        cardUpdateRide = findViewById(R.id.cardUpdateRide);

        // Display username
        tvHello.setText("Hello, " + user.getUsername());

        // ==========================
        // Profile
        // ==========================
        cardProfile.setOnClickListener(v -> {
            Intent intent = new Intent(
                    DriverMainActivity.this,
                    ViewProfileActivity.class
            );
            startActivity(intent);
        });

        // ==========================
        // Create Ride
        // ==========================
        cardCreateRide.setOnClickListener(v -> {
            Intent intent = new Intent(
                    DriverMainActivity.this,
                    CreateRideActivity.class
            );
            startActivity(intent);
        });

        // ==========================
        // Update Ride
        // ==========================
       /* cardUpdateRide.setOnClickListener(v -> {
            Intent intent = new Intent(
                    DriverMainActivity.this,
                    UpdateRideActivity.class
            );
            startActivity(intent);
        });*/
    }
}