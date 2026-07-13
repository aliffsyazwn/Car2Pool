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
import com.aliffcorp.car2pool.model.Ride;

import java.util.List;
import java.util.Locale;

public class DriverRideAdapter extends RecyclerView.Adapter<DriverRideAdapter.ViewHolder> {

    private List<Ride> rideList;
    private Context context;
    private OnRideActionListener actionListener;
    private int currentPos = -1;

    public interface OnRideActionListener {
        void onEditClick(Ride ride);
        void onCancelClick(Ride ride);
        void onLongClick(View view, Ride ride);
    }

    public DriverRideAdapter(Context context, List<Ride> rideList, OnRideActionListener actionListener) {
        this.context = context;
        this.rideList = rideList;
        this.actionListener = actionListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrigin, tvDestination, tvTime, priceText, seatsText;
        Button btnEdit, btnCancel;
        RelativeLayout rideItemCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrigin = itemView.findViewById(R.id.tvOrigin);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            tvTime = itemView.findViewById(R.id.tvTime);
            priceText = itemView.findViewById(R.id.tvPrice);
            seatsText = itemView.findViewById(R.id.tvSeats);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            rideItemCard = itemView.findViewById(R.id.rideItemCard);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_driver_ride, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ride currentRide = rideList.get(position);

        holder.tvOrigin.setText(currentRide.getOrigin());
        holder.tvDestination.setText(currentRide.getDestination());
        holder.tvTime.setText(currentRide.getDeparture_time());
        holder.priceText.setText(String.format(Locale.getDefault(), "RM %.2f", currentRide.getPrice()));

        int availableSeats = 0;
        if (currentRide.getfSeat()) availableSeats++;
        if (currentRide.getmSeat()) availableSeats++;
        if (currentRide.getrSeat()) availableSeats++;
        if (currentRide.getlSeat()) availableSeats++;

        holder.seatsText.setText("Available Seats: " + availableSeats);

        holder.btnEdit.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onEditClick(currentRide);
        });

        holder.btnCancel.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onCancelClick(currentRide);
        });

        holder.rideItemCard.setOnLongClickListener(v -> {
            currentPos = holder.getBindingAdapterPosition();
            if (actionListener != null) actionListener.onLongClick(v, currentRide);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return rideList == null ? 0 : rideList.size();
    }

    public Ride getSelectedItem() {
        if (currentPos >= 0 && rideList != null && currentPos < rideList.size()) {
            return rideList.get(currentPos);
        }
        return null;
    }
}