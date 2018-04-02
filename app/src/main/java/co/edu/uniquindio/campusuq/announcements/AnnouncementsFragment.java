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

public class AnnouncementsFragment extends DialogFragment implements View.OnClickListener {

    private static final String INDEX  = "index";
    private static final String ACTION = "action";

    private AnnouncementsActivity announcementsActivity;
    private Announcement announcement;
    private RadioButton modify;
    private RadioButton delete;
    private String action;

    public static AnnouncementsFragment newInstance(int index, String action) {
        Bundle args = new Bundle();
        args.putInt(INDEX, index);
        args.putString(ACTION, action);

        AnnouncementsFragment fragment = new AnnouncementsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        announcementsActivity = (AnnouncementsActivity) getActivity();
        assert announcementsActivity != null;

        @SuppressLint("InflateParams") View view = announcementsActivity.getLayoutInflater()
                .inflate(R.layout.fragment_dialog, null);

        Bundle args = getArguments();
        assert args != null;

        announcement = announcementsActivity.getAnnouncement(args.getInt(INDEX));
        ((TextView) view.findViewById(R.id.dialog_name)).setText(announcement.getName());
        modify = view.findViewById(R.id.dialog_modify);
        delete = view.findViewById(R.id.dialog_delete);
        action = args.getString(ACTION);

        view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_ok).setOnClickListener(this);

        return new AlertDialog.Builder(announcementsActivity).setView(view).create();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_ok:
                if (Utilities.haveNetworkConnection(announcementsActivity)) {
                    if (modify.isChecked()) {
                        Intent intent = new Intent(announcementsActivity,
                                AnnouncementsDetailActivity.class);
                        intent.putExtra("CATEGORY",
                                WebService.ACTION_INCIDENTS.equals(action) ?
                                        getString(R.string.report_incident) :
                                        getString(R.string.billboard_detail));
                        intent.putExtra(AnnouncementsSQLiteController.columns[0],
                                ""+announcement.get_ID());
                        intent.putExtra(AnnouncementsSQLiteController.columns[1],
                                announcement.getUser_ID());
                        intent.putExtra(AnnouncementsSQLiteController.columns[3],
                                announcement.getName());
                        intent.putExtra(AnnouncementsSQLiteController.columns[5],
                                announcement.getDescription());
                        announcementsActivity.startActivityForResult(intent,
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
                        JSONObject json = new JSONObject();
                        try {
                            json.put("DELETE_ID", announcement.get_ID());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        WebBroadcastReceiver.scheduleJob(announcementsActivity, action,
                                WebService.METHOD_DELETE, json.toString());
                        announcementsActivity.progressDialog.show();
                    }
                } else {
                    Toast.makeText(announcementsActivity, getString(R.string.no_internet),
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
