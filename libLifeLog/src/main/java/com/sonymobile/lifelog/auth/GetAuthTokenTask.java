package com.sonymobile.lifelog.auth;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sonymobile.lifelog.LifeLog;
import com.sonymobile.lifelog.utils.SecurePreferences;
import com.sonymobile.lifelog.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by championswimmer on 21/4/15.
 */
public class GetAuthTokenTask {

    public static final String TAG = "LifeLog:GetAuthToken";
    public static final String OAUTH2_URL = "https://platform.lifelog.sonymobile.com/oauth/2/token";
    public static final String AUTH_ACCESS_TOKEN = "access_token";
    public static final String AUTH_EXPIRES_IN = "expires_in";
    public static final String AUTH_REFRESH_TOKEN = "refresh_token";
    public static final String AUTH_TOKEN_TYPE = "token_type";
    public static final String AUTH_REFRESH_TOKEN_EXPIRES_IN = "refresh_token_expires_in";
    static String PARAM_CLIENT_ID = "client_id";
    static String PARAM_CLIENT_SECRET = "client_secret";
    static String PARAM_GRANT_TYPE = "grant_type";
    static String PARAM_CODE = "code";
    private Context mContext;
    private OnAuthenticatedListener onAuthenticatedListener;
    public GetAuthTokenTask(Context context) {
        this.mContext = context;
    }

    public void getAuth(final String authCode, OnAuthenticatedListener oal) {
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
                            spref.put(AUTH_TOKEN_TYPE, jObj.getString(AUTH_TOKEN_TYPE));
                            spref.put(AUTH_REFRESH_TOKEN, jObj.getString(AUTH_REFRESH_TOKEN));
                            spref.put(AUTH_REFRESH_TOKEN_EXPIRES_IN,
                                      jObj.getString(AUTH_REFRESH_TOKEN_EXPIRES_IN));
                            Log.d(TAG, jObj.getString(AUTH_ACCESS_TOKEN));
                            Log.d(TAG, jObj.getString(AUTH_REFRESH_TOKEN));
                            if (onAuthenticatedListener != null) {
                                onAuthenticatedListener.onAuthenticated(jObj.getString(AUTH_ACCESS_TOKEN));
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
        VolleySingleton.getInstance(mContext).addToRequestQueue(authRequest);
    }

    public interface OnAuthenticatedListener {
        void onAuthenticated(String auth_token);
    }


}
