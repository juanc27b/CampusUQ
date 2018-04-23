package co.edu.uniquindio.campusuq.quotas;

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
 * Dialogo que permite modificar rapidamente un cupo de forma parcial, o da la opciones de modificar
 * completamente o eliminar un cupo.
 */
public class QuotasFragment extends DialogFragment implements View.OnClickListener {

    private static final String FRAGMENT_QUOTAS = "fragment_quotas";
    static final String QUOTA = "quota";

    private boolean fragment_quotas;
    private QuotasActivity quotasActivity;
    private Quota q;
    private TextView quota;
    private RadioButton modify;
    private RadioButton delete;

    /**
     * Crea una nueva instancia del dialogo y asigna sus parametros.
     * @param fragment_quotas Determina el tipo de dialogo a crear.
     * @param q Cupo que se usara para el titulo del dialogo.
     * @return Instancia del nuevo dialogo.
     */
    public static QuotasFragment newInstance(boolean fragment_quotas, Quota q) {
        Bundle args = new Bundle();
        args.putBoolean(FRAGMENT_QUOTAS, fragment_quotas);
        args.putParcelable(QUOTA, q);

        QuotasFragment fragment = new QuotasFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Infla el diseño del dialogo de cupos, asigna las variables de vistas, sus listener y sus
     * valores, y crea el dialogo.
     * @param savedInstanceState No utilizado.
     * @return Nuevo dialogo creado.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        assert args != null;

        fragment_quotas = args.getBoolean(FRAGMENT_QUOTAS);

        quotasActivity = (QuotasActivity) getActivity();
        assert quotasActivity != null;

        @SuppressLint("InflateParams") View view = quotasActivity.getLayoutInflater()
                .inflate(fragment_quotas? R.layout.fragment_quotas : R.layout.fragment_dialog,
                        null);

        q = args.getParcelable(QUOTA);

        if (fragment_quotas) {
            quota = view.findViewById(R.id.quota_dialog_quota);

            view.findViewById(R.id.quota_dialog_minus).setOnClickListener(this);
            view.findViewById(R.id.quota_dialog_plus).setOnClickListener(this);
            view.findViewById(R.id.quota_dialog_cancel).setOnClickListener(this);
            view.findViewById(R.id.quota_dialog_ok).setOnClickListener(this);

            ((TextView) view.findViewById(R.id.quota_dialog_name)).setText(String
                    .format("%s %s", getString(R.string.adjust_quotas), q.getName()));
            quota.setText(q.getQuota());
        } else {
            modify = view.findViewById(R.id.dialog_modify);
            delete = view.findViewById(R.id.dialog_delete);

            view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
            view.findViewById(R.id.dialog_ok).setOnClickListener(this);

            ((TextView) view.findViewById(R.id.dialog_name)).setText(q.getName());
        }

        return new AlertDialog.Builder(quotasActivity).setView(view).create();
    }

    /**
     * Responde al listener de los botones aceptar y cancelar, para el caso de cancelar cierra el
     * cuadro de dialogo, para el caso de aceptar verifica primero si el boton modificar esta
     * seleccionado en cuyo caso inicia la actividad de edición de cupos, si no esta seleccionado
     * entonces verifica si el boton eliminar esta seleccionado en cuyo caso envia una peticion al
     * servidor para eliminar el cupo, finalmente cierra el cuadro de dialogo.
     * @param view Vista de la cual se pude obtener un identificador para saber a cual de los
     *             botones de ha dado click.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.quota_dialog_minus:
                quota.setText(String.valueOf(Math
                        .min(Math.max(Integer.parseInt(quota.getText().toString()) - 1, 0), 999)));
                break;
            case R.id.quota_dialog_plus:
                quota.setText(String.valueOf(Math
                        .min(Math.max(Integer.parseInt(quota.getText().toString()) + 1, 0), 999)));
                break;
            case R.id.quota_dialog_ok:
            case R.id.dialog_ok:
                if (Utilities.haveNetworkConnection(quotasActivity)) {
                    if (fragment_quotas) {
                        try {
                            quotasActivity.progressDialog.show();
                            WebBroadcastReceiver.scheduleJob(quotasActivity,
                                    WebService.ACTION_QUOTAS, WebService.METHOD_PUT,
                                    new JSONObject()
                                            .put("UPDATE_ID", q.get_ID())
                                            .put(QuotasSQLiteController.columns[3], quota.getText())
                                            .toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(quotasActivity, e.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else if (modify.isChecked()) {
                        quotasActivity.startActivityForResult(
                                new Intent(quotasActivity, QuotasDetailActivity.class)
                                        .putExtra("CATEGORY", getString(R.string.quota_detail_modify))
                                        .putExtra(QUOTA, q),
                                QuotasActivity.REQUEST_QUOTAS_DETAIL);
                    } else if (delete.isChecked()) {
                        quotasActivity.mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory(getString(R.string.analytics_quotas_category))
                                .setAction(getString(R.string.analytics_delete_action))
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
                            WebBroadcastReceiver.scheduleJob(quotasActivity,
                                    WebService.ACTION_QUOTAS, WebService.METHOD_DELETE,
                                    new JSONObject()
                                            .put("DELETE_ID", q.get_ID())
                                            .toString());
                            quotasActivity.progressDialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(quotasActivity, e.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(quotasActivity, R.string.no_internet,
                            Toast.LENGTH_SHORT).show();
                }
                // Tanto ok como cancel cierran el dialogo, por eso aqui no hay break
            case R.id.quota_dialog_cancel:
            case R.id.dialog_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

}
