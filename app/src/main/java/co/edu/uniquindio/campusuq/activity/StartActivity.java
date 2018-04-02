package co.edu.uniquindio.campusuq.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import co.edu.uniquindio.campusuq.R;

public class StartActivity extends AppCompatActivity {

    public ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        background = findViewById(R.id.front_image);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            background.setImageResource(R.drawable.portrait_front);
        } else {
            background.setImageResource(R.drawable.landscape_front);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(StartActivity.this, MenuActivity.class);
                intent.putExtra("CATEGORY", getString(R.string.app_title_menu));
                StartActivity.this.startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        background.setAnimation(anim);
    }

}
