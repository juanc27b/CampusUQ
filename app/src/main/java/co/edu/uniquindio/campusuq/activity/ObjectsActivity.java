package co.edu.uniquindio.campusuq.activity;

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

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.ObjectsAdapter;
import co.edu.uniquindio.campusuq.util.ObjectsSQLiteController;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;
import co.edu.uniquindio.campusuq.vo.LostObject;

public class ObjectsActivity extends MainActivity implements ObjectsAdapter.OnClickObjectListener {

    private ArrayList<LostObject> objects = new ArrayList<>();
    private boolean newActivity = true, oldObjects = true;
    private ObjectsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private IntentFilter objectsFilter = new IntentFilter(WebService.ACTION_OBJECTS);
    private BroadcastReceiver objectsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadObjects(intent.getIntExtra("INSERTED", 0));
        }
    };

    public ObjectsActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);

        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_objects);
        viewStub.inflate();

        findViewById(R.id.object_report).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ObjectsActivity.this, ObjectsDetailActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.lost_objects_detail));
                intent.putExtra(ObjectsSQLiteController.columns[1], "1");
                intent.putExtra(ObjectsSQLiteController.columns[2], "");
                intent.putExtra(ObjectsSQLiteController.columns[3], "");
                intent.putExtra(ObjectsSQLiteController.columns[4], "");
                intent.putExtra(ObjectsSQLiteController.columns[5], "");
                intent.putExtra(ObjectsSQLiteController.columns[6], "");
                startActivityForResult(intent, 0);
            }
        });

        loadObjects(0);
    }

    @Override
    public void handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            for(LostObject object : objects) if(object.getName().toLowerCase().contains(query.trim().toLowerCase())) {
                layoutManager.scrollToPosition(objects.indexOf(object));
                return;
            }
            Toast.makeText(this, "No se ha encontrado el objeto: "+query, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadObjects(int inserted) {

        if(!progressDialog.isShowing()) progressDialog.show();

        int scrollTo = oldObjects ? (newActivity ? 0 : objects.size()-1) : (inserted != 0 ? inserted-1 : 0);

        ObjectsSQLiteController dbController = new ObjectsSQLiteController(getApplicationContext(), 1);
        objects = dbController.select(String.valueOf(inserted > 0 ? objects.size()+inserted : objects.size()+3), "`"+ObjectsSQLiteController.columns[8]+"` = 'N'", null);
        dbController.destroy();

        if(newActivity) {
            adapter = new ObjectsAdapter(objects, ObjectsActivity.this);
            layoutManager = new LinearLayoutManager(ObjectsActivity.this, LinearLayoutManager.VERTICAL, false);
            RecyclerView recyclerView = findViewById(R.id.objects_recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if(newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        if(!recyclerView.canScrollVertically(-1)) {
                            if(Utilities.haveNetworkConnection(ObjectsActivity.this)) {
                                oldObjects = false;
                                progressDialog.show();
                                WebBroadcastReceiver.scheduleJob(getApplicationContext(), WebService.ACTION_OBJECTS, WebService.METHOD_GET, null);
                            } else {
                                Toast.makeText(ObjectsActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                            }
                        } else if(!recyclerView.canScrollVertically(1)) {
                            oldObjects = true;
                            loadObjects(0);
                        }
                    }
                }
            });
            newActivity = false;
        } else {
            adapter.setObjects(objects);
            layoutManager.scrollToPosition(scrollTo);
        }

        if(progressDialog.isShowing() && objects.size() > 0) progressDialog.dismiss();

    }

    @Override
    public void onObjectClick(int index, String action) {
        switch(action) {
            case ObjectsAdapter.DIALOG:
                ObjectsFragment.newInstance(index).show(getSupportFragmentManager(), null);
                break;
            case ObjectsAdapter.READED:
                LostObject object = objects.get(index);
                ObjectsSQLiteController dbController = new ObjectsSQLiteController(getApplicationContext(), 1);
                dbController.readed(object.get_ID());
                dbController.destroy();
                loadObjects(0);
                break;
            case ObjectsAdapter.FOUND:
                break;
            default:
                break;
        }
    }

    public LostObject getObject(int index) {
        return objects.get(index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode  == RESULT_OK && !progressDialog.isShowing()) progressDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(objectsReceiver, objectsFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(objectsReceiver);
    }
}