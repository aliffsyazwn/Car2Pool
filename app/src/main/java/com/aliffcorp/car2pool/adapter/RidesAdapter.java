package com.aliffcorp.car2pool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aliffcorp.car2pool.R;
import com.aliffcorp.car2pool.model.Rides;

import java.util.List;

public class RidesAdapter extends RecyclerView.Adapter<RidesAdapter.ViewHolder> {

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

    private List<Rides> ridesListData;   // list of book objects
    private Context mContext;       // activity context
    private int currentPos;         // currently selected item (long press)

    public RidesAdapter(Context context, List<Rides> listData) {
        ridesListData = listData;
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
        Rides m = ridesListData.get(position);
        holder.tvOrigin.setText(m.getOrigin());
        holder.tvDestination.setText(m.getDestination());
        holder.tvTime.setText(m.getTime());
        holder.tvDriver.setText(m.getDriver());
    }

    @Override
    public int getItemCount() {
        return ridesListData.size();
    }

    /**
     * return book object for currently selected book (index already set by long press in viewholder)
     * @return
     */
    public Rides getSelectedItem() {
        // return the book record if the current selected position/index is valid
        if (currentPos >= 0 && ridesListData != null && currentPos < ridesListData.size()) {
            return ridesListData.get(currentPos);
        }
        return null;
    }

}
