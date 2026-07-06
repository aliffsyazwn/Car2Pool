package com.aliffcorp.car2pool.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliffcorp.car2pool.R;
import com.aliffcorp.car2pool.model.Ride;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Ride> rideList;

    public BookingAdapter(List<Ride> rideList) {
        this.rideList = rideList;
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
        Ride currentRide = rideList.get(position);

        holder.rideInfoText.setText("Driver: Hafiz - " + currentRide.getDestination());
        holder.priceText.setText("RM " + String.format("%.2f", currentRide.getPrice()));
    }

    @Override
    public int getItemCount() {
        return rideList == null ? 0 : rideList.size();
    }
}