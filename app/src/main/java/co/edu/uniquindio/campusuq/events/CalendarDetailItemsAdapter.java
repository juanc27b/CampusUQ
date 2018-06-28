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
 * Adaptador de ítems de detalle de calendario.
 */
public class CalendarDetailItemsAdapter extends
        RecyclerView.Adapter<CalendarDetailItemsAdapter.CalendarDetailItemViewHolder> {

    private ArrayList<CalendarDetailItem> mItems;

    /**
     * Constructor que asigna el arreglo de ítems de detalle de calendario inicial.
     * @param mItems Arreglo de ítems de detalle de calendario.
     */
    CalendarDetailItemsAdapter(ArrayList<CalendarDetailItem> mItems) {
        this.mItems = mItems;
    }

    /**
     * Portador de vistas de ítems de detalle de calendario.
     */
    static class CalendarDetailItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView start_flag;
        private LinearLayout endLayout;
        private TextView period;
        private TextView start;
        private TextView end;

        /**
         * Obtiene los objetos de vista a partir de sus identificadores.
         * @param itemView Vista de un item en la cual buscar las subvistas que se controlan en el
         *                 adaptador.
         */
        CalendarDetailItemViewHolder(View itemView) {
            super(itemView);

            start_flag = itemView.findViewById(R.id.calendar_event_start_flag);
            endLayout = itemView.findViewById(R.id.end_date_layout);
            period = itemView.findViewById(R.id.calendar_event_period);
            start = itemView.findViewById(R.id.calendar_event_start);
            end = itemView.findViewById(R.id.calendar_event_end);
        }

        /**
         * Asigna los valores de las vistas a partir del ítem de detalle de calendario suministrado.
         * @param calendarDetailItem Ítem de detalle de calendario a visualizar.
         */
        void bindItem(CalendarDetailItem calendarDetailItem) {
            period.setText(calendarDetailItem.getPeriod());
            start.setText(calendarDetailItem.getStart());

            if (calendarDetailItem.getStart().length() >= 8 &&
                    "Inicio: ".equals(calendarDetailItem.getStart().substring(0, 8))) {
                start_flag.setImageResource(R.drawable.green_flag);
            } else if (calendarDetailItem.getStart().length() >= 5 &&
                    "Fin: ".equals(calendarDetailItem.getStart().substring(0, 5))) {
                start_flag.setImageResource(R.drawable.red_flag);
            } else {
                start_flag.setImageResource(R.drawable.yellow_flag);
            }

            if (calendarDetailItem.getEnd() != null) {
                endLayout.setVisibility(View.VISIBLE);
                end.setText(calendarDetailItem.getEnd());
            } else {
                endLayout.setVisibility(View.GONE);
            }
        }

    }

    /**
     * Crea el portador de ítems de detalle de calendario inflando su diseño.
     * @param parent Vista donde inflar el portador de ítems de detalle de calendario.
     * @param viewType Tipo de la vista (no utilizado).
     * @return Nuevo portador de ítems de detalle de calendario creado.
     */
    @Override
    public CalendarDetailItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CalendarDetailItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_detail, parent, false));
    }

    /**
     * Vincula los valores del ítem de detalle de calendario con sus vistas.
     * @param holder Portador de ítems de detalle de calendario.
     * @param position Posición del ítem de detalle de calendario en el arreglo de ítems de detalle
     *                 de calendario.
     */
    @Override
    public void onBindViewHolder(CalendarDetailItemViewHolder holder, int position) {
        holder.bindItem(mItems.get(position));

    }

    /**
     * Obtiene la cantidad de ítems en el arreglo de ítems de detalle de calendario.
     * @return Cantidad de ítems.
     */
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Obtiene el arreglo de ítems de detalle de calendario.
     * @return Arreglo de ítems de detalle de calendario.
     */
    public ArrayList<CalendarDetailItem> getItems() {
        return mItems;
    }

    /**
     * Asigna el nuevo arreglo de ítems de detalle de calendario y notifica que los datos han
     * cambiado.
     * @param mItems Arreglo de ítems de detalle de calendario.
     */
    public void setItems(ArrayList<CalendarDetailItem> mItems) {
        this.mItems = mItems;
        notifyDataSetChanged();
    }

}
