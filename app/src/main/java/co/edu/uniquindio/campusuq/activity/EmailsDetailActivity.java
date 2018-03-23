package co.edu.uniquindio.campusuq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.Toast;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.Utilities;

public class EmailsDetailActivity extends MainActivity implements View.OnClickListener {

    private Intent intent;
    private EditText from, to, name, content;

    public EmailsDetailActivity() {
        super.setHasNavigationDrawerIcon(false);
        super.setHasSearch(false);
    }

    @Override
    public void handleIntent(Intent intent) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(intent.getStringExtra("CATEGORY"));
            this.intent = intent;
            setEmail();
        }
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.activity_emails_detail);
        viewStub.inflate();

        intent = getIntent();
        from = findViewById(R.id.email_detail_from);
        to = findViewById(R.id.email_detail_to);
        name = findViewById(R.id.email_detail_name);
        content = findViewById(R.id.email_detail_content);
        setEmail();

        findViewById(R.id.email_detail_ok).setOnClickListener(this);
    }

    private void setEmail() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.email_detail_ok:
                if (Utilities.haveNetworkConnection(EmailsDetailActivity.this)) {
                    if (from.getText().length() != 0 && to.getText().length() != 0 && name.getText().length() != 0 && content.getText().length() != 0) {
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(EmailsDetailActivity.this, getString(R.string.empty_string), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EmailsDetailActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

}
