package com.aliffcorp.car2pool;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliffcorp.car2pool.adapter.DriverRideAdapter;
import com.aliffcorp.car2pool.model.Ride;
import com.aliffcorp.car2pool.model.User;
import com.aliffcorp.car2pool.remote.ApiUtils;
import com.aliffcorp.car2pool.remote.RideService;
import com.aliffcorp.car2pool.sharedpref.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverRideListActivity extends AppCompatActivity implements DriverRideAdapter.OnRideActionListener {

    private RideService rideService;
    private RecyclerView rvRideList;
    private DriverRideAdapter adapter;

    private String token;
    private int userId;

    private CardView cardHome, cardSearchRide, cardBooking, cardProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_ride_list);

        rvRideList = findViewById(R.id.rvRideList);
        rvRideList.setLayoutManager(new LinearLayoutManager(this));
        rvRideList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        if (user != null) {
            token = user.getToken();
            userId = user.getId();
        }

        rideService = ApiUtils.getRideService();

        setupBottomNavigation();
        fetchRides();
    }

    private void setupBottomNavigation() {
        cardHome = findViewById(R.id.cardHome);
        cardSearchRide = findViewById(R.id.cardSearchRide);
        cardBooking = findViewById(R.id.cardBooking);
        cardProfile = findViewById(R.id.cardProfile);

        cardHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        cardSearchRide.setOnClickListener(v -> {
            startActivity(new Intent(this, RideListActivity.class));
            finish();
        });

        cardBooking.setOnClickListener(v -> {
            startActivity(new Intent(this, BookingList.class));
            finish();
        });

        cardProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ViewProfileActivity.class));
            finish();
        });
    }

    private void fetchRides() {
        if (token == null) return;

        // Try to get driver rides. If the endpoint doesn't exist, we'll fall back to filtering all rides.
        rideService.getDriverRides(token, userId).enqueue(new Callback<List<Ride>>() {
            @Override
            public void onResponse(Call<List<Ride>> call, Response<List<Ride>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayRides(response.body());
                } else if (response.code() == 404 || response.code() == 405) {
                    // Endpoint might not exist, fallback to filtering all rides
                    fetchAllAndFilter();
                } else {
                    Toast.makeText(DriverRideListActivity.this, "Failed to load rides", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Ride>> call, Throwable t) {
                fetchAllAndFilter();
            }
        });
    }

    private void fetchAllAndFilter() {
        rideService.getAllRides(token).enqueue(new Callback<List<Ride>>() {
            @Override
            public void onResponse(Call<List<Ride>> call, Response<List<Ride>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Ride> filtered = new ArrayList<>();
                    for (Ride r : response.body()) {
                        if (r.getDriver_id() == userId) {
                            filtered.add(r);
                        }
                    }
                    displayRides(filtered);
                }
            }

            @Override
            public void onFailure(Call<List<Ride>> call, Throwable t) {
                Toast.makeText(DriverRideListActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayRides(List<Ride> rides) {
        adapter = new DriverRideAdapter(this, rides, this);
        rvRideList.setAdapter(adapter);
    }

    @Override
    public void onEditClick(Ride ride) {
        Intent intent = new Intent(this, CreateRideActivity.class);
        intent.putExtra("ride_id", ride.getRide_id());
        startActivity(intent);
    }

    @Override
    public void onCancelClick(Ride ride) {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Ride")
                .setMessage("Are you sure you want to cancel this ride?")
                .setPositiveButton("Yes", (dialog, which) -> executeCancel(ride.getRide_id()))
                .setNegativeButton("No", null)
                .show();
    }

    private void executeCancel(int rideId) {
        rideService.deleteRide(token, rideId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DriverRideListActivity.this, "Ride Cancelled", Toast.LENGTH_SHORT).show();
                    fetchRides();
                } else {
                    Toast.makeText(DriverRideListActivity.this, "Failed to cancel ride", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DriverRideListActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLongClick(View view, Ride ride) {
        registerForContextMenu(view);
        openContextMenu(view);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ride_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Ride selectedRide = adapter.getSelectedItem();
        if (selectedRide == null) return super.onContextItemSelected(item);

        if (item.getItemId() == R.id.ride_details) {
            Intent intent = new Intent(this, DriverRideDetailActivity.class);
            intent.putExtra("ride_id", selectedRide.getRide_id());
            startActivity(intent);
        }
        return super.onContextItemSelected(item);
    }
}
