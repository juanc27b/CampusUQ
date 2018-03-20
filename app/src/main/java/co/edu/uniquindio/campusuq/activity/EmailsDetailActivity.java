package co.edu.uniquindio.campusuq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;

import co.edu.uniquindio.campusuq.R;

public class EmailsDetailActivity extends MainActivity implements View.OnClickListener {

    private Intent intent;
    private EditText from, to, name, content;

    public EmailsDetailActivity() {
        super.setHasNavigationDrawerIcon(false);
        super.setHasSearch(false);
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

        findViewById(R.id.email_detail_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
        case R.id.email_detail_ok:
            setResult(RESULT_OK, intent);
            finish();
            break;
        }
    }

}
