package com.aliffcorp.car2pool;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aliffcorp.car2pool.model.Booking;
import com.aliffcorp.car2pool.model.Ride;
import com.aliffcorp.car2pool.model.User;
import com.aliffcorp.car2pool.remote.ApiUtils;
import com.aliffcorp.car2pool.remote.BookingService;
import com.aliffcorp.car2pool.remote.RideService;
import com.aliffcorp.car2pool.remote.UserService;
import com.aliffcorp.car2pool.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideDetailActivity extends AppCompatActivity {
    private RideService rideService;
    private UserService userService;
    private BookingService bookingService;
    private Ride ride;
    private User user;
    private String token;
    TextView tvOrigin;
    TextView tvDestination;
    TextView tvDriver;
    TextView tvTime;
    CheckBox cbFSeat;
    CheckBox cbRSeat;
    CheckBox cbMSeat;
    CheckBox cbLSeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ride_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        int rideId = intent.getIntExtra("ride_id", -1);

        // get references to the view elements
        tvOrigin = findViewById(R.id.tvOrigin);
        tvDestination = findViewById(R.id.tvDestination);
        tvDriver = findViewById(R.id.tvDriver);
        tvTime = findViewById(R.id.tvTime);
        cbFSeat = findViewById(R.id.cbFSeat);
        cbRSeat = findViewById(R.id.cbRSeat);
        cbMSeat = findViewById(R.id.cbMSeat);
        cbLSeat = findViewById(R.id.cbLSeat);

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        user = spm.getUser();
        token = user.getToken();

        // get ride service instance
        rideService = ApiUtils.getRideService();
        userService = ApiUtils.getUserService();
        bookingService = ApiUtils.getBookingService();

        rideService.getRides(token, rideId).enqueue(new Callback<Ride>() {
            @Override
            public void onResponse(Call<Ride> call, Response<Ride> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // server return success
                    // get ride object from response
                    ride = response.body();
                    // set values
                    tvOrigin.setText(ride.getOrigin());
                    tvTime.setText(ride.getDeparture_time());
                    tvDestination.setText(ride.getDestination());
                    cbFSeat.setChecked(ride.getfSeat());
                    cbRSeat.setChecked(ride.getrSeat());
                    cbMSeat.setChecked(ride.getmSeat());
                    cbLSeat.setChecked(ride.getlSeat());

                    setupSeatCheckBox(cbFSeat);
                    setupSeatCheckBox(cbRSeat);
                    setupSeatCheckBox(cbMSeat);
                    setupSeatCheckBox(cbLSeat);

                    // fetch driver username
                    userService.getUser(token, ride.getDriver_id()).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                tvDriver.setText(response.body().getUsername());
                            } else {
                                tvDriver.setText("Unknown Driver (" + ride.getDriver_id() + ")");
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            tvDriver.setText("Error loading driver (" + ride.getDriver_id() + ")");
                        }
                    });
                } else if (response.code() == 401) {
                    // unauthorized error. invalid token, ask user to relogin
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    // server return other error
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Ride> call, Throwable t) {
                Toast.makeText(RideDetailActivity.this, "Error connecting", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setupSeatCheckBox(CheckBox cb) {
        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                // once checked, cannot uncheck
                buttonView.setChecked(true);
                Toast.makeText(RideDetailActivity.this, "Cannot uncheck seat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clearSessionAndRedirect() {
        // clear the shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // terminate this activity
        finish();

        // forward to Login Page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void bookRide(View view) {
        if (ride == null) {
            Toast.makeText(this, "Ride info not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }

        int user_id = user.getId();
        int ride_id = ride.getRide_id();
        String origin = tvOrigin.getText().toString();
        String destination = tvDestination.getText().toString();
        String time = tvTime.getText().toString();
        CheckBox fSeat = cbFSeat.isChecked();
        CheckBox rSeat = cbRSeat.isChecked();
        CheckBox lSeat = cbLSeat.isChecked();
        CheckBox mSeat = cbMSeat.isChecked();

        //update seat fields
        ride.setfSeat();








        Booking booking = new Booking();
        booking.setUser_id(user.getId());
        booking.setRide_id(ride.getRide_id());

        bookingService.createBooking(token, booking).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RideDetailActivity.this, "Booking Successful!", Toast.LENGTH_LONG).show();
                    // Redirect to Booking List
                    Intent intent = new Intent(RideDetailActivity.this, BookingList.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RideDetailActivity.this, "Booking Failed: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Toast.makeText(RideDetailActivity.this, "Error connecting to server", Toast.LENGTH_LONG).show();
            }
        });
    }
}
