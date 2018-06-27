package co.edu.uniquindio.campusuq.emails;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.items.ItemsPresenter;

/**
 * Adaptador de correos.
 */
public class EmailsAdapter extends RecyclerView.Adapter<EmailsAdapter.EmailViewHolder> {

    private ArrayList<Email> emails;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssZ", new Locale("es", "CO"));
    private OnClickEmailListener listener;

    /**
     * Constructor que asigna el arreglo de correos inicial y la interfaz para redireccionar el
     * procesamiento del click en un correo.
     * @param emails Arreglo de correos.
     * @param emailsActivity Actividad que implementa la interfaz OnClickEmailListener.
     */
    EmailsAdapter(ArrayList<Email> emails, EmailsActivity emailsActivity) {
        this.emails = emails;
        listener = emailsActivity;
    }

    /**
     * Portador de vistas de carreos, el cual está atento a clicks.
     */
    public class EmailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView icon;
        private TextView date;
        private TextView from;
        private TextView content;

        /**
         * Obtiene los objetos de vista a partir de sus identificadores y asigna los listener de
         * click.
         * @param itemView Vista de un item en la cual buscar las subvistas que se controlan en el
         *                 adaptador.
         */
        EmailViewHolder(View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.email_icon);
            date = itemView.findViewById(R.id.email_date);
            from = itemView.findViewById(R.id.email_from);
            content = itemView.findViewById(R.id.email_content);

            itemView.findViewById(R.id.email_layout).setOnClickListener(this);
        }

        /**
         * Asigna los valores de las vistas a partir del correo suministrado.
         * @param email Correo a visualizar.
         */
        void bindItem(Email email) {
            icon.setText(email.getFrom().isEmpty() ?
                    "" : email.getFrom().substring(0, 1).toUpperCase());
            icon.setBackgroundResource(ItemsPresenter.getColor());

            try {
                date.setText(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT)
                        .format(simpleDateFormat.parse(email.getDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            from.setText(email.getFrom());
            content.setText(email.getSnippet());
        }

        /**
         * Redirige hacia la funcion en la actividad que procesara el click en el correo.
         * @param view Vista en la que se ha hecho click.
         */
        @Override
        public void onClick(View view) {
            listener.onEmailClick(getAdapterPosition());
        }

    }

    /**
     * Crea el portador de correos inflando su diseño.
     * @param parent Vista donde inflar el portador de correos.
     * @param viewType Tipo de la vista (no utilizado).
     * @return Nuevo portador de correos creado.
     */
    @Override
    public EmailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EmailViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.email_detail, parent, false));
    }

    /**
     * Vincula los valores del correo con sus vistas.
     * @param holder Portador de correos.
     * @param position Posición del correo en el arreglo de correos.
     */
    @Override
    public void onBindViewHolder(EmailViewHolder holder, int position) {
        holder.bindItem(emails.get(position));
    }

    /**
     * Obtiene la cantidad de ítems en el arreglo de correos.
     * @return Cantidad de ítems.
     */
    @Override
    public int getItemCount() {
        return emails.size();
    }

    /**
     * Obtiene el arreglo de correos.
     * @return Arreglo de correos.
     */
    public ArrayList<Email> getEmails() {
        return emails;
    }

    /**
     * Asigna el nuevo arreglo de correos y notifica que los datos han cambiado.
     * @param emails Arreglo de correos.
     */
    public void setEmails(ArrayList<Email> emails) {
        this.emails = emails;
        notifyDataSetChanged();
    }

    /**
     * Interfaz para ser implementada por una actividad hacia la cual se redireccionará el
     * procesamiento del click.
     */
    public interface OnClickEmailListener {
        void onEmailClick(int index);
    }

}
