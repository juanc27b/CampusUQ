package co.edu.uniquindio.campusuq.objects;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.users.LoginActivity;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.users.UsersActivity;
import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.users.UsersSQLiteController;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;
import co.edu.uniquindio.campusuq.users.User;

/**
 * Actividad para visualizar los objetos perdidos.
 */
public class ObjectsActivity extends MainActivity implements ObjectsAdapter.OnClickObjectListener,
        View.OnClickListener {

    static final int REQUEST_OBJECTS_DETAIL = 1001;

    private ArrayList<LostObject> objects = new ArrayList<>();
    private boolean newActivity = true;
    private ObjectsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private boolean oldObjects = true;

    private IntentFilter objectsFilter = new IntentFilter();
    private BroadcastReceiver objectsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WebService.ACTION_OBJECTS.equals(intent.getAction())) {
                loadObjects(intent.getIntExtra("INSERTED", 0));
                String response = intent.getStringExtra("RESPONSE");

                if (response != null) {
                    Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                    Log.i(ObjectsActivity.class.getSimpleName(), response);
                }
            } else if (progressDialog.isShowing()) {
                User userFound = intent.getParcelableExtra("USER");

                if (userFound != null) {
                    startActivity(
                            new Intent(ObjectsActivity.this, UsersActivity.class)
                            .putExtra("CATEGORY", getString(R.string.object_view_contact))
                            .putExtra("USER", userFound));
                }
            }
        }
    };

    /**
     * Constructor que oculta el ícono de navegación para reemplazarlo por la flecha de ir atrás.
     */
    public ObjectsActivity() {
        super.setHasNavigationDrawerIcon(false);

        objectsFilter.addAction(WebService.ACTION_OBJECTS);
        objectsFilter.addAction(WebService.ACTION_USERS);
    }

    /**
     * Asigna el fondo de la actividad, infla el diseño de objetos perdidos en la actividad
     * superior, y llama a la funcion para cargar los objetos perdidos.
     * @param savedInstanceState Parámetro para recuperar estados anteriores de la actividad.
     */
    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_objects);
        viewStub.inflate();

        findViewById(R.id.object_report).setOnClickListener(this);

        loadObjects(0);
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

            for (LostObject object : objects) {
                if (object.getName().toLowerCase().contains(query.trim().toLowerCase())) {
                    layoutManager.scrollToPosition(objects.indexOf(object));
                    return;
                }
            }

            Toast.makeText(this, getString(R.string.object_no_found) + ": " + query,
                    Toast.LENGTH_SHORT).show();
        } else {
            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setTitle(intent.getStringExtra("CATEGORY"));
                loadObjects(0);
            }
        }
    }

    /**
     * Carga los objetos perdidos desde la base de datos y los almacena en el arreglo de objetos
     * perdidos para enviarselos al adaptador, si la actividad es nueva el arreglo se envia por
     * medio de su constructor, se crea tambien el manejador de diseño y se asignan al recilador de
     * vista, al cual tambien se le asigna un listener de desplasamiento encargado de actualizar
     * desde el servidor la base de datos local al realizar un desplasamiento vetical en el limite
     * superior o cargar mas objetos perdidos desde la base de datos local al realizar un
     * desplasamiento vetical en el limite inferior, adicionalmente muestra un mensaje de de carga
     * durante el tiempo que realiza el proceso.
     * @param inserted Indica la cantidad de objetos perdidos insertados.
     */
    private void loadObjects(int inserted) {
        if (!progressDialog.isShowing()) progressDialog.show();

        int scrollTo = oldObjects ?
                (newActivity ? 0 : objects.size() - 1) :
                (inserted != 0 ? inserted - 1 : 0);

        objects = ObjectsPresenter.loadObjects(this, UsersPresenter.loadUser(this),
                objects.size() + (inserted > 0 ? inserted : 3));

        if (newActivity) {
            newActivity = false;
            adapter = new ObjectsAdapter(objects, this);
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                    false);

            RecyclerView recyclerView = findViewById(R.id.objects_recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        if (!recyclerView.canScrollVertically(-1)) {
                            if (Utilities.haveNetworkConnection(ObjectsActivity.this)) {
                                oldObjects = false;
                                progressDialog.show();
                                WebBroadcastReceiver.scheduleJob(ObjectsActivity.this,
                                        WebService.ACTION_OBJECTS, WebService.METHOD_GET,
                                        null);
                            } else {
                                Toast.makeText(ObjectsActivity.this,
                                        R.string.no_internet, Toast.LENGTH_SHORT).show();
                            }
                        } else if (!recyclerView.canScrollVertically(1)) {
                            oldObjects = true;
                            loadObjects(0);
                        }
                    }
                }
            });
        } else {
            adapter.setObjects(objects);
            layoutManager.scrollToPosition(scrollTo);
        }

        if (progressDialog.isShowing() && objects.size() > 0) progressDialog.dismiss();
    }

    /**
     * Dependiendo de la accion, en caso haber iniciado sesión como administrador o ser propietario
     * del objeto perdido, puede mostrar un cuadro de dialogo que permite modificar o eliminar el
     * objeto perdido (o un mensaje de advertencia en caso contrario), puede llamar a otra
     * aplicacion que permita visualizar con mas detalle la imagen del objeto perdido, puede marcar
     * el objeto perdido como leido, encontrado o no encontrado (este ultimo solo si es
     * administrador o propietario), o si es propietario y el objeto ha sido encontrado puede
     * contactar con quien lo encontró.
     * @param index Indice del objeto perdido que determina a cuál de los ítems del arreglo de
     *              objetos perdidos se le aplicará la accion.
     * @param action Determina si se le ha dado clic al objeto, a su imagen, al boton de leido, al
     *               boton de encontrado, al boon de no encontrado o al boton de contactar.
     */
    @Override
    public void onObjectClick(int index, String action) {
        User user = UsersPresenter.loadUser(this);
        LostObject object = objects.get(index);

        switch (action) {
            case ObjectsAdapter.OBJECT:
                if (user != null && !"campusuq@uniquindio.edu.co".equals(user.getEmail()) &&
                        ("S".equals(user.getAdministrator()) ||
                                user.get_ID().equals(object.getUserLost_ID()))) {
                    ObjectsFragment
                            .newInstance(object).show(getSupportFragmentManager(), null);
                } else {
                    Toast.makeText(this,
                            R.string.no_propietary,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case ObjectsAdapter.IMAGE:
                try {
                    String image = objects.get(index).getImage();

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
            case ObjectsAdapter.READED: {
                ObjectsSQLiteController dbController =
                        new ObjectsSQLiteController(this, 1);
                dbController.readed(object.get_ID());
                dbController.destroy();
                loadObjects(0);
                break;
            }
            case ObjectsAdapter.FOUND:
                if (user != null && !"campusuq@uniquindio.edu.co".equals(user.getEmail())) {
                    try {
                        WebBroadcastReceiver.scheduleJob(this,
                                WebService.ACTION_OBJECTS,
                                WebService.METHOD_DELETE,
                                new JSONObject()
                                        .put("UPDATE_ID", object.get_ID())
                                        .put(ObjectsSQLiteController.columns[8], user.get_ID())
                                        .toString());
                        progressDialog.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.getLocalizedMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    startActivity(new Intent(this, LoginActivity.class)
                            .putExtra("CATEGORY", getString(R.string.log_in)));
                }
                break;
            case ObjectsAdapter.NOT_FOUND:
                if (user != null && !"campusuq@uniquindio.edu.co".equals(user.getEmail()) &&
                        (user.get_ID().equals(object.getUserLost_ID()) ||
                                user.get_ID().equals(object.getUserFound_ID()))) {
                    try {
                        WebBroadcastReceiver.scheduleJob(this,
                                WebService.ACTION_OBJECTS,
                                WebService.METHOD_DELETE,
                                new JSONObject()
                                        .put("UPDATE_ID", object.get_ID())
                                        .put(ObjectsSQLiteController.columns[8], JSONObject.NULL)
                                        .toString());
                        progressDialog.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.getLocalizedMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this,
                            R.string.no_propietary,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case ObjectsAdapter.CONTACT:
                if (user != null && !"campusuq@uniquindio.edu.co".equals(user.getEmail()) &&
                        user.get_ID().equals(object.getUserLost_ID()) &&
                        object.getUserFound_ID() != null) {
                    try {
                        WebBroadcastReceiver.scheduleJob(this,
                                WebService.ACTION_USERS,
                                WebService.METHOD_GET,
                                new JSONObject()
                                        .put(UsersSQLiteController.columns[0],
                                                object.getUserFound_ID())
                                        .toString());
                        progressDialog.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.getLocalizedMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * Define la tarea a realizar cuando se da click en una de las vistas controladas por esta
     * actividad, en caso de dar click en el boton de añadir, se abrirá la actividad que permite
     * insertar un nuevo objeto perdido.
     * @param view Vista a la cual el usuario ha dado click.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.object_report: {
                User user = UsersPresenter.loadUser(this);

                if (user != null && !"campusuq@uniquindio.edu.co".equals(user.getEmail())) {
                    startActivityForResult(
                            new Intent(this, ObjectsDetailActivity.class)
                                    .putExtra("CATEGORY", getString(R.string.object_report_lost))
                                    .putExtra(ObjectsFragment.OBJECT, new LostObject(null,
                                            user.get_ID(), null, null, null,
                                            null, null, null,
                                            null, null)),
                            REQUEST_OBJECTS_DETAIL);
                } else {
                    startActivity(new Intent(this, LoginActivity.class)
                            .putExtra("CATEGORY", getString(R.string.log_in)));
                }
                break;
            }
            default:
                break;
        }
    }

    /**
     * Muestra el cuadro de dialogo de progreso si la actividad que permite modificar o insertar un
     * objeto perdido resulta en un codigo correcto.
     * @param requestCode Código de solicitud para el cual se espera un resultado.
     * @param resultCode Código de resultado que indica exito o fracaso.
     * @param data Datos retornados por la actividad (no utilizado).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_OBJECTS_DETAIL && resultCode  == RESULT_OK &&
                !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * Método del ciclo de la actividad llamado para reanudar la misma, en el que se registra un
     * receptor para estar atento a los intentos relacionados con los objetos perdidos.
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(objectsReceiver, objectsFilter);
    }

    /**
     * Método del ciclo de la actividad llamado para pausar la misma, en el que se invalida el
     * previo registro del receptor para los objetos perdidos.
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(objectsReceiver);
    }
}