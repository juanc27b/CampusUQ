package co.edu.uniquindio.campusuq.activity;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import com.facebook.AccessToken;
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
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;
import co.edu.uniquindio.campusuq.vo.Announcement;

public class AnnouncementsActivity extends MainActivity implements AnnouncementsAdapter.OnClickAnnouncementListener {

    private RecyclerView mRecyclerView;
    private AnnouncementsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public ArrayList<Announcement> announcements;
    public AnnouncementsPresenter announcementsPresenter;
    public boolean oldActivity = false;
    boolean oldAnnouncements = true;
    private String action;

    public CallbackManager callbackManager;
    public boolean loggedIn;

    public AnnouncementsActivity() {
        this.announcements = new ArrayList<Announcement>();
        announcementsPresenter = new AnnouncementsPresenter();
        this.oldActivity = false;

        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }
            @Override
            public void onCancel() {
                // App code
            }
            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        loggedIn = AccessToken.getCurrentAccessToken() == null;

        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);

        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_announcements);
        View inflated = stub.inflate();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        String category = getIntent().getStringExtra("CATEGORY");
        if (getString(R.string.billboard_information).equals(category)) {

        } else {
        }

        action = getString(R.string.security_system).equals(category) ? WebService.ACTION_INCIDENTS : WebService.ACTION_COMMUNIQUES;
        loadAnnouncements(0);

    }

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            boolean found = false;
            for (Announcement announcement : announcements) {
                if (query.trim().toLowerCase().equals(announcement.getName().toLowerCase()) ||
                        announcement.getName().toLowerCase().contains(query.trim().toLowerCase())) {
                    mLayoutManager.scrollToPosition(announcements.indexOf(announcement));
                    found = true;
                    break;
                }
            }
            if (!found) {
                Toast.makeText(this, "No se ha encontrado la noticia: "+query, Toast.LENGTH_SHORT).show();
            }
        } else if (mAdapter != null) {
            String category = intent.getStringExtra("CATEGORY");
            getSupportActionBar().setTitle(category);
            action = getString(R.string.security_system).equals(category) ? WebService.ACTION_INCIDENTS : WebService.ACTION_COMMUNIQUES;
            loadAnnouncements(0);
        }
    }

    @Override
    public void onAnnouncementClick(int pos, String action) {
        switch(action) {
            case "notice":
            case "read":
                // marcar como leido
                break;
            case "facebook":
                Toast.makeText(this, "Facebook clicked: "+pos, Toast.LENGTH_SHORT).show();
                //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
                break;
            case "twitter":
                Toast.makeText(this, "Twitter clicked: "+pos, Toast.LENGTH_SHORT).show();
                break;
            case "whatsapp":
                Toast.makeText(this, "Whatsapp clicked: "+pos, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Undefined: "+pos, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            String fbData = bundle.toString();
            Toast.makeText(this, "Fb OK: "+fbData, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Fb Error: "+data.getExtras().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAnnouncements(int inserted) {

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        int scrollTo = 0;
        int limit = inserted > 0 ? announcements.size()+inserted : announcements.size()+3;
        if (!oldAnnouncements) {
            scrollTo = (inserted != 0) ? inserted - 1 : 0;
        } else {
            scrollTo = oldActivity ? announcements.size()-1 : 0;
        }

        announcements = announcementsPresenter.loadAnnouncements(action, AnnouncementsActivity.this, limit);

        if (!oldActivity) {
            mRecyclerView = (RecyclerView) findViewById(R.id.announcements_recycler_view);
            mRecyclerView.setHasFixedSize(true);

            mLayoutManager = new LinearLayoutManager(AnnouncementsActivity.this, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new AnnouncementsAdapter(announcements, AnnouncementsActivity.this);
            mRecyclerView.setAdapter(mAdapter);

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        if (!mRecyclerView.canScrollVertically(-1)) {
                            if (Utilities.haveNetworkConnection(AnnouncementsActivity.this)) {
                                oldAnnouncements = false;
                                progressDialog.show();
                                WebBroadcastReceiver.scheduleJob(getApplicationContext(),
                                        WebService.ACTION_INCIDENTS, WebService.METHOD_GET);
                            } else {
                                Toast.makeText(AnnouncementsActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                            }
                        } else if (!mRecyclerView.canScrollVertically(1)) {
                            oldAnnouncements = true;
                            loadAnnouncements(0);
                        }
                    }
                }
            });

            oldActivity = true;
        } else {
            mAdapter.setAnnouncements(announcements);
            mLayoutManager.scrollToPosition(scrollTo);
        }

        if (progressDialog.isShowing() && announcements.size() > 0) {
            progressDialog.dismiss();
        }

    }

    private IntentFilter announcementsFilter = new IntentFilter(WebService.ACTION_INCIDENTS);
    // Define the callback for what to do when data is received
    private BroadcastReceiver announcementsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int inserted = intent.getIntExtra("INSERTED", 0);
            loadAnnouncements(inserted);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        registerReceiver(announcementsReceiver, announcementsFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        unregisterReceiver(announcementsReceiver);
    }

}