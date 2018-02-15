package co.edu.uniquindio.campusuq.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.ItemsActivity;
import co.edu.uniquindio.campusuq.vo.Item;

/**
 * Created by Juan Camilo on 7/02/2018.
 */

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {

    private ArrayList<Item> mItems;
    private static OnClickItemListener listener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ItemsAdapter(ArrayList<Item> mItems, ItemsActivity itemsActivity) {
        this.mItems = mItems;
        listener = (OnClickItemListener) itemsActivity;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public View circleView;
        public ImageView itemIcon;
        public TextView itemTitle;
        public TextView itemDescription;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            circleView = itemView.findViewById(R.id.background_circle);
            itemIcon = (ImageView) itemView.findViewById(R.id.item_icon); 
            itemTitle = (TextView) itemView.findViewById(R.id.item_title);
            itemDescription = (TextView) itemView.findViewById(R.id.item_description);
        }

        public void bindItem(Item i) {
            circleView.setBackgroundResource(i.getBackground());
            itemIcon.setImageResource(i.getImage());
            itemTitle.setText(i.getTitle());
            itemDescription.setText(i.getDescription());
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition());
        }

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...
        ItemViewHolder itemVH = new ItemViewHolder(itemView);
        return itemVH;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Item item = mItems.get(position);
        holder.bindItem(item);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setItems(ArrayList<Item> mItems) {
        this.mItems = mItems;
        notifyDataSetChanged();
    }

    public interface OnClickItemListener {
        void onItemClick(int pos);
    }

}
