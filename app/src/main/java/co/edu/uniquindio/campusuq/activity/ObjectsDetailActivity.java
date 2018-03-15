package co.edu.uniquindio.campusuq.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.ObjectsSQLiteController;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;

public class ObjectsDetailActivity extends MainActivity implements View.OnClickListener {
    private Intent intent;
    private String user_lost_ID;
    private EditText name, place, date, description;
    private TextView description_count;
    private File imageFile;
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
        intent = getIntent();
        user_lost_ID = intent.getStringExtra(ObjectsSQLiteController.columns[1]);
        name = findViewById(R.id.object_detail_name);
        name.setText(intent.getStringExtra(ObjectsSQLiteController.columns[2]));
        place = findViewById(R.id.object_detail_place);
        place.setText(intent.getStringExtra(ObjectsSQLiteController.columns[3]));
        date = findViewById(R.id.object_detail_date);
        date.setText(intent.getStringExtra(ObjectsSQLiteController.columns[4]));
        String description_text = intent.getStringExtra(ObjectsSQLiteController.columns[5]);
        description_count = findViewById(R.id.object_detail_description_count);
        description_count.setText(String.valueOf(description_text.length()));
        description = findViewById(R.id.object_detail_description);
        description.setText(description_text);
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                description_count.setText(String.valueOf(charSequence.length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        imageFile = new File(intent.getStringExtra(ObjectsSQLiteController.columns[6]));
        image = findViewById(R.id.object_detail_image);
        if(imageFile.exists()) image.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
        else image.setImageResource(R.drawable.rectangle_gray);
        image.setOnClickListener(this);
        findViewById(R.id.object_detail_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
        case R.id.object_detail_image:
            startActivityForResult(Intent.createChooser((new Intent(Intent.ACTION_GET_CONTENT)).setType("image/*"), "Select Picture"), 0);
            break;
        case R.id.object_detail_ok:
            String id = intent.getStringExtra(ObjectsSQLiteController.columns[0]);
            JSONObject json = new JSONObject();
            try {
                if(id != null) json.put("UPDATE_ID", id);
                json.put(ObjectsSQLiteController.columns[1], user_lost_ID);
                json.put(ObjectsSQLiteController.columns[2], name.getText());
                json.put(ObjectsSQLiteController.columns[3], place.getText());
                json.put(ObjectsSQLiteController.columns[4], date.getText());
                json.put(ObjectsSQLiteController.columns[5], description.getText());
                if(imageFile.exists()) {
                    json.put(ObjectsSQLiteController.columns[6], imageFile.getName());
                    byte[] imageBytes = new byte[(int) imageFile.length()];
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(imageFile));
                    bufferedInputStream.read(imageBytes);
                    bufferedInputStream.close();
                    json.put("imageString", Base64.encodeToString(imageBytes, Base64.NO_WRAP));
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            WebBroadcastReceiver.scheduleJob(getApplicationContext(), WebService.ACTION_OBJECTS, WebService.METHOD_POST, json.toString());
            setResult(RESULT_OK, intent);
            finish();
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if(uri != null) {
                String[] projection = {MediaStore.Images.Media.DATA}, selectionArgs = {DocumentsContract.getDocumentId(uri).split(":")[1]};
                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Images.Media._ID+" = ?", selectionArgs, null);
                if(cursor != null) {
                    if(cursor.moveToFirst()) {
                        imageFile = new File(cursor.getString(cursor.getColumnIndex(projection[0])));
                        if(imageFile.exists()) image.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
                        else image.setImageResource(R.drawable.rectangle_gray);
                    }
                    cursor.close();
                }
            }
        }
    }
}
