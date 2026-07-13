package com.aliffcorp.car2pool;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aliffcorp.car2pool.model.LocationItem;
import com.aliffcorp.car2pool.model.Ride;
import com.aliffcorp.car2pool.model.User;
import com.aliffcorp.car2pool.remote.ApiUtils;
import com.aliffcorp.car2pool.remote.RideService;
import com.aliffcorp.car2pool.sharedpref.SharedPrefManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateRideActivity extends AppCompatActivity {

    private AutoCompleteTextView etOrigin;
    private AutoCompleteTextView etDestination;
    private EditText etDepTime;
    private EditText etPrice;

    private CheckBox cbFSeat;
    private CheckBox cbRSeat;
    private CheckBox cbMSeat;
    private CheckBox cbLSeat;

    private Button btnCreate;
    private Button btnCancel;

    private RideService rideService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_ride);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top,
                    systemBars.right, systemBars.bottom);
            return insets;
        });

        rideService = ApiUtils.getRideService();

        etOrigin = findViewById(R.id.etOrigin);
        etDestination = findViewById(R.id.etDestination);
        etDepTime = findViewById(R.id.etDepTime);
        etPrice = findViewById(R.id.etPrice);

        cbFSeat = findViewById(R.id.cbFSeat);
        cbRSeat = findViewById(R.id.cbRSeat);
        cbMSeat = findViewById(R.id.cbMSeat);
        cbLSeat = findViewById(R.id.cbLSeat);

        btnCreate = findViewById(R.id.btnCreate);
        btnCancel = findViewById(R.id.btnCancel);

        etDepTime.setOnClickListener(v -> showTimePicker());

        btnCancel.setOnClickListener(v -> finish());

        btnCreate.setOnClickListener(v -> createRide());

        fetchLocationsFromDatabase();
    }

    private void fetchLocationsFromDatabase() {
        SharedPrefManager spm = new SharedPrefManager(this);
        User user = spm.getUser();
        if (user == null || user.getToken() == null) return;

        rideService.getAllLocations(user.getToken()).enqueue(new Callback<List<LocationItem>>() {
            @Override
            public void onResponse(Call<List<LocationItem>> call, Response<List<LocationItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LocationItem> locationItems = response.body();
                    List<String> locationNames = new ArrayList<>();

                    for (LocationItem item : locationItems) {
                        locationNames.add(item.getLocationName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            CreateRideActivity.this,
                            android.R.layout.simple_dropdown_item_1line,
                            locationNames
                    );

                    // Set the adapter to both origin and destination fields
                    etOrigin.setAdapter(adapter);
                    etDestination.setAdapter(adapter);
                } else {
                    Toast.makeText(CreateRideActivity.this, "Failed to load locations", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LocationItem>> call, Throwable t) {
                Log.e("CreateRide", "Error fetching locations", t);
            }
        });
    }

    private void showTimePicker() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute1) -> {

                    String dateTime = String.format(
                            Locale.getDefault(),
                            "%04d-%02d-%02d %02d:%02d:00",
                            year,
                            month + 1,
                            day,
                            hourOfDay,
                            minute1
                    );

                    etDepTime.setText(dateTime);

                },
                hour,
                minute,
                true
        );

        dialog.show();
    }

    private void createRide() {

        String origin = etOrigin.getText().toString().trim();
        String destination = etDestination.getText().toString().trim();
        String departureTime = etDepTime.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (origin.isEmpty()) {
            etOrigin.setError("Enter origin");
            return;
        }

        if (destination.isEmpty()) {
            etDestination.setError("Enter destination");
            return;
        }

        if (departureTime.isEmpty()) {
            etDepTime.setError("Select departure time");
            return;
        }

        // --- NEW: Price Validation ---
        if (priceStr.isEmpty()) {
            etPrice.setError("Enter price");
            return;
        }

        double price = 0.0;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            etPrice.setError("Invalid price format");
            return;
        }
        // -----------------------------

        int fSeat = cbFSeat.isChecked() ? 1 : 0;
        int rSeat = cbRSeat.isChecked() ? 1 : 0;
        int mSeat = cbMSeat.isChecked() ? 1 : 0;
        int lSeat = cbLSeat.isChecked() ? 1 : 0;

        if (fSeat + rSeat + mSeat + lSeat == 0) {
            Toast.makeText(this,
                    "Please select at least one seat",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPrefManager spm = new SharedPrefManager(this);
        User user = spm.getUser();

        rideService.createRide(
                user.getToken(),
                user.getId(),
                origin,
                destination,
                departureTime,
                price,
                fSeat,
                rSeat,
                mSeat,
                lSeat
        ).enqueue(new Callback<Ride>() {

            @Override
            public void onResponse(Call<Ride> call, Response<Ride> response) {

                if (response.isSuccessful() && response.body() != null) {

                    Ride ride = response.body();

                    Toast.makeText(CreateRideActivity.this,
                            "Ride Created Successfully",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(
                            CreateRideActivity.this,
                            DriverRideDetailActivity.class
                    );

                    intent.putExtra("ride_id", ride.getRide_id());

                    startActivity(intent);
                    finish();

                } else {

                    Toast.makeText(CreateRideActivity.this,
                            "Failed to create ride",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Ride> call, Throwable t) {

                Toast.makeText(CreateRideActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}