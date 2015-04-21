package com.sonymobile.lifelog.login;

        import android.app.Activity;
        import android.os.Build;
        import android.os.Bundle;
        import android.view.View;
        import android.webkit.WebSettings;
        import android.webkit.WebView;
        import android.webkit.WebViewClient;
        import android.widget.Toast;

        import com.sonymobile.lifelog.LifeLog;
        import com.sonymobile.lifelog.R;

        import java.util.regex.Matcher;
        import java.util.regex.Pattern;

/**
 * Created by championswimmer on 21/4/15.
 */
public class LoginActivity extends Activity{
    String authentication_code = "";
    static Pattern AUTH_CODE_PATTERN = Pattern.compile("(code)" + "(=)" + "(.*)");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final WebView wv = (WebView) findViewById(R.id.webview);
        if (Build.VERSION.SDK_INT >= 19) {
            wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        wv.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains(LifeLog.getCallback_url())) {
                    Matcher m = AUTH_CODE_PATTERN.matcher(url);
                    if (m.find()){
                        authentication_code = m.group(3);
                        GetAuthToken gat = new GetAuthToken();
                        gat.execute(authentication_code);
                        wv.setVisibility(View.GONE);
                    }

                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        wv.loadUrl("https://platform.lifelog.sonymobile.com/oauth/2/authorize?client_id="
                + LifeLog.getClient_id()
                + "&scope="
                + LifeLog.getScope());

    }

}
