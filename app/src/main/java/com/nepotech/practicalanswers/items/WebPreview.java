package com.nepotech.practicalanswers.items;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.nepotech.practicalanswers.R;


public class WebPreview extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web_preview);

        final WebView webView = (WebView) findViewById(R.id.webview);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        String url = getIntent().getStringExtra(SingleItemActivity.KEY_EXTRA_LINK);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new MyWebViewClient());
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + url);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class MyWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);
            if (newProgress > 99)
                progressBar.setVisibility(View.INVISIBLE);
            super.onProgressChanged(view, newProgress);
        }
    }
}
