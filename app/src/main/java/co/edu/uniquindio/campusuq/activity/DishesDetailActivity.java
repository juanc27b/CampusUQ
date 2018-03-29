package co.edu.uniquindio.campusuq.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.DishesSQLiteController;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;

public class DishesDetailActivity extends MainActivity implements View.OnClickListener {

    private Intent intent;
    private String _ID;
    private EditText name;
    private EditText description;
    private EditText price;
    private TextView descriptionCount;
    private ImageView image;
    private File imageFile;

    public DishesDetailActivity() {
        super.setHasNavigationDrawerIcon(false);
        super.setHasSearch(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.activity_dishes_detail);
        viewStub.inflate();

        intent = getIntent();
        name = findViewById(R.id.dish_detail_name);
        description = findViewById(R.id.dish_detail_description);
        descriptionCount = findViewById(R.id.dish_detail_description_count);
        price = findViewById(R.id.dish_detail_price);
        image = findViewById(R.id.dish_detail_image);
        setDish();

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
        image.setOnClickListener(this);
        findViewById(R.id.dish_detail_ok).setOnClickListener(this);
    }

    @Override
    public void handleIntent(Intent intent) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(intent.getStringExtra("CATEGORY"));
            this.intent = intent;
            setDish();
        }
    }

    private void setDish() {
        _ID = intent.getStringExtra(DishesSQLiteController.columns[0]);
        name.setText(intent.getStringExtra(DishesSQLiteController.columns[1]));
        description.setText(intent.getStringExtra(DishesSQLiteController.columns[2]));
        descriptionCount.setText(String.valueOf(description.getText().length()));
        price.setText(intent.getStringExtra(DishesSQLiteController.columns[3]));

        // Se concatena una cadena vacia para evitar el caso File(null)
        imageFile = new File(""+intent.getStringExtra(DishesSQLiteController.columns[4]));
        if (imageFile.exists())
            image.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
        else image.setImageResource(R.drawable.rectangle_gray);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dish_detail_image:
                startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), "Select Picture"), 0);
                break;
            case R.id.dish_detail_ok:
                if (Utilities.haveNetworkConnection(DishesDetailActivity.this)) {
                    if (name.getText().length() != 0 && description.getText().length() != 0 &&
                            price.getText().length() != 0) {
                        JSONObject json = new JSONObject();
                        try {
                            if (_ID != null) json.put("UPDATE_ID", _ID);
                            json.put(DishesSQLiteController.columns[1], name.getText());
                            json.put(DishesSQLiteController.columns[2], description.getText());
                            json.put(DishesSQLiteController.columns[3], price.getText());
                            if (imageFile.exists()) {
                                json.put(DishesSQLiteController.columns[4], imageFile.getName());
                                byte[] imageBytes = new byte[(int) imageFile.length()];
                                BufferedInputStream bufferedInputStream =
                                        new BufferedInputStream(new FileInputStream(imageFile));
                                bufferedInputStream.read(imageBytes);
                                bufferedInputStream.close();
                                json.put("imageString",
                                        Base64.encodeToString(imageBytes, Base64.NO_WRAP));
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                        WebBroadcastReceiver.scheduleJob(getApplicationContext(),
                                WebService.ACTION_DISHES, WebService.METHOD_PUT, json.toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(this, getString(R.string.empty_string),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.no_internet),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                String[] projection = {MediaStore.Images.Media.DATA};
                String[] selectionArgs = {DocumentsContract.getDocumentId(uri).split(":")[1]};
                Cursor cursor = getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                        MediaStore.Images.Media._ID+" = ?", selectionArgs, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        imageFile =
                                new File(cursor.getString(cursor.getColumnIndex(projection[0])));
                        if (imageFile.exists()) image.setImageBitmap(BitmapFactory
                                .decodeFile(imageFile.getAbsolutePath()));
                        else image.setImageResource(R.drawable.rectangle_gray);
                    }
                    cursor.close();
                }
            }
        }
    }

}
