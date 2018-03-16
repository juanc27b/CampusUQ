package co.edu.uniquindio.campusuq.activity;

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

import org.json.JSONException;
import org.json.JSONObject;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.QuotasSQLiteController;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;
import co.edu.uniquindio.campusuq.vo.Quota;

public class QuotasFragment extends DialogFragment implements View.OnClickListener {

    private static final String LONG_CLICK = "long_click", INDEX = "index";

    private QuotasActivity quotasActivity;
    private Quota q;
    private TextView quota;
    private RadioButton modify, delete;

    public static QuotasFragment newInstance(boolean long_click, int index) {
        Bundle args = new Bundle();
        args.putBoolean(LONG_CLICK, long_click);
        args.putInt(INDEX, index);
        QuotasFragment fragment = new QuotasFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Bundle args = getArguments();
        assert args != null;
        boolean long_click = args.getBoolean(LONG_CLICK);
        quotasActivity = (QuotasActivity) getActivity();
        assert quotasActivity != null;
        @SuppressLint("InflateParams") View view = quotasActivity.getLayoutInflater().inflate(long_click ? R.layout.fragment_dialog : R.layout.fragment_quotas, null);
        q = quotasActivity.getQuota(args.getInt(INDEX));
        if(long_click) {
            ((TextView) view.findViewById(R.id.dialog_name)).setText(q.getName());
            modify = view.findViewById(R.id.dialog_modify);
            delete = view.findViewById(R.id.dialog_delete);
            view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
            view.findViewById(R.id.dialog_ok).setOnClickListener(this);
        } else {
            ((TextView) view.findViewById(R.id.quota_dialog_name)).setText("Ajustar Cupos de "+q.getName());
            quota = view.findViewById(R.id.quota_dialog_quota);
            quota.setText(q.getQuota());
            view.findViewById(R.id.quota_dialog_minus).setOnClickListener(this);
            view.findViewById(R.id.quota_dialog_plus).setOnClickListener(this);
            view.findViewById(R.id.quota_dialog_cancel).setOnClickListener(this);
            view.findViewById(R.id.quota_dialog_ok).setOnClickListener(this);
        }
        return (new AlertDialog.Builder(quotasActivity)).setView(view).create();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.quota_dialog_minus:
                quota.setText(String.valueOf(Math.min(Math.max(Integer.valueOf(quota.getText().toString())-1, 0), 99)));
                break;
            case R.id.quota_dialog_plus:
                quota.setText(String.valueOf(Math.min(Math.max(Integer.valueOf(quota.getText().toString())+1, 0), 99)));
                break;
            case R.id.quota_dialog_ok:
                JSONObject json = new JSONObject();
                try {
                    json.put("UPDATE_ID", q.get_ID());
                    json.put(QuotasSQLiteController.columns[3], quota.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                quotasActivity.progressDialog.show();
                WebBroadcastReceiver.scheduleJob(quotasActivity.getApplicationContext(), WebService.ACTION_QUOTAS, WebService.METHOD_PUT, json.toString());
                dismiss();
                break;
            case R.id.dialog_ok:
                if(modify.isChecked()) {
                    Intent intent = new Intent(quotasActivity, QuotasDetailActivity.class);
                    intent.putExtra("CATEGORY", getString(R.string.quota_detail));
                    intent.putExtra(QuotasSQLiteController.columns[0], q.get_ID());
                    intent.putExtra(QuotasSQLiteController.columns[1], q.getType());
                    intent.putExtra(QuotasSQLiteController.columns[2], q.getName());
                    intent.putExtra(QuotasSQLiteController.columns[3], q.getQuota());
                    startActivityForResult(intent, 0);
                } else if(delete.isChecked()) {
                    json = new JSONObject();
                    try {
                        json.put("DELETE_ID", q.get_ID());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    WebBroadcastReceiver.scheduleJob(quotasActivity.getApplicationContext(), WebService.ACTION_QUOTAS, WebService.METHOD_DELETE, json.toString());
                    quotasActivity.progressDialog.show();
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
