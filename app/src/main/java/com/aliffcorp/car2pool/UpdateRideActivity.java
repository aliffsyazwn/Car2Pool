package com.aliffcorp.car2pool;

import android.app.TimePickerDialog;
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

public class UpdateRideActivity extends AppCompatActivity {

    private AutoCompleteTextView etOrigin;
    private AutoCompleteTextView etDestination;
    private EditText etDepTime;
    private EditText etPrice;

    private CheckBox cbFSeat;
    private CheckBox cbRSeat;
    private CheckBox cbMSeat;
    private CheckBox cbLSeat;

    private Button btnUpdate;
    private Button btnCancel;

    private RideService rideService;
    private int rideId = -1;
    private String token;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_ride); // Reusing the same layout

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

        btnUpdate = findViewById(R.id.btnCreate); // Reusing the ID from layout
        btnCancel = findViewById(R.id.btnCancel);

        btnUpdate.setText("Update Ride");

        SharedPrefManager spm = new SharedPrefManager(this);
        User user = spm.getUser();
        if (user != null) {
            token = user.getToken();
            userId = user.getId();
        }

        rideId = getIntent().getIntExtra("ride_id", -1);
        if (rideId != -1) {
            fetchRideDetails();
        } else {
            Toast.makeText(this, "Invalid ride ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        etDepTime.setOnClickListener(v -> showTimePicker());
        btnCancel.setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(v -> updateRide());

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
                Toast.makeText(UpdateRideActivity.this, "Error loading ride info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchLocationsFromDatabase() {
        rideService.getAllLocations(token).enqueue(new Callback<List<LocationItem>>() {
            @Override
            public void onResponse(Call<List<LocationItem>> call, Response<List<LocationItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LocationItem> locationItems = response.body();
                    List<String> locationNames = new ArrayList<>();
                    for (LocationItem item : locationItems) {
                        locationNames.add(item.getLocationName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            UpdateRideActivity.this,
                            android.R.layout.simple_dropdown_item_1line,
                            locationNames
                    );
                    etOrigin.setAdapter(adapter);
                    etDestination.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<LocationItem>> call, Throwable t) {
                Log.e("UpdateRide", "Error fetching locations", t);
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

    private void updateRide() {
        String origin = etOrigin.getText().toString().trim();
        String destination = etDestination.getText().toString().trim();
        String departureTime = etDepTime.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (origin.isEmpty()) { etOrigin.setError("Enter origin"); return; }
        if (destination.isEmpty()) { etDestination.setError("Enter destination"); return; }
        if (departureTime.isEmpty()) { etDepTime.setError("Select departure time"); return; }
        if (priceStr.isEmpty()) { etPrice.setError("Enter price"); return; }

        double price = 0.0;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            etPrice.setError("Invalid price format");
            return;
        }

        int fSeat = cbFSeat.isChecked() ? 1 : 0;
        int rSeat = cbRSeat.isChecked() ? 1 : 0;
        int mSeat = cbMSeat.isChecked() ? 1 : 0;
        int lSeat = cbLSeat.isChecked() ? 1 : 0;

        rideService.updateRide(
                token,
                rideId,
                userId,
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
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateRideActivity.this, "Ride Updated Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UpdateRideActivity.this, "Failed to update ride", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Ride> call, Throwable t) {
                Toast.makeText(UpdateRideActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
