package co.edu.uniquindio.campusuq.activity;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.EmailsAdapter;
import co.edu.uniquindio.campusuq.util.EmailsSQLiteController;
import co.edu.uniquindio.campusuq.util.Utilities;
import co.edu.uniquindio.campusuq.util.WebBroadcastReceiver;
import co.edu.uniquindio.campusuq.util.WebService;
import co.edu.uniquindio.campusuq.vo.Email;

public class EmailsActivity extends MainActivity implements EmailsAdapter.OnClickEmailListener {
    private ArrayList<Email> emails = new ArrayList<>();
    private boolean newActivity = true, oldEmails = true;
    private EmailsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private IntentFilter emailsFilter = new IntentFilter(WebService.ACTION_EMAILS);
    private BroadcastReceiver emailsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadEmails(intent.getIntExtra("INSERTED", 0));
        }
    };

    public EmailsActivity() {
        super.setHasNavigationDrawerIcon(false);
        emails.add(new Email("1", "NOTICIAS acerca de Modificaciones al Registro de Asignaturas", "donoreply uqvirtual", "", "14 de Noviembre, 7:35 AM", "NOTICIAS acerca de modificaciones al ...\nPara conocer las estrategias y ..."));//
        emails.add(new Email("2", "", "Oficina Asesora de Comunicaciones", "", "05 de Noviembre, 12:48 PM", "Fwd: Entrega de Incentivos Programa ...\nAtentamente me permito informarles que ..."));//
    }

    @Override
    public void handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            for(Email email : emails) if(email.getName().toLowerCase().contains(query.trim().toLowerCase())) {
                layoutManager.scrollToPosition(emails.indexOf(email));
                return;
            }
            Toast.makeText(this, "No se ha encontrado el correo: "+query, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);
        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_emails);
        viewStub.inflate();
        FloatingActionButton insert = findViewById(R.id.fab);
        insert.setVisibility(View.VISIBLE);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmailsActivity.this, EmailsDetailActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.institutional_mail));
                startActivityForResult(intent, 0);
            }
        });
        loadEmails(0);
    }

    private void loadEmails(int inserted) {
        if(!progressDialog.isShowing()) progressDialog.show();
        int scrollTo = oldEmails? (newActivity? 0 : emails.size()-1) : (inserted != 0? inserted-1 : 0);
        /*EmailsSQLiteController dbController = new EmailsSQLiteController(getApplicationContext(), 1);
        emails = dbController.select(String.valueOf(inserted > 0? emails.size()+inserted : emails.size()+6), null, null);
        dbController.destroy();*/
        if(newActivity) {
            adapter = new EmailsAdapter(emails, this);
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            RecyclerView recyclerView = findViewById(R.id.emails_recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if(newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        if(!recyclerView.canScrollVertically(-1)) {
                            if(Utilities.haveNetworkConnection(EmailsActivity.this)) {
                                oldEmails = false;
                                progressDialog.show();
                                WebBroadcastReceiver.scheduleJob(getApplicationContext(), WebService.ACTION_EMAILS, WebService.METHOD_GET, null);
                            } else {
                                Toast.makeText(EmailsActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                            }
                        } else if(!recyclerView.canScrollVertically(1)) {
                            oldEmails = true;
                            loadEmails(0);
                        }
                    }
                }
            });
            newActivity = false;
        } else {
            adapter.setEmails(emails);
            layoutManager.scrollToPosition(scrollTo);
        }
        if(progressDialog.isShowing() && emails.size() > 0) progressDialog.dismiss();
    }

    @Override
    public void onEmailClick(int index) {
        Email email = emails.get(index);
        Intent intent = new Intent(this, EmailsContentActivity.class);
        intent.putExtra("CATEGORY", getString(R.string.institutional_mail));
        intent.putExtra(EmailsSQLiteController.columns[0], email.get_ID());
        intent.putExtra(EmailsSQLiteController.columns[1], email.getName());
        intent.putExtra(EmailsSQLiteController.columns[2], email.getFrom());
        intent.putExtra(EmailsSQLiteController.columns[3], email.getTo());
        intent.putExtra(EmailsSQLiteController.columns[4], email.getDate());
        intent.putExtra(EmailsSQLiteController.columns[5], email.getContent());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode  == RESULT_OK && !progressDialog.isShowing()) progressDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(emailsReceiver, emailsFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(emailsReceiver);
    }
}
