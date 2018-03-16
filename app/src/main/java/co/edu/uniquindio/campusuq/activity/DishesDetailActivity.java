package co.edu.uniquindio.campusuq.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.DishesSQLiteController;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;

public class DishesDetailActivity extends MainActivity implements View.OnClickListener {

    private TextView name, description, descriptionCount, price;
    private ImageView image;

    public DishesDetailActivity() {
        super.setHasNavigationDrawerIcon(false);
        super.setHasSearch(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);

        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.activity_dishes_detail);
        viewStub.inflate();

        Intent intent = getIntent();
        name = findViewById(R.id.dish_detail_name);
        name.setText(intent.getStringExtra(DishesSQLiteController.columns[1]));
        String description_text = intent.getStringExtra(DishesSQLiteController.columns[2]);
        descriptionCount = findViewById(R.id.dish_detail_description_count);
        descriptionCount.setText(String.valueOf(description_text.length()));
        description = findViewById(R.id.dish_detail_description);
        description.setText(description_text);
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                descriptionCount.setText(String.valueOf(charSequence.length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        price = findViewById(R.id.dish_detail_price);
        price.setText(intent.getStringExtra(DishesSQLiteController.columns[3]));

        File imgFile = new  File(intent.getStringExtra(DishesSQLiteController.columns[4]));
        image = findViewById(R.id.dish_detail_image);
        if(imgFile.exists()) image.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
        image.setOnClickListener(this);

        findViewById(R.id.dish_detail_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
        case R.id.dish_detail_image:
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
            break;
        case R.id.dish_detail_ok:
            JSONObject json = new JSONObject();
            try {
                String id = getIntent().getStringExtra(DishesSQLiteController.columns[0]);
                if(id != null) json.put("UPDATE_ID", id);
                json.put(DishesSQLiteController.columns[1], name.getText());
                json.put(DishesSQLiteController.columns[2], description.getText());
                json.put(DishesSQLiteController.columns[3], price.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            WebBroadcastReceiver.scheduleJob(getApplicationContext(), WebService.ACTION_DISHES, WebService.METHOD_PUT, json.toString());
            setResult(RESULT_OK, getIntent());
            finish();
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            try {
                image.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}