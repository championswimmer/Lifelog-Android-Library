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

import java.nio.charset.Charset;

/**
 * Created by championswimmer on 21/4/15.
 */
public class RefreshAuthTokenTask {

    public static final String TAG = "LifeLog:RefreshAuth";
    public static final String OAUTH2_URL = "https://platform.lifelog.sonymobile.com/oauth/2/refresh_token";
    public static final String AUTH_ACCESS_TOKEN = "access_token";
    public static final String AUTH_ISSUED_AT = "issued_at";
    public static final String AUTH_EXPIRES_IN = "expires_in";
    public static final String AUTH_EXPIRES = "expires";
    public static final String AUTH_REFRESH_TOKEN = "refresh_token";
    static String PARAM_CLIENT_ID = "client_id";
    static String PARAM_CLIENT_SECRET = "client_secret";
    static String PARAM_GRANT_TYPE = "grant_type";
    static String PARAM_REFRESH_TOKEN = "refresh_token";
    private Context mContext;
    private OnAuthenticatedListener onAuthenticatedListener;

    public RefreshAuthTokenTask(Context context) {
        this.mContext = context;
    }

    public void refreshAuth(OnAuthenticatedListener oal) {
        onAuthenticatedListener = oal;
        final SecurePreferences spref = LifeLog.getSecurePreference(mContext);

        final String refreshAuthBody =
                PARAM_CLIENT_ID + "=" + LifeLog.getClient_id() + "&"
                        + PARAM_CLIENT_SECRET + "=" + LifeLog.getClient_secret() + "&"
                        + PARAM_GRANT_TYPE + "=" + "refresh_token" + "&"
                        + PARAM_REFRESH_TOKEN + "=" + spref.getString(AUTH_REFRESH_TOKEN);

        Log.d(TAG, refreshAuthBody);

        JsonObjectRequest authRequest = new JsonObjectRequest(Request.Method.POST,
                OAUTH2_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jObj) {
                        try {

                            spref.put(AUTH_ACCESS_TOKEN, jObj.getString(AUTH_ACCESS_TOKEN));
                            spref.put(AUTH_EXPIRES_IN, jObj.getString(AUTH_EXPIRES_IN));
                            spref.put(AUTH_EXPIRES, jObj.getString(AUTH_EXPIRES));
                            spref.put(AUTH_ISSUED_AT, jObj.getString(AUTH_ISSUED_AT));
                            spref.put(AUTH_REFRESH_TOKEN, jObj.getString(AUTH_REFRESH_TOKEN));
                            Log.d(TAG, jObj.getString(AUTH_ACCESS_TOKEN));
                            Log.d(TAG, jObj.getString(AUTH_EXPIRES));
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
