package com.sonymobile.lifelog.auth;

import android.content.Context;

import com.android.volley.toolbox.JsonObjectRequest;
import com.sonymobile.lifelog.LifeLog;
import com.sonymobile.lifelog.utils.VolleySingleton;

/**
 * Created by championswimmer on 21/4/15.
 */
public class GetAuthTokenTask {

    private static final String TAG = "LifeLog:GetAuthToken";
    private static final String OAUTH2_URL = "https://platform.lifelog.sonymobile.com/oauth/2/token";
    private final static String PARAM_CLIENT_ID = "client_id";
    private final static String PARAM_CLIENT_SECRET = "client_secret";
    private final static String PARAM_GRANT_TYPE = "grant_type";
    private final static String PARAM_CODE = "code";
    private final Context mContext;

    public GetAuthTokenTask(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public void getAuth(final String authCode, final OnAuthenticatedListener listener) {

        final String authRequestBody =
                PARAM_CLIENT_ID + "=" + LifeLog.getClientId() + "&"
                        + PARAM_CLIENT_SECRET + "=" + LifeLog.getClientSecret() + "&"
                        + PARAM_GRANT_TYPE + "=" + "authorization_code" + "&"
                        + PARAM_CODE + "=" + authCode;

        JsonObjectRequest authRequest = new GetTokenJsonRequest(mContext, OAUTH2_URL, authRequestBody, listener);

        VolleySingleton volley = VolleySingleton.getInstance(mContext);
        volley.addToRequestQueue(authRequest);

        // VolleySingleton is public and visible, client app may stop the thread of Volley to suppress
        // battery consumption etc. Call start API explicitly to ensure for operation start
        volley.getRequestQueue().start();
    }

}
