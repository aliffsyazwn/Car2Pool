package com.aliffcorp.car2pool;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

import com.aliffcorp.car2pool.model.Ride;
import com.aliffcorp.car2pool.model.User;
import com.aliffcorp.car2pool.remote.ApiUtils;
import com.aliffcorp.car2pool.remote.RideService;
import com.aliffcorp.car2pool.sharedpref.SharedPrefManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateRideActivity extends AppCompatActivity {

    private AutoCompleteTextView etOrigin;
    private AutoCompleteTextView etDestination;
    private EditText etDepTime;

    private CheckBox cbFSeat;
    private CheckBox cbRSeat;
    private CheckBox cbMSeat;
    private CheckBox cbLSeat;

    private Button btnBack;
    private Button btnConfirmEdit;

    private RideService rideService;
    private Ride currentRide;

    private int rideId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_ride);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (view, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        etOrigin = findViewById(R.id.etOrigin);
        etDestination = findViewById(R.id.etDestination);
        etDepTime = findViewById(R.id.etDepTime);

        cbFSeat = findViewById(R.id.cbFSeat);
        cbRSeat = findViewById(R.id.cbRSeat);
        cbMSeat = findViewById(R.id.cbMSeat);
        cbLSeat = findViewById(R.id.cbLSeat);

        btnBack = findViewById(R.id.btnBack);
        btnConfirmEdit = findViewById(R.id.btnConfirmEdit);

        // Prevent updating before the previous ride data loads
        btnConfirmEdit.setEnabled(false);

        SharedPrefManager spm = new SharedPrefManager(this);

        if (!spm.isLoggedIn()) {

            Intent intent = new Intent(
                    UpdateRideActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
            finish();
            return;
        }

        User user = spm.getUser();
        token = user.getToken();

        rideId = getIntent().getIntExtra("ride_id", -1);

        if (rideId == -1) {

            Toast.makeText(
                    this,
                    "Invalid ride ID",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        rideService = ApiUtils.getRideService();

        loadExistingRide();

        etDepTime.setOnClickListener(v -> showTimePicker());
        btnBack.setOnClickListener(v -> finish());
        btnConfirmEdit.setOnClickListener(v -> updateRide());
    }

    private void loadExistingRide() {

        rideService.getRides(token, rideId)
                .enqueue(new Callback<Ride>() {

                    @Override
                    public void onResponse(Call<Ride> call, Response<Ride> response) {
                        if (response.isSuccessful()
                                && response.body() != null) {

                            currentRide = response.body();

                            // Show previous details
                            etOrigin.setText(
                                    currentRide.getOrigin()
                            );

                            etDestination.setText(
                                    currentRide.getDestination()
                            );

                            etDepTime.setText(
                                    currentRide.getDeparture_time()
                            );

                            // Fill Checkboxes
                            cbFSeat.setChecked(
                                    currentRide.getfSeat()
                            );

                            cbRSeat.setChecked(
                                    currentRide.getrSeat()
                            );

                            cbMSeat.setChecked(
                                    currentRide.getmSeat()
                            );

                            cbLSeat.setChecked(
                                    currentRide.getlSeat()
                            );

                            btnConfirmEdit.setEnabled(true);

                        } else {

                            Toast.makeText(
                                    UpdateRideActivity.this,
                                    "Unable to load ride. Code: "
                                            + response.code(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Ride> call, Throwable t) {
                        Toast.makeText(
                                UpdateRideActivity.this,
                                "Connection error: "
                                        + t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void showTimePicker() {

        Calendar calendar = Calendar.getInstance();

        String existingDateTime =
                etDepTime.getText().toString().trim();

        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
        );

        format.setLenient(false);

        try {
            Date existingDate =
                    format.parse(existingDateTime);

            if (existingDate != null) {
                calendar.setTime(existingDate);
            }

        }
        catch (ParseException ignored) {
            // Uses the current date and time if parsing fails
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, selectedMinute) -> {

                    String dateTime = String.format(
                            Locale.getDefault(),
                            "%04d-%02d-%02d %02d:%02d:00",
                            year,
                            month + 1,
                            day,
                            hourOfDay,
                            selectedMinute
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
        String origin =
                etOrigin.getText().toString().trim();
        String destination =
                etDestination.getText().toString().trim();
        String departureTime =
                etDepTime.getText().toString().trim();

        if (origin.isEmpty()) {

            etOrigin.setError("Origin is required");
            etOrigin.requestFocus();
            return;
        }
        if (destination.isEmpty()) {

            etDestination.setError(
                    "Destination is required"
            );

            etDestination.requestFocus();
            return;
        }
        if (departureTime.isEmpty()) {
            etDepTime.setError(
                    "Departure time is required"
            );
            return;
        }
        if (currentRide == null) {
            Toast.makeText(
                    this,
                    "Ride data has not finished loading",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        int fSeat = cbFSeat.isChecked() ? 1 : 0;
        int rSeat = cbRSeat.isChecked() ? 1 : 0;
        int mSeat = cbMSeat.isChecked() ? 1 : 0;
        int lSeat = cbLSeat.isChecked() ? 1 : 0;

        btnConfirmEdit.setEnabled(false);
        rideService.updateSeat(
                token,
                rideId,
                currentRide.getDriver_id(),
                origin,
                destination,
                departureTime,
                fSeat,
                rSeat,
                mSeat,
                lSeat
        ).enqueue(new Callback<Ride>() {

            @Override
            public void onResponse(Call<Ride> call, Response<Ride> response) {
                btnConfirmEdit.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(
                            UpdateRideActivity.this,
                            "Ride updated successfully",
                            Toast.LENGTH_SHORT
                    ).show();
                    setResult(RESULT_OK);
                    finish();
                }
                else {
                    Toast.makeText(
                            UpdateRideActivity.this,
                            "Unable to update ride. Code: "
                                    + response.code(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<Ride> call, Throwable t) {
                btnConfirmEdit.setEnabled(true);
                Toast.makeText(
                        UpdateRideActivity.this,
                        "Connection error: "
                                + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}