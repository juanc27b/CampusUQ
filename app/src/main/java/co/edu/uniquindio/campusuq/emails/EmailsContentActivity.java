package co.edu.uniquindio.campusuq.emails;

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

public class EmailsContentActivity extends MainActivity {

    public WebView webView;
    public WebSettings webSettings;

    public EmailsContentActivity() {
        super.setHasNavigationDrawerIcon(false);
        super.setHasSearch(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub viewStub = findViewById(R.id.layout_stub);
        viewStub.setLayoutResource(R.layout.content_emails_detail);
        viewStub.inflate();

        Intent intent = getIntent();

        TextView name = findViewById(R.id.content_email_name);

        name.setText(intent.getStringExtra(EmailsSQLiteController.columns[1]));
        ((TextView) findViewById(R.id.content_email_icon))
                .setText(name.getText().subSequence(0, 1).toString().toUpperCase());
        ((TextView) findViewById(R.id.content_email_from))
                .setText(intent.getStringExtra(EmailsSQLiteController.columns[2]));

        try {
            ((TextView) findViewById(R.id.content_email_date)).setText(DateFormat
                    .getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT)
                    .format(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ",
                            new Locale("es", "CO"))
                            .parse(intent.getStringExtra(EmailsSQLiteController.columns[4]))));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        webView = findViewById(R.id.webview_email_content);

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
                Toast.makeText(EmailsContentActivity.this, getString(R.string.oh_no) +
                        ' ' + description, Toast.LENGTH_SHORT).show();
            }
        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
            }
        });

        webView.loadData(intent.getStringExtra(EmailsSQLiteController.columns[6]),
                "text/html", null);
    }

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
