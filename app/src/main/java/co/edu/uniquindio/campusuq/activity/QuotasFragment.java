package co.edu.uniquindio.campusuq.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.QuotasServiceController;
import co.edu.uniquindio.campusuq.vo.Quota;

import static org.apache.commons.lang3.math.NumberUtils.max;
import static org.apache.commons.lang3.math.NumberUtils.min;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class QuotasFragment extends DialogFragment implements View.OnClickListener {
    private static final String INDEX = "index";

    private int index;
    private TextView quota;

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
        QuotasActivity quotasActivity = (QuotasActivity) getActivity();
        View view = quotasActivity.getLayoutInflater().inflate(R.layout.fragment_quotas, null);
        index = getArguments().getInt(INDEX);
        Quota q = quotasActivity.quotas.get(index);
        ((TextView) view.findViewById(R.id.quota_dialog_name)).setText("Ajustar Cupos de "+q.getName());
        quota = view.findViewById(R.id.quota_dialog_quota);
        quota.setText(q.getQuota());
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
            quota.setText(String.valueOf(min(max(toInt(quota.getText().toString())-1, 0), 99)));
            break;
        case R.id.quota_dialog_plus:
            quota.setText(String.valueOf(min(max(toInt(quota.getText().toString())+1, 0), 99)));
            break;
        case R.id.quota_dialog_ok:
            QuotasActivity quotasActivity = (QuotasActivity) getActivity();
            Quota q = quotasActivity.quotas.get(index);
            q.setQuota(quota.getText().toString());
            quotasActivity.quotas.set(index, q);

            JSONObject json = new JSONObject();
            try {
                json.put("UPDATE_ID", q.get_ID());
                json.put("Cupo", q.getQuota());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PostAsync postAsync = new PostAsync(quotasActivity.getApplicationContext());
            postAsync.execute(json.toString());

            //QuotasServiceController.modQuota("{\"UPDATE_ID\":\""+q.get_ID()+"\",\"Cupo\":\""+q.getQuota()+"\"}");
            //Log.i("post Cupo", "{\"UPDATE_ID\":\""+q.get_ID()+"\",\"Cupo\":\""+q.getQuota()+"\"}");
            quotasActivity.mAdapter.setQuotas(quotasActivity.quotas);
        case R.id.quota_dialog_cancel:
            dismiss();
            break;
        }
    }

    public class PostAsync extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;

        public PostAsync(Context context) {
            progressDialog = ((QuotasActivity) getActivity()).progressDialog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            QuotasServiceController.modQuota(strings[0]);
            Log.i("post Cupo", strings[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(progressDialog.isShowing()) progressDialog.dismiss();
        }
    }
}