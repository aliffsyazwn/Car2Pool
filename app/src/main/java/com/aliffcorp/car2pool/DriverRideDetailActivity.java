package com.aliffcorp.car2pool;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aliffcorp.car2pool.model.Ride;
import com.aliffcorp.car2pool.model.User;
import com.aliffcorp.car2pool.remote.ApiUtils;
import com.aliffcorp.car2pool.remote.RideService;
import com.aliffcorp.car2pool.remote.UserService;
import com.aliffcorp.car2pool.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverRideDetailActivity extends AppCompatActivity {

    private RideService rideService;
    private UserService userService;

    private TextView tvOrigin, tvDestination, tvTime, tvSeats, tvDriver;
    private Button btnBack, btnEditRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_ride_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top,
                    systemBars.right, systemBars.bottom);
            return insets;
        });

        tvOrigin = findViewById(R.id.tvOrigin);
        tvDestination = findViewById(R.id.tvDestination);
        tvTime = findViewById(R.id.tvTime);
        tvSeats = findViewById(R.id.tvSeats);
        tvDriver = findViewById(R.id.tvDriver);

        btnBack = findViewById(R.id.btnBack);
        btnEditRide = findViewById(R.id.btnEditRide);

        SharedPrefManager spm = new SharedPrefManager(this);

        if (!spm.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        User user = spm.getUser();
        String token = user.getToken();

        int rideId = getIntent().getIntExtra("ride_id", -1);

        rideService = ApiUtils.getRideService();
        userService = ApiUtils.getUserService();

        rideService.getRides(token, rideId).enqueue(new Callback<Ride>() {
            @Override
            public void onResponse(Call<Ride> call, Response<Ride> response) {

                if (response.isSuccessful() && response.body() != null) {

                    Ride ride = response.body();

                    tvOrigin.setText(ride.getOrigin());
                    tvDestination.setText(ride.getDestination());
                    tvTime.setText(ride.getDeparture_time());

                    userService.getUser(token, ride.getDriver_id())
                            .enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {

                                    if (response.isSuccessful() && response.body() != null) {
                                        tvDriver.setText(response.body().getUsername());
                                    }
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    tvDriver.setText("Unknown");
                                }
                            });

                } else {
                    Toast.makeText(DriverRideDetailActivity.this,
                            "Unable to load ride.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Ride> call, Throwable t) {
                Toast.makeText(DriverRideDetailActivity.this,
                        "Connection Error",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());

      /*  btnEditRide.setOnClickListener(v -> {
            Intent intent = new Intent(
                    DriverRideDetailActivity.this,
                    UpdateRideActivity.class
            );

            intent.putExtra("ride_id", rideId);
            startActivity(intent);
        });*/
    }
}