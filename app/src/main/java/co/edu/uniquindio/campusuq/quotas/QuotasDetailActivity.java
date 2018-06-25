package co.edu.uniquindio.campusuq.quotas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;

import org.json.JSONException;
import org.json.JSONObject;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;

/**
 * Actividad para modificar o insertar nuevos cupos.
 */
public class QuotasDetailActivity extends MainActivity implements View.OnClickListener {

    private Intent intent;
    private Quota q;
    private EditText name;
    private EditText quota;

    /**
     * Constructor que oculta el ícono de navegación reemplazandolo por una flecha de ir atrás, y
     * oculta también el botón de busqueda.
     */
    public QuotasDetailActivity() {
        super.setHasNavigationDrawerIcon(false);
        super.setHasSearch(false);
    }

    /**
     * Asigna el fondo de la actividad, infla el diseño de edición de cupos en la actividad
     * superior, asigna las variables de vistas y el listener de click de la vista del boton OK, y
     * llama la funcion para asignar los valores de las variables y vistas.
     * @param savedInstanceState Parámetro para recuperar estados anteriores de la actividad.
     */
    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.activity_quotas_detail);
        viewStub.inflate();

        intent = getIntent();
        name = findViewById(R.id.quota_detail_name);
        quota = findViewById(R.id.quota_detail_quota);

        findViewById(R.id.quota_detail_ok).setOnClickListener(this);

        setQuota();
    }

    /**
     * Método para manejar nuevas llamadas a la actividad, asigna un nuevo título a la actividad en
     * caso de haberse creado previamente.
     * @param intent Contiene los datos nuevos.
     */
    @Override
    public void handleIntent(Intent intent) {
        setIntent(intent);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(intent.getIntExtra(Utilities.CATEGORY, R.string.app_name));
            this.intent = intent;
            setQuota();
        }
    }

    /**
     * Asigna los valores a las variables y vistas desde el cupo obtenido del intento.
     */
    private void setQuota() {
        q = intent.getParcelableExtra(QuotasFragment.QUOTA);
        name.setText(q.getName());
        quota.setText(q.getQuota());
    }

    /**
     * Responde al listener de la vista del boton OK y envia los datos del cupo al servidor para
     * insertarlo o actualizarlo, o en caso de error muestra un mensaje con la informacion de dicho
     * error.
     * @param view Vista de la cual se puede obtener el identificador del boton OK.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.quota_detail_ok:
                if (Utilities.haveNetworkConnection(this)) {
                    if (name.getText().length() != 0 && quota.getText().length() != 0) {
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory(getString(R.string.analytics_quotas_category))
                                .setAction(getString(q.get_ID() == null ?
                                        R.string.analytics_create_action : R.string.analytics_modify_action))
                                .setLabel(getString(
                                        "S".equals(q.getType()) ? R.string.analytics_computer_rooms_label :
                                        "P".equals(q.getType()) ? R.string.analytics_parking_lots_label :
                                        "L".equals(q.getType()) ? R.string.analytics_laboratories_label :
                                        "E".equals(q.getType()) ? R.string.analytics_study_areas_label :
                                        "C".equals(q.getType()) ? R.string.analytics_cultural_and_sport_label :
                                        R.string.analytics_auditoriums_label))
                                .setValue(1)
                                .build());

                        try {
                            JSONObject json = new JSONObject();
                            if (q.get_ID() != null) json.put("UPDATE_ID", q.get_ID());
                            json.put(QuotasSQLiteController.columns[1], q.getType());
                            json.put(QuotasSQLiteController.columns[2], name.getText());
                            json.put(QuotasSQLiteController.columns[3], quota.getText());
                            WebBroadcastReceiver.startService(getApplicationContext(),
                                    WebService.ACTION_QUOTAS, WebService.METHOD_POST,
                                    json.toString());
                            setResult(RESULT_OK, intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, e.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, R.string.empty_string,
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, R.string.no_internet,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Método del ciclo de la actividad llamado para destruir la misma, en el que se anulan
     * instancias.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        intent = null;
        q = null;
        name = null;
        quota = null;
    }

}
