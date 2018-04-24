package co.edu.uniquindio.campusuq.news;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;

public class NewsActivity extends MainActivity implements NewsAdapter.OnClickNewListener {

    private String action;
    public ArrayList<New> news = new ArrayList<>();
    public boolean newActivity = true;
    private NewsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    boolean oldNews = true;

    private String socialNetwork = NewsAdapter.UNDEFINED;

    public CallbackManager facebookCallbackManager;
    public boolean facebookLoggedIn;
    public ShareDialog shareDialog;

    public TwitterAuthClient mTwitterAuthClient;
    public Callback<TwitterSession> twitterCallback;
    public boolean twitterLoggedIn;

    private IntentFilter newsFilter = new IntentFilter(WebService.ACTION_NEWS);
    private BroadcastReceiver newsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int inserted = intent.getIntExtra("INSERTED", 0);
            loadNews(inserted);
        }
    };

    public NewsActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_news);
        stub.inflate();

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
        facebookLoggedIn = AccessToken.getCurrentAccessToken() != null;
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

        loadNews(0);
    }

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            for (New n : news) if (n.getName().toLowerCase().contains(query.trim().toLowerCase())) {
                layoutManager.scrollToPosition(news.indexOf(n));
                return;
            }
            Toast.makeText(this, getString(R.string.new_no_found) + ": " + query,
                    Toast.LENGTH_SHORT).show();
        } else {
            String category = intent.getStringExtra("CATEGORY");
            action = getString(R.string.news).equals(category) ?
                    WebService.ACTION_NEWS : WebService.ACTION_EVENTS;
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(category);
                loadNews(0);
            }
        }
    }

    @Override
    public void onNewClick(int index, String action) {
        switch (action) {
            case NewsAdapter.NOTICE:
            case NewsAdapter.MORE: {
                Intent intent = new Intent(this, NewsContentActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.news_detail));
                intent.putExtra("TITLE", news.get(index).getName());
                intent.putExtra("DATE", news.get(index).getDate());
                intent.putExtra("AUTHOR", news.get(index).getAuthor());
                intent.putExtra("LINK", news.get(index).getLink());
                intent.putExtra("CONTENT", news.get(index).getContent());
                startActivity(intent);
                break;
            }
            case NewsAdapter.IMAGE:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW)
                            .setDataAndType(FileProvider.getUriForFile(this,
                                    "co.edu.uniquindio.campusuq.provider",
                                    new File(news.get(index).getImage())), "image/*")
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case NewsAdapter.FACEBOOK:
                if (Utilities.haveNetworkConnection(this)) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(getString(R.string.analytics_news_category))
                            .setAction(getString(R.string.analytics_share_action))
                            .setLabel(getString(WebService.ACTION_NEWS.equals(this.action) ?
                                    R.string.analytics_news_label : R.string.analytics_events_label))
                            .setValue(1)
                            .build());
                    socialNetwork = NewsAdapter.FACEBOOK;
                    if (!facebookLoggedIn) {
                        LoginManager.getInstance().logInWithReadPermissions(this,
                                Arrays.asList("public_profile", "user_friends"));
                    } else if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent content = new ShareLinkContent.Builder()
                                .setContentTitle(news.get(index).getName())
                                .setContentUrl(Uri.parse(news.get(index).getLink()))
                                .setContentDescription(news.get(index).getSummary())
                                .build();
                        shareDialog.show(content);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.no_internet),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case NewsAdapter.TWITTER:
                if (Utilities.haveNetworkConnection(this)) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(getString(R.string.analytics_news_category))
                            .setAction(getString(R.string.analytics_share_action))
                            .setLabel(getString(WebService.ACTION_NEWS.equals(this.action) ?
                                    R.string.analytics_news_label : R.string.analytics_events_label))
                            .setValue(1)
                            .build());
                    socialNetwork = NewsAdapter.TWITTER;
                    if (!twitterLoggedIn) {
                        mTwitterAuthClient.authorize(this, twitterCallback);
                    } else {
                        final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                                .getActiveSession();
                        final Intent twitterIntent = new ComposerActivity.Builder(this)
                                .session(session)
                                .image(Uri.fromFile(new File("" +
                                        news.get(index).getImage())))
                                .text(news.get(index).getName())
                                .hashtags("#Uniquindio")
                                .createIntent();
                        startActivity(twitterIntent);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.no_internet),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case NewsAdapter.WHATSAPP:
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.analytics_news_category))
                        .setAction(getString(R.string.analytics_share_action))
                        .setLabel(getString(WebService.ACTION_NEWS.equals(this.action) ?
                                R.string.analytics_news_label : R.string.analytics_events_label))
                        .setValue(1)
                        .build());
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, news.get(index).getName());
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.whatsapp");
                try {
                    startActivity(sendIntent);
                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(this, R.string.no_whatsapp,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(this, getString(R.string.undefined) + ": " + index,
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (socialNetwork) {
            case NewsAdapter.FACEBOOK:
                facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
                break;
            case NewsAdapter.TWITTER:
                mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
                break;
            default:
                break;
        }
        if (resultCode == RESULT_OK){
            Toast.makeText(this, R.string.social_ok, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.social_error, Toast.LENGTH_SHORT).show();
        }
        socialNetwork = NewsAdapter.UNDEFINED;
    }

    private void loadNews(int inserted) {
        if (!progressDialog.isShowing()) progressDialog.show();

        int scrollTo = oldNews ?
                (newActivity ? 0 : news.size() - 1) : (inserted > 0 ? inserted - 1 : 0);

        news = NewsPresenter.loadNews(action, this,
                inserted > 0 ? news.size() + inserted : news.size() + 3);

        if (newActivity) {
            newActivity = false;
            adapter = new NewsAdapter(news, this);
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                    false);

            RecyclerView recyclerView = findViewById(R.id.news_recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        if (!recyclerView.canScrollVertically(-1)) {
                            if (Utilities.haveNetworkConnection(NewsActivity.this)) {
                                oldNews = false;
                                progressDialog.show();
                                WebBroadcastReceiver.scheduleJob(getApplicationContext(),
                                        action, WebService.METHOD_GET, null);
                            } else {
                                Toast.makeText(NewsActivity.this,
                                        R.string.no_internet, Toast.LENGTH_SHORT).show();
                            }
                        } else if (!recyclerView.canScrollVertically(1)) {
                            oldNews = true;
                            loadNews(0);
                        }
                    }
                }
            });
        } else {
            adapter.setNews(news);
            layoutManager.scrollToPosition(scrollTo);
        }

        if (progressDialog.isShowing() && !news.isEmpty()) progressDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        registerReceiver(newsReceiver, newsFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        unregisterReceiver(newsReceiver);
    }

}
