package com.aliffcorp.car2pool;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.PopupMenu;
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

public class BookingList extends AppCompatActivity
        implements BookingAdapter.OnBookingActionListener {

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

    // Long Press Context Menu
    @Override
    public void onLongClick(View view, Booking booking) {

        PopupMenu popup = new PopupMenu(this, view);

        popup.getMenu().add(0, 1, 0, "View Booking Detail");
        popup.getMenu().add(0, 2, 1, "View Driver Detail");

        popup.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {

                case 1:

                    Toast.makeText(
                            BookingList.this,
                            "Booking ID : " + booking.getBooking_id(),
                            Toast.LENGTH_SHORT
                    ).show();


                    return true;

                case 2:

                    if (booking.getRide() != null) {

                        Intent intent = new Intent(
                                BookingList.this,
                                RideDetailActivity.class);

                        intent.putExtra(
                                "ride_id",
                                booking.getRide().getRide_id());

                        startActivity(intent);
                    }

                    return true;
            }

            return false;
        });

        popup.show();
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
}