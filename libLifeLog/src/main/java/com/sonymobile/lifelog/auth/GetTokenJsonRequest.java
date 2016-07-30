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

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

/* package */ class GetTokenJsonRequest extends JsonObjectRequest {
    private static final String TAG = GetTokenJsonRequest.class.getSimpleName();
    private final String mBody;

    public GetTokenJsonRequest(final Context context, final String url, final String body, final OnAuthenticatedListener listener) {
        super(Request.Method.POST, url, null, new ResponseListener(context, listener) , new ErrorListener(context, listener));
        mBody = body;
    }

    @Override
    public String getBodyContentType() {
        return String.format("application/x-www-form-urlencoded; charset=%s", new Object[]{"utf-8"});
    }

    @Override
    public byte[] getBody() {
        return mBody.getBytes(Charset.forName("utf-8"));
    }

    private static class ResponseListener implements Response.Listener<JSONObject> {
        private final Context mContext;
        private final OnAuthenticatedListener mListener;

        private ResponseListener(final Context context, final OnAuthenticatedListener listener) {
            super();
            mContext = context;
            mListener = listener;
        }

        @Override
        public void onResponse(JSONObject jObj) {
            try {
                if (Debug.isDebuggable(mContext)) {
                    Log.v(TAG, "json from server: " + jObj);
                    Log.d(TAG, "access token: " + jObj.getString(AuthTokenConstants.AUTH_ACCESS_TOKEN));
                    Log.d(TAG, "refresh token: " + jObj.getString(AuthTokenConstants.AUTH_REFRESH_TOKEN));
                }
                SecurePreferences spref = LifeLog.getSecurePreference(mContext);
                spref.put(AuthTokenConstants.AUTH_TOKEN_TYPE, jObj.getString(AuthTokenConstants.AUTH_TOKEN_TYPE));
                spref.put(AuthTokenConstants.AUTH_EXPIRES_IN, jObj.getString(AuthTokenConstants.AUTH_EXPIRES_IN));
                spref.put(AuthTokenConstants.AUTH_REFRESH_TOKEN, jObj.getString(AuthTokenConstants.AUTH_REFRESH_TOKEN));
                spref.put(AuthTokenConstants.AUTH_REFRESH_TOKEN_EXPIRES_IN,
                          jObj.getString(AuthTokenConstants.AUTH_REFRESH_TOKEN_EXPIRES_IN));
                spref.put(AuthTokenConstants.AUTH_ACCESS_TOKEN, jObj.getString(AuthTokenConstants.AUTH_ACCESS_TOKEN));
                spref.put(AuthTokenConstants.AUTH_ISSUE_AT, String.valueOf(System.currentTimeMillis() / 1000));
                if (mListener != null) {
                    mListener.onAuthenticated();
                }
            } catch (JSONException e) {
                if (Debug.isDebuggable(mContext)) {
                    Log.w(TAG, "JSONException", e);
                }
                if (mListener != null) {
                    mListener.onError(e);
                }
            }
        }
    }

    private static class ErrorListener implements Response.ErrorListener {
        private final Context mContext;
        private final OnAuthenticatedListener mListener;

        private ErrorListener(final Context Context, final OnAuthenticatedListener listener) {
            mContext = Context;
            mListener = listener;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            if (Debug.isDebuggable(mContext)) {
                Log.w(TAG, "VolleyError: " + new String(error.networkResponse.data), error);
            }
            if (mListener != null) {
                mListener.onError(error);
            }
        }
    }
}
