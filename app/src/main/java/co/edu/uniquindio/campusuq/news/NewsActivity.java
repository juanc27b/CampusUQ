package co.edu.uniquindio.campusuq.news;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
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

import org.apache.commons.lang3.StringUtils;

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
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
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
            loadNews(intent.getIntExtra("INSERTED", 0));
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

        swipeRefreshLayout = findViewById(R.id.news_swipe_refresh);
        recyclerView = findViewById(R.id.news_recycler_view);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.haveNetworkConnection(NewsActivity.this)) {
                    oldNews = false;
                    WebBroadcastReceiver.startService(getApplicationContext(),
                            action, WebService.METHOD_GET, null);
                } else {
                    Toast.makeText(NewsActivity.this,
                            R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new NewsAdapter(new ArrayList<New>(), this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_SETTLING &&
                        !recyclerView.canScrollVertically(1)) {
                    oldNews = true;
                    loadNews(0);
                }
            }
        });

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
            if (recyclerView != null) {
                ArrayList<New> news = ((NewsAdapter) recyclerView.getAdapter()).getNews();

                for (New n : news) {
                    if (StringUtils.stripAccents(n.getName()).toLowerCase()
                            .contains(StringUtils.stripAccents(query.trim()).toLowerCase())) {
                        recyclerView.smoothScrollToPosition(news.indexOf(n));
                        return;
                    }
                }
            }
            Toast.makeText(this, getString(R.string.new_no_found) + ": " + query,
                    Toast.LENGTH_SHORT).show();
        } else {
            setIntent(intent);
            int category = intent.getIntExtra(Utilities.CATEGORY, R.string.app_name);
            action = category == R.string.news ? WebService.ACTION_NEWS : WebService.ACTION_EVENTS;
            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setTitle(category);
                loadNews(0);
            }
        }
    }

    private void loadNews(int inserted) {
        swipeRefreshLayout.setRefreshing(true);

        NewsAdapter newsAdapter = (NewsAdapter) recyclerView.getAdapter();
        int scrollTo = oldNews ? newsAdapter.getItemCount() - 1 : (inserted > 0 ? inserted - 1 : 0);
        newsAdapter.setNews(NewsPresenter.loadNews(action,
                this, newsAdapter.getItemCount() + (inserted > 0 ? inserted : 3)));
        recyclerView.getLayoutManager().scrollToPosition(scrollTo);

        if (newsAdapter.getItemCount() > 0) swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onNewClick(int index, String action) {
        New n = ((NewsAdapter) recyclerView.getAdapter()).getNews().get(index);

        switch (action) {
            case NewsAdapter.NOTICE:
            case NewsAdapter.MORE: {
                startActivity(new Intent(this, NewsContentActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.news_detail)
                        .putExtra("TITLE", n.getName())
                        .putExtra("DATE", n.getDate())
                        .putExtra("AUTHOR", n.getAuthor())
                        .putExtra(Utilities.LINK, n.getLink())
                        .putExtra("CONTENT", n.getContent()));
                break;
            }
            case NewsAdapter.IMAGE:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW)
                            .setDataAndType(FileProvider.getUriForFile(this,
                                    "co.edu.uniquindio.campusuq.provider",
                                    new File(n.getImage())), "image/*")
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
                                .setContentTitle(n.getName())
                                .setContentUrl(Uri.parse(n.getLink()))
                                .setContentDescription(n.getSummary())
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
                                .image(Uri.fromFile(new File("" + n.getImage())))
                                .text(n.getName() + "\n\n" + n.getLink())
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
                sendIntent.putExtra(Intent.EXTRA_TEXT, n.getName() + "\n\n" + n.getLink());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        action = null;
        swipeRefreshLayout = null;
        recyclerView = null;
        facebookCallbackManager = null;
        shareDialog = null;
        mTwitterAuthClient = null;
        twitterCallback = null;
    }

}
