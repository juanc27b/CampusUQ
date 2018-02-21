package co.edu.uniquindio.campusuq.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.WebContentActivity;

/**
 * Created by Juan Camilo on 21/02/2018.
 */

public class LoadWebContentAsync extends AsyncTask<String, Void, String[]> {

    private Context context;
    private ProgressDialog pDialog;

    public LoadWebContentAsync(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setTitle(context.getString(R.string.loading_content));
        pDialog.setMessage(context.getString(R.string.please_wait));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String[] doInBackground(String... args) {
        String[] webContent = new String[3];
        String category = args[0];
        webContent[0] = category;
        String[] content = new String[2];
        if (context.getString(R.string.symbols).equals(category)) {
            content = ItemsPresenter.getInformation("Simbolos", context);
        } else if (context.getString(R.string.institutional_welfare).equals(category)) {
            content = ItemsPresenter.getInformation("Cursos Culturales y Deportivos", context);
        }
        webContent[1] = content[0];
        webContent[2] = content[1];
        return webContent;
    }

    @Override
    protected void onPostExecute(String[] webContent) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
        Intent intent = new Intent(context, WebContentActivity.class);
        intent.putExtra("CATEGORY", webContent[0]);
        intent.putExtra("LINK", webContent[1]);
        intent.putExtra("CONTENT", webContent[2]);
        context.startActivity(intent);
    }

}
