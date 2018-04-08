package co.edu.uniquindio.campusuq.events;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;

/**
 * Created by Juan Camilo on 1/03/2018.
 */

public class CalendarDetailItemsAdapter extends
        RecyclerView.Adapter<CalendarDetailItemsAdapter.CalendarDetailItemViewHolder> {

    private ArrayList<CalendarDetailItem> mItems;

    // Provide a suitable constructor (depends on the kind of dataset)
    CalendarDetailItemsAdapter(ArrayList<CalendarDetailItem> mItems) {
        this.mItems = mItems;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class CalendarDetailItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ImageView start_flag;
        private LinearLayout endLayout;
        private TextView period;
        private TextView start;
        private TextView end;

        CalendarDetailItemViewHolder(View itemView) {
            super(itemView);

            start_flag = itemView.findViewById(R.id.calendar_event_start_flag);
            endLayout = itemView.findViewById(R.id.end_date_layout);
            period = itemView.findViewById(R.id.calendar_event_period);
            start = itemView.findViewById(R.id.calendar_event_start);
            end = itemView.findViewById(R.id.calendar_event_end);
        }

        void bindItem(CalendarDetailItem i) {
            period.setText(i.getPeriod());
            start.setText(i.getStart());

            if (i.getStart().length() >= 8 && i.getStart().substring(0, 8).equals("Inicio: ")) {
                start_flag.setImageResource(R.drawable.green_flag);
            } else if (i.getStart().length() >= 5 && i.getStart().substring(0, 5).equals("Fin: ")) {
                start_flag.setImageResource(R.drawable.red_flag);
            } else {
                start_flag.setImageResource(R.drawable.yellow_flag);
            }

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
    public CalendarDetailItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CalendarDetailItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_detail, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CalendarDetailItemViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bindItem(mItems.get(position));

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
