package com.aliffcorp.car2pool;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aliffcorp.car2pool.model.User;
import com.aliffcorp.car2pool.sharedpref.SharedPrefManager;

public class MainActivity extends AppCompatActivity {

    private TextView tvHello;
    private CardView cardSearchRide;
    private CardView cardBooking;
    private CardView cardProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        tvHello = findViewById(R.id.tvHello);

        cardSearchRide = findViewById(R.id.cardSearchRide);
        cardBooking = findViewById(R.id.cardBooking);
        cardProfile = findViewById(R.id.cardProfile);

        // Check Login
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());

        if (!spm.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Display username
        User user = spm.getUser();
        tvHello.setText("Welcome back, " + user.getUsername() + "!");

        // ===========================
        // Bottom Navigation
        // ===========================

        // Search Ride
        cardSearchRide.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RideListActivity.class);
            startActivity(intent);
        });


        // My Booking
        cardBooking.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BookingList.class);
            startActivity(intent);
        });

        cardProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewProfileActivity.class);
            startActivity(intent);
        });

    }
}