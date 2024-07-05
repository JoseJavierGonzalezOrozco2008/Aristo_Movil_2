package com.example.aristomovil2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

public class Manuales extends ActividadBase {
    private WebView webView;

    @SuppressLint({"MissingInflatedId", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manuales_webview);

        webView = findViewById(R.id.webViewManu);
        webView.getSettings().setJavaScriptEnabled(true);

        String url = getIntent().getStringExtra("url");

        webView.setWebViewClient(new WebViewClient());

        assert url != null;
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("url", webView.getUrl());
    }
}
