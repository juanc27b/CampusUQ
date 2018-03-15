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
import co.edu.uniquindio.campusuq.util.DishesSQLiteController;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;
import co.edu.uniquindio.campusuq.vo.Dish;

public class DishesFragment extends DialogFragment implements View.OnClickListener {

    private static final String INDEX = "index";

    private DishesActivity dishesActivity;
    private Dish dish;
    private RadioButton modify, delete;

    public static DishesFragment newInstance(int index) {
        Bundle args = new Bundle();
        args.putInt(INDEX, index);
        DishesFragment fragment = new DishesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dishesActivity = (DishesActivity) getActivity();
        assert dishesActivity != null;
        @SuppressLint("InflateParams") View view = dishesActivity.getLayoutInflater().inflate(R.layout.fragment_dialog, null);
        Bundle args = getArguments();
        assert args != null;
        dish = dishesActivity.getDish(args.getInt(INDEX));
        ((TextView) view.findViewById(R.id.dialog_name)).setText(dish.getName());
        modify = view.findViewById(R.id.dialog_modify);
        delete = view.findViewById(R.id.dialog_delete);
        view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_ok).setOnClickListener(this);
        return (new AlertDialog.Builder(dishesActivity)).setView(view).create();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
        case R.id.dialog_ok:
            if(modify.isChecked()) {
                Intent intent = new Intent(dishesActivity, DishesDetailActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.restaurant_detail));
                intent.putExtra(DishesSQLiteController.columns[0], dish.get_ID());
                intent.putExtra(DishesSQLiteController.columns[1], dish.getName());
                intent.putExtra(DishesSQLiteController.columns[2], dish.getDescription());
                intent.putExtra(DishesSQLiteController.columns[3], dish.getPrice());
                intent.putExtra(DishesSQLiteController.columns[4], dish.getImage());
                startActivityForResult(intent, 0);
            } else if(delete.isChecked()) {
                JSONObject json = new JSONObject();
                try {
                    json.put("DELETE_ID", dish.get_ID());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                WebBroadcastReceiver.scheduleJob(dishesActivity.getApplicationContext(), WebService.ACTION_DISHES, WebService.METHOD_DELETE, json.toString());
                dishesActivity.progressDialog.show();
            }
            // Tanto ok como cancel cierran el dialogo, por eso aqui no hay break
        case R.id.dialog_cancel:
            dismiss();
            break;
        }
    }
}