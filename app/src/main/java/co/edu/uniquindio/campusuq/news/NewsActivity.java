package co.edu.uniquindio.campusuq.news;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
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
import com.twitter.sdk.android.core.Twitter;

import java.util.ArrayList;
import java.util.Arrays;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;

public class NewsActivity extends MainActivity implements NewsAdapter.OnClickNewListener {

    private String action;
    public CallbackManager callbackManager;
    public boolean loggedIn;
    public ShareDialog shareDialog;
    public ArrayList<New> news = new ArrayList<>();
    public boolean newActivity = true;
    private NewsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    boolean oldNews = true;

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

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {}

            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException exception) {}
        });
        loggedIn = AccessToken.getCurrentAccessToken() == null;
        shareDialog = new ShareDialog(this);

        Twitter.initialize(this);

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
            Toast.makeText(this, "No se ha encontrado la noticia: "+query,
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
    public void onNewClick(int pos, String action) {
        switch (action) {
            case "notice":
            case "more":
                Intent intent = new Intent(this, NewsContentActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.news_detail));
                intent.putExtra("TITLE", news.get(pos).getName());
                intent.putExtra("DATE", news.get(pos).getDate());
                intent.putExtra("AUTHOR", news.get(pos).getAuthor());
                intent.putExtra("LINK", news.get(pos).getLink());
                intent.putExtra("CONTENT", news.get(pos).getContent());
                startActivity(intent);
                break;
            case "facebook":
                if (!loggedIn) {
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
        if (resultCode == RESULT_OK)
            Toast.makeText(this, R.string.social_ok, Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, R.string.socia_error, Toast.LENGTH_SHORT).show();
    }

    private void loadNews(int inserted) {

        if (!progressDialog.isShowing()) progressDialog.show();

        int scrollTo = oldNews ?
                (newActivity ? 0 : news.size()-1) : (inserted > 0 ? inserted-1 : 0);

        news = NewsPresenter.loadNews(action, this,
                inserted > 0 ? news.size()+inserted : news.size()+3);

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
                                Toast.makeText(NewsActivity.this, R.string.no_internet,
                                        Toast.LENGTH_SHORT).show();
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
        registerReceiver(newsReceiver, newsFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(newsReceiver);
    }

}
