package co.edu.uniquindio.campusuq.notifications;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import co.edu.uniquindio.campusuq.R;

/**
 * Adaptador de notificaciones.
 */
public class NotificationsDetailAdapter
        extends RecyclerView.Adapter<NotificationsDetailAdapter.NotificationViewHolder> {

    private ArrayList<NotificationDetail> notificationDetails;
    private ArrayList<Boolean> checkeds;
    private Context context;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssZ", new Locale("es", "CO"));
    private OnClickNotificationListener listener;
    private boolean checkBoxes = false;

    /**
     * Constructor que asigna el arreglo de notificaciones inicial y la interfaz para
     * redireccionar el procesamiento del click en una notificación.
     * @param notificationDetails Arreglo de notificaciones.
     * @param notificationsDetailActivity Actividad que implementa la interfaz
     *                                    OnClickNotificationListener.
     */
    NotificationsDetailAdapter(ArrayList<NotificationDetail> notificationDetails,
                               ArrayList<Boolean> checkeds,
                               NotificationsDetailActivity notificationsDetailActivity) {
        this.notificationDetails = notificationDetails;
        this.checkeds = checkeds;
        context = notificationsDetailActivity;
        listener = notificationsDetailActivity;
    }

    /**
     * Portador de vistas de notificaciones, el cual está atento a clicks.
     */
    public class NotificationViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private CheckBox checkBox;
        private ImageView imageView;
        private TextView name;
        private TextView dateTime;
        private TextView description;

        NotificationViewHolder(View itemView) {
            super(itemView);

            LinearLayout linearLayout = itemView.findViewById(R.id.notification_detail_layout);
            checkBox = itemView.findViewById(R.id.notification_detail_check_box);
            imageView = itemView.findViewById(R.id.notification_detail_image);
            name = itemView.findViewById(R.id.notification_detail_name);
            dateTime = itemView.findViewById(R.id.notification_detail_date_time);
            description = itemView.findViewById(R.id.notification_detail_description);

            linearLayout.setOnClickListener(this);
            linearLayout.setOnLongClickListener(this);
            checkBox.setOnClickListener(this);
        }

        void bindItem(NotificationDetail notificationDetail, boolean checked) {
            checkBox.setVisibility(checkBoxes ? View.VISIBLE : View.GONE);
            checkBox.setChecked(checked);
            imageView.setVisibility(checkBoxes ? View.GONE : View.VISIBLE);
            name.setText(String.format("%s - %s",
                    context.getString(notificationDetail.getCategory()),
                    notificationDetail.getName()));

            try {
                dateTime.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                        .format(simpleDateFormat.parse(notificationDetail.getDateTime())));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            description.setText(notificationDetail.getDescription());
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.notification_detail_layout:
                    listener.onNotificationClick(false, checkBoxes, getAdapterPosition());
                    break;
                case R.id.notification_detail_check_box:
                    checkeds.set(getAdapterPosition(), ((CheckBox) view).isChecked());
                    break;
            }
        }

        @Override
        public boolean onLongClick(View view) {
            checkBoxes = !checkBoxes;
            notifyDataSetChanged();
            listener.onNotificationClick(true, checkBoxes, getAdapterPosition());
            return true;
        }
    }

    /**
     * Crea el portador de notificaciones inflando su diseño.
     * @param parent Vista donde inflar el portador de notificaciones.
     * @param viewType Tipo de la vista (no utilizado).
     * @return Nuevo portador de notificaciones creado.
     */
    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NotificationViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_detail, parent, false));
    }

    /**
     * Vincula los valores de la notificación con sus vistas.
     * @param holder Portador de notificaciones.
     * @param position Posición de la notificación en el arreglo de notificaciones.
     */
    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        holder.bindItem(notificationDetails.get(position), checkeds.get(position));
    }

    /**
     * Obtiene la cantidad de ítems en el arreglo de notificaciones.
     * @return Cantidad de ítems.
     */
    @Override
    public int getItemCount() {
        return notificationDetails.size();
    }

    /**
     * Obtiene el arreglo de notificaciones.
     * @return Arreglo de notificaciones.
     */
    ArrayList<NotificationDetail> getNotificationDetails() {
        return notificationDetails;
    }

    ArrayList<Boolean> getCheckeds() {
        return checkeds;
    }

    /**
     * Asigna el nuevo arreglo de notificaciones y notifica que los datos han cambiado.
     * @param notificationDetails Arreglo de notificaciones.
     */
    void setNotificationDetails(ArrayList<NotificationDetail> notificationDetails,
                                ArrayList<Boolean> checkeds) {
        this.notificationDetails = notificationDetails;
        this.checkeds = checkeds;
        notifyDataSetChanged();
    }

    /**
     * Interfaz para ser implementada por una actividad hacia la cual se redireccionará el
     * procesamiento del click.
     */
    public interface OnClickNotificationListener {
        void onNotificationClick(boolean longClick, boolean checkBoxes, int index);
    }

}
