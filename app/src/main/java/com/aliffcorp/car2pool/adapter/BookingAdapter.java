package com.aliffcorp.car2pool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
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

    // Interface
    public interface OnBookingActionListener {
        void onCancelClick(Booking booking);
        void onLongClick(View view, Booking booking);
    }

    public BookingAdapter(Context context,
                          List<Booking> bookingList,
                          OnBookingActionListener actionListener) {

        this.context = context;
        this.bookingList = bookingList;
        this.actionListener = actionListener;
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {

        TextView rideInfoText;
        TextView priceText;
        TextView bookingDateText;
        TextView statusText;
        Button btnCancel;
        RelativeLayout bookingCard;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);

            rideInfoText = itemView.findViewById(R.id.tvRideInfo);
            priceText = itemView.findViewById(R.id.tvPrice);
            bookingDateText = itemView.findViewById(R.id.tvBookingDate);
            statusText = itemView.findViewById(R.id.tvStatus);
            btnCancel = itemView.findViewById(R.id.btnCancel);

            // Root Layout
            bookingCard = itemView.findViewById(R.id.bookingCard);
        }
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);

        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {

        Booking currentBooking = bookingList.get(position);

        String origin = "Unknown";
        String destination = "Unknown";
        String driverName = "Driver: Ahmad";
        String departureTime = "N/A";

        if (currentBooking.getRide() != null) {

            origin = currentBooking.getRide().getOrigin();
            destination = currentBooking.getRide().getDestination();

            if (currentBooking.getRide().getDeparture_time() != null) {
                departureTime = currentBooking.getRide().getDeparture_time();
            }

            if (currentBooking.getRide().getDriver() != null) {
                driverName = "Driver: " +
                        currentBooking.getRide().getDriver().getUsername();
            }
        }

        holder.rideInfoText.setText(origin + " ➜ " + destination);
        holder.bookingDateText.setText("Date: " + departureTime);
        holder.statusText.setText(driverName);
        holder.priceText.setText("");

        // Cancel Button
        holder.btnCancel.setOnClickListener(v -> {

            if (actionListener != null) {
                actionListener.onCancelClick(currentBooking);
            }

        });

        // Long Press Booking Card
        holder.bookingCard.setOnLongClickListener(v -> {

            if (actionListener != null) {
                actionListener.onLongClick(v, currentBooking);
            }

            return true;
        });

    }

    @Override
    public int getItemCount() {
        return bookingList == null ? 0 : bookingList.size();
    }

}