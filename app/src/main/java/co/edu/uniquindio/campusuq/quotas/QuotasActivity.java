package co.edu.uniquindio.campusuq.quotas;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;
import co.edu.uniquindio.campusuq.users.User;

/**
 * Actividad para visualizar los cupos de salas de cómputo, parqueaderos, laboratorios, zonas de
 * estudio, espacios culturales y deportivos y auditorios.
 */
public class QuotasActivity extends MainActivity implements QuotasAdapter.OnClickQuotaListener,
        View.OnClickListener {

    static final int REQUEST_QUOTAS_DETAIL = 1001;

    private String type;
    private ArrayList<Quota> quotas = new ArrayList<>();
    private boolean newActivity = true;
    private QuotasAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private IntentFilter quotasFilter = new IntentFilter(WebService.ACTION_QUOTAS);
    private BroadcastReceiver quotasReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadQuotas();
            String response = intent.getStringExtra("RESPONSE");

            if (response != null) {
                Log.i(QuotasActivity.class.getSimpleName(), response);
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * Constructor que oculta el ícono de navegación reemplazandolo por una flecha de ir atrás.
     */
    public QuotasActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    /**
     * Asigna el fondo de la actividad, infla el diseño de cupos en la actividad superior, en caso
     * de haber iniciado sesion como administrador hace visible el botón para añadir nuevos cupos, y
     * llama a la funcion para cargar los cupos.
     * @param savedInstanceState Parámetro para recuperar estados anteriores de la actividad.
     */
    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_quotas);
        viewStub.inflate();

        FloatingActionButton insert = findViewById(R.id.fab);

        insert.setOnClickListener(this);
        User user = UsersPresenter.loadUser(this);
        if (user != null && "S".equals(user.getAdministrator())) insert.setVisibility(View.VISIBLE);

        loadQuotas();
    }

    /**
     * Método para manejar nuevas llamadas a la actividad, dependiendo de la accion del intento,
     * puede buscar un ítem o alterar la actividad para que muestre los cupos de otra funcionalidad.
     * @param intent Intento que contiene la accion a realizar.
     */
    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            for (Quota quota : quotas) {
                if (quota.getName().toLowerCase().contains(query.trim().toLowerCase())) {
                    layoutManager.scrollToPosition(quotas.indexOf(quota));
                    return;
                }
            }

            Toast.makeText(this, getString(R.string.quota_no_found) + ": " + query,
                    Toast.LENGTH_SHORT).show();
        } else {
            String category = intent.getStringExtra("CATEGORY");
            type = category.equals(getString(R.string.computer_rooms)) ? "S" :
                    category.equals(getString(R.string.parking_lots)) ? "P" :
                    category.equals(getString(R.string.laboratories)) ? "L" :
                    category.equals(getString(R.string.study_areas)) ? "E" :
                    category.equals(getString(R.string.cultural_and_sport)) ? "C" : "A";
            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setTitle(category);
                loadQuotas();
            }
        }
    }

    /**
     * Carga los cupos del tipo requerido desde la base de datos y los almacena en el arreglo quotas
     * para enviarselos al adaptador, si la actividad es nueva el arreglo se envia por medio de su
     * constructor, se crea tambien el manejador de diseño y se asignan al recilador de vista, al
     * cual tambien se le asigna un listener de desplasamiento encargado de actualizar desde
     * el servidor la base de datos local al realizar un desplasamiento vetical en el limite
     * superior o inferior, adicionalmente muestra un mensaje de de carga durante el tiempo que
     * realiza el proceso.
     */
    private void loadQuotas() {
        if (!progressDialog.isShowing()) progressDialog.show();

        quotas = QuotasPresenter.loadQuotas(this, type);

        if (newActivity) {
            newActivity = false;
            adapter = new QuotasAdapter(quotas, this);
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                    false);

            RecyclerView recyclerView = findViewById(R.id.quotas_recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        if (!recyclerView.canScrollVertically(-1) ||
                                !recyclerView.canScrollVertically(1)) {
                            if (Utilities.haveNetworkConnection(QuotasActivity.this)) {
                                progressDialog.show();
                                WebBroadcastReceiver.scheduleJob(QuotasActivity.this,
                                        WebService.ACTION_QUOTAS, WebService.METHOD_GET,
                                        null);
                            } else {
                                Toast.makeText(QuotasActivity.this,
                                        R.string.no_internet, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        } else {
            adapter.setQuotas(quotas);
        }

        if (progressDialog.isShowing() && quotas.size() > 0) progressDialog.dismiss();
    }

    /**
     * En caso haber iniciado sesión como administrador, muestra un cuadro de dialogo que permite
     * modificar (parcial o totalmente) o eliminar el cupo, en caso contrario muestra un mensaje de
     * advertencia.
     * @param fragment_quotas Determina el tipo de cuadro de dialogo a mostrar, si el valor es true
     *                        mostrará un cuadro de dialogo que permite modificar rapidamente el
     *                        numero del cupo (modificación parcial), si el valor es false mostrara
     *                        un cuadro de dialogo con las opciones de modificar totalmente
     *                        (llamando a otra actividad para poder hacerlo) o eliminar el cupo.
     * @param index Indice del cupo que determina a cuál de los ítems del arreglo de cupos se le
     *              aplicará el cuadro de dialogo.
     */
    @Override
    public void onQuotaClick(boolean fragment_quotas, int index) {
        User user = UsersPresenter.loadUser(this);
        if (user != null && user.getAdministrator().equals("S")) {
            QuotasFragment.newInstance(fragment_quotas, quotas.get(index))
                    .show(getSupportFragmentManager(), null);
        } else {
            Toast.makeText(this, R.string.no_administrator,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Define la tarea a realizar cuando se da click en una de las vistas controladas por esta
     * actividad, en caso de dar click en el boton de añadir, el cual solo es visible si se ha
     * iniciado sesión como administrador se abrirá la actividad que permite insertar un nuevo cupo.
     * @param view Vista a la cual el usuario ha dado click.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                startActivityForResult(new Intent(this, QuotasDetailActivity.class)
                                .putExtra("CATEGORY", getString(R.string.quota_detail_insert))
                                .putExtra(QuotasFragment.QUOTA,
                                        new Quota(null, type, null, null)),
                        REQUEST_QUOTAS_DETAIL);
                break;
            default:
                break;
        }
    }

    /**
     * Muestra el cuadro de dialogo de progreso si la actividad que permite modificar totalmente o
     * insertar un cupo resulta en un codigo correcto.
     * @param requestCode Código de solicitud para el cual se espera un resultado.
     * @param resultCode Código de resultado que indica exito o fracaso.
     * @param data Datos retornados por la actividad (no utilizado).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_QUOTAS_DETAIL && resultCode  == RESULT_OK &&
                !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * Método del ciclo de la actividad llamado para reanudar la misma, en el que se registra un
     * receptor para estar atento a los intentos relacionados con los cupos.
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(quotasReceiver, quotasFilter);
    }

    /**
     * Método del ciclo de la actividad llamado para pausar la misma, en el que se invalida el
     * previo registro del receptor para los cupos.
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(quotasReceiver);
    }

}
