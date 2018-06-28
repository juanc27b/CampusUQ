package co.edu.uniquindio.campusuq.notifications;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.announcements.AnnouncementsActivity;
import co.edu.uniquindio.campusuq.emails.EmailsActivity;
import co.edu.uniquindio.campusuq.items.ItemsActivity;
import co.edu.uniquindio.campusuq.news.NewsActivity;
import co.edu.uniquindio.campusuq.objects.ObjectsActivity;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Actividad que permite visualizar los detalles de notificacion de la funcionalidad Bandeja de
 * notificaciones.
 */
public class NotificationsDetailActivity extends MainActivity
        implements View.OnClickListener, NotificationsDetailAdapter.OnClickNotificationListener {

    private CheckBox selectAll;
    private Button delete;
    private RecyclerView recyclerView;

    /**
     * Constructor que oculta el ícono de navegación para reemplazarlo por la flecha de ir atrás.
     */
    public NotificationsDetailActivity() {super.setHasNavigationDrawerIcon(false);}

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_notifications_detail);
        viewStub.inflate();

        selectAll = findViewById(R.id.notifications_detail_select_all);
        delete = findViewById(R.id.notifications_detail_delete);
        recyclerView = findViewById(R.id.notifications_recycler_view);

        selectAll.setOnClickListener(this);
        selectAll.setVisibility(View.GONE);
        delete.setOnClickListener(this);
        delete.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new NotificationsDetailAdapter(new ArrayList<NotificationDetail>(),
                new ArrayList<Boolean>(), this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        loadNotifications();
    }

    /**
     * Método para manejar nuevas llamadas a la actividad, dependiendo de la accion del intento,
     * puede buscar un ítem, o cambiar el titulo de la actividad y volver a cargar los ítems.
     * @param intent Intento que contiene la accion a realizar.
     */
    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            if (recyclerView != null) {
                ArrayList<NotificationDetail> notificationDetails =
                        ((NotificationsDetailAdapter) recyclerView.getAdapter())
                                .getNotificationDetails();

                for (NotificationDetail notificationDetail : notificationDetails) {
                    if (StringUtils.stripAccents(notificationDetail.getName()).toLowerCase()
                            .contains(StringUtils.stripAccents(query.trim()).toLowerCase())) {
                        recyclerView.smoothScrollToPosition(notificationDetails
                                .indexOf(notificationDetail));
                        return;
                    }
                }
            }

            Toast.makeText(this, getString(R.string.item_no_found) + ": " + query,
                    Toast.LENGTH_SHORT).show();
        } else {
            setIntent(intent);
            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setTitle(intent.getIntExtra(Utilities.CATEGORY, R.string.app_name));
                loadNotifications();
            }
        }
    }

    /**
     * Carca desde la base de datos local el arreglo de detalles de notificacion.
     */
    private void loadNotifications() {
        ArrayList<NotificationDetail> notificationDetails =
                NotificationsPresenter.loadNotificationDetails(this);
        ArrayList<Boolean> checkeds =
                new ArrayList<>(Collections.nCopies(notificationDetails.size(), false));
        ((NotificationsDetailAdapter) recyclerView.getAdapter())
                .setNotificationDetails(notificationDetails, checkeds);

        if (notificationDetails.size() == 0) {
            Toast.makeText(this, R.string.no_records, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Define la tarea a realizar cuando se da click en una de las vistas controladas por esta
     * actividad, en caso de dar clic en el boton de seleccionar todos los detalles de notificacion,
     * como se nombre lo indica apareceran seleccionadas todos los detalles de notificacion, si ya
     * estaban todos seleccionados entonces se les quitara la seleccion a todos, en caso de dar clic
     * al boton de eliminar se eliminaran todos los detalles de notificacion que esten seleccionados
     * en ese momento.
     * @param view Vista a la cual el usuario ha dado click.
     */
    @Override
    public void onClick(View view) {
        NotificationsDetailAdapter notificationsDetailAdapter =
                (NotificationsDetailAdapter) recyclerView.getAdapter();
        ArrayList<NotificationDetail> notificationDetails =
                notificationsDetailAdapter.getNotificationDetails();

        switch (view.getId()) {
            case R.id.notifications_detail_select_all:
                notificationsDetailAdapter.setNotificationDetails(notificationDetails,
                        new ArrayList<>(Collections.nCopies(notificationDetails.size(),
                                ((CheckBox) view).isChecked())));
                break;
            case R.id.notifications_detail_delete: {
                ArrayList<Boolean> checkeds = notificationsDetailAdapter.getCheckeds();
                ArrayList<String> _IDs = new ArrayList<>();

                for (int index = 0; index < checkeds.size(); index++) if (checkeds.get(index)) {
                    _IDs.add(notificationDetails.get(index).get_ID());
                }

                NotificationsPresenter.deleteNotificationDetail(this, _IDs.toArray());
                finish();
                break;
            }
        }
    }

    /**
     * Procesa los clics en un detalle de notificacion, si es un clic normal se abre la
     * funcionalidad que corresponde al detalle de notificacion y se elimina dicho detalle de
     * notificacion, si es un clic largo se intercambiara entre el modo de la interfaz para
     * seleccion de detalles de notificacion y el
     * modo normal.
     * @param longClick Valor booleano que determina si el clic dado es normal o largo.
     * @param checkBoxes Valor que determina si se estab visualizando las casillas de verificación.
     * @param index Indice del detalle de notificacion al cual se le ha dado clic.
     */
    @Override
    public void onNotificationClick(boolean longClick, boolean checkBoxes, int index) {
        if (longClick) {
            int visibility = checkBoxes ? View.VISIBLE : View.GONE;
            selectAll.setVisibility(visibility);
            delete.setVisibility(visibility);
        } else {
            NotificationsDetailAdapter notificationsDetailAdapter =
                    (NotificationsDetailAdapter) recyclerView.getAdapter();
            NotificationDetail notificationDetail =
                    notificationsDetailAdapter.getNotificationDetails().remove(index);
            notificationsDetailAdapter.getCheckeds().remove(index);
            NotificationsPresenter
                    .deleteNotificationDetail(this, notificationDetail.get_ID());
            Intent intent = null;

            switch (notificationDetail.getCategory()) {
                case R.string.news:
                case R.string.events:
                    intent = new Intent(this, NewsActivity.class)
                            .putExtra(Utilities.CATEGORY, notificationDetail.getCategory());
                    break;
                case R.string.lost_objects:
                    intent = new Intent(this, ObjectsActivity.class)
                            .putExtra(Utilities.CATEGORY, notificationDetail.getCategory());
                    break;
                case R.string.academic_calendar:
                    intent = new Intent(this, ItemsActivity.class)
                            .putExtra(Utilities.CATEGORY, notificationDetail.getCategory());
                    break;
                case R.string.security_system:
                case R.string.billboard_information:
                    intent = new Intent(this, AnnouncementsActivity.class)
                            .putExtra(Utilities.CATEGORY, notificationDetail.getCategory());
                    break;
                case R.string.institutional_mail:
                    intent = new Intent(this, EmailsActivity.class)
                            .putExtra(Utilities.CATEGORY, notificationDetail.getCategory());
                    break;
            }

            if (intent != null) startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            notificationsDetailAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Método del ciclo de la actividad llamado para destruir la misma, en el que se anulan
     * instancias.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        selectAll = null;
        delete = null;
        recyclerView = null;
    }

}
