package com.sonymobile.lifelog.login;

        import android.app.Activity;
        import android.os.Build;
        import android.os.Bundle;
        import android.webkit.WebSettings;
        import android.webkit.WebView;
        import android.webkit.WebViewClient;
        import android.widget.Toast;

        import com.sonymobile.lifelog.LifeLog;
        import com.sonymobile.lifelog.R;

/**
 * Created by championswimmer on 21/4/15.
 */
public class LoginActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        WebView wv = (WebView) findViewById(R.id.webview);
        LLWebViewClient llwvc = new LLWebViewClient();
        if (Build.VERSION.SDK_INT >= 19) {
            wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        wv.setWebViewClient(llwvc);
        wv.loadUrl("https://platform.lifelog.sonymobile.com/oauth/2/authorize?client_id="
                + LifeLog.getClient_id()
                + "&scope="
                + LifeLog.getScope());

    }

    private class LLWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("championswimmer")) {
                Toast.makeText(getApplicationContext(), "The code is " + url, Toast.LENGTH_SHORT).show();
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
