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

    private CardView cardHome;
    private CardView cardUpdateRide;
    private CardView cardCreateRide;
    private CardView cardProfile;

    @Override
    protected void onResume() {
        super.onResume();

        if (rideService != null && token != null) {
            fetchRides();
        }
    }

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
        cardUpdateRide = findViewById(R.id.cardUpdateRide);
        cardCreateRide = findViewById(R.id.cardCreateRide);
        cardProfile = findViewById(R.id.cardProfile);

        // Home
        cardHome.setOnClickListener(v -> {
            startActivity(new Intent(
                    DriverRideListActivity.this,
                    DriverMainActivity.class));
            finish();
        });

        // Current Page
        cardUpdateRide.setOnClickListener(v -> {
            // Already on My Rides page
        });

        // Create Ride
        cardCreateRide.setOnClickListener(v -> {
            startActivity(new Intent(
                    DriverRideListActivity.this,
                    CreateRideActivity.class));
            finish();
        });

        // Profile
        cardProfile.setOnClickListener(v -> {
            startActivity(new Intent(
                    DriverRideListActivity.this,
                    ViewProfileActivity.class));
            finish();
        });
    }

    private void fetchRides() {

        if (token == null) {
            return;
        }

        fetchAllAndFilter();
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
        Intent intent = new Intent(
                this,
                UpdateRideActivity.class
        );

        intent.putExtra(
                "ride_id",
                ride.getRide_id()
        );

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
