package co.edu.uniquindio.campusuq.util;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.DishesActivity;
import co.edu.uniquindio.campusuq.vo.Dish;

public class DishesAdapter extends RecyclerView.Adapter<DishesAdapter.DishViewHolder> {

    private ArrayList<Dish> dishes;
    private OnClickDishListener listener;

    public DishesAdapter(ArrayList<Dish> dishes, DishesActivity dishesActivity) {
        this.dishes = dishes;
        listener = dishesActivity;
    }

    public class DishViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView image;
        private TextView name;
        private TextView description;
        private TextView price;

        DishViewHolder(View view) {
            super(view);

            image = view.findViewById(R.id.dish_image);
            name = view.findViewById(R.id.dish_name);
            description = view.findViewById(R.id.dish_description);
            price = view.findViewById(R.id.dish_price);

            view.findViewById(R.id.dish_layout).setOnClickListener(this);
        }

        void bindItem(Dish dish) {
            File imageFile = new File(dish.getImage());
            if (imageFile.exists()) image.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
            else image.setImageResource(R.drawable.rectangle_gray);

            name.setText(dish.getName());
            description.setText(dish.getDescription());
            price.setText("$ "+dish.getPrice());
        }

        @Override
        public void onClick(View view) {
            listener.onDishClick(getAdapterPosition());
        }
    }

    @Override
    public DishViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DishViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dish_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(DishViewHolder holder, int position) {
        holder.bindItem(dishes.get(position));
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    public void setDishes(ArrayList<Dish> dishes) {
        this.dishes = dishes;
        notifyDataSetChanged();
    }

    public interface OnClickDishListener {
        void onDishClick(int index);
    }

}
