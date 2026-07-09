package com.aliffcorp.car2pool;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);

        rvBookList = findViewById(R.id.rvBookList);

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        bookingService = ApiUtils.getBookingService();

        bookingService.getBookings(token).enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Booking> bookings = response.body();
                    adapter = new BookingAdapter(BookingList.this, bookings, BookingList.this);
                    rvBookList.setAdapter(adapter);
                    rvBookList.setLayoutManager(new LinearLayoutManager(BookingList.this));
                    rvBookList.addItemDecoration(new DividerItemDecoration(BookingList.this, DividerItemDecoration.VERTICAL));
                } else {
                    Toast.makeText(BookingList.this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
                    Log.e("BookingList", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                Toast.makeText(BookingList.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("BookingList", "Failure: ", t);
            }
        });
    }

    @Override
    public void onCancelClick(Booking booking) {
        // Implement cancel logic if needed
        Toast.makeText(this, "Cancel clicked for booking: " + booking.getBooking_id(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClick(Booking booking) {
        // Implement edit logic if needed
        Toast.makeText(this, "Edit clicked for booking: " + booking.getBooking_id(), Toast.LENGTH_SHORT).show();
    }
}
