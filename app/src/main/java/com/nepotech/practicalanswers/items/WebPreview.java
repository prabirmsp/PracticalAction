package com.nepotech.practicalanswers.items;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.nepotech.practicalanswers.Global;
import com.nepotech.practicalanswers.R;


public class WebPreview extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_preview);
        // Transition
        overridePendingTransition(Global.B_enter, Global.A_exit);

        final WebView webView = (WebView) findViewById(R.id.webview);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        String url = getIntent().getStringExtra(SingleItemActivity.KEY_EXTRA_LINK);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        disableDebug(webView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) //required for running javascript on android 4.1 or later
        {
            settings.setAllowFileAccessFromFileURLs(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
            settings.setBuiltInZoomControls(true);
        }
        webView.setWebChromeClient(new MyWebViewClient());
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + url);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(Global.A_enter, Global.B_exit);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(Global.A_enter, Global.B_exit);
    }

    @TargetApi(19)
    private void disableDebug(WebView webView){
        webView.setWebContentsDebuggingEnabled(false);
    }

}
