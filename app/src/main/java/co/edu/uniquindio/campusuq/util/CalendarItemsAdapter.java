package co.edu.uniquindio.campusuq.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.CalendarActivity;
import co.edu.uniquindio.campusuq.vo.CalendarItem;

/**
 * Created by Juan Camilo on 1/03/2018.
 */

public class CalendarItemsAdapter extends RecyclerView.Adapter<CalendarItemsAdapter.CalendarItemViewHolder> {

    private ArrayList<CalendarItem> mItems;
    private static CalendarItemsAdapter.OnClickItemListener listener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public CalendarItemsAdapter(ArrayList<CalendarItem> mItems, CalendarActivity calendarActivity) {
        this.mItems = mItems;
        listener = (CalendarItemsAdapter.OnClickItemListener) calendarActivity;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class CalendarItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView calendarEvent;
        public TextView calendarDate;

        public CalendarItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            calendarEvent = (TextView) itemView.findViewById(R.id.calendar_event);
            calendarDate = (TextView) itemView.findViewById(R.id.calendar_event_date);
        }

        public void bindItem(CalendarItem i) {
            calendarEvent.setText(i.getEvent());
            calendarDate.setText(i.getDate());
        }

        @Override
        public void onClick(View v) {
            listener.onCalendarItemClick(getAdapterPosition());
        }

    }

    // Create new views (invoked by the layout manager)
    @Override
    public CalendarItemsAdapter.CalendarItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...
        CalendarItemsAdapter.CalendarItemViewHolder itemVH = new CalendarItemsAdapter.CalendarItemViewHolder(itemView);
        return itemVH;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CalendarItemsAdapter.CalendarItemViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        CalendarItem item = mItems.get(position);
        holder.bindItem(item);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setItems(ArrayList<CalendarItem> mItems) {
        this.mItems = mItems;
        notifyDataSetChanged();
    }

    public interface OnClickItemListener {
        void onCalendarItemClick(int pos);
    }

}
