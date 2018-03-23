package co.edu.uniquindio.campusuq.activity;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.AnnouncementsAdapter;
import co.edu.uniquindio.campusuq.util.AnnouncementsPresenter;
import co.edu.uniquindio.campusuq.util.AnnouncementsSQLiteController;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;
import co.edu.uniquindio.campusuq.vo.Announcement;

public class AnnouncementsActivity extends MainActivity implements AnnouncementsAdapter.OnClickAnnouncementListener, View.OnClickListener {

    public static final int REQUEST_ANNOUNCEMENT_DETAIL = 1005;

    private String action;
    private Button report;
    private FloatingActionButton fab;
    private CallbackManager callbackManager;
    private ArrayList<Announcement> announcements = new ArrayList<>();
    private AnnouncementsPresenter announcementsPresenter = new AnnouncementsPresenter();
    private boolean newActivity = true;
    private AnnouncementsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private boolean oldAnnouncements = true;

    private IntentFilter announcementsFilter = new IntentFilter(WebService.ACTION_INCIDENTS);
    private BroadcastReceiver announcementsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadAnnouncements(intent.getIntExtra("INSERTED", 0));
        }
    };

    public AnnouncementsActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            for (Announcement announcement : announcements) if (announcement.getName().toLowerCase().contains(query.trim().toLowerCase())) {
                layoutManager.scrollToPosition(announcements.indexOf(announcement));
                return;
            }
            Toast.makeText(this, "No se ha encontrado el anuncio: "+query, Toast.LENGTH_SHORT).show();
        } else {
            String category = intent.getStringExtra("CATEGORY");
            action = getString(R.string.security_system).equals(category) ? WebService.ACTION_INCIDENTS : WebService.ACTION_COMMUNIQUES;
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(category);
                changeConfiguration();
                loadAnnouncements(0);
            }
        }
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_announcements);
        viewStub.inflate();
        report = findViewById(R.id.report_incident);
        fab = findViewById(R.id.fab);

        report.setOnClickListener(this);
        fab.setOnClickListener(this);
        changeConfiguration();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });

        loadAnnouncements(0);

    }

    public void changeConfiguration() {
        if (WebService.ACTION_INCIDENTS.equals(action)) {
            fab.setVisibility(View.GONE);
            report.setVisibility(View.VISIBLE);
        } else {
            report.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
        }
    }

    private void loadAnnouncements(int inserted) {

        if (!progressDialog.isShowing()) progressDialog.show();

        int scrollTo = oldAnnouncements ? (newActivity ? 0 : announcements.size()-1) : (inserted != 0 ? inserted-1 : 0);

        announcements = announcementsPresenter.loadAnnouncements(action, this, inserted > 0 ? announcements.size()+inserted : announcements.size()+3);

        if (newActivity) {
            newActivity = false;
            adapter = new AnnouncementsAdapter(announcements, this);
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

            RecyclerView recyclerView = findViewById(R.id.announcements_recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        if (!recyclerView.canScrollVertically(-1)) {
                            if (Utilities.haveNetworkConnection(AnnouncementsActivity.this)) {
                                oldAnnouncements = false;
                                progressDialog.show();
                                WebBroadcastReceiver.scheduleJob(getApplicationContext(),
                                        action, WebService.METHOD_GET, null);
                            } else {
                                Toast.makeText(AnnouncementsActivity.this,
                                        getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                            }
                        } else if (!recyclerView.canScrollVertically(1)) {
                            oldAnnouncements = true;
                            loadAnnouncements(0);
                        }
                    }
                }
            });
        } else {
            adapter.setAnnouncements(announcements);
            layoutManager.scrollToPosition(scrollTo);
        }

        if (progressDialog.isShowing() && announcements.size() > 0) progressDialog.dismiss();

    }

    @Override
    public void onAnnouncementClick(int index, String action) {
        switch (action) {
            case "notice":
                AnnouncementsFragment.newInstance(index).show(getSupportFragmentManager(), null);
                break;
            case "read": {
                AnnouncementsSQLiteController dbController = new AnnouncementsSQLiteController(getApplicationContext(), 1);
                dbController.readed(announcements.get(index).get_ID());
                dbController.destroy();
                loadAnnouncements(0);
                break;
            }
            case "facebook":
                Toast.makeText(this, "Facebook clicked: "+index, Toast.LENGTH_SHORT).show();
                //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
                break;
            case "twitter":
                Toast.makeText(this, "Twitter clicked: "+index, Toast.LENGTH_SHORT).show();
                break;
            case "whatsapp":
                Toast.makeText(this, "Whatsapp clicked: "+index, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Undefined: "+index, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public Announcement getAnnouncement(int index) {
        return announcements.get(index);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.report_incident: {
                Intent intent = new Intent(this, AnnouncementsDetailActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.report_incident));
                startActivityForResult(intent, REQUEST_ANNOUNCEMENT_DETAIL);
                break;
            }
            case R.id.fab: {
                Intent intent = new Intent(this, AnnouncementsDetailActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.billboard_detail));
                startActivityForResult(intent, REQUEST_ANNOUNCEMENT_DETAIL);
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ANNOUNCEMENT_DETAIL:
                if (resultCode  == RESULT_OK && !progressDialog.isShowing()) progressDialog.show();
                break;
            default:
                callbackManager.onActivityResult(requestCode, resultCode, data);
                if (resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    String fbData = bundle.toString();
                    Toast.makeText(this, "Fb OK: "+fbData, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Fb Error: "+data.getExtras().toString(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(announcementsReceiver, announcementsFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(announcementsReceiver);
    }

}
