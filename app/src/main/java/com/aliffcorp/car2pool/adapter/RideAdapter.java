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
import com.aliffcorp.car2pool.model.Ride;

import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        public TextView tvOrigin;
        public TextView tvDestination;
        public TextView tvTime;
        public TextView tvPrice;
        public TextView tvDriver;

        public Button btnDetail;

        public ViewHolder(View itemView) {
            super(itemView);

            tvOrigin = itemView.findViewById(R.id.tvOrigin);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDriver = itemView.findViewById(R.id.tvDriver);
            btnDetail = itemView.findViewById(R.id.btnDetail);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            currentPos = getAdapterPosition();
            return false;
        }
    }

    private List<Ride> rideListData;
    private Context mContext;
    private int currentPos = -1;

    public RideAdapter(Context context, List<Ride> listData) {
        this.mContext = context;
        this.rideListData = listData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ride_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Ride ride = rideListData.get(position);

        holder.tvOrigin.setText(ride.getOrigin());
        holder.tvDestination.setText(ride.getDestination());
        holder.tvTime.setText(ride.getDeparture_time());

        // Price
        holder.tvPrice.setText(String.format("RM %.2f", ride.getPrice()));

        // Driver Name
        if (ride.getDriver() != null) {
            holder.tvDriver.setText(ride.getDriver().getUsername());
        } else {
            holder.tvDriver.setText("Unknown Driver");
        }

        // Book button
        holder.btnDetail.setTag(ride);
    }

    @Override
    public int getItemCount() {
        return rideListData.size();
    }

    public Ride getSelectedItem() {
        if (currentPos >= 0 &&
                rideListData != null &&
                currentPos < rideListData.size()) {

            return rideListData.get(currentPos);
        }

        return null;
    }

    public Ride getItemAt(int position) {

        if (rideListData != null &&
                position >= 0 &&
                position < rideListData.size()) {

            return rideListData.get(position);
        }

        return null;
    }

    public void filterList(List<Ride> filteredList) {
        rideListData = filteredList;
        notifyDataSetChanged();
    }
}