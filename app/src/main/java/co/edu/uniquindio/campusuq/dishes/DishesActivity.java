package co.edu.uniquindio.campusuq.dishes;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;
import co.edu.uniquindio.campusuq.users.User;

/**
 * Actividad para visualizar los platos del menú del restaurante.
 */
public class DishesActivity extends MainActivity implements DishesAdapter.OnClickDishListener,
        View.OnClickListener {

    static final int REQUEST_DISHES_DETAIL = 1001;

    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private boolean oldDishes = true;

    private IntentFilter dishesFilter = new IntentFilter(WebService.ACTION_DISHES);
    private BroadcastReceiver dishesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadDishes(intent.getIntExtra("INSERTED", 0));
            String response = intent.getStringExtra("RESPONSE");

            if (response != null) {
                Log.i(DishesActivity.class.getSimpleName(), response);
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * Constructor que oculta el ícono de navegación para reemplazarlo por la flecha de ir atrás.
     */
    public DishesActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    /**
     * Asigna el fondo de la actividad, infla el diseño de platos en la actividad superior, en caso
     * de haber iniciado sesion como administrador hace visible el botón para añadir nuevos platos,
     * se crea el adaptador de platos y el manejador de diseño lineal y se asignan al recilador de
     * vista, al cual tambien se le asigna un listener de actualización encargado de actualizar
     * desde el servidor la base de datos local al realizar un desplasamiento vetical en el limite
     * superior, y un listener de despalzamiento encargado de cargar mas platos desde la base de
     * datos local al realizar un desplasamiento vetical en el limite inferior, y finalmente llama
     * a la funcion para cargar los platos.
     * @param savedInstanceState Parámetro para recuperar estados anteriores de la actividad.
     */
    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_dishes);
        viewStub.inflate();

        FloatingActionButton insert = findViewById(R.id.fab);
        swipeRefreshLayout = findViewById(R.id.dishes_swipe_refresh);
        recyclerView = findViewById(R.id.dishes_recycler_view);

        insert.setOnClickListener(this);
        User user = UsersPresenter.loadUser(this);
        if (user != null && "S".equals(user.getAdministrator())) insert.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.haveNetworkConnection(DishesActivity.this)) {
                    oldDishes = false;
                    WebBroadcastReceiver.startService(getApplicationContext(),
                            WebService.ACTION_DISHES, WebService.METHOD_GET,
                            null);
                } else {
                    Toast.makeText(DishesActivity.this,
                            R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new DishesAdapter(new ArrayList<Dish>(), this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_SETTLING &&
                        !recyclerView.canScrollVertically(1)) {
                    oldDishes = true;
                    loadDishes(0);
                }
            }
        });

        loadDishes(0);
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
                ArrayList<Dish> dishes = ((DishesAdapter) recyclerView.getAdapter()).getDishes();

                for (Dish dish : dishes) {
                    if (StringUtils.stripAccents(dish.getName()).toLowerCase()
                            .contains(StringUtils.stripAccents(query.trim()).toLowerCase())) {
                        recyclerView.smoothScrollToPosition(dishes.indexOf(dish));
                        return;
                    }
                }
            }

            Toast.makeText(this, getString(R.string.dish_no_found) + ": " + query,
                    Toast.LENGTH_SHORT).show();
        } else {
            setIntent(intent);
            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setTitle(intent.getIntExtra(Utilities.CATEGORY, R.string.app_name));
                loadDishes(0);
            }
        }
    }

    /**
     * Carga los platos desde la base de datos y los almacena en el adaptador, adicionalmente
     * muestra un mensaje de de carga durante el tiempo que realiza el proceso.
     * @param inserted Indica la cantidad de platos insertados.
     */
    private void loadDishes(int inserted) {
        swipeRefreshLayout.setRefreshing(true);

        DishesAdapter dishesAdapter = (DishesAdapter) recyclerView.getAdapter();
        int scrollTo = oldDishes ?
                dishesAdapter.getItemCount() - 1 : (inserted > 0 ? inserted - 1 : 0);
        dishesAdapter.setDishes(DishesPresenter.loadDishes(this,
                dishesAdapter.getItemCount() + (inserted > 0 ? inserted : 12)));
        recyclerView.getLayoutManager().scrollToPosition(scrollTo);

        if (dishesAdapter.getItemCount() > 0) swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Dependiendo de la accion, en caso haber iniciado sesión como administrador, puede mostrar un
     * cuadro de dialogo que permite modificar o eliminar el plato (o un mensaje de advertencia en
     * caso contrario), o puede llamar a otra aplicacion que permita visualizar con mas detalle la
     * imagen del plato.
     * @param index Indice del plato que determina a cuál de los ítems del arreglo de platos se le
     *              aplicará el cuadro de dialogo.
     * @param action Determina si se quiere abrir un cuadro de dialogo o visualizar con mas detalle
     *               la imagen del plato.
     */
    @Override
    public void onDishClick(int index, String action) {
        switch (action) {
            case DishesAdapter.DISH: {
                User user = UsersPresenter.loadUser(this);
                if (user != null && "S".equals(user.getAdministrator())) {
                    DishesFragment.newInstance(((DishesAdapter) recyclerView.getAdapter())
                            .getDishes().get(index)).show(getSupportFragmentManager(), null);
                } else {
                    Toast.makeText(this,
                            R.string.no_administrator,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case DishesAdapter.IMAGE:
                try {
                    String image = ((DishesAdapter) recyclerView.getAdapter())
                            .getDishes().get(index).getImage();

                    if (image != null) {
                        startActivity(new Intent(Intent.ACTION_VIEW)
                                .setDataAndType(FileProvider.getUriForFile(this,
                                        "co.edu.uniquindio.campusuq.provider",
                                        new File(image)), "image/*")
                                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Define la tarea a realizar cuando se da click en una de las vistas controladas por esta
     * actividad, en caso de dar click en el boton de añadir, el cual solo es visible si se ha
     * iniciado sesión como administrador se abrirá la actividad que permite insertar un nuevo
     * plato.
     * @param view Vista a la cual el usuario ha dado click.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                startActivityForResult(new Intent(this, DishesDetailActivity.class)
                                .putExtra(Utilities.CATEGORY, R.string.restaurant_detail)
                                .putExtra(DishesFragment.DISH, new Dish(null, null,
                                        null, null, null)),
                        REQUEST_DISHES_DETAIL);
                break;
            default:
                break;
        }
    }

    /**
     * Muestra el cuadro de dialogo de progreso si la actividad que permite modificar o insertar un
     * plato resulta en un codigo correcto.
     * @param requestCode Código de solicitud para el cual se espera un resultado.
     * @param resultCode Código de resultado que indica exito o fracaso.
     * @param data Datos retornados por la actividad (no utilizado).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DISHES_DETAIL && resultCode  == RESULT_OK) {
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    /**
     * Método del ciclo de la actividad llamado para reanudar la misma, en el que se registra un
     * receptor para estar atento a los intentos relacionados con los platos.
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(dishesReceiver, dishesFilter);
    }

    /**
     * Método del ciclo de la actividad llamado para pausar la misma, en el que se invalida el
     * previo registro del receptor para los platos.
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(dishesReceiver);
    }

}