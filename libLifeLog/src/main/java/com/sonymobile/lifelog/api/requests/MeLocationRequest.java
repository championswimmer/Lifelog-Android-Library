package com.sonymobile.lifelog.api.requests;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sonymobile.lifelog.LifeLog;
import com.sonymobile.lifelog.api.models.MeLocation;
import com.sonymobile.lifelog.utils.ISO8601Date;
import com.sonymobile.lifelog.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by championswimmer on 21/4/15.
 */
public class MeLocationRequest {

    public static final String TAG = "LifeLog:LocationAPI";
    static JsonObjectRequest lastLocationRequest;
    static String API_URL = LifeLog.API_BASE_URL + "/users/me/locations";
    String startTime, endTime;
    Integer limit;
    String authToken;

    public MeLocationRequest(Calendar start, Calendar end, Integer lim) {
        if (start != null) startTime = ISO8601Date.fromCalendar(start);
        if (end != null) endTime = ISO8601Date.fromCalendar(end);
        limit = lim;
    }

    public static MeLocationRequest prepareRequest(Calendar start, Calendar end, Integer lim) {
        if (lim == null || lim > 500) {
            lim = 500;
        }
        return new MeLocationRequest(start, end, lim);
    }

    public static MeLocationRequest prepareRequest(Integer lim) {
        return prepareRequest(null, null, lim);
    }

    public void get(Context context, final OnLocationFetched olf) {
        final Context appContext = context.getApplicationContext();
        Log.v(TAG, "get called");
        final ArrayList<MeLocation> locations = new ArrayList<>(limit);
        LifeLog.checkAuthentication(appContext, new LifeLog.OnAuthenticationChecked() {
            @Override
            public void onAuthChecked(boolean authenticated) {
                authToken = LifeLog.getAuthToken(appContext);
            }
        });
        String requestUrl = API_URL;
        String params = "";
        if (startTime != null) {
            params += "start_time=" + startTime;
        }
        if (endTime != null) {
            params += "end_time=" + endTime;
        }
        if (limit != null) {
            params += "limit=" + limit;
        }
        if (params.length() > 1) {
            requestUrl += "?" + params;
        }
        final JsonObjectRequest locationRequest = new JsonObjectRequest(Request.Method.GET,
                requestUrl, (JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.v(TAG, jsonObject.toString());
                        try {
                            if (jsonObject.has("error")) {
                                if (jsonObject.getJSONObject("error").getString("code").contains("401")) {
                                    LifeLog.checkAuthentication(appContext, new LifeLog.OnAuthenticationChecked() {
                                        @Override
                                        public void onAuthChecked(boolean authenticated) {
                                            if (authenticated && (lastLocationRequest != null))
                                                VolleySingleton.getInstance(appContext).addToRequestQueue(lastLocationRequest);
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            JSONArray resultArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < resultArray.length(); i++) {
                                locations.add(new MeLocation(resultArray.getJSONObject(i)));
                            }
                            olf.onLocationFetched(locations);
                            lastLocationRequest = null;
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
        lastLocationRequest = locationRequest;
        VolleySingleton.getInstance(appContext).addToRequestQueue(locationRequest);
    }

    public interface OnLocationFetched {
        void onLocationFetched(ArrayList<MeLocation> locations);
    }

}
