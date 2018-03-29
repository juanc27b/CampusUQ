package co.edu.uniquindio.campusuq.events;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;

/**
 * Created by Juan Camilo on 1/03/2018.
 */

public class CalendarItemsAdapter extends RecyclerView.Adapter<CalendarItemsAdapter.CalendarItemViewHolder> {

    private ArrayList<CalendarItem> mItems;
    private OnClickItemListener listener;

    // Provide a suitable constructor (depends on the kind of dataset)
    CalendarItemsAdapter(ArrayList<CalendarItem> mItems, CalendarActivity calendarActivity) {
        this.mItems = mItems;
        listener = calendarActivity;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class CalendarItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        TextView calendarEvent;
        TextView calendarDate;

        CalendarItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            calendarEvent = itemView.findViewById(R.id.calendar_event);
            calendarDate = itemView.findViewById(R.id.calendar_event_date);
        }

        void bindItem(CalendarItem i) {
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
    public CalendarItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...
        return new CalendarItemViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CalendarItemViewHolder holder, int position) {
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
