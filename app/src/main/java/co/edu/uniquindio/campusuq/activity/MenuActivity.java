package co.edu.uniquindio.campusuq.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;

import co.edu.uniquindio.campusuq.R;

public class MenuActivity extends MainActivity {

    public MenuActivity() {
        super.setHasSearch(false);
    }

    @Override
    public void addContent() {
        super.addContent();

        super.setBackground(R.drawable.portrait_background, R.drawable.landscape_background);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_menu);
        View inflated = stub.inflate();

        LinearLayout information = (LinearLayout) inflated.findViewById(R.id.information_module_layout);
        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ItemsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.information_module));
                MenuActivity.this.startActivity(intent);
            }
        });
        LinearLayout services = (LinearLayout) inflated.findViewById(R.id.services_module_layout);
        services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ItemsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.services_module));
                MenuActivity.this.startActivity(intent);
            }
        });
        LinearLayout state = (LinearLayout) inflated.findViewById(R.id.state_module_layout);
        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ItemsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.state_module));
                MenuActivity.this.startActivity(intent);
            }
        });
        LinearLayout communication = (LinearLayout) inflated.findViewById(R.id.communication_module_layout);
        communication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ItemsActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.communication_module));
                MenuActivity.this.startActivity(intent);
            }
        });

    }

}
