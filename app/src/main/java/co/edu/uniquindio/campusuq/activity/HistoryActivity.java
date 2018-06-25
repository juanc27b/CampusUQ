package co.edu.uniquindio.campusuq.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.View;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.announcements.Announcement;
import co.edu.uniquindio.campusuq.announcements.AnnouncementsPresenter;
import co.edu.uniquindio.campusuq.objects.LostObject;
import co.edu.uniquindio.campusuq.objects.ObjectsPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Actividad que muestra los items del historial, los cuales correcponden a items que han sido
 * marcados como leidos en el Sistema de seguridad, Objetos Perdidos y Cartelera.
 */
public class HistoryActivity extends MainActivity implements View.OnClickListener {

    private static class HistoryItem {
        String _ID;
        CheckBox checkBox;

        HistoryItem(String _ID, CheckBox checkBox) {
            this._ID = _ID;
            this.checkBox = checkBox;
        }
    }

    private TextView noRecords;
    private CheckBox historySelectAll;
    private ScrollView history;
    private LinearLayout objectsHistory;
    private LinearLayout incidentsHistory;
    private LinearLayout communiquesHistory;
    private ArrayList<HistoryItem> objectHistoryItems = new ArrayList<>();
    private ArrayList<HistoryItem> announcementHistoryItems = new ArrayList<>();

    /**
     * Constructor que oculta el ícono de navegación reemplazandolo por una flecha de ir atrás.
     */
    public HistoryActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.activity_history);
        viewStub.inflate();

        noRecords = findViewById(R.id.no_records);
        historySelectAll = findViewById(R.id.history_select_all);
        history = findViewById(R.id.history);
        objectsHistory = findViewById(R.id.objects_history);
        incidentsHistory = findViewById(R.id.incidents_history);
        communiquesHistory = findViewById(R.id.communiques_history);

        historySelectAll.setOnClickListener(this);
        findViewById(R.id.history_ok).setOnClickListener(this);

        loadHistory();
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

            for (HistoryItem historyItem : objectHistoryItems) {
                if (StringUtils.stripAccents(historyItem.checkBox.getText().toString())
                        .toLowerCase()
                        .contains(StringUtils.stripAccents(query.trim()).toLowerCase())) {
                    history.smoothScrollTo(0, ((View) historyItem.checkBox.getParent())
                            .getTop() + historyItem.checkBox.getTop());
                    return;
                }
            }

            for (HistoryItem historyItem : announcementHistoryItems) {
                if (StringUtils.stripAccents(historyItem.checkBox.getText().toString())
                        .toLowerCase()
                        .contains(StringUtils.stripAccents(query.trim()).toLowerCase())) {
                    history.smoothScrollTo(0, ((View) historyItem.checkBox.getParent())
                            .getTop() + historyItem.checkBox.getTop());
                    return;
                }
            }

            Toast.makeText(this, getString(R.string.item_no_found) + ": " + query,
                    Toast.LENGTH_SHORT).show();
        } else {
            setIntent(intent);
            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setTitle(intent.getIntExtra(Utilities.CATEGORY, R.string.app_name));
                loadHistory();
            }
        }
    }

    private void loadHistory() {
        ArrayList<LostObject> objects = ObjectsPresenter.loadReadedObjects(this);
        boolean objectsIsEmpty = objects.isEmpty();
        objectsHistory.setVisibility(objectsIsEmpty ? View.GONE : View.VISIBLE);
        final String html = "<b>%s</b><br/>%s";

        for (LostObject object : objects) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(Html.fromHtml(String
                    .format(html, object.getName(), object.getDescription())));
            objectsHistory.addView(checkBox);
            objectHistoryItems.add(new HistoryItem(object.get_ID(), checkBox));
        }

        ArrayList<Announcement> announcements =
                AnnouncementsPresenter.loadReadedAnnouncements(this, "I");
        boolean incidentsIsEmpty = announcements.isEmpty();
        incidentsHistory.setVisibility(incidentsIsEmpty ? View.GONE : View.VISIBLE);

        for (Announcement announcement : announcements) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(Html.fromHtml(String
                    .format(html, announcement.getName(), announcement.getDescription())));
            incidentsHistory.addView(checkBox);
            announcementHistoryItems.add(new HistoryItem(announcement.get_ID(), checkBox));
        }

        announcements = AnnouncementsPresenter.loadReadedAnnouncements(this, "C");
        boolean communiquesIsEmpty = announcements.isEmpty();
        communiquesHistory.setVisibility(communiquesIsEmpty ? View.GONE : View.VISIBLE);

        for (Announcement announcement : announcements) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(Html.fromHtml(String
                    .format(html, announcement.getName(), announcement.getDescription())));
            communiquesHistory.addView(checkBox);
            announcementHistoryItems.add(new HistoryItem(announcement.get_ID(), checkBox));
        }

        boolean historyIsEmpty = objectsIsEmpty && incidentsIsEmpty && communiquesIsEmpty;
        noRecords.setVisibility(historyIsEmpty ? View.VISIBLE : View.GONE);
        int historyVisibility = historyIsEmpty ? View.GONE : View.VISIBLE;
        historySelectAll.setVisibility(historyVisibility);
        history.setVisibility(historyVisibility);

        if (!historyIsEmpty &&
                getIntent().getBooleanExtra(Utilities.SELECT_ALL, false)) {
            historySelectAll.setChecked(true);
            historySelectAll.callOnClick();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.history_select_all: {
                boolean isChecked = ((CheckBox) view).isChecked();

                for (HistoryItem historyItem : objectHistoryItems) {
                    historyItem.checkBox.setChecked(isChecked);
                }

                for (HistoryItem historyItem : announcementHistoryItems) {
                    historyItem.checkBox.setChecked(isChecked);
                }

                break;
            }
            case R.id.history_ok: {
                ArrayList<String> _IDs = new ArrayList<>();

                for (HistoryItem historyItem : objectHistoryItems) {
                    if (historyItem.checkBox.isChecked()) _IDs.add(historyItem._ID);
                }

                ObjectsPresenter.deleteHistory(this, _IDs.toArray());
                _IDs.clear();

                for (HistoryItem historyItem : announcementHistoryItems) {
                    if (historyItem.checkBox.isChecked()) _IDs.add(historyItem._ID);
                }

                AnnouncementsPresenter.deleteHistory(this, _IDs.toArray());
                finish();
                break;
            }
        }
    }

    /**
     * Método del ciclo de la actividad llamado para destruir la misma, en el que se anulan
     * instancias.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        noRecords = null;
        historySelectAll = null;
        history = null;
        objectsHistory = null;
        incidentsHistory = null;
        communiquesHistory = null;
    }

}
