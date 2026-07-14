package com.aliffcorp.car2pool;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.widget.Button;

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
    private Button btnBookNow;

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
        btnBookNow = findViewById(R.id.btnBookNow);

        // Check Login
        // Check Login
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());

        if (!spm.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

// Get logged in user
        User user = spm.getUser();

// If driver accidentally opens MainActivity, redirect
        if (user != null &&
                user.getRole() != null &&
                user.getRole().equalsIgnoreCase("driver")) {

            Intent intent = new Intent(
                    MainActivity.this,
                    DriverMainActivity.class);

            startActivity(intent);
            finish();
            return;
        }

// Display username
        tvHello.setText("Welcome back, " + user.getUsername() + "!");
        ObjectAnimator scaleX =
                ObjectAnimator.ofFloat(btnBookNow, "scaleX", 1f, 1.08f);

        ObjectAnimator scaleY =
                ObjectAnimator.ofFloat(btnBookNow, "scaleY", 1f, 1.08f);

        scaleX.setDuration(700);
        scaleY.setDuration(700);

        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);

        scaleX.setRepeatMode(ValueAnimator.REVERSE);
        scaleY.setRepeatMode(ValueAnimator.REVERSE);

        scaleX.start();
        scaleY.start();

        // ===========================
        // Bottom Navigation
        // ===========================
        // Search Ride
        cardSearchRide.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RideListActivity.class);
            startActivity(intent);
        });
        btnBookNow.setOnClickListener(v -> {
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