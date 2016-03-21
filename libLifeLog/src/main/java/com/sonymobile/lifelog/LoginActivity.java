package com.sonymobile.lifelog;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sonymobile.lifelog.auth.GetAuthTokenTask;
import com.sonymobile.lifelog.utils.Debug;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by championswimmer on 21/4/15.
 */
public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final Uri AUTH_BASE_URL = Uri.parse("https://platform.lifelog.sonymobile.com/oauth/2/authorize");

    static Pattern AUTH_CODE_PATTERN = Pattern.compile("(code)" + "(=)" + "(.*)");
    String authentication_code = "";

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.activity_login);

        mWebView = (WebView) findViewById(R.id.webview);
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains(LifeLog.getCallback_url())) {
                    Matcher m = AUTH_CODE_PATTERN.matcher(url);
                    if (m.find()) {
                        mWebView.setVisibility(View.GONE);
                        authentication_code = m.group(3);
                        GetAuthTokenTask gat = new GetAuthTokenTask(getApplicationContext());
                        gat.getAuth(authentication_code, new GetAuthTokenTask.OnAuthenticatedListener() {
                            @Override
                            public void onAuthenticated(String authToken) {
                                LifeLog.auth_token = authToken;
                                setResult(RESULT_OK);
                                finish();
                            }

                            @Override
                            public void onError(Exception e) {
                                if (Debug.isDebuggable(LoginActivity.this)) {
                                    Log.w(TAG, "onError", e);
                                }
                                setResult(RESULT_CANCELED);
                                finish();
                            }
                        });
                    }

                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                setProgress(newProgress * 100);
            }
        });

        if (TextUtils.isEmpty(LifeLog.getScope())) {
            throw new RuntimeException("Scope parameter is empty!");
        }

        Uri uri = AUTH_BASE_URL.buildUpon()
                .appendQueryParameter("client_id", LifeLog.getClient_id())
                .appendQueryParameter("scope", LifeLog.getScope()).build();

        mWebView.loadUrl(uri.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mWebView.restoreState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        mWebView.destroy();
        super.onDestroy();
    }
}
