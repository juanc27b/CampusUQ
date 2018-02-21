package co.edu.uniquindio.campusuq.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import co.edu.uniquindio.campusuq.util.NewsServiceController;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.vo.New;
import co.edu.uniquindio.campusuq.vo.NewCategory;
import co.edu.uniquindio.campusuq.vo.NewRelation;

public class NewsActivity extends MainActivity implements NewsAdapter.OnClickNewListener {

    private RecyclerView mRecyclerView;
    private NewsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public ArrayList<New> news;
    public boolean oldActivity;

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

        addItems(getIntent().getStringExtra("CATEGORY"));

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
            this.oldActivity = true;
            addItems(category);
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

    public void addItems(String category) {

        LoadNewsAsync loadImageAsync = new LoadNewsAsync(this);
        loadImageAsync.execute(category, "new");

    }

    public class LoadNewsAsync extends AsyncTask<String, Void, Boolean> {

        private Context context;
        private ProgressDialog pDialog;
        private int scrollTo = 0;

        public LoadNewsAsync(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setTitle(context.getString(R.string.loading_content));
            pDialog.setMessage(context.getString(R.string.please_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args) {

            Boolean success = true;

            NewsSQLiteController dbController = new NewsSQLiteController(NewsActivity.this, 1);
            String validRows = null;
            String lastNewId = null;

            String events = "";
            ArrayList<NewCategory> categories = dbController.selectCategory(null,
                    NewsSQLiteController.CAMPOS_CATEGORIA[1] + " = ?", new String[]{"Eventos"});
            ArrayList<NewRelation> relations;
            if (categories.size() > 0) {
                relations = dbController.selectRelation(null,
                        NewsSQLiteController.CAMPOS_RELACION[0] + " = ?", new String[]{categories.get(0).getId()});
                for (NewRelation relation : relations) {
                    events += relation.getNewId() + ",";
                }
                events = events.substring(0, events.length() - 1);
                if (args[0].equals(getString(R.string.news))) {
                    validRows = NewsSQLiteController.CAMPOS_TABLA[0]+" NOT IN ("+events+")";
                    lastNewId = "/no_eventos";
                } else {
                    validRows = NewsSQLiteController.CAMPOS_TABLA[0]+" IN ("+events+")";
                    lastNewId = "/eventos";
                }

            }

            if (args[1].equals("new") && haveNetworkConnection(NewsActivity.this)) {
                int inserted = 0;
                categories = NewsServiceController.getNewCategories();
                for (NewCategory category: categories) {
                    ArrayList<NewCategory> oldCategories = dbController.selectCategory("1",
                            NewsSQLiteController.CAMPOS_CATEGORIA[0]+" = ?", new String[]{category.getId()});
                    if (oldCategories.size() == 0) {
                        dbController.insertCategory(category.getId(), category.getName(), category.getLink());
                    }
                }

                ArrayList<New> lastNews = dbController.select("1", validRows, null);
                if (lastNews.size() > 0) {
                    lastNewId += "/"+lastNews.get(0).getId();
                }
                ArrayList<New> updated = NewsServiceController.getNews(lastNewId);
                for (New mNew : updated) {
                    String imagePath = Utilities.saveImage(mNew.getImage(), context);
                    if (imagePath != null) {
                        mNew.setImage(imagePath);
                    } else {
                        success = false;
                    }
                    ArrayList<New> olds = dbController.select("1",
                            NewsSQLiteController.CAMPOS_TABLA[0]+" = ?", new String[]{mNew.getId()});
                    if (olds.size() > 0) {
                        dbController.update(mNew.getId(), mNew.getName(), mNew.getLink(), mNew.getImage(),
                                mNew.getSummary(), mNew.getContent(), mNew.getDate(), mNew.getAuthor());
                    } else {
                        dbController.insert(mNew.getId(), mNew.getName(), mNew.getLink(), mNew.getImage(),
                                mNew.getSummary(), mNew.getContent(), mNew.getDate(), mNew.getAuthor());
                        relations = NewsServiceController.getNewRelations(mNew.getId());
                        for (NewRelation relation : relations) {
                            dbController.insertRelation(relation.getCategoryId(), relation.getNewId());
                        }
                        inserted += 1;
                    }
                }
                int limit = news.size()+inserted;
                if (limit < 3) {
                    limit = 3;
                }
                scrollTo = (inserted != 0) ? inserted - 1 : 0;
                news = dbController.select(""+limit,
                        validRows, null);
            } else {
                scrollTo = oldActivity ? news.size()-1 : 0;
                news = dbController.select(""+(news.size()+3),
                        validRows, null);
            }

            return success;

        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            } else {
                if (pDialog != null) {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                }

                if (!oldActivity) {
                    mRecyclerView = (RecyclerView) findViewById(R.id.news_recycler_view);
                    mRecyclerView.setHasFixedSize(true);

                    mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
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
                                    if (haveNetworkConnection(context)) {
                                        LoadNewsAsync loadImageAsync = new LoadNewsAsync(context);
                                        loadImageAsync.execute(getIntent().getStringExtra("CATEGORY"), "new");
                                    } else {
                                        Toast.makeText(context, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
                                    }
                                } else if (!mRecyclerView.canScrollVertically(1)) {
                                    LoadNewsAsync loadImageAsync = new LoadNewsAsync(context);
                                    loadImageAsync.execute(getIntent().getStringExtra("CATEGORY"), "old");
                                }
                            }
                        }
                    });

                    oldActivity = true;
                } else {
                    mAdapter.setNews(news);
                    mLayoutManager.scrollToPosition(scrollTo);
                }

            }
        }

    }

}
