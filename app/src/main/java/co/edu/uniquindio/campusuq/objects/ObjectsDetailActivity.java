package co.edu.uniquindio.campusuq.objects;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewStub;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;

public class ObjectsDetailActivity extends MainActivity implements View.OnClickListener {

    private static final int REQUEST_IMAGE = 1010;

    private Intent intent;
    private String _ID;
    private EditText name;
    private EditText place;
    private EditText dateLost;
    private EditText timeLost;
    private EditText description;
    private TextView descriptionCount;
    private ImageView image;
    private File imageFile;

    public ObjectsDetailActivity() {
        super.setHasNavigationDrawerIcon(false);
        super.setHasSearch(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.activity_objects_detail);
        viewStub.inflate();

        intent = getIntent();
        name = findViewById(R.id.object_detail_name);
        place = findViewById(R.id.object_detail_place);
        dateLost = findViewById(R.id.object_detail_date_lost);
        timeLost = findViewById(R.id.object_detail_time_lost);
        description = findViewById(R.id.object_detail_description);
        descriptionCount = findViewById(R.id.object_detail_description_count);
        image = findViewById(R.id.object_detail_image);
        setObject();

        dateLost.setOnClickListener(this);
        timeLost.setOnClickListener(this);
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
        findViewById(R.id.object_detail_ok).setOnClickListener(this);
    }

    @Override
    public void handleIntent(Intent intent) {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(intent.getStringExtra("CATEGORY"));
            this.intent = intent;
            setObject();
        }
    }

    private void setObject() {
        _ID = intent.getStringExtra(ObjectsSQLiteController.columns[0]);
        name.setText(intent.getStringExtra(ObjectsSQLiteController.columns[2]));
        place.setText(intent.getStringExtra(ObjectsSQLiteController.columns[3]));
        String dateTimeLost = intent.getStringExtra(ObjectsSQLiteController.columns[4]);

        if (dateTimeLost != null && dateTimeLost.length() >= 19) {
            dateLost.setText(dateTimeLost.substring(0, 10));
            timeLost.setText(dateTimeLost.substring(11, 19));
        }

        description.setText(intent.getStringExtra(ObjectsSQLiteController.columns[6]));
        descriptionCount.setText(String.valueOf(description.getText().length()));

        // Se concatena una cadena vacia para evitar el caso File(null)
        imageFile =
                new File(""+intent.getStringExtra(ObjectsSQLiteController.columns[7]));

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
            case R.id.object_detail_date_lost: {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        dateLost.setText(String.format(Locale.US, "%04d-%02d-%02d",
                                year, month+1, day));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            }
            case R.id.object_detail_time_lost: {
                Calendar calendar = Calendar.getInstance();
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        timeLost.setText(String.format(Locale.US, "%02d:%02d:00", hour,
                                minute));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                        false).show();
                break;
            }
            case R.id.object_detail_image:
                startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI).setType("image/*"),
                        getString(R.string.select_image)), REQUEST_IMAGE);
                break;
            case R.id.object_detail_ok:
                if (Utilities.haveNetworkConnection(this)) {
                    if (name.getText().length() != 0 && place.getText().length() != 0 &&
                            dateLost.getText().length() != 0 && timeLost.getText().length() != 0 &&
                            description.getText().length() != 0) {
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory(getString(R.string.analytics_objects_category))
                                .setAction(getString(_ID == null ?
                                        R.string.analytics_create_action :
                                        R.string.analytics_modify_action))
                                .setLabel(getString(R.string.analytics_lost_objects_label))
                                .setValue(1)
                                .build());

                        try {
                            JSONObject json = new JSONObject();
                            if (_ID != null) json.put("UPDATE_ID", _ID);
                            json.put(ObjectsSQLiteController.columns[1],
                                    intent.getStringExtra(ObjectsSQLiteController.columns[1]));
                            json.put(ObjectsSQLiteController.columns[2], name.getText());
                            json.put(ObjectsSQLiteController.columns[3], place.getText());
                            json.put(ObjectsSQLiteController.columns[4],
                                    dateLost.getText()+"T"+timeLost.getText()+"-0500");
                            json.put(ObjectsSQLiteController.columns[6], description.getText());

                            if (imageFile.exists()) {
                                json.put(ObjectsSQLiteController.columns[7], imageFile.getName());
                                json.put("imageString", imageFile.getAbsolutePath());
                            }

                            json.put(ObjectsSQLiteController.columns[8],
                                    intent.getStringExtra(ObjectsSQLiteController.columns[8]));
                            WebBroadcastReceiver.scheduleJob(getApplicationContext(),
                                    WebService.ACTION_OBJECTS, WebService.METHOD_POST,
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
