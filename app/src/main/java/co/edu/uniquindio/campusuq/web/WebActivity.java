package co.edu.uniquindio.campusuq.web;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.Utilities;

public class WebActivity extends AppCompatActivity {

    public WebView webView;
    public WebSettings webSettings;
    public EditText urlText;

    public String pendingURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Toolbar toolbar = findViewById(R.id.web_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        urlText = findViewById(R.id.url_edit_text);
        pendingURL = getIntent().getStringExtra(Utilities.URL);
        urlText.setText(pendingURL);

        webView = findViewById(R.id.webview);

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
            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                                        String failingUrl) {
                Toast.makeText(WebActivity.this, getString(R.string.oh_no) + ' ' +
                        description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                pendingURL = request.getUrl().toString();
                if (pendingURL.startsWith("https://accounts.google.com/o/oauth2/v2/auth")) {
                    //loadOAuthURL();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(pendingURL)));
                    return true;
                } else {
                    urlText.setText(pendingURL);
                    return false;
                }
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

        ImageView goBack = findViewById(R.id.go_back_button);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }
        });

        ImageView goForward = findViewById(R.id.go_forward_button);
        goForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoForward()) {
                    webView.goForward();
                }
            }
        });

        ImageView cancel = findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webView.loadUrl(pendingURL);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        pendingURL = getIntent().getStringExtra(Utilities.URL);
        urlText.setText(pendingURL);
        webView.loadUrl(pendingURL);
        webView.clearHistory();
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

}
