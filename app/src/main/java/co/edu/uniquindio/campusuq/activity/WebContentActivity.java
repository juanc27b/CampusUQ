package co.edu.uniquindio.campusuq.activity;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import co.edu.uniquindio.campusuq.R;

public class WebContentActivity extends MainActivity {

    public WebView webView;
    public WebSettings webSettings;

    public WebContentActivity() {
        super.setHasSearch(false);
        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent() {
        super.addContent();

        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);

        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_web);
        View inflated = stub.inflate();

        String link = getIntent().getStringExtra("LINK");
        String content = getIntent().getStringExtra("CONTENT");

        webView = (WebView) findViewById(R.id.webview_content);

        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(true);

        webView.loadDataWithBaseURL(link, content, "text/html", null, null);
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(WebContentActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

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
