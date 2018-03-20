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
import co.edu.uniquindio.campusuq.util.ObjectsSQLiteController;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;
import co.edu.uniquindio.campusuq.vo.LostObject;

public class ObjectsFragment extends DialogFragment implements View.OnClickListener {

    private static final String INDEX = "index";

    private ObjectsActivity objectsActivity;
    private LostObject object;
    private RadioButton modify, delete;

    public static ObjectsFragment newInstance(int index) {
        Bundle args = new Bundle();
        args.putInt(INDEX, index);
        ObjectsFragment fragment = new ObjectsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        objectsActivity = (ObjectsActivity) getActivity();
        assert objectsActivity != null;
        @SuppressLint("InflateParams") View view = objectsActivity.getLayoutInflater().inflate(R.layout.fragment_dialog, null);
        Bundle args = getArguments();
        assert args != null;
        object = objectsActivity.getObject(args.getInt(INDEX));
        ((TextView) view.findViewById(R.id.dialog_name)).setText(object.getName());
        modify = view.findViewById(R.id.dialog_modify);
        delete = view.findViewById(R.id.dialog_delete);
        view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_ok).setOnClickListener(this);
        return (new AlertDialog.Builder(objectsActivity)).setView(view).create();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
        case R.id.dialog_ok:
            if(modify.isChecked()) {
                Intent intent = new Intent(objectsActivity, ObjectsDetailActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.object_report_lost));
                intent.putExtra(ObjectsSQLiteController.columns[0], object.get_ID());
                intent.putExtra(ObjectsSQLiteController.columns[1], "1");
                intent.putExtra(ObjectsSQLiteController.columns[2], object.getName());
                intent.putExtra(ObjectsSQLiteController.columns[3], object.getPlace());
                intent.putExtra(ObjectsSQLiteController.columns[4], object.getDate());
                intent.putExtra(ObjectsSQLiteController.columns[5], object.getDescription());
                intent.putExtra(ObjectsSQLiteController.columns[6], object.getImage());
                startActivityForResult(intent, 0);
            } else if(delete.isChecked()) {
                JSONObject json = new JSONObject();
                try {
                    json.put("DELETE_ID", object.get_ID());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                WebBroadcastReceiver.scheduleJob(objectsActivity.getApplicationContext(), WebService.ACTION_OBJECTS, WebService.METHOD_DELETE, json.toString());
                objectsActivity.progressDialog.show();
            }
            // Tanto ok como cancel cierran el dialogo, por eso aqui no hay break
        case R.id.dialog_cancel:
            dismiss();
            break;
        }
    }

}