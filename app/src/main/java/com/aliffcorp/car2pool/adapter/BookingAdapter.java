package com.aliffcorp.car2pool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliffcorp.car2pool.R;
import com.aliffcorp.car2pool.model.Booking;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;
    private Context context;
    private OnCancelClickListener cancelListener;

    public interface OnCancelClickListener {
        void onCancelClick(Booking booking);
    }

    public void setOnCancelClickListener(OnCancelClickListener listener) {
        this.cancelListener = listener;
    }

    public BookingAdapter(Context context, List<Booking> bookingList, OnCancelClickListener cancelListener) {
        this.context = context;
        this.bookingList = bookingList;
        this.cancelListener = cancelListener;
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView rideInfoText;
        TextView priceText;
        TextView bookingDateText;
        TextView statusText;
        Button btnCancel;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            rideInfoText = itemView.findViewById(R.id.tvRideInfo);
            priceText = itemView.findViewById(R.id.tvPrice);
            bookingDateText = itemView.findViewById(R.id.tvBookingDate);
            statusText = itemView.findViewById(R.id.tvStatus);
            btnCancel = itemView.findViewById(R.id.btnCancel);
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

        // Set up safe fallback values
        String origin = "N/A";
        String destination = "N/A";
        String driverName = "Driver: Unknown";
        String departureTime = "Unknown";

        // Navigate the nested JSON safely to avoid NullPointerExceptions
        if (currentBooking.getRide() != null) {
            origin = currentBooking.getRide().getOrigin();
            destination = currentBooking.getRide().getDestination();

            // Extract the actual date/time from the nested Ride object
            if (currentBooking.getRide().getDeparture_time() != null) {
                departureTime = currentBooking.getRide().getDeparture_time();
            }

            if (currentBooking.getRide().getDriver() != null) {
                driverName = "Driver: " + currentBooking.getRide().getDriver().getUsername();
            }
        }

        // Bind the extracted data to their exact corresponding XML views
        // XML ID: tvRideInfo -> Assign Origin and Destination
        holder.rideInfoText.setText(origin + " ➔ " + destination);

        // XML ID: tvBookingDate -> Assign the actual departure time from JSON
        holder.bookingDateText.setText("Date: " + departureTime);

        // XML ID: tvStatus -> Assign the driver's name
        holder.statusText.setText(driverName);

        // XML ID: tvPrice -> Clear it since there's no price in your current JSON response
        holder.priceText.setText("");

        holder.btnCancel.setOnClickListener(v -> {
            if (cancelListener != null) {
                cancelListener.onCancelClick(currentBooking);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList == null ? 0 : bookingList.size();
    }
}
