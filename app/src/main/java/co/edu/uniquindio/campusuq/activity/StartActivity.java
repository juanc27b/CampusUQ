package co.edu.uniquindio.campusuq.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Actividad de bienvenida de la aplicación en la que se muestra una corta animación con el logotipo
 * de la Universidad del Quindío y luego redirige a la actividad de menú.
 */
public class StartActivity extends AppCompatActivity {

    public ImageView background;

    /**
     * Método que adjunta el contexto necesario para el correcto funcionamiento de la opción de
     * cambio de idioma.
     * @param newBase Contexto a adjuntar.
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Utilities.getLanguage(newBase));
    }

    /**
     * Método llamado durante el ciclo de vida para crear la actividad, se encarga de establecer
     * el diseño de la actividad y un fondo para la misma.
     * @param savedInstanceState Parámetro usado para recuperar estados anteriores de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        background = findViewById(R.id.front_image);
        background.setImageResource(getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT ?
                R.drawable.portrait_front : R.drawable.landscape_front);
    }

    /**
     * Método del ciclo de vida de la actividad llamado para reanudar la misma, en el que se crea
     * una animación estilo "aparecer gradualmente", se le asigna un listener para abrir la
     * actividad del menú al finalizar la animación, y por último se asigna esta animación al
     * fondo de la actividad.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(StartActivity.this, MenuActivity.class)
                        .putExtra(Utilities.CATEGORY, R.string.app_title_menu));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        background.setAnimation(anim);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        background = null;
    }

}
