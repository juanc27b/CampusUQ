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
        private ImageView circleView;
        private ImageView itemIcon;
        private TextView itemTitle;
        private TextView itemDescription;

        ItemViewHolder(View view) {
            super(view);

            circleView = view.findViewById(R.id.background_circle);
            itemIcon = view.findViewById(R.id.item_icon);
            itemTitle = view.findViewById(R.id.item_title);
            itemDescription = view.findViewById(R.id.item_description);

            view.setOnClickListener(this);
        }

        void bindItem(Item item) {
            circleView.setBackgroundResource(item.getBackground());

            if (item.getImage() != 0) {
                itemIcon.setVisibility(View.VISIBLE);
                itemIcon.setImageResource(item.getImage());
                itemIcon.setColorFilter(item.getBackground() == R.drawable.circle_white ?
                        0xff47a22c : Color.WHITE);
            } else {
                itemIcon.setVisibility(View.GONE);
            }

            itemTitle.setText(item.getTitle());
            itemDescription.setText(item.getDescription());
        }

        @Override
        public void onClick(View view) {
            view.setEnabled(false);
            listener.onItemClick(getAdapterPosition());
            view.setEnabled(true);
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

    ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public interface OnClickItemListener {
        void onItemClick(int pos);
    }

}
