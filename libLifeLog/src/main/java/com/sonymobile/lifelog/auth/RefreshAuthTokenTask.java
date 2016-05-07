package com.sonymobile.lifelog.auth;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sonymobile.lifelog.LifeLog;
import com.sonymobile.lifelog.utils.Debug;
import com.sonymobile.lifelog.utils.SecurePreferences;
import com.sonymobile.lifelog.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

/**
 * Created by championswimmer on 21/4/15.
 */
public class RefreshAuthTokenTask {

    public static final String TAG = "LifeLog:RefreshAuth";
    public static final String OAUTH2_URL = "https://platform.lifelog.sonymobile.com/oauth/2/refresh_token";
    public static final String AUTH_TOKEN_TYPE = GetAuthTokenTask.AUTH_TOKEN_TYPE;
    public static final String AUTH_ACCESS_TOKEN = GetAuthTokenTask.AUTH_ACCESS_TOKEN;
    public static final String AUTH_ISSUED_AT = GetAuthTokenTask.AUTH_ISSUE_AT;
    public static final String AUTH_EXPIRES_IN = GetAuthTokenTask.AUTH_EXPIRES_IN;
    public static final String AUTH_REFRESH_TOKEN = GetAuthTokenTask.AUTH_REFRESH_TOKEN;
    public static final String AUTH_REFRESH_TOKEN_EXPIRES_IN = GetAuthTokenTask.AUTH_REFRESH_TOKEN_EXPIRES_IN;
    static String PARAM_CLIENT_ID = "client_id";
    static String PARAM_CLIENT_SECRET = "client_secret";
    static String PARAM_GRANT_TYPE = "grant_type";
    static String PARAM_REFRESH_TOKEN = "refresh_token";
    private Context mContext;
    private OnAuthenticatedListener onAuthenticatedListener;
    public RefreshAuthTokenTask(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public void refreshAuth(OnAuthenticatedListener oal) {
        onAuthenticatedListener = oal;
        final SecurePreferences spref = LifeLog.getSecurePreference(mContext);

        final String refreshAuthBody =
                PARAM_CLIENT_ID + "=" + LifeLog.getClient_id() + "&"
                        + PARAM_CLIENT_SECRET + "=" + LifeLog.getClient_secret() + "&"
                        + PARAM_GRANT_TYPE + "=" + "refresh_token" + "&"
                        + PARAM_REFRESH_TOKEN + "=" + spref.getString(AUTH_REFRESH_TOKEN);

        if (Debug.isDebuggable(mContext)) {
            Log.d(TAG, refreshAuthBody);
        }

        JsonObjectRequest authRequest = new JsonObjectRequest(Request.Method.POST,
                OAUTH2_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jObj) {
                        if (Debug.isDebuggable(mContext)) {
                            Log.v(TAG, jObj.toString());
                        }
                        try {
                            spref.put(AUTH_TOKEN_TYPE, jObj.getString(AUTH_TOKEN_TYPE));
                            spref.put(AUTH_ACCESS_TOKEN, jObj.getString(AUTH_ACCESS_TOKEN));
                            spref.put(AUTH_EXPIRES_IN, jObj.getString(AUTH_EXPIRES_IN));
                            spref.put(AUTH_ISSUED_AT, String.valueOf(System.currentTimeMillis() / 1000));
                            spref.put(AUTH_REFRESH_TOKEN, jObj.getString(AUTH_REFRESH_TOKEN));
                            spref.put(AUTH_REFRESH_TOKEN_EXPIRES_IN, jObj.getString(AUTH_REFRESH_TOKEN_EXPIRES_IN));
                            if (Debug.isDebuggable(mContext)) {
                                Log.d(TAG, jObj.getString(AUTH_ACCESS_TOKEN));
                                Log.d(TAG, jObj.getString(AUTH_REFRESH_TOKEN));
                            }
                            if (onAuthenticatedListener != null) {
                                onAuthenticatedListener.onAuthenticated(jObj.getString(AUTH_ACCESS_TOKEN));
                            }
                        } catch (JSONException e) {
                            if (Debug.isDebuggable(mContext)) {
                                Log.w(TAG, "JSONException", e);
                            }
                            //TODO: handle malformed json
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (Debug.isDebuggable(mContext)) {
                            Log.w(TAG, "VolleyError: " + new String(volleyError.networkResponse.data), volleyError);
                        }

                    }
                }
        ) {
            @Override
            public String getBodyContentType() {
                return String.format("application/x-www-form-urlencoded; charset=%s", new Object[]{"utf-8"});
            }

            @Override
            public byte[] getBody() {
                return refreshAuthBody.getBytes(Charset.forName("utf-8"));
            }

        };
        VolleySingleton.getInstance(mContext).addToRequestQueue(authRequest);
    }

    public interface OnAuthenticatedListener {
        void onAuthenticated(String auth_token);
    }


}
