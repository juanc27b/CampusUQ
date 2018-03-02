package co.edu.uniquindio.campusuq.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.vo.Dish;

public class DishesFragment extends DialogFragment implements View.OnClickListener {
    private static final String INDEX = "index";

    private int index;

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
        DishesActivity dishesActivity = (DishesActivity) getActivity();
        View view = dishesActivity.getLayoutInflater().inflate(R.layout.fragment_dishes, null);
        index = getArguments().getInt(INDEX);
        Dish dish = dishesActivity.dishes.get(index);
        ((TextView) view.findViewById(R.id.dish_dialog_name)).setText(dish.getName());
        view.findViewById(R.id.dish_dialog_cancel).setOnClickListener(this);
        view.findViewById(R.id.dish_dialog_ok).setOnClickListener(this);
        return (new AlertDialog.Builder(dishesActivity)).setView(view).create();
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }
}