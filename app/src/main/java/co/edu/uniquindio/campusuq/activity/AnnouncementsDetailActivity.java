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
import co.edu.uniquindio.campusuq.util.AnnouncementsPresenter;
import co.edu.uniquindio.campusuq.util.AnnouncementsSQLiteController;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;
import co.edu.uniquindio.campusuq.vo.Announcement;

public class AnnouncementsDetailActivity extends MainActivity implements View.OnClickListener {

    private String action, announcement_ID;
    private Intent intent;
    private TextView titleText, nameText, descriptionText, descriptionCount, imageText;
    private EditText name, description;
    private File imageFile;
    private ImageView image;
    private AnnouncementsPresenter announcementsPresenter = new AnnouncementsPresenter();

    public AnnouncementsDetailActivity() {
        super.setHasNavigationDrawerIcon(false);
        super.setHasSearch(false);
    }

    @Override
    public void handleIntent(Intent intent) {
        String category = intent.getStringExtra("CATEGORY");
        action = getString(R.string.report_incident).equals(category) ? WebService.ACTION_INCIDENTS : WebService.ACTION_COMMUNIQUES;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(category);
            this.intent = intent;
            setAnnouncement();
        }
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.activity_announcements_detail);
        viewStub.inflate();

        intent = getIntent();
        titleText = findViewById(R.id.announcement_detail_title);
        nameText = findViewById(R.id.announcement_detail_name_text);
        name = findViewById(R.id.announcement_detail_name);
        descriptionText = findViewById(R.id.announcement_detail_description_text);
        description = findViewById(R.id.announcement_detail_description);
        descriptionCount = findViewById(R.id.announcement_detail_description_count);
        imageText = findViewById(R.id.announcement_detail_image_text);
        image = findViewById(R.id.announcement_detail_image);
        setAnnouncement();

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
        findViewById(R.id.announcement_detail_ok).setOnClickListener(this);
    }

    public void setAnnouncement() {
        if (WebService.ACTION_INCIDENTS.equals(action)) {
            titleText.setText(R.string.incident_detail_title);
            nameText.setText(R.string.incident_detail_name);
            name.setHint(R.string.incident_detail_name_hint);
            descriptionText.setText(R.string.incident_detail_description);
            description.setHint(R.string.incident_detail_description_hint);
            imageText.setText(R.string.incident_detail_image);
        } else {
            titleText.setText(R.string.communique_detail_title);
            nameText.setText(R.string.communique_detail_name);
            name.setHint(R.string.communique_detail_name_hint);
            descriptionText.setText(R.string.communique_detail_description);
            description.setHint(R.string.communique_detail_description_hint);
            imageText.setText(R.string.communique_detail_image);
        }
        announcement_ID = intent.getStringExtra("ANNOUNCEMENT");
        if (announcement_ID != null) {
            Announcement announcement = announcementsPresenter.getAnnouncementByID(announcement_ID, AnnouncementsDetailActivity.this);
            name.setText(announcement.getName());
            description.setText(announcement.getDescription());
            descriptionCount.setText(String.valueOf(descriptionText.length()));

            /*imageFile = new File(intent.getStringExtra(AnnouncementsSQLiteController.linkColumns[3]));
            if(imageFile.exists()) image.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
            else image.setImageResource(R.drawable.rectangle_gray);*/
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.announcement_detail_image:
                startActivityForResult(Intent.createChooser((new Intent(Intent.ACTION_GET_CONTENT)).setType("image/*"), "Select Picture"), 0);
                break;
            case R.id.announcement_detail_ok:
                if (Utilities.haveNetworkConnection(AnnouncementsDetailActivity.this)) {
                    if (name.getText().length() != 0 && description.getText().length() != 0) {
                        JSONObject json = new JSONObject();
                        try {
                            if (announcement_ID != null) json.put("UPDATE_ID", announcement_ID);
                            json.put(AnnouncementsSQLiteController.columns[1], WebService.ACTION_INCIDENTS.equals(action) ? "I" : "C");
                            json.put(AnnouncementsSQLiteController.columns[2], name.getText());
                            json.put(AnnouncementsSQLiteController.columns[3], "CurrentDate");
                            json.put(AnnouncementsSQLiteController.columns[4], description.getText());
                            if (imageFile.exists()) {
                                json.put(AnnouncementsSQLiteController.linkColumns[3], imageFile.getName());
                                byte[] imageBytes = new byte[(int) imageFile.length()];
                                BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(imageFile));
                                bufferedInputStream.read(imageBytes);
                                bufferedInputStream.close();
                                json.put("imageString", Base64.encodeToString(imageBytes, Base64.NO_WRAP));
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                        WebBroadcastReceiver.scheduleJob(getApplicationContext(), action, WebService.METHOD_POST, json.toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(AnnouncementsDetailActivity.this, getString(R.string.empty_string), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AnnouncementsDetailActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
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
                String[] projection = {MediaStore.Images.Media.DATA}, selectionArgs = {DocumentsContract.getDocumentId(uri).split(":")[1]};
                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Images.Media._ID+" = ?", selectionArgs, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        imageFile = new File(cursor.getString(cursor.getColumnIndex(projection[0])));
                        if (imageFile.exists()) image.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
                        else image.setImageResource(R.drawable.rectangle_gray);
                    }
                    cursor.close();
                }
            }
        }
    }

}
