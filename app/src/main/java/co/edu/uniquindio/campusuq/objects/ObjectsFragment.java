package co.edu.uniquindio.campusuq.objects;

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
 * Dialogo que da la opciones de modificar o eliminar un objeto perdido.
 */
public class ObjectsFragment extends DialogFragment implements View.OnClickListener {

    static final String OBJECT = "object";

    private ObjectsActivity objectsActivity;
    private LostObject object;
    private RadioButton modify;
    private RadioButton delete;

    /**
     * Crea una nueva instancia del dialogo y asigna sus parametros.
     * @param object Objeto perdido que se usara para el titulo del dialogo.
     * @return Instancia del nuevo dialogo.
     */
    public static ObjectsFragment newInstance(LostObject object) {
        Bundle args = new Bundle();
        args.putParcelable(OBJECT, object);

        ObjectsFragment fragment = new ObjectsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Infla el diseño del dialogo de objetos perdidos, asigna las variables de vistas, sus listener
     * y sus valores, y crea el dialogo.
     * @param savedInstanceState No utilizado.
     * @return Nuevo dialogo creado.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        objectsActivity = (ObjectsActivity) getActivity();
        assert objectsActivity != null;

        @SuppressLint("InflateParams") View view = objectsActivity.getLayoutInflater()
                .inflate(R.layout.fragment_dialog, null);

        Bundle args = getArguments();
        assert args != null;

        object = args.getParcelable(OBJECT);
        modify = view.findViewById(R.id.dialog_modify);
        delete = view.findViewById(R.id.dialog_delete);

        view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_ok).setOnClickListener(this);

        ((TextView) view.findViewById(R.id.dialog_name)).setText(object.getName());

        return new AlertDialog.Builder(objectsActivity).setView(view).create();
    }

    /**
     * Responde al listener de los botones aceptar y cancelar, para el caso de cancelar cierra el
     * cuadro de dialogo, para el caso de aceptar verifica primero si el boton modificar esta
     * seleccionado en cuyo caso inicia la actividad de edición de objetos perdidos, si no esta
     * seleccionado entonces verifica si el boton eliminar esta seleccionado en cuyo caso envia una
     * peticion al servidor para eliminar el objeto perdido, finalmente cierra el cuadro de dialogo.
     * @param view Vista de la cual se pude obtener un identificador para saber a cual de los
     *             botones de ha dado click.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_ok:
                if (Utilities.haveNetworkConnection(objectsActivity)) {
                    if (modify.isChecked()) {
                        objectsActivity.startActivityForResult(
                                new Intent(objectsActivity, ObjectsDetailActivity.class)
                                        .putExtra(Utilities.CATEGORY, R.string.object_report_lost)
                                        .putExtra(OBJECT, object),
                                ObjectsActivity.REQUEST_OBJECTS_DETAIL);
                    } else if (delete.isChecked()) {
                        objectsActivity.mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory(getString(R.string.analytics_objects_category))
                                .setAction(getString(R.string.analytics_delete_action))
                                .setLabel(getString(R.string.analytics_lost_objects_label))
                                .setValue(1)
                                .build());

                        try {
                            WebBroadcastReceiver.startService(objectsActivity,
                                    WebService.ACTION_OBJECTS,
                                    WebService.METHOD_DELETE,
                                    new JSONObject()
                                            .put("DELETE_ID", object.get_ID())
                                            .toString());
                            objectsActivity.swipeRefreshLayout.setRefreshing(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(objectsActivity, e.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(objectsActivity, R.string.no_internet,
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
