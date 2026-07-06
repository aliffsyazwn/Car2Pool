package com.aliffcorp.car2pool;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliffcorp.car2pool.adapter.BookingAdapter;
import com.aliffcorp.car2pool.model.Ride;
import com.aliffcorp.car2pool.remote.ApiUtils;
import com.aliffcorp.car2pool.remote.RideService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerViewBookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Call the method to fetch data from your API
        fetchRides();
    }

    private void fetchRides() {
        RideService service = ApiUtils.getRideService();

        String apiKey = "____"; // belum letak

        service.getAllRides(apiKey).enqueue(new Callback<List<Ride>>() {
            @Override
            public void onResponse(Call<List<Ride>> call, Response<List<Ride>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Ride> rides = response.body();

                    // Pass the fetched list to the adapter
                    adapter = new BookingAdapter(rides);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(BookingList.this, "No booking has been found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Ride>> call, Throwable t) {
                Toast.makeText(BookingList.this, "Network error, please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }
}