package com.aliffcorp.car2pool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aliffcorp.car2pool.R;
import com.aliffcorp.car2pool.model.Booking;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;
    private Context context;
    private OnBookingActionListener actionListener;

    // 1. Rename the interface to handle multiple actions
    public interface OnBookingActionListener {
        void onCancelClick(Booking booking);
        void onEditClick(Booking booking); // Add Edit click
    }

    public BookingAdapter(Context context, List<Booking> bookingList, OnBookingActionListener actionListener) {
        this.context = context;
        this.bookingList = bookingList;
        this.actionListener = actionListener;
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView rideInfoText, priceText, bookingDateText, statusText;
        Button btnCancel, btnEdit; // 2. Declare the Edit button

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            rideInfoText = itemView.findViewById(R.id.tvRideInfo);
            priceText = itemView.findViewById(R.id.tvPrice);
            bookingDateText = itemView.findViewById(R.id.tvBookingDate);
            statusText = itemView.findViewById(R.id.tvStatus);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnEdit = itemView.findViewById(R.id.btnEdit); // Link the Edit button
        }
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking currentBooking = bookingList.get(position);

        String origin = "Unknown";
        String destination = "Unknown";
        String driverName = "Unknown";
        String departureTime = "N/A";

        if (currentBooking.getRide() != null) {
            origin = currentBooking.getRide().getOrigin();
            destination = currentBooking.getRide().getDestination();

            if (currentBooking.getRide().getDeparture_time() != null) {
                departureTime = currentBooking.getRide().getDeparture_time();
            }
            if (currentBooking.getRide().getDriver() != null) {
                driverName = "Driver: " + currentBooking.getRide().getDriver().getUsername();
            }
        }

        holder.rideInfoText.setText(origin + " ➔ " + destination);
        holder.bookingDateText.setText("Date: " + departureTime);
        holder.statusText.setText(driverName);
        holder.priceText.setText("");

        // 3. Set click listener for Cancel
        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionListener.onCancelClick(currentBooking);
            }
        });

        // 4. Set click listener for Edit
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionListener.onEditClick(currentBooking);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList == null ? 0 : bookingList.size();
    }
}
