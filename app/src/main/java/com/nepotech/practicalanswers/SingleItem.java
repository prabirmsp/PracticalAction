package com.nepotech.practicalanswers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class SingleItem extends AppCompatActivity {
    private class MyWebViewClient extends WebViewClient {
          @Override
          public boolean shouldOverrideUrlLoading(WebView view, String url) {
              view.loadUrl(url);
              return true;
          }
    }
    Button btnBack;
    WebView webview;
    String alias;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_singleitem);
        Intent in= SingleItem.this.getIntent();
		alias = in.getStringExtra("alias");
        webview=(WebView)findViewById(R.id.webView1);
        webview.setWebViewClient(new MyWebViewClient());
        openURL();
    }

     /** Opens the URL in a browser */
    private void openURL() {
        webview.loadUrl("http://answers.practicalaction.org/our-resources/item/" + alias + "?tmpl=component");
        webview.requestFocus();
    }
}
