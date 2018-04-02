package co.edu.uniquindio.campusuq.items;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;

/**
 * Created by Juan Camilo on 7/02/2018.
 */

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {

    private ArrayList<Item> items;
    private OnClickItemListener listener;

    // Provide a suitable constructor (depends on the kind of dataset)
    ItemsAdapter(ArrayList<Item> items, ItemsActivity itemsActivity) {
        this.items = items;
        listener = itemsActivity;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        View circleView;
        ImageView itemIcon;
        TextView itemTitle;
        TextView itemDescription;

        ItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            circleView = itemView.findViewById(R.id.background_circle);
            itemIcon = itemView.findViewById(R.id.item_icon);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemDescription = itemView.findViewById(R.id.item_description);
        }

        void bindItem(Item i) {
            circleView.setBackgroundResource(i.getBackground());
            if (i.getImage() != 0) {
                itemIcon.setVisibility(View.VISIBLE);
                itemIcon.setImageResource(i.getImage());
                itemIcon.setColorFilter(Color.WHITE);
            } else {
                itemIcon.setVisibility(View.GONE);
            }
            itemTitle.setText(i.getTitle());
            itemDescription.setText(i.getDescription());
        }

        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            listener.onItemClick(getAdapterPosition());
            v.setEnabled(true);
        }

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bindItem(items.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public interface OnClickItemListener {
        void onItemClick(int pos);
    }

}
