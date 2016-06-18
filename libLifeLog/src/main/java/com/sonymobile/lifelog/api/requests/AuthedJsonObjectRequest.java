package com.sonymobile.lifelog.api.requests;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sonymobile.lifelog.LifeLog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/* package */ class AuthedJsonObjectRequest extends JsonObjectRequest {
    private final Context mContext;

    public AuthedJsonObjectRequest(Context context, int method, String url, JSONObject jsonRequest, Response
            .Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        mContext = context.getApplicationContext();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headerMap = new HashMap<>(super.getHeaders());
        headerMap.put("Authorization", "Bearer " + LifeLog.getAuthToken(mContext));
        headerMap.put("Accept", "application/json");
        return headerMap;
    }
}
