package co.edu.uniquindio.campusuq.dishes;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;

import org.json.JSONException;
import org.json.JSONObject;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;

/**
 * Dialogo que da la opciones de modificar o eliminar un plato del menú del restaurante.
 */
public class DishesFragment extends DialogFragment implements View.OnClickListener {

    static final String DISH = "dish";

    private DishesActivity dishesActivity;
    private Dish dish;
    private RadioButton modify;
    private RadioButton delete;

    /**
     * Crea una nueva instancia del dialogo y asigna sus parametros.
     * @param dish Plato que se usara para el titulo del dialogo.
     * @return Instancia del nuevo dialogo.
     */
    public static DishesFragment newInstance(Dish dish) {
        Bundle args = new Bundle();
        args.putParcelable(DISH, dish);

        DishesFragment fragment = new DishesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Infla el diseño del dialogo de platos, asigna las variables de vistas, sus listener y sus
     * valores, y crea el dialogo.
     * @param savedInstanceState No utilizado.
     * @return Nuevo dialogo creado.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dishesActivity = (DishesActivity) getActivity();
        assert dishesActivity != null;

        @SuppressLint("InflateParams") View view = dishesActivity.getLayoutInflater()
                .inflate(R.layout.fragment_dialog, null);

        Bundle args = getArguments();
        assert args != null;

        dish = args.getParcelable(DISH);
        modify = view.findViewById(R.id.dialog_modify);
        delete = view.findViewById(R.id.dialog_delete);

        view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_ok).setOnClickListener(this);

        ((TextView) view.findViewById(R.id.dialog_name)).setText(dish.getName());

        return new AlertDialog.Builder(dishesActivity).setView(view).create();
    }

    /**
     * Responde al listener de los botones aceptar y cancelar, para el caso de cancelar cierra el
     * cuadro de dialogo, para el caso de aceptar verifica primero si el boton modificar esta
     * seleccionado en cuyo caso inicia la actividad de edición de platos, si no esta seleccionado
     * entonces verifica si el boton eliminar esta seleccionado en cuyo caso envia una peticion al
     * servidor para eliminar el plato, finalmente cierra el cuadro de dialogo.
     * @param view Vista de la cual se pude obtener un identificador para saber a cual de los
     *             botones de ha dado click.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_ok:
                if (Utilities.haveNetworkConnection(dishesActivity)) {
                    if (modify.isChecked()) {
                        dishesActivity.startActivityForResult(
                                new Intent(dishesActivity, DishesDetailActivity.class)
                                        .putExtra("CATEGORY", getString(R.string.restaurant_detail))
                                        .putExtra(DISH, dish),
                                DishesActivity.REQUEST_DISHES_DETAIL);
                    } else if (delete.isChecked()) {
                        dishesActivity.mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory(getString(R.string.analytics_dishes_category))
                                .setAction(getString(R.string.analytics_delete_action))
                                .setLabel(getString(R.string.analytics_restaurant_label))
                                .setValue(1)
                                .build());

                        try {
                            WebBroadcastReceiver.startService(dishesActivity,
                                    WebService.ACTION_DISHES,
                                    WebService.METHOD_DELETE,
                                    new JSONObject()
                                            .put("DELETE_ID", dish.get_ID())
                                            .toString());
                            dishesActivity.progressDialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(dishesActivity, e.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(dishesActivity, getString(R.string.no_internet),
                            Toast.LENGTH_SHORT).show();
                }
                // Tanto ok como cancel cierran el dialogo, por eso aqui no hay break
            case R.id.dialog_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

}
