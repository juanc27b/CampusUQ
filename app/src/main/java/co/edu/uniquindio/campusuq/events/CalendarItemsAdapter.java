package co.edu.uniquindio.campusuq.events;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;

/**
 * Adaptador de ítems de calendario.
 */
public class CalendarItemsAdapter
        extends RecyclerView.Adapter<CalendarItemsAdapter.CalendarItemViewHolder> {

    private ArrayList<CalendarItem> mItems;
    private OnClickItemListener listener;

    /**
     * Constructor que asigna el arreglo de ítems de calendario inicial y la interfaz para
     * redireccionar el procesamiento del click en un ítem de calendario.
     * @param mItems Arreglo de ítems de calendario.
     * @param calendarActivity Actividad que implementa la interfaz OnClickItemListener.
     */
    CalendarItemsAdapter(ArrayList<CalendarItem> mItems, CalendarActivity calendarActivity) {
        this.mItems = mItems;
        listener = calendarActivity;
    }

    /**
     * Portador de vistas de ítems de calendario, el cual está atento a clicks.
     */
    public class CalendarItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView calendarEvent;
        TextView calendarDate;

        /**
         * Obtiene los objetos de vista a partir de sus identificadores y asigna los listener de
         * click.
         * @param itemView Vista de un item en la cual buscar las subvistas que se controlan en el
         *                 adaptador.
         */
        CalendarItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            calendarEvent = itemView.findViewById(R.id.calendar_event);
            calendarDate = itemView.findViewById(R.id.calendar_event_date);
        }

        /**
         * Asigna los valores de las vistas a partir del ítem de calendario suministrado.
         * @param calendarItem Ítem de calendario a visualizar.
         */
        void bindItem(CalendarItem calendarItem) {
            calendarEvent.setText(calendarItem.getEvent());
            calendarDate.setText(calendarItem.getDate());
        }

        /**
         * Redirige hacia la funcion en la actividad que procesara el click en el ítem de
         * calendario.
         * @param view Vista en la que se ha hecho click (no utilizado).
         */
        @Override
        public void onClick(View view) {
            listener.onCalendarItemClick(getAdapterPosition());
        }

    }

    /**
     * Crea el portador de ítems de calendario inflando su diseño.
     * @param parent Vista donde inflar el portador de ítems de calendario.
     * @param viewType Tipo de la vista (no utilizado).
     * @return Nuevo portador de ítems de calendario creado.
     */
    @Override
    public CalendarItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CalendarItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_item, parent, false));
    }

    /**
     * Vincula los valores del ítem de calendario con sus vistas.
     * @param holder Portador de ítems de calendario.
     * @param position Posición del ítem de calendario en el arreglo de ítems de calendario.
     */
    @Override
    public void onBindViewHolder(CalendarItemViewHolder holder, int position) {
        holder.bindItem(mItems.get(position));

    }

    /**
     * Obtiene la cantidad de ítems en el arreglo de ítems de calendario.
     * @return Cantidad de ítems.
     */
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Obtiene el arreglo de ítems de calendario.
     * @return Arreglo de ítems de calendario.
     */
    public ArrayList<CalendarItem> getItems() {
        return mItems;
    }

    /**
     * Asigna el nuevo arreglo de ítems de calendario y notifica que los datos han cambiado.
     * @param mItems Arreglo de ítems de calendario.
     */
    public void setItems(ArrayList<CalendarItem> mItems) {
        this.mItems = mItems;
        notifyDataSetChanged();
    }

    /**
     * Interfaz para ser implementada por una actividad hacia la cual se redireccionará el
     * procesamiento del click.
     */
    public interface OnClickItemListener {
        void onCalendarItemClick(int pos);
    }

}
