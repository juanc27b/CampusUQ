package co.edu.uniquindio.campusuq.dishes;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Adaptador de platos.
 */
public class DishesAdapter extends RecyclerView.Adapter<DishesAdapter.DishViewHolder> {

    static final String DISH  = "dish";
    static final String IMAGE = "image";

    private ArrayList<Dish> dishes;
    private OnClickDishListener listener;

    /**
     * Constructor que asigna el arreglo de platos inicial y la interfaz para redireccionar el
     * procesamiento del click en un plato.
     * @param dishes Arreglo de cupos.
     * @param dishesActivity Actividad que implementa la interfaz OnClickQuotaListener.
     */
    DishesAdapter(ArrayList<Dish> dishes, DishesActivity dishesActivity) {
        this.dishes = dishes;
        listener = dishesActivity;
    }

    /**
     * Portador de vistas de platos, el cual está atento a clicks.
     */
    public class DishViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView image;
        private TextView name;
        private TextView description;
        private TextView price;

        /**
         * Obtiene los objetos de vista a partir de sus identificadores y asigna los listener de
         * click.
         * @param view Vista de un item en la cual buscar las subvistas que se controlan en el
         *             adaptador.
         */
        DishViewHolder(View view) {
            super(view);

            image = view.findViewById(R.id.dish_image);
            name = view.findViewById(R.id.dish_name);
            description = view.findViewById(R.id.dish_description);
            price = view.findViewById(R.id.dish_price);

            view.findViewById(R.id.dish_layout).setOnClickListener(this);
            image.setOnClickListener(this);
        }

        /**
         * Asigna los valores de las vistas a partir del plato suministrado.
         * @param dish Plato a visualizar.
         */
        void bindItem(Dish dish) {
            // Se concatena una cadena vacia para evitar el caso File(null)
            File imageFile = new File("" + dish.getImage());

            if (imageFile.exists()) {
                image.setImageBitmap(Utilities.getResizedBitmap(BitmapFactory
                        .decodeFile(imageFile.getAbsolutePath())));
            } else {
                image.setImageResource(R.drawable.rectangle_gray);
            }

            name.setText(dish.getName());
            description.setText(dish.getDescription());
            price.setText(NumberFormat
                    .getCurrencyInstance().format(Long.parseLong(dish.getPrice())));
        }

        /**
         * Redirige hacia la funcion en la actividad que procesara el click en el cupo asignando una
         * accion que depende de a cual vista se le ha dado click.
         * @param view Vista en la que se ha hecho click.
         */
        @Override
        public void onClick(View view) {
            String action;

            switch (view.getId()) {
                case R.id.dish_layout: action = DISH       ; break;
                case R.id.dish_image : action = IMAGE      ; break;
                default              : action = "undefined"; break;
            }

            listener.onDishClick(getAdapterPosition(), action);
        }

    }

    /**
     * Crea el portador de platos inflando su diseño.
     * @param parent Vista donde inflar el portador de cupos.
     * @param viewType Tipo de la vista (no utilizado).
     * @return Nuevo portador de platos creado.
     */
    @Override
    public DishViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DishViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dish_detail, parent, false));
    }

    /**
     * Vincula los valores del plato con sus vistas.
     * @param holder Portador de platos.
     * @param position Posición del plato en el arreglo de platos.
     */
    @Override
    public void onBindViewHolder(DishViewHolder holder, int position) {
        holder.bindItem(dishes.get(position));
    }

    /**
     * Obtiene la cantidad de ítems en el arreglo de platos.
     * @return Cantidad de ítems.
     */
    @Override
    public int getItemCount() {
        return dishes.size();
    }

    /**
     * Obtiene el arreglo de platos.
     * @return Arreglo de platos.
     */
    public ArrayList<Dish> getDishes() {
        return dishes;
    }

    /**
     * Asigna el nuevo arreglo de platos y notifica que los datos han cambiado.
     * @param dishes Arreglo de platos.
     */
    public void setDishes(ArrayList<Dish> dishes) {
        this.dishes = dishes;
        notifyDataSetChanged();
    }

    /**
     * Interfaz para ser implementada por una actividad hacia la cual se redireccionará el
     * procesamiento del click.
     */
    public interface OnClickDishListener {
        void onDishClick(int index, String action);
    }

}
