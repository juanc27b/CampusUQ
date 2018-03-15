package co.edu.uniquindio.campusuq.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.QuotasSQLiteController;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;
import co.edu.uniquindio.campusuq.vo.Quota;

public class QuotasFragment extends DialogFragment implements View.OnClickListener {

    private static final String INDEX = "index";

    private QuotasActivity quotasActivity;
    private Quota quota;
    private TextView quotaText;

    public static QuotasFragment newInstance(int index) {
        Bundle args = new Bundle();
        args.putInt(INDEX, index);
        QuotasFragment fragment = new QuotasFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        quotasActivity = (QuotasActivity) getActivity();
        assert quotasActivity != null;
        @SuppressLint("InflateParams") View view = quotasActivity.getLayoutInflater().inflate(R.layout.fragment_quotas, null);
        Bundle args = getArguments();
        assert args != null;
        quota = quotasActivity.getQuota(args.getInt(INDEX));
        ((TextView) view.findViewById(R.id.quota_dialog_name)).setText("Ajustar Cupos de "+quota.getName());
        quotaText = view.findViewById(R.id.quota_dialog_quota);
        quotaText.setText(quota.getQuota());
        view.findViewById(R.id.quota_dialog_minus).setOnClickListener(this);
        view.findViewById(R.id.quota_dialog_plus).setOnClickListener(this);
        view.findViewById(R.id.quota_dialog_cancel).setOnClickListener(this);
        view.findViewById(R.id.quota_dialog_ok).setOnClickListener(this);
        return (new AlertDialog.Builder(quotasActivity)).setView(view).create();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.quota_dialog_minus:
                quotaText.setText(String.valueOf(Math.min(Math.max(Integer.valueOf(quotaText.getText().toString())-1, 0), 99)));
                break;
            case R.id.quota_dialog_plus:
                quotaText.setText(String.valueOf(Math.min(Math.max(Integer.valueOf(quotaText.getText().toString())+1, 0), 99)));
                break;
            case R.id.quota_dialog_ok:
                JSONObject json = new JSONObject();
                try {
                    json.put("UPDATE_ID", quota.get_ID());
                    json.put(QuotasSQLiteController.columns[3], quotaText.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                quotasActivity.progressDialog.show();
                WebBroadcastReceiver.scheduleJob(quotasActivity.getApplicationContext(), WebService.ACTION_QUOTAS, WebService.METHOD_PUT, json.toString());
                // Tanto ok como cancel cierran el dialogo, por eso aqui no hay break
            case R.id.quota_dialog_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }
}