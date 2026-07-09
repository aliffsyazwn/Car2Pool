package com.aliffcorp.car2pool;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliffcorp.car2pool.adapter.BookingAdapter;
import com.aliffcorp.car2pool.model.Booking;
import com.aliffcorp.car2pool.model.User;
import com.aliffcorp.car2pool.remote.ApiUtils;
import com.aliffcorp.car2pool.remote.BookingService;
import com.aliffcorp.car2pool.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingList extends AppCompatActivity {
    private BookingService bookingService;
    private RecyclerView rvBookList;
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

        // get reference to the RecyclerView rideList
        rvBookList = findViewById(R.id.rvBookList);

        // set layout to recycler view
        rvBookList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // add separator between item in the list
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvBookList.getContext(),
                DividerItemDecoration.VERTICAL);
        rvBookList.addItemDecoration(dividerItemDecoration);

        // get booking service instance
        bookingService = ApiUtils.getBookingService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch bookings every time the activity is shown
        fetchBookings();
    }

    private void fetchBookings() {
        //Get the Token from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        if (user == null || user.getToken() == null) {
            Toast.makeText(this, "Session invalid. Please login.", Toast.LENGTH_SHORT).show();
            clearSessionAndRedirect();
            return;
        }

        String token = user.getToken();

        // Make the API Call
        bookingService.getBookings(token).enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                if (response.code() == 200 && response.body() != null) {
                    List<Booking> bookings = response.body();

                    // initialize adapter
                    adapter = new BookingAdapter(getApplicationContext(), bookings, new BookingAdapter.OnBookingActionListener() {
                        @Override
                        public void onCancelClick(Booking booking) {
                            confirmCancellation(booking, token);
                        }

                        @Override
                        public void onEditClick(Booking booking) {
                            // TODO: Implement edit functionality
                        }
                    });
                    // set adapter to the RecyclerView
                    rvBookList.setAdapter(adapter);

                } else if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    Toast.makeText(BookingList.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                Toast.makeText(BookingList.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Car2Pool", t.toString());
            }
        });
    }

    private void confirmCancellation(Booking booking, String token) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cancel Booking")
                .setMessage("Are you sure, want to cancel the booking?")
                .setPositiveButton("Yes", (dialog, which) -> executeCancel(booking.getBooking_id(), token))
                .setNegativeButton("No", null)
                .show();
    }

    private void executeCancel(int bookingId, String token) {
        bookingService.cancelBooking(token, bookingId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BookingList.this, "Booking Cancelled", Toast.LENGTH_SHORT).show();
                    // Refresh the list so the deleted item disappears
                    fetchBookings();
                } else {
                    Toast.makeText(BookingList.this, "Failed to cancel booking", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(BookingList.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clearSessionAndRedirect() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
