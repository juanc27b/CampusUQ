package co.edu.uniquindio.campusuq.announcements;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;

public class AnnouncementsDetailActivity extends MainActivity implements View.OnClickListener {

    private static final int REQUEST_LINK_0 = 1010;
    private static final int REQUEST_LINK_1 = 1011;
    private static final int REQUEST_LINK_2 = 1012;
    private static final int REQUEST_LINK_3 = 1013;
    private static final int REQUEST_LINK_4 = 1014;
    private static final int REQUEST_LINK_5 = 1015;
    private static final int REQUEST_LINK_6 = 1016;
    private static final int REQUEST_LINK_7 = 1017;
    private static final int REQUEST_LINK_8 = 1018;
    private static final int REQUEST_LINK_9 = 1019;

    private String type;
    private String[] link_IDs;
    private String[] linkTypes;
    private File[] linksFiles;
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
        link_IDs = new String[10];
        linkTypes = new String[10];
        linksFiles = new File[10];
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
                linkTypes[i] = announcementLink.getType();
                linksFiles[i] = new File(announcementLink.getLink());

                if (linksFiles[i].exists()) {
                    if ("I".equals(linkTypes[i])) {
                        images[i].setImageBitmap(Utilities.getResizedBitmap(BitmapFactory
                                .decodeFile(linksFiles[i].getAbsolutePath())));
                    } else {
                        MediaMetadataRetriever mediaMetadataRetriever =
                                new MediaMetadataRetriever();
                        mediaMetadataRetriever.setDataSource(linksFiles[i].getAbsolutePath());
                        images[i].setImageBitmap(Utilities.getResizedBitmap(
                                mediaMetadataRetriever.getFrameAtTime(1000000)));
                    }
                } else {
                    images[i].setImageResource(R.drawable.rectangle_gray);
                }

                images[i].setVisibility(View.VISIBLE);
            } else {
                images[i].setImageResource(R.drawable.rectangle_gray);
                images[i].setVisibility(i != announcementLinks.size() ?
                        View.GONE : View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.announcement_detail_image_0: getImage(REQUEST_LINK_0); break;
            case R.id.announcement_detail_image_1: getImage(REQUEST_LINK_1); break;
            case R.id.announcement_detail_image_2: getImage(REQUEST_LINK_2); break;
            case R.id.announcement_detail_image_3: getImage(REQUEST_LINK_3); break;
            case R.id.announcement_detail_image_4: getImage(REQUEST_LINK_4); break;
            case R.id.announcement_detail_image_5: getImage(REQUEST_LINK_5); break;
            case R.id.announcement_detail_image_6: getImage(REQUEST_LINK_6); break;
            case R.id.announcement_detail_image_7: getImage(REQUEST_LINK_7); break;
            case R.id.announcement_detail_image_8: getImage(REQUEST_LINK_8); break;
            case R.id.announcement_detail_image_9: getImage(REQUEST_LINK_9); break;
            case R.id.announcement_detail_ok:
                if (Utilities.haveNetworkConnection(this)) {
                    if (name.getText().length() != 0 && description.getText().length() != 0) {
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory(getString(R.string.analytics_announcements_category))
                                .setAction(getString(_ID == null ?
                                        R.string.analytics_create_action :
                                        R.string.analytics_modify_action))
                                .setLabel(getString(type.equals("I") ?
                                        R.string.analytics_security_system_label :
                                        R.string.analytics_billboard_information_label))
                                .setValue(1)
                                .build());

                        try {
                            JSONObject json = new JSONObject();
                            if (_ID != null) json.put("UPDATE_ID", _ID);
                            json.put(AnnouncementsSQLiteController.columns[1], intent
                                    .getStringExtra(AnnouncementsSQLiteController.columns[1]));
                            json.put(AnnouncementsSQLiteController.columns[2], type);
                            json.put(AnnouncementsSQLiteController.columns[3], name.getText());
                            // La fecha se pone en el servidor, no aqui
                            json.put(AnnouncementsSQLiteController.columns[5],
                                    description.getText());
                            JSONArray links = new JSONArray();

                            for (int i = 0; i < linksFiles.length; i++) {
                                if (linksFiles[i] != null && linksFiles[i].exists()) {
                                    JSONObject link = new JSONObject();

                                    if (link_IDs[i] != null) {
                                        link.put("UPDATE_ID", link_IDs[i]);
                                    }

                                    link.put(AnnouncementsSQLiteController.linkColumns[2],
                                            linkTypes[i]);
                                    link.put(AnnouncementsSQLiteController.linkColumns[3],
                                            linksFiles[i].getName());
                                    link.put("imageString", linksFiles[i].getAbsolutePath());
                                    links.put(link);
                                }
                            }

                            json.put("links", links);
                            WebBroadcastReceiver.scheduleJob(getApplicationContext(),
                                    type.equals("I") ? WebService.ACTION_INCIDENTS :
                                            WebService.ACTION_COMMUNIQUES,
                                    WebService.METHOD_POST, json.toString());
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

    private void getImage(int requestCode) {
        startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI).setType("image/* video/*")
                .putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"}),
                getString(R.string.select_image_or_video)), requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode >= REQUEST_LINK_0 && requestCode <= REQUEST_LINK_9 &&
                resultCode == RESULT_OK) {
            Uri uri = data.getData();

            if (uri != null) try {
                int index = requestCode - REQUEST_LINK_0;
                linksFiles[index] = new File(Utilities.getPath(this, uri));

                if (linksFiles[index].exists()) {
                    if (uri.toString().contains("image")) {
                        linkTypes[index] = "I";
                        images[index].setImageBitmap(Utilities.getResizedBitmap(BitmapFactory
                                .decodeFile(linksFiles[index].getAbsolutePath())));
                    } else {
                        linkTypes[index] = "V";
                        MediaMetadataRetriever mediaMetadataRetriever =
                                new MediaMetadataRetriever();
                        mediaMetadataRetriever.setDataSource(linksFiles[index].getAbsolutePath());
                        images[index].setImageBitmap(Utilities.getResizedBitmap(
                                mediaMetadataRetriever.getFrameAtTime(1000000)));
                    }

                    if (++index < images.length) images[index].setVisibility(View.VISIBLE);
                } else {
                    images[index].setImageResource(R.drawable.rectangle_gray);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.get_image_error) +
                        ":\n" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
