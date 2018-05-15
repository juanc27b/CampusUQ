package co.edu.uniquindio.campusuq.quotas;

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
 * Adaptador de cupos.
 */
public class QuotasAdapter extends RecyclerView.Adapter<QuotasAdapter.QuotaViewHolder> {

    private ArrayList<Quota> quotas;
    private OnClickQuotaListener listener;

    /**
     * Constructor que asigna el arreglo de cupos inicial y la interfaz para redireccionar el
     * procesamiento del click en un cupo.
     * @param quotas Arreglo de cupos.
     * @param quotasActivity Actividad que implementa la interfaz OnClickQuotaListener.
     */
    QuotasAdapter(ArrayList<Quota> quotas, QuotasActivity quotasActivity) {
        this.quotas = quotas;
        listener = quotasActivity;
    }

    /**
     * Portador de vistas de cupos, el cual está atento a clicks y clicks largos.
     */
    public class QuotaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        private ImageView icon;
        private TextView name;
        private TextView quota;

        /**
         * Obtiene los objetos de vista a partir de sus identificadores y asigna los listener de
         * click.
         * @param view Vista de un item en la cual buscar las subvistas que se controlan en el
         *             adaptador.
         */
        QuotaViewHolder(View view) {
            super(view);

            icon = view.findViewById(R.id.quota_icon);
            name = view.findViewById(R.id.quota_name);
            quota = view.findViewById(R.id.quota_quota);
            LinearLayout layout = view.findViewById(R.id.quota_layout);

            layout.setOnClickListener(this);
            layout.setOnLongClickListener(this);
        }

        /**
         * Asigna los valores de las vistas a partir del cupo suministrado.
         * @param q Cupo a visualizar.
         */
        void bindItem(Quota q) {
            icon.setImageResource("0".equals(q.getQuota()) ?
                    R.drawable.circle_gray : R.drawable.circle_green);
            name.setText(q.getName());
            quota.setText(q.getQuota());
        }

        /**
         * Redirige hacia la funcion en la actividad que procesara el click en el cupo.
         * @param view Vista en la que se ha hecho click (no utilizado).
         */
        @Override
        public void onClick(View view) {
            listener.onQuotaClick(true, getAdapterPosition());
        }

        /**
         * Redirige hacia la funcion en la actividad que procesara el click largo en el cupo.
         * @param view Vista en la que se ha hecho click largo (ignorado).
         * @return Indicador de procesamiento del click largo.
         */
        @Override
        public boolean onLongClick(View view) {
            listener.onQuotaClick(false, getAdapterPosition());
            return true;
        }

    }

    /**
     * Crea el portador de cupos inflando su diseño.
     * @param parent Vista donde inflar el portador de cupos.
     * @param viewType Tipo de la vista (no utilizado).
     * @return Nuevo portador de cupos creado.
     */
    @Override
    public QuotaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new QuotaViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quota_detail, parent, false));
    }

    /**
     * Vincula los valores del cupo con sus vistas.
     * @param holder Portador de cupos.
     * @param position Posición del cupo en el arreglo de cupos.
     */
    @Override
    public void onBindViewHolder(QuotaViewHolder holder, int position) {
        holder.bindItem(quotas.get(position));
    }

    /**
     * Obtiene la cantidad de ítems en el arreglo de cupos.
     * @return Cantidad de ítems.
     */
    @Override
    public int getItemCount() {
        return quotas.size();
    }

    /**
     * Obtiene el arreglo de cupos.
     * @return Arreglo de cupos.
     */
    public ArrayList<Quota> getQuotas() {
        return quotas;
    }

    /**
     * Asigna el nuevo arreglo de cupos y notifica que los datos han cambiado.
     * @param quotas Arreglo de cupos.
     */
    public void setQuotas(ArrayList<Quota> quotas) {
        this.quotas = quotas;
        notifyDataSetChanged();
    }

    /**
     * Interfaz para ser implementada por una actividad hacia la cual se redireccionará el
     * procesamiento del click.
     */
    public interface OnClickQuotaListener {
        void onQuotaClick(boolean fragment_quotas, int index);
    }

}
