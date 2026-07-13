package com.aliffcorp.car2pool;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliffcorp.car2pool.adapter.RideAdapter;
import com.aliffcorp.car2pool.model.LocationItem;
import com.aliffcorp.car2pool.model.Ride;
import com.aliffcorp.car2pool.model.User;
import com.aliffcorp.car2pool.remote.ApiUtils;
import com.aliffcorp.car2pool.remote.BookingService;
import com.aliffcorp.car2pool.remote.RideService;
import com.aliffcorp.car2pool.sharedpref.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideListActivity extends AppCompatActivity {

    private RideService rideService;
    private BookingService bookingService;
    private RecyclerView rvRideList;
    private RideAdapter adapter;
    private CardView cardHome;
    private CardView cardSearchRide;
    private CardView cardBooking;
    private CardView cardProfile;

    // UI Elements for Search
    private AutoCompleteTextView etSearchOrigin;
    private AutoCompleteTextView etSearchDestination;

    // Master list to hold all rides for filtering
    private List<Ride> allRidesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ride_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get reference to the RecyclerView rideList
        rvRideList = findViewById(R.id.rvRideList);
        etSearchOrigin = findViewById(R.id.etSearchOrigin);
        etSearchDestination = findViewById(R.id.etSearchDestination);

        //register for context menu
        registerForContextMenu(rvRideList);

        // Setup the TextWatcher to listen to both search boxes
        TextWatcher searchWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRides();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etSearchOrigin.addTextChangedListener(searchWatcher);
        etSearchDestination.addTextChangedListener(searchWatcher);

        // get user info from SharedPreferences to get token value
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get ride service instance
        rideService = ApiUtils.getRideService();
        bookingService = ApiUtils.getBookingService();
        setupBottomNavigation();

        // Fetch locations for the dropdown search bars
        fetchLocations(token);

        // execute the call. send the user token when sending the query
        rideService.getAllRides(token).enqueue(new Callback<List<Ride>>() {
            @Override
            public void onResponse(Call<List<Ride>> call, Response<List<Ride>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // Get list of ride object from response
                    List<Ride> rides = response.body();

                    // filter out rides where all four seats are taken (checked)
                    if (rides != null) {
                        rides.removeIf(ride -> ride.getfSeat() && ride.getmSeat() && ride.getrSeat() && ride.getlSeat());

                        // --- NEW: Time Filtering & Sorting ---
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date currentDate = new Date();

                        // Remove rides that have already departed
                        rides.removeIf(ride -> {
                            try {
                                Date rideDate = sdf.parse(ride.getDeparture_time());
                                return rideDate != null && rideDate.before(currentDate);
                            } catch (Exception e) {
                                return false; // If date parsing fails, keep it in the list
                            }
                        });

                        // Sort the remaining rides from nearest to furthest time
                        Collections.sort(rides, new Comparator<Ride>() {
                            @Override
                            public int compare(Ride r1, Ride r2) {
                                try {
                                    Date date1 = sdf.parse(r1.getDeparture_time());
                                    Date date2 = sdf.parse(r2.getDeparture_time());
                                    return date1.compareTo(date2);
                                } catch (Exception e) {
                                    return 0;
                                }
                            }
                        });
                        // --- END NEW ---

                        // Save to our master list for the search filter
                        allRidesList.clear();
                        allRidesList.addAll(rides);
                    }

                    // initialize adapter (using allRidesList now)
                    adapter = new RideAdapter(getApplicationContext(), allRidesList);

                    // set adapter to the RecyclerView
                    rvRideList.setAdapter(adapter);

                    // set layout to recycler view
                    rvRideList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    // add separator between item in the list
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvRideList.getContext(),
                            DividerItemDecoration.VERTICAL);
                    rvRideList.addItemDecoration(dividerItemDecoration);
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
            public void onFailure(Call<List<Ride>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
            }
        });
    }

    // --- ADDED THIS MISSING METHOD TO FETCH LOCATIONS ---
    private void fetchLocations(String token) {
        bookingService.getAllLocations(token).enqueue(new Callback<List<LocationItem>>() {
            @Override
            public void onResponse(Call<List<LocationItem>> call, Response<List<LocationItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LocationItem> locationItems = response.body();
                    List<String> locationNames = new ArrayList<>();

                    for (LocationItem item : locationItems) {
                        locationNames.add(item.getLocationName());
                    }

                    android.widget.ArrayAdapter<String> dropdownAdapter = new android.widget.ArrayAdapter<>(
                            RideListActivity.this,
                            android.R.layout.simple_dropdown_item_1line,
                            locationNames
                    );

                    etSearchOrigin.setAdapter(dropdownAdapter);
                    etSearchDestination.setAdapter(dropdownAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<LocationItem>> call, Throwable t) {
                Log.e("MyApp:", "Failed to fetch locations for dropdown: " + t.getMessage());
            }
        });
    }
    // ----------------------------------------------------

    // New method to handle the filtering logic
    private void filterRides() {
        String searchOrigin = etSearchOrigin.getText().toString().toLowerCase().trim();
        String searchDestination = etSearchDestination.getText().toString().toLowerCase().trim();

        List<Ride> filteredRides = new ArrayList<>();

        for (Ride ride : allRidesList) {
            String origin = ride.getOrigin() != null ? ride.getOrigin().toLowerCase() : "";
            String destination = ride.getDestination() != null ? ride.getDestination().toLowerCase() : "";

            if (origin.contains(searchOrigin) && destination.contains(searchDestination)) {
                filteredRides.add(ride);
            }
        }

        if (adapter != null) {
            adapter.filterList(filteredRides);
        }
    }

    public void clearSessionAndRedirect() {
        // clear the shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // terminate this MainActivity
        finish();

        // forward to Login Page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    public void detailClicked(View view) {

        Ride selectedRide = (Ride) view.getTag();

        if (selectedRide != null) {
            doViewDetails(selectedRide);
        }
    }
    private void doViewDetails(Ride selectedRide) {
        Log.d("MyApp:", "viewing details: " + selectedRide.toString());
        // forward user to BookDetailsActivity, passing the selected book id
        Intent intent = new Intent(getApplicationContext(), RideDetailActivity.class);
        intent.putExtra("ride_id", selectedRide.getRide_id());
        startActivity(intent);
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

        // Already on Ride List
        cardSearchRide.setOnClickListener(v -> {
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
}