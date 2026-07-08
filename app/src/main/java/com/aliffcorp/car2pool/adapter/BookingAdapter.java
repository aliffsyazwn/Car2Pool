package com.aliffcorp.car2pool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliffcorp.car2pool.R;
import com.aliffcorp.car2pool.model.Booking;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;
    private Context context;

    public BookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView rideInfoText;
        TextView priceText;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            rideInfoText = itemView.findViewById(R.id.textRideInfo);
            priceText = itemView.findViewById(R.id.textPrice);
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

        holder.rideInfoText.setText("Booking ID: " + currentBooking.getBooking_id());
        holder.priceText.setText("Driver ID: " + currentBooking.getDriver_id());
    }

    @Override
    public int getItemCount() {
        return bookingList == null ? 0 : bookingList.size();
    }
}
