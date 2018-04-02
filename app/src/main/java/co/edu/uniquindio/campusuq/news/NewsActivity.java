package co.edu.uniquindio.campusuq.news;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
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

    private RecyclerView mRecyclerView;
    private NewsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public ArrayList<New> news;
    public NewsPresenter newsPresenter;
    public boolean oldActivity = false;
    boolean oldNews = true;
    private String action;

    private String socialNetwork = NewsAdapter.UNDEFINED;

    public CallbackManager facebookCallbackManager;
    public boolean facebookLoggedIn;
    public ShareDialog shareDialog;

    public TwitterAuthClient mTwitterAuthClient;
    public Callback<TwitterSession> twitterCallback;
    public boolean twitterLoggedIn;

    public NewsActivity() {
        this.news = new ArrayList<>();
        newsPresenter = new NewsPresenter();
        this.oldActivity = false;

        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);

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

        String category = getIntent().getStringExtra("CATEGORY");
        action = getString(R.string.news).equals(category) ? WebService.ACTION_NEWS : WebService.ACTION_EVENTS;
        loadNews(0);

    }

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            boolean found = false;
            for (New mNew : news) {
                if (query.trim().toLowerCase().equals(mNew.getName().toLowerCase()) ||
                        mNew.getName().toLowerCase().contains(query.trim().toLowerCase())) {
                    mLayoutManager.scrollToPosition(news.indexOf(mNew));
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
            action = getString(R.string.news).equals(category) ? WebService.ACTION_NEWS : WebService.ACTION_EVENTS;
            loadNews(0);
        }
    }

    @Override
    public void onNewClick(int pos, String action) {
        switch(action) {
            case NewsAdapter.NOTICE:
            case NewsAdapter.MORE:
                Intent intent = new Intent(NewsActivity.this, NewsContentActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.news_detail));
                intent.putExtra("TITLE", news.get(pos).getName());
                intent.putExtra("DATE", news.get(pos).getDate());
                intent.putExtra("AUTHOR", news.get(pos).getAuthor());
                intent.putExtra("LINK", news.get(pos).getLink());
                intent.putExtra("CONTENT", news.get(pos).getContent());
                NewsActivity.this.startActivity(intent);
                break;
            case NewsAdapter.FACEBOOK:
                if (Utilities.haveNetworkConnection(NewsActivity.this)) {
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
                                .setContentTitle(news.get(pos).getName())
                                .setContentUrl(Uri.parse(news.get(pos).getLink()))
                                .setContentDescription(news.get(pos).getSummary())
                                .build();
                        shareDialog.show(content);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
                break;
            case NewsAdapter.TWITTER:
                if (Utilities.haveNetworkConnection(NewsActivity.this)) {
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
                        final Intent twitterIntent = new ComposerActivity.Builder(NewsActivity.this)
                                .session(session)
                                .image(Uri.fromFile(new File(news.get(pos).getImage())))
                                .text(news.get(pos).getName())
                                .hashtags("#Uniquindio")
                                .createIntent();
                        startActivity(twitterIntent);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
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
                sendIntent.putExtra(Intent.EXTRA_TEXT, news.get(pos).getName());
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.whatsapp");
                try {
                    startActivity(sendIntent);
                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(this, "No se ha instalado Whatsapp", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(this, "Undefined: "+pos, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Operación correcta", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error en la operación", Toast.LENGTH_SHORT).show();
        }
        socialNetwork = NewsAdapter.UNDEFINED;
    }

    private void loadNews(int inserted) {

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        int scrollTo;
        int limit = inserted > 0 ? news.size()+inserted : news.size()+3;
        if (!oldNews) {
            scrollTo = (inserted != 0) ? inserted - 1 : 0;
        } else {
            scrollTo = oldActivity ? news.size()-1 : 0;
        }

        news = newsPresenter.loadNews(action, NewsActivity.this, limit);

        if (!oldActivity) {
            mRecyclerView = findViewById(R.id.news_recycler_view);
            mRecyclerView.setHasFixedSize(true);

            mLayoutManager = new LinearLayoutManager(NewsActivity.this, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new NewsAdapter(news, NewsActivity.this);
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
                            if (Utilities.haveNetworkConnection(NewsActivity.this)) {
                                oldNews = false;
                                progressDialog.show();
                                WebBroadcastReceiver.scheduleJob(getApplicationContext(),
                                        action, WebService.METHOD_GET, null);
                            } else {
                                Toast.makeText(NewsActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                            }
                        } else if (!mRecyclerView.canScrollVertically(1)) {
                            oldNews = true;
                            loadNews(0);
                        }
                    }
                }
            });

            oldActivity = true;
        } else {
            mAdapter.setNews(news);
            mLayoutManager.scrollToPosition(scrollTo);
        }

        if (progressDialog.isShowing() && news.size() > 0) {
            progressDialog.dismiss();
        }

    }

    private IntentFilter newsFilter = new IntentFilter(WebService.ACTION_NEWS);
    // Define the callback for what to do when data is received
    private BroadcastReceiver newsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int inserted = intent.getIntExtra("INSERTED", 0);
            loadNews(inserted);
        }
    };

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
