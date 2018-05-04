package co.edu.uniquindio.campusuq.activity;

import android.os.Bundle;
import android.view.ViewStub;

import co.edu.uniquindio.campusuq.R;

public class CreditsActivity extends MainActivity {

    public CreditsActivity() {
        super.setHasSearch(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_credits);
        viewStub.inflate();
    }

}
