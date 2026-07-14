package com.aliffcorp.car2pool;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.aliffcorp.car2pool.model.Ride;
import com.aliffcorp.car2pool.remote.ApiUtils;
import com.aliffcorp.car2pool.remote.RideService;
import com.aliffcorp.car2pool.model.Booking;
import com.aliffcorp.car2pool.remote.BookingService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private TextView tvTotalRide;

    private RideService rideService;
    private BookingService bookingService;
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

        // Shared Preference
        SharedPrefManager spm = new SharedPrefManager(this);

        // Check login
        if (!spm.isLoggedIn()) {
            Intent intent = new Intent(DriverMainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Get logged in user
        User user = spm.getUser();

        // Only allow drivers
        if (user == null || user.getRole() == null ||
                !user.getRole().equalsIgnoreCase("driver")) {

            Intent intent = new Intent(DriverMainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize Views
        tvHello = findViewById(R.id.tvHello);
        tvTotalRide = findViewById(R.id.tvTotalRide);
        bookingService = ApiUtils.getBookingService();
        rideService = ApiUtils.getRideService();

        cardProfile = findViewById(R.id.cardProfile);
        cardCreateRide = findViewById(R.id.cardCreateRide);
        cardUpdateRide = findViewById(R.id.cardUpdateRide);

        // Welcome message
        tvHello.setText("Welcome back, " + user.getUsername() + "!");
        loadDashboard(user);

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
       cardUpdateRide.setOnClickListener(v -> {
            Intent intent = new Intent(
                    DriverMainActivity.this,
                    DriverRideListActivity.class
            );
            startActivity(intent);
        });
    }
    private void loadDashboard(User user) {

        rideService.getAllRides(user.getToken()).enqueue(new Callback<List<Ride>>() {

            @Override
            public void onResponse(Call<List<Ride>> call, Response<List<Ride>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    int todayRide = 0;

                    String today =
                            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    .format(new Date());

                    for (Ride ride : response.body()) {

                        if (ride.getDriver_id() != user.getId())
                            continue;

                        if (ride.getDeparture_time().startsWith(today)) {
                            todayRide++;
                        }
                    }

                    tvTotalRide.setText(String.valueOf(todayRide));

                } else {
                    tvTotalRide.setText("0");
                }
            }

            @Override
            public void onFailure(Call<List<Ride>> call, Throwable t) {
                tvTotalRide.setText("0");
            }
        });
    }

}