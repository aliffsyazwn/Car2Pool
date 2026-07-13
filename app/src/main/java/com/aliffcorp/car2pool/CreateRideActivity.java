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
import android.app.DatePickerDialog;

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
    private int rideId = -1;
    private String token;
    private int userId;


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

        SharedPrefManager spm = new SharedPrefManager(this);
        User user = spm.getUser();
        if (user != null) {
            token = user.getToken();
            userId = user.getId();
        }

        rideId = getIntent().getIntExtra("ride_id", -1);
        if (rideId != -1) {
            btnCreate.setText("Update Ride");
            fetchRideDetails();
        }

        etDepTime.setOnClickListener(v -> showDateTimePicker());

        btnCancel.setOnClickListener(v -> finish());

        btnCreate.setOnClickListener(v -> createRide());

        fetchLocationsFromDatabase();
    }

    private void fetchRideDetails() {
        rideService.getRides(token, rideId).enqueue(new Callback<Ride>() {
            @Override
            public void onResponse(Call<Ride> call, Response<Ride> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Ride ride = response.body();
                    etOrigin.setText(ride.getOrigin());
                    etDestination.setText(ride.getDestination());
                    etDepTime.setText(ride.getDeparture_time());
                    etPrice.setText(String.valueOf(ride.getPrice()));
                    cbFSeat.setChecked(ride.getfSeat());
                    cbMSeat.setChecked(ride.getmSeat());
                    cbRSeat.setChecked(ride.getrSeat());
                    cbLSeat.setChecked(ride.getlSeat());
                }
            }

            @Override
            public void onFailure(Call<Ride> call, Throwable t) {
                Toast.makeText(CreateRideActivity.this, "Error loading ride info", Toast.LENGTH_SHORT).show();
            }
        });
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

    private void showDateTimePicker() {

        Calendar now = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {

                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);

                    TimePickerDialog timePicker = new TimePickerDialog(
                            CreateRideActivity.this,
                            (timeView, hourOfDay, minute) -> {

                                selected.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selected.set(Calendar.MINUTE, minute);
                                selected.set(Calendar.SECOND, 0);

                                // Prevent past date/time
                                if (selected.before(Calendar.getInstance())) {
                                    Toast.makeText(
                                            CreateRideActivity.this,
                                            "Please select a future date and time.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    return;
                                }

                                String dateTime = String.format(
                                        Locale.getDefault(),
                                        "%04d-%02d-%02d %02d:%02d:00",
                                        year,
                                        month + 1,
                                        dayOfMonth,
                                        hourOfDay,
                                        minute
                                );

                                etDepTime.setText(dateTime);

                            },
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            true
                    );

                    timePicker.show();

                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_MONTH, 1);   // Move to tomorrow
        minDate.set(Calendar.HOUR_OF_DAY, 0);
        minDate.set(Calendar.MINUTE, 0);
        minDate.set(Calendar.SECOND, 0);
        minDate.set(Calendar.MILLISECOND, 0);

        datePicker.getDatePicker().setMinDate(minDate.getTimeInMillis());

        datePicker.show();
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

        if (priceStr.isEmpty()) {
            etPrice.setError("Enter price");
            return;
        }

        double price = 0.0;
        try {
            price = Double.parseDouble(priceStr);
        }
        catch (NumberFormatException e) {
            etPrice.setError("Invalid price format");
            return;
        }

        int fSeat = cbFSeat.isChecked() ? 1 : 0;
        int rSeat = cbRSeat.isChecked() ? 1 : 0;
        int mSeat = cbMSeat.isChecked() ? 1 : 0;
        int lSeat = cbLSeat.isChecked() ? 1 : 0;

        if (fSeat == 1 && rSeat == 1 && mSeat == 1 && lSeat == 1) {
            Toast.makeText(CreateRideActivity.this, "At least one seat must be available", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rideId == -1) {
            rideService.createRide(
                    token,
                    userId,
                    origin,
                    destination,
                    departureTime,
                    (float) price,
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
}