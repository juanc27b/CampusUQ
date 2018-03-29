package co.edu.uniquindio.campusuq.announcements;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;

public class AnnouncementsDetailActivity extends MainActivity implements View.OnClickListener {

    private String type;
    private Integer[] link_IDs;
    private File[] imageFiles;
    private Intent intent;
    private String _ID;
    private TextView titleText;
    private TextView nameText;
    private EditText name;
    private TextView descriptionText;
    private EditText description;
    private TextView descriptionCount;
    private TextView imageText;
    private ImageView[] images;

    public AnnouncementsDetailActivity() {
        super.setHasNavigationDrawerIcon(false);
        super.setHasSearch(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

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
        images = new ImageView[]{
                findViewById(R.id.announcement_detail_image_0),
                findViewById(R.id.announcement_detail_image_1),
                findViewById(R.id.announcement_detail_image_2),
                findViewById(R.id.announcement_detail_image_3),
                findViewById(R.id.announcement_detail_image_4),
                findViewById(R.id.announcement_detail_image_5),
                findViewById(R.id.announcement_detail_image_6),
                findViewById(R.id.announcement_detail_image_7),
                findViewById(R.id.announcement_detail_image_8),
                findViewById(R.id.announcement_detail_image_9),
        };
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
        for (ImageView image : images) image.setOnClickListener(this);
        findViewById(R.id.announcement_detail_ok).setOnClickListener(this);
    }

    @Override
    public void handleIntent(Intent intent) {
        String category = intent.getStringExtra("CATEGORY");
        type = getString(R.string.report_incident).equals(category) ? "I" : "C";
        link_IDs = new Integer[10];
        imageFiles = new File[10];
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(category);
            this.intent = intent;
            setAnnouncement();
        }
    }

    public void setAnnouncement() {
        _ID = intent.getStringExtra(AnnouncementsSQLiteController.columns[0]);

        if (type.equals("I")) {
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

        name.setText(intent.getStringExtra(AnnouncementsSQLiteController.columns[3]));
        description.setText(intent.getStringExtra(AnnouncementsSQLiteController.columns[5]));
        descriptionCount.setText(String.valueOf(description.getText().length()));

        ArrayList<AnnouncementLink> announcementLinks = _ID != null ? AnnouncementsPresenter
                .getAnnouncementsLinks(this, _ID) : new ArrayList<AnnouncementLink>();

        for (int i = 0; i < images.length; i++) {
            if (i < announcementLinks.size()) {
                AnnouncementLink announcementLink = announcementLinks.get(i);

                link_IDs[i] = announcementLink.get_ID();

                imageFiles[i] = new File(announcementLink.getLink());
                if (imageFiles[i].exists()) images[i].setImageBitmap(BitmapFactory
                        .decodeFile(imageFiles[i].getAbsolutePath()));
                else images[i].setImageResource(R.drawable.rectangle_gray);
                images[i].setVisibility(View.VISIBLE);
            } else {
                images[i].setImageResource(R.drawable.rectangle_gray);
                images[i].setVisibility(i != announcementLinks.size() ? View.GONE :
                        View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.announcement_detail_image_0: getImage(0); break;
            case R.id.announcement_detail_image_1: getImage(1); break;
            case R.id.announcement_detail_image_2: getImage(2); break;
            case R.id.announcement_detail_image_3: getImage(3); break;
            case R.id.announcement_detail_image_4: getImage(4); break;
            case R.id.announcement_detail_image_5: getImage(5); break;
            case R.id.announcement_detail_image_6: getImage(6); break;
            case R.id.announcement_detail_image_7: getImage(7); break;
            case R.id.announcement_detail_image_8: getImage(8); break;
            case R.id.announcement_detail_image_9: getImage(9); break;
            case R.id.announcement_detail_ok:
                if (Utilities.haveNetworkConnection(AnnouncementsDetailActivity.this)) {
                    if (name.getText().length() != 0 && description.getText().length() != 0) {
                        JSONObject json = new JSONObject();
                        try {
                            if (_ID != null) json.put("UPDATE_ID", _ID);
                            json.put(AnnouncementsSQLiteController.columns[1], intent.getIntExtra(
                                    AnnouncementsSQLiteController.columns[1],
                                    0));
                            json.put(AnnouncementsSQLiteController.columns[2], type);
                            json.put(AnnouncementsSQLiteController.columns[3], name.getText());
                            // La fecha se pone en el servidor, no aqui
                            json.put(AnnouncementsSQLiteController.columns[5],
                                    description.getText());
                            JSONArray links = new JSONArray();
                            for (int i = 0; i < imageFiles.length; i++)
                                if (imageFiles[i] != null && imageFiles[i].exists()) {
                                JSONObject link = new JSONObject();
                                if (link_IDs[i] != null) json.put("UPDATE_ID", link_IDs[i]);
                                link.put(AnnouncementsSQLiteController.linkColumns[2], "I");
                                link.put(AnnouncementsSQLiteController.linkColumns[3],
                                        imageFiles[i].getName());
                                byte[] imageBytes = new byte[(int) imageFiles[i].length()];
                                BufferedInputStream bufferedInputStream =
                                        new BufferedInputStream(new FileInputStream(imageFiles[i]));
                                bufferedInputStream.read(imageBytes);
                                bufferedInputStream.close();
                                link.put("imageString",
                                        Base64.encodeToString(imageBytes, Base64.NO_WRAP));
                                links.put(link);
                            }
                            json.put("links", links);
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                        WebBroadcastReceiver.scheduleJob(getApplicationContext(), type.equals("I") ?
                                        WebService.ACTION_INCIDENTS : WebService.ACTION_COMMUNIQUES,
                                WebService.METHOD_POST, json.toString());
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

    private void getImage(int requestCode) {
        startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*"), "Select Picture"), requestCode);
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
                        imageFiles[requestCode] = new File(cursor.getString(cursor
                                .getColumnIndex(projection[0])));
                        if (imageFiles[requestCode].exists()) {
                            images[requestCode].setImageBitmap(BitmapFactory
                                    .decodeFile(imageFiles[requestCode].getAbsolutePath()));
                            if (++requestCode < images.length)
                                images[requestCode].setVisibility(View.VISIBLE);
                        } else {
                            images[requestCode].setImageResource(R.drawable.rectangle_gray);
                        }
                    }
                    cursor.close();
                }
            }
        }
    }

}
