package co.edu.uniquindio.campusuq.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.ViewStub;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;

public class NotificationsActivity extends MainActivity implements
        CompoundButton.OnCheckedChangeListener {

    private Switch events;
    private Switch news;
    private Switch academicCalendar;
    private Switch lostObjects;
    private Switch securitySystem;
    private Switch billboardInformation;
    private Switch institutionalMail;

    public NotificationsActivity() {
        super.setHasSearch(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_notifications);
        viewStub.inflate();

        events = findViewById(R.id.notifications_events);
        news = findViewById(R.id.notifications_news);
        academicCalendar = findViewById(R.id.notifications_academic_calendar);
        lostObjects = findViewById(R.id.notifications_lost_objects);
        securitySystem = findViewById(R.id.notifications_security_system);
        billboardInformation = findViewById(R.id.notifications_billboard_information);
        institutionalMail = findViewById(R.id.notifications_institutional_mail);

        setNotifications();
    }

    @Override
    public void handleIntent(Intent intent) {
        setIntent(intent);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) setNotifications();
    }

    public void setNotifications() {
        unregisterListener();
        ArrayList<Notification> notifications =
                NotificationsPresenter.loadNotifications(this);
        events.setChecked(notifications.get(0).getActivated().equals("S"));
        news.setChecked(notifications.get(1).getActivated().equals("S"));
        academicCalendar.setChecked(notifications.get(2).getActivated().equals("S"));
        lostObjects.setChecked(notifications.get(3).getActivated().equals("S"));
        securitySystem.setChecked(notifications.get(4).getActivated().equals("S"));
        billboardInformation.setChecked(notifications.get(5).getActivated().equals("S"));
        institutionalMail.setChecked(notifications.get(6).getActivated().equals("S"));
        registerListener();
    }

    public void registerListener() {
        events.setOnCheckedChangeListener(this);
        news.setOnCheckedChangeListener(this);
        academicCalendar.setOnCheckedChangeListener(this);
        lostObjects.setOnCheckedChangeListener(this);
        securitySystem.setOnCheckedChangeListener(this);
        billboardInformation.setOnCheckedChangeListener(this);
        institutionalMail.setOnCheckedChangeListener(this);
    }

    public void unregisterListener() {
        events.setOnCheckedChangeListener(null);
        news.setOnCheckedChangeListener(null);
        academicCalendar.setOnCheckedChangeListener(null);
        lostObjects.setOnCheckedChangeListener(null);
        securitySystem.setOnCheckedChangeListener(null);
        billboardInformation.setOnCheckedChangeListener(null);
        institutionalMail.setOnCheckedChangeListener(null);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String _ID = "";

        switch (buttonView.getId()) {
            case R.id.notifications_events:
                _ID = "0";
                break;
            case R.id.notifications_news:
                _ID = "1";
                break;
            case R.id.notifications_academic_calendar:
                _ID = "2";
                break;
            case R.id.notifications_lost_objects:
                _ID = "3";
                break;
            case R.id.notifications_security_system:
                _ID = "4";
                break;
            case R.id.notifications_billboard_information:
                _ID = "5";
                break;
            case R.id.notifications_institutional_mail:
                _ID = "6";
                break;
            default:
                break;
        }

        NotificationsPresenter.updateNotification(this, _ID, isChecked ? "S" : "N");
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.analytics_notifications_category))
                .setAction(getString(R.string.analytics_modify_action))
                .setLabel(getString(R.string.analytics_adjust_notifications_label))
                .setValue(1)
                .build());
    }

    /**
     * Método del ciclo de la actividad llamado para destruir la misma, en el que se anulan
     * instancias.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        events = null;
        news = null;
        academicCalendar = null;
        lostObjects = null;
        securitySystem = null;
        billboardInformation = null;
        institutionalMail = null;
    }

}
