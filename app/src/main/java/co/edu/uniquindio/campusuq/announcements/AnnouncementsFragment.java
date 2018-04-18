package co.edu.uniquindio.campusuq.announcements;

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
 * Dialogo que da la opciones de modificar o eliminar un anuncio.
 */
public class AnnouncementsFragment extends DialogFragment implements View.OnClickListener {

    static final String ANNOUNCEMENT = "announcement";
    private static final String ACTION = "action";

    private AnnouncementsActivity announcementsActivity;
    private Announcement announcement;
    private RadioButton modify;
    private RadioButton delete;
    private String action;

    /**
     * Crea una nueva instancia del dialogo y asigna sus parametros.
     * @param announcement Anuncio que se usara para el titulo del dialogo.
     * @param action Accion que determina si el dialogo fue llamado desde la funcionalidad de
     *               incidentes o de comunicados.
     * @return Instancia del nuevo dialogo.
     */
    public static AnnouncementsFragment newInstance(Announcement announcement, String action) {
        Bundle args = new Bundle();
        args.putParcelable(ANNOUNCEMENT, announcement);
        args.putString(ACTION, action);

        AnnouncementsFragment fragment = new AnnouncementsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Infla el diseño del dialogo de anuncios, asigna las variables de vistas, sus listener y sus
     * valores, y crea el dialogo.
     * @param savedInstanceState No utilizado.
     * @return Nuevo dialogo creado.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        announcementsActivity = (AnnouncementsActivity) getActivity();
        assert announcementsActivity != null;

        @SuppressLint("InflateParams") View view = announcementsActivity.getLayoutInflater()
                .inflate(R.layout.fragment_dialog, null);

        Bundle args = getArguments();
        assert args != null;

        announcement = args.getParcelable(ANNOUNCEMENT);
        modify = view.findViewById(R.id.dialog_modify);
        delete = view.findViewById(R.id.dialog_delete);
        action = args.getString(ACTION);

        view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_ok).setOnClickListener(this);

        ((TextView) view.findViewById(R.id.dialog_name)).setText(announcement.getName());

        return new AlertDialog.Builder(announcementsActivity).setView(view).create();
    }

    /**
     * Responde al listener de los botones aceptar y cancelar, para el caso de cancelar cierra el
     * cuadro de dialogo, para el caso de aceptar verifica primero si el boton modificar esta
     * seleccionado en cuyo caso inicia la actividad de edición de anuncios, si no esta seleccionado
     * entonces verifica si el boton eliminar esta seleccionado en cuyo caso envia una peticion al
     * servidor para eliminar el anuncio, finalmente cierra el cuadro de dialogo.
     * @param view Vista de la cual se pude obtener un identificador para saber a cual de los
     *             botones de ha dado click.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_ok:
                if (Utilities.haveNetworkConnection(announcementsActivity)) {
                    if (modify.isChecked()) {
                        announcementsActivity.startActivityForResult(
                                new Intent(announcementsActivity, AnnouncementsDetailActivity.class)
                                        .putExtra("CATEGORY",
                                                WebService.ACTION_INCIDENTS.equals(action) ?
                                                        getString(R.string.report_incident) :
                                                        getString(R.string.billboard_detail))
                                        .putExtra(ANNOUNCEMENT, announcement),
                                AnnouncementsActivity.REQUEST_ANNOUNCEMENT_DETAIL);
                    } else if (delete.isChecked()) {
                        announcementsActivity.mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory(getString(R.string.analytics_announcements_category))
                                .setAction(getString(R.string.analytics_delete_action))
                                .setLabel(getString(announcement.getType().equals("I") ?
                                        R.string.analytics_security_system_label :
                                        R.string.analytics_billboard_information_label))
                                .setValue(1)
                                .build());

                        try {
                            WebBroadcastReceiver.scheduleJob(announcementsActivity,
                                    action,
                                    WebService.METHOD_DELETE,
                                    new JSONObject()
                                            .put("DELETE_ID", announcement.get_ID())
                                            .toString());
                            announcementsActivity.progressDialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(announcementsActivity, e.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(announcementsActivity, R.string.no_internet,
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
