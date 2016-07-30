package com.sonymobile.lifelog.auth;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sonymobile.lifelog.LifeLog;
import com.sonymobile.lifelog.utils.Debug;
import com.sonymobile.lifelog.utils.SecurePreferences;
import com.sonymobile.lifelog.utils.VolleySingleton;

/**
 * Created by championswimmer on 21/4/15.
 */
public class RefreshAuthTokenTask {
    private static final String OAUTH2_URL = "https://platform.lifelog.sonymobile.com/oauth/2/refresh_token";
    private final static String PARAM_CLIENT_ID = "client_id";
    private final static String PARAM_CLIENT_SECRET = "client_secret";
    private final static String PARAM_GRANT_TYPE = "grant_type";
    private final static String PARAM_REFRESH_TOKEN = "refresh_token";
    private Context mContext;

    public RefreshAuthTokenTask(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public void refreshAuth(final OnAuthenticatedListener listener) {
        final SecurePreferences spref = LifeLog.getSecurePreference(mContext);

        final String refreshAuthBody =
                PARAM_CLIENT_ID + "=" + LifeLog.getClientId() + "&"
                        + PARAM_CLIENT_SECRET + "=" + LifeLog.getClientSecret() + "&"
                        + PARAM_GRANT_TYPE + "=" + "refresh_token" + "&"
                        + PARAM_REFRESH_TOKEN + "=" + spref.getString(AuthTokenConstants.AUTH_REFRESH_TOKEN);

        JsonObjectRequest authRequest = new GetTokenJsonRequest(mContext, OAUTH2_URL, refreshAuthBody, listener);
        VolleySingleton.getInstance(mContext).addToRequestQueue(authRequest);
    }
}
