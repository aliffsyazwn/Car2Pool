package com.aliffcorp.car2pool;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.cardview.widget.CardView;

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
    private int userId;

    private CardView cardHome;
    private CardView cardSearchRide;
    private CardView cardBooking;
    private CardView cardProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);

        rvBookList = findViewById(R.id.rvBookList);

        rvBookList.setLayoutManager(new LinearLayoutManager(this));
        rvBookList.addItemDecoration(
                new DividerItemDecoration(this,
                        DividerItemDecoration.VERTICAL));

        SharedPrefManager spm =
                new SharedPrefManager(getApplicationContext());

        User user = spm.getUser();

        if (user != null) {
            token = user.getToken();
            userId = user.getId();
        }

        bookingService = ApiUtils.getBookingService();

        // ==========================
        // Bottom Navigation
        // ==========================

        cardHome = findViewById(R.id.cardHome);
        cardSearchRide = findViewById(R.id.cardSearchRide);
        cardBooking = findViewById(R.id.cardBooking);
        cardProfile = findViewById(R.id.cardProfile);

        cardHome.setOnClickListener(v -> {
            startActivity(new Intent(
                    BookingList.this,
                    MainActivity.class));
            finish();
        });

        cardSearchRide.setOnClickListener(v -> {
            startActivity(new Intent(
                    BookingList.this,
                    RideListActivity.class));
            finish();
        });

        // Already in Booking page
        cardBooking.setOnClickListener(v -> {
        });

        cardProfile.setOnClickListener(v -> {
            startActivity(new Intent(
                    BookingList.this,
                    ViewProfileActivity.class));
            finish();
        });

        fetchBookings();
    }

    private void fetchBookings() {

        if (token == null) return;

        bookingService.getUserBookings(token, userId)
                .enqueue(new Callback<List<Booking>>() {

                    @Override
                    public void onResponse(Call<List<Booking>> call,
                                           Response<List<Booking>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            adapter = new BookingAdapter(
                                    BookingList.this,
                                    response.body(),
                                    BookingList.this);

                            rvBookList.setAdapter(adapter);

                        } else if (response.code() == 204) {

                            Toast.makeText(
                                    BookingList.this,
                                    "No Booking Found",
                                    Toast.LENGTH_SHORT).show();

                        } else if (response.code() == 401) {

                            Toast.makeText(
                                    BookingList.this,
                                    "Session expired.",
                                    Toast.LENGTH_LONG).show();

                            handleLogout();

                        } else {

                            Toast.makeText(
                                    BookingList.this,
                                    "Failed to load bookings",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Booking>> call,
                                          Throwable t) {

                        Toast.makeText(
                                BookingList.this,
                                t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Cancel Button
    @Override
    public void onCancelClick(Booking booking) {

        new AlertDialog.Builder(this)
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel this booking?")
                .setPositiveButton("Yes",
                        (dialog, which) -> executeCancel(booking.getBooking_id()))
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onLongClick(View view, Booking booking) {
        registerForContextMenu(view);
        openContextMenu(view);
    }


    private void executeCancel(int bookingId) {

        bookingService.cancelBooking(token, bookingId)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call,
                                           Response<Void> response) {

                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    BookingList.this,
                                    "Booking Cancelled",
                                    Toast.LENGTH_SHORT).show();

                            fetchBookings();

                        } else {

                            Toast.makeText(
                                    BookingList.this,
                                    "Failed to cancel booking",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                        Toast.makeText(
                                BookingList.this,
                                "Network Error",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleLogout() {

        SharedPrefManager spm =
                new SharedPrefManager(getApplicationContext());

        spm.logout();

        finish();

        startActivity(new Intent(
                BookingList.this,
                LoginActivity.class));
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ride_context_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        Booking selectedBooking = adapter.getSelectedItem();

        if (selectedBooking == null) {
            return super.onContextItemSelected(item);
        }

        Log.d("MyApp", "selected "+ selectedBooking.toString());    // debug purpose

        if (item.getItemId() == R.id.ride_details) {
            // user clicked details contextual menu
            doRideDetails(selectedBooking);
        }
        else if (item.getItemId() == R.id.driver_details) {
            // user clicked the delete contextual menu
            doDriverDetails(selectedBooking);
        }

        return super.onContextItemSelected(item);
    }

    private void doRideDetails(Booking selectedBooking) {
        Log.d("MyApp:", "viewing details: " + selectedBooking.toString());
        // forward user to RideDetailsActivity, passing the selected book id
        Intent intent = new Intent(getApplicationContext(), RideDetailActivity.class);
        intent.putExtra("book_id", selectedBooking.getBooking_id());
        startActivity(intent);
    }

    private void doDriverDetails(Booking selectedBooking) {
        Log.d("MyApp:", "viewing details: " + selectedBooking.toString());
        // forward user to DriverDetailActivity, passing the driver_id
        Intent intent = new Intent(getApplicationContext(), DriverDetailActivity.class);
        if (selectedBooking.getRide() != null) {
            intent.putExtra("user_id", selectedBooking.getRide().getDriver_id());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Driver info not available", Toast.LENGTH_SHORT).show();
        }
    }
}