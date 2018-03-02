package co.edu.uniquindio.campusuq.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.DishesActivity;
import co.edu.uniquindio.campusuq.activity.QuotasActivity;
import co.edu.uniquindio.campusuq.vo.Dish;
import co.edu.uniquindio.campusuq.vo.Quota;

public class DishesAdapter extends RecyclerView.Adapter<DishesAdapter.DishViewHolder> {
    public static class DishViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView name, description, price;

        public DishViewHolder(View view) {
            super(view);
            view.findViewById(R.id.dish_layout).setOnClickListener(this);
            image = view.findViewById(R.id.dish_image);
            name = view.findViewById(R.id.dish_name);
            description = view.findViewById(R.id.dish_description);
            price = view.findViewById(R.id.dish_price);
        }

        public void bindItem(Dish dish) {
            name.setText(dish.getName());
            description.setText(dish.getDescription());
            price.setText("$ "+dish.getPrice());
        }

        @Override
        public void onClick(View view) {
            listener.onDishClick(getAdapterPosition());
        }
    }

    private ArrayList<Dish> mDishes;
    private static OnClickDishListener listener;

    public DishesAdapter(ArrayList<Dish> mDishes, DishesActivity dishesActivity) {
        this.mDishes = mDishes;
        listener = dishesActivity;
    }

    @Override
    public DishViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DishViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dish_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(DishViewHolder holder, int position) {
        holder.bindItem(mDishes.get(position));
    }

    @Override
    public int getItemCount() {
        return mDishes.size();
    }

    public void setDishes(ArrayList<Dish> mDishes) {
        this.mDishes = mDishes;
        notifyDataSetChanged();
    }

    public interface OnClickDishListener {
        void onDishClick(int pos);
    }
}
