package co.edu.uniquindio.campusuq.events;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Actividad para visualizar los ítems de calendario de la funcionalidad Calendario académico.
 */
public class CalendarActivity extends MainActivity
        implements CalendarItemsAdapter.OnClickItemListener {

    private TextView categoryText;
    private RecyclerView recyclerView;

    private String category;

    /**
     * Constructor que oculta el ícono de navegación reemplazandolo por una flecha de ir atrás.
     */
    public CalendarActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    /**
     * Asigna el fondo de la actividad, infla el diseño de ítems de calendario en la actividad
     * superior, se crea el adaptador de ítems de calendario y el manejador de diseño lineal y se
     * asignan al recilador de vista y finalmente llama a la funcion para cargar los ítems de
     * calendario.
     * @param savedInstanceState Parámetro para recuperar estados anteriores de la actividad.
     */
    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_calendar);
        stub.inflate();

        categoryText = findViewById(R.id.category_text);
        recyclerView = findViewById(R.id.events_recycler_view);

        category = getIntent().getStringExtra(Utilities.CATEGORY);
        categoryText.setText(String.format("%s: %s", getString(R.string.category), category));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new CalendarItemsAdapter(new ArrayList<CalendarItem>(),
                this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
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
                ArrayList<CalendarItem> items =
                        ((CalendarItemsAdapter) recyclerView.getAdapter()).getItems();

                for (CalendarItem item : items) {
                    if (StringUtils.stripAccents(item.getEvent()).toLowerCase()
                            .contains(StringUtils.stripAccents(query.trim()).toLowerCase())) {
                        recyclerView.smoothScrollToPosition(items.indexOf(item));
                        return;
                    }
                }
            }

            Toast.makeText(this, getString(R.string.event_no_found) + ": " + query,
                    Toast.LENGTH_SHORT).show();
        } else if (recyclerView != null) {
            setIntent(intent);
            category = intent.getStringExtra(Utilities.CATEGORY);
            categoryText.setText(String.format("%s: %s", getString(R.string.category), category));
            setItems();
        }
    }

    /**
     * Establede los ítems de calendario obteniedolos desde la base de datos local.
     */
    private void setItems() {
        ((CalendarItemsAdapter) recyclerView.getAdapter())
                .setItems(CalendarPresenter.getCalendarItems(category, this));
    }

    /**
     * Inicia la actividad de detalle de calendario en el ítem al cual se le ha dado clic.
     * @param index Indice del ítem de calendario al que se le a dado clic.
     */
    @Override
    public void onCalendarItemClick(int index) {
        startActivity(new Intent(this, CalendarDetailActivity.class)
                .putExtra("EVENT", ((CalendarItemsAdapter) recyclerView.getAdapter())
                        .getItems().get(index).getEvent())
                .putExtra(Utilities.CATEGORY, category));
    }

    /**
     * Método del ciclo de la actividad llamado para destruir la misma, en el que se anulan
     * instancias.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        categoryText = null;
        recyclerView = null;
        category = null;
    }

}
