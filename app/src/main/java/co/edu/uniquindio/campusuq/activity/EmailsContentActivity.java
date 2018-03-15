package co.edu.uniquindio.campusuq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewStub;
import android.widget.TextView;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.EmailsSQLiteController;

public class EmailsContentActivity extends MainActivity {
    public EmailsContentActivity() {
        super.setHasNavigationDrawerIcon(false);
        super.setHasSearch(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);
        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_emails_detail);
        viewStub.inflate();
        Intent intent = getIntent();
        ((TextView) findViewById(R.id.content_email_name)).setText(intent.getStringExtra(EmailsSQLiteController.columns[1]));
        ((TextView) findViewById(R.id.content_email_from)).setText(intent.getStringExtra(EmailsSQLiteController.columns[2]));
        ((TextView) findViewById(R.id.content_email_date)).setText(intent.getStringExtra(EmailsSQLiteController.columns[4]));
    }
}
