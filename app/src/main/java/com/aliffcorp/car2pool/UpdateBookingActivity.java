package com.aliffcorp.car2pool;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aliffcorp.car2pool.model.Booking;
import com.aliffcorp.car2pool.model.User;
import com.aliffcorp.car2pool.remote.ApiUtils;
import com.aliffcorp.car2pool.remote.BookingService;
import com.aliffcorp.car2pool.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateBookingActivity extends AppCompatActivity {

    private TextView tvCurrentBookingInfo;
    private EditText etEditField;
    private Button btnSaveUpdate;

    private BookingService bookingService;
    private String token;
    private int bookingId;
    private Booking currentBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_booking);

        tvCurrentBookingInfo = findViewById(R.id.tvCurrentBookingInfo);
        etEditField = findViewById(R.id.etEditField);
        btnSaveUpdate = findViewById(R.id.btnSaveUpdate);

        //Get Token
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        if (user != null) {
            token = user.getToken();
        }

        // Get the Booking ID passed from BookingList
        bookingId = getIntent().getIntExtra("booking_id", -1);

        bookingService = ApiUtils.getBookingService();

        // Load existing data
        if (bookingId != -1) {
            loadBookingDetails();
        } else {
            Toast.makeText(this, "Error: Booking ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Handle Save Button
        btnSaveUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBookingUpdate();
            }
        });
    }

    private void loadBookingDetails() {
        bookingService.getBooking(token, bookingId).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentBooking = response.body();
                    tvCurrentBookingInfo.setText("Booking ID: " + currentBooking.getBooking_id());
                } else {
                    Toast.makeText(UpdateBookingActivity.this, "Failed to load information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Toast.makeText(UpdateBookingActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveBookingUpdate() {
        String newValue = etEditField.getText().toString().trim();

        if (newValue.isEmpty()) {
            etEditField.setError("This field cannot be empty");
            return;
        }

        bookingService.updateBooking(token, bookingId, currentBooking).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateBookingActivity.this, "Successfully updated!", Toast.LENGTH_SHORT).show();
                    finish(); // Closes this screen and goes back to the list
                } else {
                    Toast.makeText(UpdateBookingActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                    Log.e("UpdateBooking", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Toast.makeText(UpdateBookingActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}