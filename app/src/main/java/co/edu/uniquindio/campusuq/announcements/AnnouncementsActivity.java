package co.edu.uniquindio.campusuq.announcements;

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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;

import java.util.ArrayList;
import java.util.Arrays;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.users.LoginActivity;
import co.edu.uniquindio.campusuq.users.User;
import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;

public class AnnouncementsActivity extends MainActivity implements
        AnnouncementsAdapter.OnClickAnnouncementListener, View.OnClickListener {

    public static final int REQUEST_ANNOUNCEMENT_DETAIL = 1005;

    private String action;
    private Button report;
    private FloatingActionButton fab;
    private ArrayList<Announcement> announcements = new ArrayList<>();
    private boolean newActivity = true;
    private AnnouncementsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private boolean oldAnnouncements = true;

    private String socialNetwork = AnnouncementsAdapter.UNDEFINED;

    private CallbackManager facebookCallbackManager;
    public boolean facebookLoggedIn;
    public ShareDialog shareDialog;

    public TwitterAuthClient mTwitterAuthClient;
    public Callback<TwitterSession> twitterCallback;
    public boolean twitterLoggedIn;

    private IntentFilter announcementsFilter = new IntentFilter(WebService.ACTION_INCIDENTS);
    private BroadcastReceiver announcementsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadAnnouncements(intent.getIntExtra("INSERTED", 0));
            String response = intent.getStringExtra("RESPONSE");
            if (response != null) Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
        }
    };

    public AnnouncementsActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

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
        facebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(facebookCallbackManager,
                new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                facebookLoggedIn = true;

            }

            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException exception) {
                exception.printStackTrace();
            }
        });
        facebookLoggedIn = AccessToken.getCurrentAccessToken() == null;
        shareDialog = new ShareDialog(this);

        Twitter.initialize(this);
        mTwitterAuthClient = new TwitterAuthClient();
        twitterCallback = new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                twitterLoggedIn = true;
            }
            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                exception.printStackTrace();
            }
        };
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        TwitterAuthToken authToken = session != null ? session.getAuthToken() : null;
        twitterLoggedIn = authToken != null;

        loadAnnouncements(0);
    }

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            for (Announcement announcement : announcements)
                if (announcement.getName().toLowerCase().contains(query.trim().toLowerCase())) {
                layoutManager.scrollToPosition(announcements.indexOf(announcement));
                return;
            }
            Toast.makeText(this, "No se ha encontrado el anuncio: "+query,
                    Toast.LENGTH_SHORT).show();
        } else {
            String category = intent.getStringExtra("CATEGORY");
            action = getString(R.string.security_system).equals(category) ?
                    WebService.ACTION_INCIDENTS : WebService.ACTION_COMMUNIQUES;
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(category);
                changeConfiguration();
                loadAnnouncements(0);
            }
        }
    }

    public void changeConfiguration() {
        if (WebService.ACTION_INCIDENTS.equals(action)) {
            fab.setVisibility(View.GONE);
            report.setVisibility(View.VISIBLE);
        } else {
            report.setVisibility(View.GONE);
            User user = UsersPresenter.loadUser(this);
            fab.setVisibility(user != null && user.getAdministrator().equals("S") ?
                    View.VISIBLE : View.GONE);
        }
    }

    private void loadAnnouncements(int inserted) {

        if (!progressDialog.isShowing()) progressDialog.show();

        int scrollTo = oldAnnouncements ? (newActivity ? 0 : announcements.size()-1) :
                (inserted != 0 ? inserted-1 : 0);

        announcements = AnnouncementsPresenter.loadAnnouncements(action, this,
                inserted > 0 ? announcements.size()+inserted : announcements.size()+3);

        ArrayList<String> announcement_IDs = new ArrayList<>();
        for (Announcement announcement : announcements)
            announcement_IDs.add(String.valueOf(announcement.get_ID()));

        ArrayList<AnnouncementLink> announcementsLinks = AnnouncementsPresenter
                .getAnnouncementsLinks(this,
                        announcement_IDs.toArray(new String[announcement_IDs.size()]));

        if (newActivity) {
            newActivity = false;
            adapter = new AnnouncementsAdapter(announcements, announcementsLinks,
                    this);
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                    false);

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
                            if (Utilities
                                    .haveNetworkConnection(AnnouncementsActivity.this)) {
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
            adapter.setAnnouncements(announcements, announcementsLinks);
            layoutManager.scrollToPosition(scrollTo);
        }

        if (progressDialog.isShowing() && announcements.size() > 0) progressDialog.dismiss();

    }

    @Override
    public void onAnnouncementClick(int index, String action) {
        switch (action) {
            case AnnouncementsAdapter.ANNOUNCEMENT:
                User user = UsersPresenter.loadUser(this);
                if (user != null && user.getAdministrator().equals("S")) {
                    AnnouncementsFragment.newInstance(index, this.action)
                            .show(getSupportFragmentManager(), null);
                } else if (user != null && WebService.ACTION_INCIDENTS.equals(action)) {
                    if (user.get_ID() == announcements.get(index).getUser_ID()) {
                        AnnouncementsFragment.newInstance(index, this.action)
                                .show(getSupportFragmentManager(), null);
                    } else {
                        Toast.makeText(this, getString(R.string.no_propietary),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.no_administrator),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case AnnouncementsAdapter.READ: {
                AnnouncementsPresenter
                        .readed(getApplicationContext(), announcements.get(index).get_ID());
                loadAnnouncements(0);
                break;
            }
            case AnnouncementsAdapter.FACEBOOK:
                if (Utilities.haveNetworkConnection(AnnouncementsActivity.this)) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(getString(R.string.analytics_announcements_category))
                            .setAction(getString(R.string.analytics_share_action))
                            .setLabel(getString(WebService.ACTION_INCIDENTS.equals(this.action) ?
                                    R.string.analytics_security_system_label :
                                    R.string.analytics_billboard_information_label))
                            .setValue(1)
                            .build());
                    socialNetwork = AnnouncementsAdapter.FACEBOOK;
                    if (!facebookLoggedIn) {
                        LoginManager.getInstance().logInWithReadPermissions(this,
                                Arrays.asList("public_profile", "user_friends"));
                    } else if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent content = new ShareLinkContent.Builder()
                                .setContentTitle(announcements.get(index).getName())
                                .setContentDescription(announcements.get(index).getDescription())
                                .build();
                        shareDialog.show(content);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
                break;
            case AnnouncementsAdapter.TWITTER:
                if (Utilities.haveNetworkConnection(AnnouncementsActivity.this)) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(getString(R.string.analytics_announcements_category))
                            .setAction(getString(R.string.analytics_share_action))
                            .setLabel(getString(WebService.ACTION_INCIDENTS.equals(this.action) ?
                                    R.string.analytics_security_system_label :
                                    R.string.analytics_billboard_information_label))
                            .setValue(1)
                            .build());
                    if (!twitterLoggedIn) {
                        mTwitterAuthClient.authorize(this, twitterCallback);
                    } else {
                        final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                                .getActiveSession();
                        final Intent twitterIntent = new ComposerActivity.Builder(AnnouncementsActivity.this)
                                .session(session)
                                .text(announcements.get(index).getName())
                                .hashtags("#Uniquindio")
                                .createIntent();
                        startActivity(twitterIntent);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
                break;
            case AnnouncementsAdapter.WHATSAPP:
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.analytics_announcements_category))
                        .setAction(getString(R.string.analytics_share_action))
                        .setLabel(getString(WebService.ACTION_INCIDENTS.equals(this.action) ?
                                R.string.analytics_security_system_label :
                                R.string.analytics_billboard_information_label))
                        .setValue(1)
                        .build());
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, announcements.get(index).getName());
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.whatsapp");
                try {
                    startActivity(sendIntent);
                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(this, "No se ha instalado Whatsapp", Toast.LENGTH_SHORT).show();
                }
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
        int id = view.getId();
        switch (id) {
            case R.id.report_incident:
            case R.id.fab: {
                User user = UsersPresenter.loadUser(this);
                if (user != null && !user.getEmail().equals("campusuq@uniquindio.edu.co")) {
                    Intent intent =
                            new Intent(this, AnnouncementsDetailActivity.class);
                    intent.putExtra("CATEGORY", getString(id == R.id.report_incident ?
                            R.string.report_incident : R.string.billboard_detail));
                    intent.putExtra(AnnouncementsSQLiteController.columns[1], user.get_ID());
                    startActivityForResult(intent, REQUEST_ANNOUNCEMENT_DETAIL);
                } else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("CATEGORY", getString(R.string.log_in));
                    startActivity(intent);
                }
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
                switch (socialNetwork) {
                    case AnnouncementsAdapter.FACEBOOK:
                        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
                        break;
                    case AnnouncementsAdapter.TWITTER:
                        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
                        break;
                    default:
                        break;
                }
                if (resultCode == RESULT_OK){
                    Toast.makeText(this, "Operación correcta", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error en la operación", Toast.LENGTH_SHORT).show();
                }
                socialNetwork = AnnouncementsAdapter.UNDEFINED;
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
