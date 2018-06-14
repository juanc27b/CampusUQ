package co.edu.uniquindio.campusuq.dishes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;

/**
 * Actividad para modificar o insertar nuevos platos.
 */
public class DishesDetailActivity extends MainActivity implements View.OnClickListener {

    private static final int REQUEST_IMAGE = 1010;

    private Intent intent;
    private Dish dish;
    private EditText name;
    private EditText description;
    private EditText price;
    private TextView descriptionCount;
    private ImageView image;
    private File imageFile;

    /**
     * Constructor que oculta el ícono de navegación reemplazandolo por una flecha de ir atrás, y
     * oculta también el botón de busqueda.
     */
    public DishesDetailActivity() {
        super.setHasNavigationDrawerIcon(false);
        super.setHasSearch(false);
    }

    /**
     * Asigna el fondo de la actividad, infla el diseño de edición de platos en la actividad
     * superior, asigna las variables de vistas y los listener de click de la vista de descripcion,
     * imagen y del boton OK, y llama la funcion para asignar los valores de las variables y vistas.
     * @param savedInstanceState Parámetro para recuperar estados anteriores de la actividad.
     */
    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.activity_dishes_detail);
        viewStub.inflate();

        intent = getIntent();
        name = findViewById(R.id.dish_detail_name);
        description = findViewById(R.id.dish_detail_description);
        descriptionCount = findViewById(R.id.dish_detail_description_count);
        price = findViewById(R.id.dish_detail_price);
        image = findViewById(R.id.dish_detail_image);

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                descriptionCount.setText(String.valueOf(charSequence.length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        image.setOnClickListener(this);
        findViewById(R.id.dish_detail_ok).setOnClickListener(this);

        setDish();
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
            setDish();
        }
    }

    /**
     * Asigna los valores a las variables y vistas desde el plato obtenido del intento.
     */
    private void setDish() {
        dish = intent.getParcelableExtra(DishesFragment.DISH);
        name.setText(dish.getName());
        description.setText(dish.getDescription());
        descriptionCount.setText(String.valueOf(description.getText().length()));
        price.setText(dish.getPrice());

        // Se concatena una cadena vacia para evitar el caso File(null)
        imageFile = new File("" + dish.getImage());

        if (imageFile.exists()) {
            image.setImageBitmap(Utilities
                    .getResizedBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath())));
        } else {
            image.setImageResource(R.drawable.rectangle_gray);
        }
    }

    /**
     * Responde al listener de la vista de imagen y del boton OK, en el caso de la imagen abre otra
     * aplicacion que permita seleccionar una imagen, en el caso del boton OK envia los datos del
     * plato al servidor para insertarlo o actualizarlo, o en caso de error muestra un mensaje con
     * la informacion de dicho error.
     * @param view Vista de la cual se puede obtener el identificador del boton OK.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dish_detail_image:
                getImage();
                break;
            case R.id.dish_detail_ok:
                if (Utilities.haveNetworkConnection(this)) {
                    if (name.getText().length() != 0 && description.getText().length() != 0 &&
                            price.getText().length() != 0) {
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory(getString(R.string.analytics_dishes_category))
                                .setAction(getString(dish.get_ID() == null ? R.string.analytics_create_action : R.string.analytics_modify_action))
                                .setLabel(getString(R.string.analytics_restaurant_label))
                                .setValue(1)
                                .build());

                        try {
                            JSONObject json = new JSONObject();
                            if (dish.get_ID() != null) json.put("UPDATE_ID", dish.get_ID());
                            json.put(DishesSQLiteController.columns[1], name.getText());
                            json.put(DishesSQLiteController.columns[2], description.getText());
                            json.put(DishesSQLiteController.columns[3], price.getText());

                            if (imageFile.exists()) {
                                json.put(DishesSQLiteController.columns[4], imageFile.getName());
                                json.put("imageString", imageFile.getAbsolutePath());
                            }

                            WebBroadcastReceiver.startService(getApplicationContext(),
                                    WebService.ACTION_DISHES, WebService.METHOD_PUT,
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

    private void getImage() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        } else {
            startActivityForResult(Intent.createChooser(
                    new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            .setType("image/*"),
                    getString(R.string.select_image)), REQUEST_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getImage();
        } else {
            Toast.makeText(this,
                    R.string.read_permission_required,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Si el codigo requerido corresponde a obtener imagen y la actividad resulta en un codigo
     * exitoso se utilizan los datos retornados para asignar el archivo y la vista de imagen del
     * plato.
     * @param requestCode Código de solicitud para el cual se espera un resultado.
     * @param resultCode Código de resultado que indica exito o fracaso.
     * @param data Datos retornados por la actividad.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) try {
            imageFile = new File(Utilities.getPath(this, data.getData()));

            if (imageFile.length() <= Utilities.UPLOAD_FILE_MAX_MB * 1024 * 1024) {
                if (imageFile.exists()) {
                    image.setImageBitmap(Utilities.getResizedBitmap(BitmapFactory
                            .decodeFile(imageFile.getAbsolutePath())));
                } else {
                    image.setImageResource(R.drawable.rectangle_gray);
                }
            } else {
                Toast.makeText(this,
                        getString(R.string.too_large_file) +
                                ' ' + Utilities.UPLOAD_FILE_MAX_MB + " MB",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.get_image_error) +
                    ":\n" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        intent = null;
        dish = null;
        name = null;
        description = null;
        price = null;
        descriptionCount = null;
        image = null;
        imageFile = null;
    }

}
