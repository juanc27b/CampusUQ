package co.edu.uniquindio.campusuq.emails;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.web.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.web.WebService;
import pub.devrel.easypermissions.EasyPermissions;

public class EmailsActivity extends MainActivity implements EmailsAdapter.OnClickEmailListener,
        EasyPermissions.PermissionCallbacks {

    private ArrayList<Email> emails = new ArrayList<>();
    private boolean newActivity = true;
    private EmailsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private boolean oldEmails = true;

    private IntentFilter emailsFilter = new IntentFilter(WebService.ACTION_EMAILS);
    private BroadcastReceiver emailsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent exceptionIntent = intent.getParcelableExtra("INTENT");
            if (exceptionIntent == null) {
                loadEmails(intent.getIntExtra("INSERTED", 0));
            } else {
                if(progressDialog.isShowing()) progressDialog.dismiss();
                startActivityForResult(exceptionIntent, EmailsPresenter.REQUEST_AUTHORIZATION);
            }

        }
    };

    public EmailsActivity() {
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_emails);
        viewStub.inflate();
        FloatingActionButton insert = findViewById(R.id.fab);

        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmailsActivity.this,
                        EmailsDetailActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.institutional_mail));
                startActivityForResult(intent, 0);
            }
        });
        insert.setVisibility(View.VISIBLE);

        loadEmails(0);
    }

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            for (Email email : emails)
                if(email.getName().toLowerCase().contains(query.trim().toLowerCase())) {
                layoutManager.scrollToPosition(emails.indexOf(email));
                return;
            }
            Toast.makeText(this, getString(R.string.email_no_found)+query,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void loadEmails(int inserted) {
        if (!progressDialog.isShowing()) progressDialog.show();

        int scrollTo = oldEmails ?
                (newActivity ? 0 : emails.size()-1) : (inserted != 0 ? inserted-1 : 0);

        EmailsSQLiteController dbController = new EmailsSQLiteController(this, 1);
        emails = dbController.select(""+emails.size()+(inserted > 0 ? inserted : 6));
        dbController.destroy();

        if (newActivity) {
            newActivity = false;
            adapter = new EmailsAdapter(emails, this);
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                    false);

            RecyclerView recyclerView = findViewById(R.id.emails_recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        if (!recyclerView.canScrollVertically(-1)) {
                            if (Utilities.haveNetworkConnection(EmailsActivity.this)) {
                                oldEmails = false;
                                progressDialog.show();
                                WebBroadcastReceiver.scheduleJob(EmailsActivity.this,
                                        WebService.ACTION_EMAILS, WebService.METHOD_GET,
                                        null);
                            } else {
                                Toast.makeText(EmailsActivity.this,
                                        getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                            }
                        } else if (!recyclerView.canScrollVertically(1)) {
                            oldEmails = true;
                            loadEmails(0);
                        }
                    }
                }
            });
        } else {
            adapter.setEmails(emails);
            layoutManager.scrollToPosition(scrollTo);
        }

        if (emails.isEmpty()) WebBroadcastReceiver.scheduleJob(this,
                WebService.ACTION_EMAILS, WebService.METHOD_GET, null);

        if (progressDialog.isShowing() && emails.size() > 0) progressDialog.dismiss();
    }

    @Override
    public void onEmailClick(int index) {
        Email email = emails.get(index);
        Intent intent = new Intent(this, EmailsContentActivity.class);
        intent.putExtra("CATEGORY", getString(R.string.institutional_mail));
        intent.putExtra(EmailsSQLiteController.columns[1], email.getName());
        intent.putExtra(EmailsSQLiteController.columns[2], email.getFrom());
        intent.putExtra(EmailsSQLiteController.columns[4], email.getDate());
        intent.putExtra(EmailsSQLiteController.columns[6], email.getContent());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        registerReceiver(emailsReceiver, emailsFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        unregisterReceiver(emailsReceiver);
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case EmailsPresenter.REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    progressDialog.show();
                    WebBroadcastReceiver.scheduleJob(this, WebService.ACTION_EMAILS,
                            WebService.METHOD_GET, null);
                } else {
                    Toast.makeText(this,
                            R.string.email_authorization,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

}
