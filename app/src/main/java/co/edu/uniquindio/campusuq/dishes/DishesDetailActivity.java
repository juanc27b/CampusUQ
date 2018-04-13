package co.edu.uniquindio.campusuq.dishes;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;

import org.json.JSONObject;

import java.io.File;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;

public class DishesDetailActivity extends MainActivity implements View.OnClickListener {

    private static final int REQUEST_IMAGE = 1010;

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

        if (imageFile.exists()) {
            image.setImageBitmap(Utilities.getResizedBitmap(BitmapFactory
                    .decodeFile(imageFile.getAbsolutePath())));
        } else {
            image.setImageResource(R.drawable.rectangle_gray);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dish_detail_image:
                startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI).setType("image/*"),
                        getString(R.string.select_image)), REQUEST_IMAGE);
                break;
            case R.id.dish_detail_ok:
                if (Utilities.haveNetworkConnection(this)) {
                    if (name.getText().length() != 0 && description.getText().length() != 0 &&
                            price.getText().length() != 0) {
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory(getString(R.string.analytics_dishes_category))
                                .setAction(getString(_ID == null ?
                                        R.string.analytics_create_action : R.string.analytics_modify_action))
                                .setLabel(getString(R.string.analytics_restaurant_label))
                                .setValue(1)
                                .build());

                        try {
                            JSONObject json = new JSONObject();
                            if (_ID != null) json.put("UPDATE_ID", _ID);
                            json.put(DishesSQLiteController.columns[1], name.getText());
                            json.put(DishesSQLiteController.columns[2], description.getText());
                            json.put(DishesSQLiteController.columns[3], price.getText());

                            if (imageFile.exists()) {
                                json.put(DishesSQLiteController.columns[4], imageFile.getName());
                                json.put("imageString", imageFile.getAbsolutePath());
                            }

                            WebBroadcastReceiver.scheduleJob(getApplicationContext(),
                                    WebService.ACTION_DISHES, WebService.METHOD_PUT,
                                    json.toString());
                            setResult(RESULT_OK, intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, e.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, R.string.empty_string,
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, R.string.no_internet,
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

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) try {
            imageFile = new File(Utilities.getPath(this, data.getData()));

            if (imageFile.exists()) {
                image.setImageBitmap(Utilities.getResizedBitmap(BitmapFactory
                        .decodeFile(imageFile.getAbsolutePath())));
            } else {
                image.setImageResource(R.drawable.rectangle_gray);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.get_image_error)+
                    ":\n"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
