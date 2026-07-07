package com.aliffcorp.car2pool;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliffcorp.car2pool.adapter.RideAdapter;
import com.aliffcorp.car2pool.model.Ride;
import com.aliffcorp.car2pool.model.User;
import com.aliffcorp.car2pool.remote.ApiUtils;
import com.aliffcorp.car2pool.remote.RideService;
import com.aliffcorp.car2pool.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideListActivity extends AppCompatActivity {

    private RideService rideService;
    private RecyclerView rvRideList;
    private RideAdapter adapter;

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

        //register for context menu
        registerForContextMenu(rvRideList);

        // get user info from SharedPreferences to get token value
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // get ride service instance
        rideService = ApiUtils.getRideService();

        // execute the call. send the user token when sending the query
        rideService.getAllRides(token).enqueue(new Callback<List<Ride>>() {
            @Override
            public void onResponse(Call<List<Ride>> call, Response<List<Ride>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // Get list of ride object from response
                    List<Ride> rides = response.body();

                    // initialize adapter
                    adapter = new RideAdapter(getApplicationContext(), rides);

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
    public void bookClicked(View view) {
        RecyclerView rvRideList = findViewById(R.id.rvRideList);
        int position = rvRideList.getChildAdapterPosition((View) view.getParent());
        if (position != RecyclerView.NO_POSITION && adapter != null) {
            Ride selectedRide = adapter.getItemAt(position); // Or however your adapter exposes the list item
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
}