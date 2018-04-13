package co.edu.uniquindio.campusuq.objects;

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
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.users.LoginActivity;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.users.UsersActivity;
import co.edu.uniquindio.campusuq.users.UsersPresenter;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;
import co.edu.uniquindio.campusuq.users.User;

public class ObjectsActivity extends MainActivity implements ObjectsAdapter.OnClickObjectListener,
        View.OnClickListener {

    private ArrayList<LostObject> objects = new ArrayList<>();
    private boolean newActivity = true;
    private ObjectsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private boolean oldObjects = true;

    private IntentFilter objectsFilter = new IntentFilter(WebService.ACTION_OBJECTS);
    private BroadcastReceiver objectsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadObjects(intent.getIntExtra("INSERTED", 0));
            String response = intent.getStringExtra("RESPONSE");
            if (response != null) {
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                Log.i(ObjectsActivity.class.getSimpleName(), response);
            }
        }
    };

    public ObjectsActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_objects);
        viewStub.inflate();

        findViewById(R.id.object_report).setOnClickListener(this);

        loadObjects(0);
    }

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            for (LostObject object : objects)
                if (object.getName().toLowerCase().contains(query.trim().toLowerCase())) {
                    layoutManager.scrollToPosition(objects.indexOf(object));
                    return;
                }
            Toast.makeText(this, getString(R.string.object_no_found)+query,
                    Toast.LENGTH_SHORT).show();
        } else {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(intent.getStringExtra("CATEGORY"));
                loadObjects(0);
            }
        }
    }

    private void loadObjects(int inserted) {
        if (!progressDialog.isShowing()) progressDialog.show();

        int scrollTo = oldObjects ? (newActivity ? 0 : objects.size()-1) :
                (inserted != 0 ? inserted-1 : 0);

        objects = ObjectsPresenter.loadObjects(this, UsersPresenter.loadUser(this),
                objects.size()+(inserted > 0 ? inserted : 3));

        if (newActivity) {
            newActivity = false;
            adapter = new ObjectsAdapter(objects, this);
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                    false);

            RecyclerView recyclerView = findViewById(R.id.objects_recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        if (!recyclerView.canScrollVertically(-1)) {
                            if (Utilities.haveNetworkConnection(ObjectsActivity.this)) {
                                oldObjects = false;
                                progressDialog.show();
                                WebBroadcastReceiver.scheduleJob(ObjectsActivity.this,
                                        WebService.ACTION_OBJECTS, WebService.METHOD_GET,
                                        null);
                            } else {
                                Toast.makeText(ObjectsActivity.this,
                                        R.string.no_internet, Toast.LENGTH_SHORT).show();
                            }
                        } else if (!recyclerView.canScrollVertically(1)) {
                            oldObjects = true;
                            loadObjects(0);
                        }
                    }
                }
            });
        } else {
            adapter.setObjects(objects);
            layoutManager.scrollToPosition(scrollTo);
        }

        if (progressDialog.isShowing() && objects.size() > 0) progressDialog.dismiss();
    }

    @Override
    public void onObjectClick(int index, String action) {
        User user = UsersPresenter.loadUser(this);
        LostObject object = objects.get(index);
        ObjectsSQLiteController dbController = new ObjectsSQLiteController(this, 1);

        switch (action) {
            case ObjectsAdapter.OBJECT:
                if (user != null && !user.getEmail().equals("campusuq@uniquindio.edu.co") &&
                        (user.getAdministrator().equals("S") ||
                                object.getUserLost_ID().equals(user.get_ID()))) {
                    ObjectsFragment.newInstance(index).show(getSupportFragmentManager(), null);
                } else {
                    Toast.makeText(this,
                            R.string.no_propietary,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case ObjectsAdapter.IMAGE:
                startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(
                        new File("" + objects.get(index).getImage())), "image/*"));
                break;
            case ObjectsAdapter.READED:
                dbController.readed(object.get_ID());
                loadObjects(0);
                break;
            case ObjectsAdapter.FOUND:
                if (user != null && !user.getEmail().equals("campusuq@uniquindio.edu.co")) {
                    JSONObject json = new JSONObject();
                    try {
                        json.put("UPDATE_ID", object.get_ID());
                        json.put(ObjectsSQLiteController.columns[8], user.get_ID());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    WebBroadcastReceiver.scheduleJob(this, WebService.ACTION_OBJECTS,
                            WebService.METHOD_DELETE, json.toString());
                    progressDialog.show();
                } else {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("CATEGORY", getString(R.string.log_in));
                    startActivity(intent);
                }
                break;
            case ObjectsAdapter.NOT_FOUND:
                if (user != null && !user.getEmail().equals("campusuq@uniquindio.edu.co") &&
                        object.getUserFound_ID() != null &&
                        object.getUserFound_ID().equals(user.get_ID())) {
                    JSONObject json = new JSONObject();
                    try {
                        json.put("UPDATE_ID", object.get_ID());
                        json.put(ObjectsSQLiteController.columns[8], JSONObject.NULL);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    WebBroadcastReceiver.scheduleJob(this, WebService.ACTION_OBJECTS,
                            WebService.METHOD_DELETE, json.toString());
                    progressDialog.show();
                } else {
                    Toast.makeText(this,
                            R.string.no_propietary,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case ObjectsAdapter.CONTACT:
                if (user != null && !user.getEmail().equals("campusuq@uniquindio.edu.co") &&
                        object.getUserLost_ID().equals(user.get_ID()) &&
                        object.getUserFound_ID() != null) {
                    Intent intent = new Intent(this, UsersActivity.class);
                    intent.putExtra("CATEGORY", getString(R.string.object_view_contact));
                    intent.putExtra("USER", user);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }

        dbController.destroy();
    }

    public LostObject getObject(int index) {
        return objects.get(index);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.object_report: {
                User user = UsersPresenter.loadUser(this);
                if (user != null && !user.getEmail().equals("campusuq@uniquindio.edu.co")) {
                    Intent intent = new Intent(this, ObjectsDetailActivity.class);
                    intent.putExtra("CATEGORY", getString(R.string.object_report_lost));
                    intent.putExtra(ObjectsSQLiteController.columns[1], user.get_ID());
                    startActivityForResult(intent, 0);
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
        if (resultCode  == RESULT_OK && !progressDialog.isShowing()) progressDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        registerReceiver(objectsReceiver, objectsFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        unregisterReceiver(objectsReceiver);
    }
}