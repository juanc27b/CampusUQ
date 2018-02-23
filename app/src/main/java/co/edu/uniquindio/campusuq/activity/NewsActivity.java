package co.edu.uniquindio.campusuq.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import co.edu.uniquindio.campusuq.util.NewsAdapter;
import co.edu.uniquindio.campusuq.util.NewsSQLiteController;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;
import co.edu.uniquindio.campusuq.vo.New;
import co.edu.uniquindio.campusuq.vo.NewCategory;
import co.edu.uniquindio.campusuq.vo.NewRelation;

public class NewsActivity extends MainActivity implements NewsAdapter.OnClickNewListener {

    private RecyclerView mRecyclerView;
    private NewsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public ArrayList<New> news;
    public boolean oldActivity = false;

    private ProgressDialog pDialog;
    boolean oldNews = true;

    private String action;

    public CallbackManager callbackManager;
    public boolean loggedIn;

    public NewsActivity() {
        this.news = new ArrayList<New>();
        this.oldActivity = false;

        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent() {
        super.addContent();

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
        stub.setLayoutResource(R.layout.content_news);
        View inflated = stub.inflate();

        pDialog = new ProgressDialog(NewsActivity.this);
        pDialog.setTitle(getString(R.string.loading_content));
        pDialog.setMessage(getString(R.string.please_wait));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);

        String category = getIntent().getStringExtra("CATEGORY");
        action = getString(R.string.news).equals(category) ? WebService.ACTION_NEWS : WebService.ACTION_EVENTS;

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
            this.oldActivity = true;
        }
    }

    @Override
    public void onNewClick(int pos, String action) {
        switch(action) {
            case "notice":
            case "more":
                Intent intent = new Intent(NewsActivity.this, NewsContentActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.news_detail));
                intent.putExtra("TITLE", news.get(pos).getName());
                intent.putExtra("DATE", news.get(pos).getDate());
                intent.putExtra("AUTHOR", news.get(pos).getAuthor());
                intent.putExtra("LINK", news.get(pos).getLink());
                intent.putExtra("CONTENT", news.get(pos).getContent());
                NewsActivity.this.startActivity(intent);
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

    private void loadNews(int inserted) {

        int scrollTo = 0;

        NewsSQLiteController dbController = new NewsSQLiteController(getApplicationContext(), 1);
        String validRows = null;

        String events = "";
        ArrayList<NewCategory> categories = dbController.selectCategory(null,
                NewsSQLiteController.CAMPOS_CATEGORIA[1] + " = ?", new String[]{"Eventos"});
        ArrayList<NewRelation> relations;
        if (categories.size() > 0) {
            relations = dbController.selectRelation(null,
                    NewsSQLiteController.CAMPOS_RELACION[0] + " = ?", new String[]{categories.get(0).get_ID()});
            for (NewRelation relation : relations) {
                events += relation.getNew_ID() + ",";
            }
            events = events.substring(0, events.length() - 1);
            if (WebService.ACTION_NEWS.equals(action)) {
                validRows = NewsSQLiteController.CAMPOS_TABLA[0]+" NOT IN ("+events+")";
            } else {
                validRows = NewsSQLiteController.CAMPOS_TABLA[0]+" IN ("+events+")";
            }
        }

        int limit = inserted > 0 ? news.size()+inserted : news.size()+3;
        if (!oldNews) {
            scrollTo = (inserted != 0) ? inserted - 1 : 0;
        } else {
            scrollTo = oldActivity ? news.size()-1 : 0;
        }
        news = dbController.select(""+limit,
                validRows, null);

        dbController.destroy();

        if (!oldActivity) {
            mRecyclerView = (RecyclerView) findViewById(R.id.news_recycler_view);
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
                            if (haveNetworkConnection(NewsActivity.this)) {
                                oldNews = false;
                                pDialog.show();
                                WebBroadcastReceiver.scheduleJob(getApplicationContext(), WebService.ACTION_NEWS);
                            } else {
                                Toast.makeText(NewsActivity.this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
                            }
                        } else if (!mRecyclerView.canScrollVertically(1)) {
                            oldNews = true;
                            pDialog.show();
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

        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }

    }

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
        IntentFilter filter = new IntentFilter(WebService.ACTION_NEWS);
        registerReceiver(newsReceiver, filter);

        pDialog.show();
        loadNews(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        unregisterReceiver(newsReceiver);
    }

}
