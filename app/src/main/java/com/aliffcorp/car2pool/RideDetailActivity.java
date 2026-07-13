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
import com.aliffcorp.car2pool.StringUtils;
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
        int bookId = intent.getIntExtra("book_id", -1);

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

        if (bookId != -1) {
            findViewById(R.id.btnBook).setVisibility(View.GONE);
            fetchRideByBookingId(bookId);
        } else if (rideId != -1) {
            fetchRideDetails(rideId);
        }
    }

    private void fetchRideByBookingId(int bookId) {
        bookingService.getBooking(token, bookId).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Booking booking = response.body();
                    ride = booking.getRide();
                    if (ride != null) {
                        displayRideInfo(ride);
                    } else {
                        Toast.makeText(RideDetailActivity.this, "Ride info not found in booking", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RideDetailActivity.this, "Error loading booking", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Toast.makeText(RideDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchRideDetails(int rideId) {
        rideService.getRides(token, rideId).enqueue(new Callback<Ride>() {
            @Override
            public void onResponse(Call<Ride> call, Response<Ride> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // server return success
                    // get ride object from response
                    ride = response.body();
                    displayRideInfo(ride);
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
                Toast.makeText(RideDetailActivity.this, "Error connecting", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayRideInfo(Ride ride) {
        if (ride == null) {
            Toast.makeText(this, "Ride data is missing", Toast.LENGTH_SHORT).show();
            return;
        }
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
                    tvDriver.setText(StringUtils.capitalize(response.body().getUsername()));
                } else {
                    tvDriver.setText("Unknown Driver (" + ride.getDriver_id() + ")");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                tvDriver.setText("Error loading driver (" + ride.getDriver_id() + ")");
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

        //update seat based on form content
        int driver_id = ride.getDriver_id();
        String origin = tvOrigin.getText().toString();
        String destination = tvDestination.getText().toString();
        String departure_time = tvTime.getText().toString();
        int fSeat = cbFSeat.isChecked() ? 1 : 0;
        int rSeat = cbRSeat.isChecked() ? 1 : 0;
        int mSeat = cbMSeat.isChecked() ? 1 : 0;
        int lSeat = cbLSeat.isChecked() ? 1 : 0;

        // update all fields excluding the ride_id
        ride.setDriver_id(driver_id);
        ride.setOrigin(origin);
        ride.setDestination(destination);
        ride.setDeparture_time(departure_time);
        ride.setfSeat(fSeat);
        ride.setrSeat(rSeat);
        ride.setmSeat(mSeat);
        ride.setlSeat(lSeat);

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        bookingService.addBooking(user.getToken(), user.getId(), ride.getRide_id()).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                // for debug purpose
                Log.d("MyApp:", "Booking Response: " + response.raw().toString());

                if (response.code() == 201) {
                    // book added successfully, now update ride seat
                    rideService.updateSeat(user.getToken(), ride.getRide_id(),
                            ride.getDriver_id(), ride.getOrigin(), ride.getDestination(), ride.getDeparture_time(),
                            fSeat, rSeat, mSeat, lSeat).enqueue(new Callback<Ride>() {
                        @Override
                        public void onResponse(Call<Ride> callRide, Response<Ride> responseRide) {
                            Log.d("MyApp:", "Ride Update Response: " + responseRide.raw().toString());
                            if (responseRide.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Booking added successfully.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Booking added, but failed to update seats.", Toast.LENGTH_LONG).show();
                            }
                            finish();
                            Intent intent = new Intent(RideDetailActivity.this, BookingList.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<Ride> callRide, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Booking added, but network error updating seats.", Toast.LENGTH_LONG).show();
                            finish();
                            Intent intent = new Intent(RideDetailActivity.this, BookingList.class);
                            startActivity(intent);
                        }
                    });
                }
                else if (response.code() == 401) {
                    // invalid token, ask user to relogin
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    // server return other error
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Error [" + t.getMessage() + "]",
                        Toast.LENGTH_LONG).show();
                // for debug purpose
                Log.e("MyApp:", "Error: " + t.getMessage(), t);
            }
        });


    }
}
