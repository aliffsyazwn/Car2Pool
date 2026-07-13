package com.aliffcorp.car2pool;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
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
import com.aliffcorp.car2pool.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverRideDetailActivity extends AppCompatActivity {

    private RideService rideService;

    private TextView tvOrigin, tvDestination, tvTime, tvPrice;
    private Button btnBack;
    private CheckBox cbFSeat, cbRSeat, cbMSeat, cbLSeat;

    private int rideId;
    private String token;

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
        tvPrice = findViewById(R.id.tvPrice);

        cbFSeat = findViewById(R.id.cbFSeat);
        cbRSeat = findViewById(R.id.cbRSeat);
        cbMSeat = findViewById(R.id.cbMSeat);
        cbLSeat = findViewById(R.id.cbLSeat);

        btnBack = findViewById(R.id.btnBack);

        SharedPrefManager spm = new SharedPrefManager(this);

        if (!spm.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        User user = spm.getUser();
        token = user.getToken();
        rideId = getIntent().getIntExtra("ride_id", -1);

        rideService = ApiUtils.getRideService();

        btnBack.setOnClickListener(v -> finish());

    }
    @Override
    protected void onResume() {
        super.onResume();

        if (rideService != null && token != null && rideId != -1) {
            loadRideDetails();
        }
    }

    private void showAvailableSeat(CheckBox checkBox, boolean available) {
        checkBox.setClickable(false);
        checkBox.setFocusable(false);

        if (available) {
            checkBox.setChecked(true);
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setChecked(false);
            checkBox.setVisibility(View.INVISIBLE);
        }
    }

    private void loadRideDetails() {
        if (rideId == -1 || token == null) {
            return;
        }

        rideService.getRides(token, rideId).enqueue(new Callback<Ride>() {

            @Override
            public void onResponse(Call<Ride> call, Response<Ride> response) {

                if (response.isSuccessful() && response.body() != null) {

                    Ride ride = response.body();

                    tvOrigin.setText(ride.getOrigin());
                    tvDestination.setText(ride.getDestination());
                    tvTime.setText(ride.getDeparture_time());
                    tvPrice.setText(String.format("RM %.2f", ride.getPrice()));

                    showAvailableSeat(cbFSeat, ride.getfSeat());
                    showAvailableSeat(cbRSeat, ride.getrSeat());
                    showAvailableSeat(cbMSeat, ride.getmSeat());
                    showAvailableSeat(cbLSeat, ride.getlSeat());


                } else {
                    Toast.makeText(DriverRideDetailActivity.this,
                            "Unable to load ride.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Ride> call, Throwable t) {
                Toast.makeText(DriverRideDetailActivity.this,
                        "Connection error",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}