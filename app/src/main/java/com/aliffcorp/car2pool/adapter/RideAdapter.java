package com.aliffcorp.car2pool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aliffcorp.car2pool.R;
import com.aliffcorp.car2pool.model.Ride;

import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView tvOrigin;
        public TextView tvDestination;
        public TextView tvTime;
        public TextView tvDriver;


        public ViewHolder(View itemView) {
            super(itemView);
            tvOrigin = itemView.findViewById(R.id.tvOrigin);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDriver = itemView.findViewById(R.id.tvDriver);


            itemView.setOnLongClickListener(this);  //register long click action to this viewholder instance
        }

        public boolean onLongClick(View v) {
            currentPos = getAdapterPosition(); //key point, record the position here
            return false;
        }
    }

    // adapter class definitions

    private List<Ride> rideListData;   // list of book objects
    private Context mContext;       // activity context
    private int currentPos;         // currently selected item (long press)

    public RideAdapter(Context context, List<Ride> listData) {
        rideListData = listData;
        mContext = context;
    }

    private Context getmContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate layout using the single item layout
        View view = inflater.inflate(R.layout.rides_list_item, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // bind data to the view holder instance
        Ride m = rideListData.get(position);
        holder.tvOrigin.setText(m.getOrigin());
        holder.tvDestination.setText(m.getDestination());
        holder.tvTime.setText(m.getTime());
        holder.tvDriver.setText(m.getDriver());
    }

    @Override
    public int getItemCount() {
        return rideListData.size();
    }

    /**
     * return book object for currently selected book (index already set by long press in viewholder)
     * @return
     */
    public Ride getSelectedItem() {
        // return the book record if the current selected position/index is valid
        if (currentPos >= 0 && rideListData != null && currentPos < rideListData.size()) {
            return rideListData.get(currentPos);
        }
        return null;
    }

}
