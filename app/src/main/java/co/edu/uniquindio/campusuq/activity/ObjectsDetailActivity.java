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
import co.edu.uniquindio.campusuq.util.ObjectsSQLiteController;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;

public class ObjectsDetailActivity extends MainActivity implements View.OnClickListener {

    private String userLost_ID;
    private TextView name, place, date, description, descriptionCount;
    private ImageView image;

    public ObjectsDetailActivity() {
        super.setHasNavigationDrawerIcon(false);
        super.setHasSearch(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);

        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.activity_objects_detail);
        viewStub.inflate();

        Intent intent = getIntent();
        userLost_ID = intent.getStringExtra(ObjectsSQLiteController.columns[1]);
        name = findViewById(R.id.object_detail_name);
        name.setText(intent.getStringExtra(ObjectsSQLiteController.columns[2]));
        place = findViewById(R.id.object_detail_place);
        place.setText(intent.getStringExtra(ObjectsSQLiteController.columns[3]));
        date = findViewById(R.id.object_detail_date);
        date.setText(intent.getStringExtra(ObjectsSQLiteController.columns[4]));
        String descriptionText = intent.getStringExtra(ObjectsSQLiteController.columns[5]);
        descriptionCount = findViewById(R.id.object_detail_description_count);
        descriptionCount.setText(String.valueOf(descriptionText.length()));
        description = findViewById(R.id.object_detail_description);
        description.setText(descriptionText);
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

        File imgFile = new  File(intent.getStringExtra(ObjectsSQLiteController.columns[6]));
        image = findViewById(R.id.object_detail_image);
        if(imgFile.exists()) image.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
        image.setOnClickListener(this);

        findViewById(R.id.object_detail_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.object_detail_image:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
                break;
            case R.id.object_detail_ok:
                JSONObject json = new JSONObject();
                try {
                    String id = getIntent().getStringExtra(ObjectsSQLiteController.columns[0]);
                    if(id != null) json.put("UPDATE_ID", id);
                    json.put(ObjectsSQLiteController.columns[1], userLost_ID);
                    json.put(ObjectsSQLiteController.columns[2], name.getText());
                    json.put(ObjectsSQLiteController.columns[3], place.getText());
                    json.put(ObjectsSQLiteController.columns[4], date.getText());
                    json.put(ObjectsSQLiteController.columns[5], description.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                WebBroadcastReceiver.scheduleJob(getApplicationContext(), WebService.ACTION_OBJECTS, WebService.METHOD_POST, json.toString());
                setResult(RESULT_OK, getIntent());
                finish();
                break;
            default:
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
