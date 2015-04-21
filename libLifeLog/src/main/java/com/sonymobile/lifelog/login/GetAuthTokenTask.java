package com.sonymobile.lifelog.login;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sonymobile.lifelog.LifeLog;
import com.sonymobile.lifelog.utils.SecurePreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by championswimmer on 21/4/15.
 */
public class GetAuthTokenTask {

    private Context mContext;
    RequestQueue loginQueue;

    private OnAuthenticatedListener onAuthenticatedListener;

    public GetAuthTokenTask(Context context) {
        this.mContext = context;
        loginQueue = Volley.newRequestQueue(mContext);
    }

    public static final String TAG = "LifeLog:GetAuthToken";

    public static final String OAUTH2_URL = "https://platform.lifelog.sonymobile.com/oauth/2/token";

    static String PARAM_CLIENT_ID = "client_id";
    static String PARAM_CLIENT_SECRET = "client_secret";
    static String PARAM_GRANT_TYPE = "grant_type";
    static String PARAM_CODE = "code";

    public static final String AUTH_ACCESS_TOKEN = "access_token";
    public static final String AUTH_ISSUED_AT = "issued_at";
    public static final String AUTH_EXPIRES_IN = "expires_in";
    public static final String AUTH_REFRESH_TOKEN = "refresh_token";

    protected void getAuth(final String authCode, OnAuthenticatedListener oal) {
        onAuthenticatedListener = oal;

        final String authRequestBody =
                PARAM_CLIENT_ID + "=" + LifeLog.getClient_id() + "&"
                        + PARAM_CLIENT_SECRET + "=" + LifeLog.getClient_secret() + "&"
                        + PARAM_GRANT_TYPE + "=" + "authorization_code" + "&"
                        + PARAM_CODE + "=" + authCode;

        JsonObjectRequest authRequest = new JsonObjectRequest(Request.Method.POST,
                OAUTH2_URL,
                authRequestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jObj) {
                        try {
                            SecurePreferences spref = new SecurePreferences(mContext,
                                    LifeLog.LIFELOG_PREFS, LifeLog.getClient_secret(), true);
                            spref.put(AUTH_ACCESS_TOKEN, jObj.getString(AUTH_ACCESS_TOKEN));
                            spref.put(AUTH_EXPIRES_IN, jObj.getString(AUTH_EXPIRES_IN));
                            spref.put(AUTH_ISSUED_AT, jObj.getString(AUTH_ISSUED_AT));
                            spref.put(AUTH_REFRESH_TOKEN, jObj.getString(AUTH_REFRESH_TOKEN));
                            Log.d(TAG, jObj.getString(AUTH_ACCESS_TOKEN));
                            Log.d(TAG, jObj.getString(AUTH_EXPIRES_IN));
                            Log.d(TAG, jObj.getString(AUTH_REFRESH_TOKEN));
                            if (onAuthenticatedListener != null) {
                                onAuthenticatedListener.onAuthenticated();
                            }
                        } catch (JSONException e) {
                            //TODO: handle malformed json
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }
        ) {
            @Override
            public String getBodyContentType() {
                return String.format("application/x-www-form-urlencoded; charset=%s", new Object[]{"utf-8"});
            }
        };
        loginQueue.add(authRequest);
    }

    public interface OnAuthenticatedListener {
        void onAuthenticated();
    }


}
