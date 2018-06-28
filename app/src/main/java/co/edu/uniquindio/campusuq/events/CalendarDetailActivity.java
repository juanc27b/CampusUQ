package co.edu.uniquindio.campusuq.events;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Actividad para visualizar los ítems de detalle de calendario de la funcionalidad Calendario
 * académico.
 */
public class CalendarDetailActivity extends MainActivity {

    private TextView eventText;
    private RecyclerView recyclerView;
    private TextView categoryText;

    private String event;
    private String category;

    /**
     * Constructor que oculta el ícono de navegación reemplazandolo por una flecha de ir atrás.
     */
    public CalendarDetailActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    /**
     * Asigna el fondo de la actividad, infla el diseño de ítems de detalle de calendario en la
     * actividad superior, se crea el adaptador de ítems de detalle de calendario y el manejador de
     * diseño lineal y se asignan al recilador de vista y finalmente llama a la funcion para cargar
     * los ítems de detalle de calendario.
     * @param savedInstanceState Parámetro para recuperar estados anteriores de la actividad.
     */
    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_calendar_detail);
        stub.inflate();

        eventText = findViewById(R.id.event_detail_text);
        recyclerView = findViewById(R.id.event_detail_recycler_view);
        categoryText = findViewById(R.id.category_detail_text);

        recyclerView.setHasFixedSize(true);
        recyclerView
                .setAdapter(new CalendarDetailItemsAdapter(new ArrayList<CalendarDetailItem>()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        event = getIntent().getStringExtra("EVENT");
        eventText.setText(event);
        category = getIntent().getStringExtra(Utilities.CATEGORY);
        categoryText.setText(String.format("%s: %s", getString(R.string.category), category));
        setItems();
    }

    /**
     * Método para manejar nuevas llamadas a la actividad, dependiendo de la accion del intento,
     * puede buscar un ítem o cambiar el titulo de la instancia de la actividad.
     * @param intent Intento que contiene la accion a realizar.
     */
    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            if (recyclerView != null) {
                ArrayList<CalendarDetailItem> items =
                        ((CalendarDetailItemsAdapter) recyclerView.getAdapter()).getItems();

                for (CalendarDetailItem item : items) {
                    if (item.getPeriod().toLowerCase().contains(query.trim().toLowerCase()) ||
                            item.getStart().toLowerCase().contains(query.trim().toLowerCase()) ||
                            item.getEnd().toLowerCase().contains(query.trim().toLowerCase())) {
                        recyclerView.smoothScrollToPosition(items.indexOf(item));
                        return;
                    }
                }
            }

            Toast.makeText(this, getString(R.string.date_no_found) + ": " + query,
                    Toast.LENGTH_SHORT).show();
        } else if (recyclerView != null) {
            setIntent(intent);
            event = intent.getStringExtra("EVENT");
            eventText.setText(event);
            category = intent.getStringExtra(Utilities.CATEGORY);
            categoryText.setText(String.format("%s: %s", getString(R.string.category), category));
            setItems();
        }
    }

    /**
     * Establece los ítems de detalle de calendario obteniendolos desde la base de datos local.
     */
    private void setItems() {
        ((CalendarDetailItemsAdapter) recyclerView.getAdapter())
                .setItems(CalendarPresenter.getCalendarDetailItems(event, category, this));
    }

    /**
     * Método del ciclo de la actividad llamado para destruir la misma, en el que se anulan
     * instancias.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventText = null;
        recyclerView = null;
        categoryText = null;
        event = null;
        category = null;
    }

}
