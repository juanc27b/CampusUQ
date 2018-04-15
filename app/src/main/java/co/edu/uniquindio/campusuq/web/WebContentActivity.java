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

public class WebContentActivity extends MainActivity {

    public WebView webView;
    public WebSettings webSettings;

    public WebContentActivity() {
        super.setHasSearch(false);
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_web);
        stub.inflate();

        String category = getIntent().getStringExtra("CATEGORY");
        getSupportActionBar().setTitle(category);

        String link = getIntent().getStringExtra("LINK");
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

    @Override
    public void handleIntent(Intent intent) {
        if (webView != null) {
            String category = intent.getStringExtra("CATEGORY");
            getSupportActionBar().setTitle(category);

            String link = intent.getStringExtra("LINK");
            String content = intent.getStringExtra("CONTENT");

            if (content != null) {
                webView.loadDataWithBaseURL(link, content, "text/html", null,
                        null);
            } else {
                webView.loadUrl(link);
            }
        }
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
