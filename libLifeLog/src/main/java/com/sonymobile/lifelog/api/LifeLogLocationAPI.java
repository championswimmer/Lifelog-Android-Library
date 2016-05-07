package com.sonymobile.lifelog.api;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sonymobile.lifelog.LifeLog;
import com.sonymobile.lifelog.utils.Debug;
import com.sonymobile.lifelog.utils.ISO8601Date;
import com.sonymobile.lifelog.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by championswimmer on 21/4/15.
 */
public class LifeLogLocationAPI {
    private static final String TAG = "LifeLog:LocationAPI";

    public interface OnLocationFetched {
        void onLocationFetched (ArrayList<LifeLogLocation> locations);
    }

    private String startTime, endTime;
    private int limit;

    private static final Uri LOCATION_BASE_URL =
            Uri.parse(LifeLog.API_BASE_URL).buildUpon().appendEncodedPath("/v1/users/me/locations").build();

    public LifeLogLocationAPI(@Nullable Calendar start, @Nullable Calendar end, int lim) {
        if (start != null) startTime = ISO8601Date.fromCalendar(start);
        if (end != null) endTime = ISO8601Date.fromCalendar(end);
        limit = lim;
    }

    public static LifeLogLocationAPI prepareRequest(Calendar start, Calendar end, int lim) {
        if (lim > 500) {
            lim = 500;
        }
        return new LifeLogLocationAPI(start, end, lim);
    }
    public static LifeLogLocationAPI prepareRequest(int lim) {
        return prepareRequest(null, null, lim);
    }

    public void get(Context context, final OnLocationFetched olf) {
        final Context appContext = context.getApplicationContext();
        if (Debug.isDebuggable(appContext)) {
            Log.v(TAG, "get called");
        }
        LifeLog.checkAuthentication(appContext, new LifeLog.OnAuthenticationChecked() {
            @Override
            public void onAuthChecked(boolean authenticated) {
                if (authenticated) {
                    callLocationApi(appContext, olf);
                }
            }
        });
    }

    private void callLocationApi(final Context context, final OnLocationFetched olf) {
        final String authToken = LifeLog.getAuthToken(context);

        Uri.Builder uriBuilder = LOCATION_BASE_URL.buildUpon();
        if (!TextUtils.isEmpty(startTime)) {
            uriBuilder.appendQueryParameter("start_time", startTime);
        }
        if (!TextUtils.isEmpty(endTime)) {
            uriBuilder.appendQueryParameter("end_time", endTime);
        }
        if (limit > 0) {
            uriBuilder.appendQueryParameter("limit", String.valueOf(limit));
        }

        final JsonObjectRequest locationRequest = new JsonObjectRequest(Request.Method.GET,
                uriBuilder.toString(), (JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if (Debug.isDebuggable(context)) {
                            Log.v(TAG, jsonObject.toString());
                        }

                        try {
                            final ArrayList<LifeLogLocation> locations = new ArrayList<>(limit);
                            JSONArray resultArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < resultArray.length(); i++) {
                                    locations.add(new LifeLogLocation(resultArray.getJSONObject(i)));
                            }
                            olf.onLocationFetched(locations);
                        } catch (JSONException e) {
                            if (Debug.isDebuggable(context)) {
                                Log.w(TAG, "JSONException", e);
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (Debug.isDebuggable(context)) {
                            Log.w(TAG, "VolleyError: " + new String(volleyError.networkResponse.data), volleyError);
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> originalHeader = super.getHeaders();
                Map<String, String> headerMap = new HashMap<>();
                headerMap.putAll(originalHeader);

                headerMap.put("Authorization", "Bearer " + authToken);
                headerMap.put("Accept", "application/json");
                return headerMap;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(locationRequest);
    }

    public static class LifeLogLocation {
        public static class sourceClass {
            String name = "";
            String type = "";
            String id = "";
        }
        static class positionClass {
            double latitude = 0;
            double longitude = 0;
        }

        public LifeLogLocation(JSONObject jobj) throws JSONException {
            id = jobj.getString("id");
            source = new sourceClass();
            JSONArray jarr = jobj.getJSONArray("sources");
            for (int i = 0; i < jarr.length(); i++) {
                try {
                    //Log.d(TAG, "jarr " + jarr.getJSONObject(i).toString());
                    source.id = jarr.getJSONObject(i).getString("id");
                    source.name = jarr.getJSONObject(i).getString("name");
                    source.type = jarr.getJSONObject(i).getString("type");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            startTime = jobj.getString("startTime");
            endTime = jobj.getString("endTime");
            position.latitude = jobj.getJSONObject("position").getDouble("latitude");
            position.longitude = jobj.getJSONObject("position").getDouble("longitude");
            altitude = jobj.getInt("altitude");
            accuracy = jobj.getInt("accuracy");


        }

        String id = "";
        sourceClass source = new sourceClass();
        String startTime = "";
        String endTime = "";
        positionClass position = new positionClass();
        int altitude = 0;
        int accuracy = 0;

        public String getId() {
            return id;
        }

        public Calendar getStartTime() throws ParseException {
            return ISO8601Date.toCalendar(startTime);
        }

        public Calendar getEndTime() throws ParseException {
            return ISO8601Date.toCalendar(endTime);
        }

        public sourceClass getSource() {
            return source;
        }

        public int getAltitude() {
            return altitude;
        }
        public double getLatitude() {
            return position.latitude;
        }
        public double getLongitude() {
            return position.longitude;
        }

        public int getAccuracy() {
            return accuracy;
        }
    }

}
