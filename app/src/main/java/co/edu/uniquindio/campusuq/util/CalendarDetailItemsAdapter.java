package co.edu.uniquindio.campusuq.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.vo.CalendarDetailItem;

/**
 * Created by Juan Camilo on 1/03/2018.
 */

public class CalendarDetailItemsAdapter extends RecyclerView.Adapter<CalendarDetailItemsAdapter.CalendarDetailItemViewHolder> {

    private ArrayList<CalendarDetailItem> mItems;

    // Provide a suitable constructor (depends on the kind of dataset)
    public CalendarDetailItemsAdapter(ArrayList<CalendarDetailItem> mItems) {
        this.mItems = mItems;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class CalendarDetailItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout endLayout;
        public TextView period;
        public TextView start;
        public TextView end;

        public CalendarDetailItemViewHolder(View itemView) {
            super(itemView);
            endLayout = (LinearLayout) itemView.findViewById(R.id.end_date_layout);
            period = (TextView) itemView.findViewById(R.id.calendar_event_period);
            start = (TextView) itemView.findViewById(R.id.calendar_event_start);
            end = (TextView) itemView.findViewById(R.id.calendar_event_end);
        }

        public void bindItem(CalendarDetailItem i) {
            period.setText(i.getPeriod());
            start.setText(i.getStart());
            if (i.getEnd() != null) {
                endLayout.setVisibility(View.VISIBLE);
                end.setText(i.getEnd());
            } else {
                endLayout.setVisibility(View.GONE);
            }
        }

    }

    // Create new views (invoked by the layout manager)
    @Override
    public CalendarDetailItemsAdapter.CalendarDetailItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_detail, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...
        CalendarDetailItemsAdapter.CalendarDetailItemViewHolder itemVH =
                new CalendarDetailItemsAdapter.CalendarDetailItemViewHolder(itemView);
        return itemVH;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CalendarDetailItemsAdapter.CalendarDetailItemViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        CalendarDetailItem item = mItems.get(position);
        holder.bindItem(item);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setItems(ArrayList<CalendarDetailItem> mItems) {
        this.mItems = mItems;
        notifyDataSetChanged();
    }

}
