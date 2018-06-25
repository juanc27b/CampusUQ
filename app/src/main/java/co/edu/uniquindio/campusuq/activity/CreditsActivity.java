package co.edu.uniquindio.campusuq.activity;

import android.os.Bundle;
import android.view.ViewStub;

import co.edu.uniquindio.campusuq.R;

/**
 * Actividad que muestra los creditos de la aplicacion.
 */
public class CreditsActivity extends MainActivity {

    /**
     * Contructor que oculta el boton de busqueda.
     */
    public CreditsActivity() {
        super.setHasSearch(false);
    }

    /**
     * Asigna el fondo de la actividad e infla el diseño de los creditos.
     * @param savedInstanceState Parámetro usado para recuperar estados anteriores de la actividad.
     */
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
