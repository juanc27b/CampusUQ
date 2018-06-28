package co.edu.uniquindio.campusuq.web;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewStub;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Actividad que permite visualizar contenido web.
 */
public class WebContentActivity extends MainActivity {

    public WebView webView;
    public WebSettings webSettings;

    /**
     * Constructor que oculta el ícono de navegación reemplazandolo por una flecha de ir atrás, y
     * oculta también el botón de busqueda.
     */
    public WebContentActivity() {
        super.setHasSearch(false);
        super.setHasNavigationDrawerIcon(false);
    }

    /**
     * Asigna el fondo de la actividad, infla el diseño de contenido web en la actividad superior y
     * asigna las variables de vistas.
     * @param savedInstanceState Parámetro para recuperar estados anteriores de la actividad.
     */
    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_web);
        stub.inflate();

        getSupportActionBar()
                .setTitle(getIntent().getIntExtra(Utilities.CATEGORY, R.string.app_name));

        String link = getIntent().getStringExtra(Utilities.LINK);
        String content = getIntent().getStringExtra("CONTENT");

        webView = findViewById(R.id.webview_content);

        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(true);

        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(WebContentActivity.this, getString(R.string.oh_no) +
                        ' ' + description, Toast.LENGTH_SHORT).show();
            }
        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        if (content != null) {
            webView.loadDataWithBaseURL(link, content, "text/html", null,
                    null);
        } else {
            webView.loadUrl(link);
        }
    }

    /**
     * Método para manejar nuevas llamadas a la actividad, dependiendo de la accion del intento
     * puede cambiar el titulo de la actividad.
     * @param intent Intento que contiene la accion a realizar.
     */
    @Override
    public void handleIntent(Intent intent) {
        if (webView != null) {
            setIntent(intent);
            getSupportActionBar()
                    .setTitle(intent.getIntExtra(Utilities.CATEGORY, R.string.app_name));

            String link = intent.getStringExtra(Utilities.LINK);
            String content = intent.getStringExtra("CONTENT");

            if (content != null) {
                webView.loadDataWithBaseURL(link, content, "text/html", null,
                        null);
            } else {
                webView.loadUrl(link);
            }
        }
    }

    /**
     * Permite volver atraz en la vista web.
     * @param keyCode Codigo de la tecla.
     * @param event Evento.
     * @return Retorna el valor que retornaria la actividad superior.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Método del ciclo de la actividad llamado para destruir la misma, en el que se anulan
     * instancias.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView = null;
        webSettings = null;
    }

}
