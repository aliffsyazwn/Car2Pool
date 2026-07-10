package com.aliffcorp.car2pool;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

public class BookingList extends AppCompatActivity implements BookingAdapter.OnBookingActionListener {

    private BookingService bookingService;
    private RecyclerView rvBookList;
    private BookingAdapter adapter;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);

        rvBookList = findViewById(R.id.rvBookList);
        // Best practice: Set LayoutManager and ItemDecoration once in onCreate
        rvBookList.setLayoutManager(new LinearLayoutManager(this));
        rvBookList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        if (user != null) {
            token = user.getToken();
        }

        bookingService = ApiUtils.getBookingService();
        fetchBookings();
    }

    private void fetchBookings() {
        if (token == null) return;

        bookingService.getBookings(token).enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Booking> bookings = response.body();
                    adapter = new BookingAdapter(BookingList.this, bookings, BookingList.this);
                    rvBookList.setAdapter(adapter);
                } else if (response.code() == 204) {
                    // Added this back in to prevent blank screen errors when database is empty
                    Toast.makeText(BookingList.this, "No Booking found", Toast.LENGTH_SHORT).show();
                    rvBookList.setAdapter(null);
                } else {
                    Toast.makeText(BookingList.this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
                    Log.e("BookingList", "Error: " + response.message());
                }
            }

            // You were missing the onFailure method for the fetch API call!
            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                Toast.makeText(BookingList.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("BookingList", "Failure: ", t);
            }
        });
    }

    // --- BUTTON CLICK LOGIC ---

    @Override
    public void onCancelClick(Booking booking) {
        // Show confirmation popup
        new AlertDialog.Builder(this)
                .setTitle("Cancel Booking")
                .setMessage("Are you sure want to cancel the booking?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If they click Yes, run the executeCancel method below
                        executeCancel(booking.getBooking_id());
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    // You were missing this method entirely! This actually tells the server to delete the booking.
    private void executeCancel(int bookingId) {
        if (token == null) return;

        bookingService.cancelBooking(token, bookingId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BookingList.this, "Booking cancelled", Toast.LENGTH_SHORT).show();
                    // Refresh the list automatically so the deleted item goes away
                    fetchBookings();
                } else {
                    Toast.makeText(BookingList.this, "Failed to cancel", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(BookingList.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditClick(Booking booking) {
        // Opens the Update screen and passes the Booking ID
        Intent intent = new Intent(BookingList.this, UpdateBookingActivity.class);
        intent.putExtra("booking_id", booking.getBooking_id());
        startActivity(intent);
    }
}