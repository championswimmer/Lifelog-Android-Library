package com.sonymobile.lifelog.api.requests;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sonymobile.lifelog.LifeLog;
import com.sonymobile.lifelog.api.models.Me;
import com.sonymobile.lifelog.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by championswimmer on 11/5/16.
 */
public class MeRequest {

    public static final String TAG = "LifeLog:MeRequest";
    static JsonObjectRequest lastMeRequest;
    static String API_URL = LifeLog.API_BASE_URL + "/users/me";
    String authToken;

    Gson gson;

    private MeRequest() {

    }

    public static MeRequest prepareRequest() {
        return new MeRequest();
    }

    public void get(final Context context, final OnMeFetched omf) {
        Log.v(TAG, "get called");

        LifeLog.checkAuthentication(context, new LifeLog.OnAuthenticationChecked() {
            @Override
            public void onAuthChecked(boolean authenticated) {
                authToken = LifeLog.getAuthToken(context);
            }
        });
        String requestUrl = API_URL;

        final JsonObjectRequest meRequest = new JsonObjectRequest(Request.Method.GET,
                requestUrl, (JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.v(TAG, jsonObject.toString());
                        try {
                            if (jsonObject.has("error")) {
                                if (jsonObject.getJSONObject("error").getString("code").contains("401")) {
                                    LifeLog.checkAuthentication(context, new LifeLog.OnAuthenticationChecked() {
                                        @Override
                                        public void onAuthChecked(boolean authenticated) {
                                            if (authenticated && (lastMeRequest != null))
                                                VolleySingleton.getInstance(context).addToRequestQueue(lastMeRequest);
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            JSONArray resultArray = jsonObject.getJSONArray("result");
                            JSONObject meObject = resultArray.getJSONObject(0);

                            gson = new Gson();
                            Me meData = gson.fromJson(meObject.toString(), Me.class);

                            omf.onMeFetched(meData);

                            lastMeRequest = null;
                        } catch (JSONException e) {
                            e.printStackTrace();
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<>(5);
                headerMap.put("Authorization", "Bearer " + authToken);
                headerMap.put("Accept", "application/json");
                //headerMap.put("Accept-Encoding", "gzip");
                //headerMap.put("Content-Encoding", "gzip");
                return headerMap;
            }
        };
        lastMeRequest = meRequest;
        VolleySingleton.getInstance(context).addToRequestQueue(meRequest);

    }

    public interface OnMeFetched {
        void onMeFetched(Me meData);
    }


}
