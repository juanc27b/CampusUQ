package co.edu.uniquindio.campusuq.news;

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
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;
import co.edu.uniquindio.campusuq.util.Utilities;

/**
 * Actividad para visualizar el contenido completo de una noticia de las funcionalidades Noticias y
 * Eventos.
 */
public class NewsContentActivity extends MainActivity {

    public TextView title;
    public TextView date;
    public TextView author;

    public WebView webView;
    public WebSettings webSettings;

    /**
     * Constructor que oculta el ícono de navegación reemplazandolo por una flecha de ir atrás, y
     * oculta también el botón de busqueda.
     */
    public NewsContentActivity() {
        super.setHasSearch(false);
        super.setHasNavigationDrawerIcon(false);
    }

    /**
     * Asigna el fondo de la actividad, infla el diseño de contenido de noticia en la actividad
     * superior y asigna las variables de vistas.
     * @param savedInstanceState Parámetro para recuperar estados anteriores de la actividad.
     */
    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_news_detail);
        stub.inflate();

        Intent intent = getIntent();
        title = findViewById(R.id.content_new_title);
        date = findViewById(R.id.content_new_date);
        author = findViewById(R.id.content_new_author);
        webView = findViewById(R.id.webview_news_content);

        title.setText(intent.getStringExtra("TITLE"));

        try {
            date.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                    .format(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ",
                            new Locale("es", "CO"))
                            .parse(intent.getStringExtra("DATE"))));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        author.setText(intent.getStringExtra("AUTHOR"));

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
            public void onReceivedError(WebView view, int errorCode, String description,
                                        String failingUrl) {
                Toast.makeText(NewsContentActivity.this, "Oh no! "+description,
                        Toast.LENGTH_SHORT).show();
            }
        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        webView.loadDataWithBaseURL(intent.getStringExtra(Utilities.LINK),
                intent.getStringExtra("CONTENT"), "text/html", null,
                null);
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
        title = null;
        date = null;
        author = null;
        webView = null;
        webSettings = null;
    }

}
