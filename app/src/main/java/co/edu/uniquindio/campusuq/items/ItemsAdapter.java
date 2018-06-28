package co.edu.uniquindio.campusuq.items;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;

/**
 * Adaptador de ítems.
 */
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {

    private static int colorPrimary = 0;

    private ArrayList<Item> items;
    private OnClickItemListener listener;

    /**
     * Constructor que asigna el arreglo de ítems y la interfaz para redireccionar el procesamiento
     * del click en un ítem, adicionalmente utiliza el contexto de la actividad pasada como
     * parametro para optener el color primario de los recursos.
     * @param items Arreglo de ítems.
     * @param itemsActivity Actividad que implementa la interfaz OnClickItemListener.
     */
    ItemsAdapter(ArrayList<Item> items, ItemsActivity itemsActivity) {
        if (colorPrimary == 0) {
            colorPrimary = ContextCompat.getColor(itemsActivity, R.color.colorPrimary);
        }

        this.items = items;
        listener = itemsActivity;
    }

    /**
     * Portador de vistas de ítems, el cual está atento a clicks.
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView circleView;
        private ImageView itemIcon;
        private TextView itemTitle;
        private TextView itemDescription;

        /**
         * Obtiene los objetos de vista a partir de sus identificadores y asigna los listener de
         * click.
         * @param itemView Vista de un item en la cual buscar las subvistas que se controlan en el
         *                 adaptador.
         */
        ItemViewHolder(View itemView) {
            super(itemView);

            circleView = itemView.findViewById(R.id.background_circle);
            itemIcon = itemView.findViewById(R.id.item_icon);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemDescription = itemView.findViewById(R.id.item_description);

            itemView.setOnClickListener(this);
        }

        /**
         * Asigna los valores de las vistas a partir del ítem suministrado.
         * @param item Ítem a visualizar.
         */
        void bindItem(Item item) {
            circleView.setBackgroundResource(item.getBackground());

            if (item.getImage() != 0) {
                itemIcon.setVisibility(View.VISIBLE);
                itemIcon.setImageResource(item.getImage());
                itemIcon.setColorFilter(item.getBackground() == R.drawable.circle_white ?
                        colorPrimary : Color.WHITE);
            } else {
                itemIcon.setVisibility(View.GONE);
            }

            itemTitle.setText(item.getTitle());
            itemDescription.setText(Html.fromHtml(item.getDescription()));
        }

        /**
         * Redirige hacia la funcion en la actividad que procesara el click en el ítem.
         * @param view Vista en la que se ha hecho click.
         */
        @Override
        public void onClick(View view) {
            view.setEnabled(false);
            listener.onItemClick(getAdapterPosition());
            view.setEnabled(true);
        }

    }

    /**
     * Crea el portador de ítems inflando su diseño.
     * @param parent Vista donde inflar el portador de ítems.
     * @param viewType Tipo de la vista (no utilizado).
     * @return Nuevo portador de ítems creado.
     */
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail, parent, false));
    }

    /**
     * Vincula los valores del ítem con sus vistas.
     * @param holder Portador de ítems.
     * @param position Posición del ítem en el arreglo de ítems.
     */
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.bindItem(items.get(position));
    }

    /**
     * Obtiene la cantidad de ítems en el arreglo.
     * @return Cantidad de ítems.
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Obtiene el arrego de ítems.
     * @return Arreglo de ítems
     */
    ArrayList<Item> getItems() {
        return items;
    }

    /**
     * Asigna el nuevo arreglo de ítems y notifica que los datos han cambiado.
     * @param items Arreglo de ítems.
     */
    public void setItems(ArrayList<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    /**
     * Interfaz para ser implementada por una actividad hacia la cual se redireccionará el
     * procesamiento del click.
     */
    public interface OnClickItemListener {
        void onItemClick(int pos);
    }

}
