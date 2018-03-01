package co.edu.uniquindio.campusuq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import co.edu.uniquindio.campusuq.R;

public class NewsContentActivity extends MainActivity {

    public TextView newTitle;
    public TextView newDate;
    public TextView newAuthor;

    public WebView webView;
    public WebSettings webSettings;

    public NewsContentActivity() {
        super.setHasSearch(false);
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);

        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);

        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_news_detail);
        View inflated = stub.inflate();

        Intent intent = getIntent();
        String title = intent.getStringExtra("TITLE");
        String date = intent.getStringExtra("DATE");
        String author = intent.getStringExtra("AUTHOR");
        String link = intent.getStringExtra("LINK");
        String content = intent.getStringExtra("CONTENT");

        newTitle = (TextView) findViewById(R.id.content_new_title);
        newTitle.setText(title);
        newDate = (TextView) findViewById(R.id.content_new_date);
        newDate.setText(date);
        newAuthor = (TextView) findViewById(R.id.content_new_author);
        newAuthor.setText(author);

        webView = (WebView) findViewById(R.id.webview_news_content);

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
                Toast.makeText(NewsContentActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        webView.loadDataWithBaseURL(link, content, "text/html", null, null);

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