package com.sonymobile.lifelog.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sonymobile.lifelog.LifeLog;
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

    public static final String TAG = "LifeLog:LocationAPI";


    public interface OnLocationFetched {
        void onLocationFetched (ArrayList<LifeLogLocation> locations);
    }


    String startTime, endTime;
    Integer limit;
    String authToken;

    static JsonObjectRequest lastLocationRequest;

    static String LOCATION_BASE_URL = LifeLog.API_BASE_URL + "/v1/users/me/locations";

    public LifeLogLocationAPI(Calendar start, Calendar end, Integer lim) {
        if (start != null) startTime = ISO8601Date.fromCalendar(start);
        if (end != null) endTime = ISO8601Date.fromCalendar(end);
        limit = lim;
    }

    public static LifeLogLocationAPI prepareRequest(Calendar start, Calendar end, Integer lim) {
        if (lim==null || lim > 500) {
            lim = 500;
        }
        return new LifeLogLocationAPI(start, end, lim);
    }
    public static LifeLogLocationAPI prepareRequest(Integer lim) {
        return prepareRequest(null, null, lim);
    }

    public void get(Context context, final OnLocationFetched olf) {
        final Context appContext = context.getApplicationContext();
        Log.v(TAG, "get called");
        final ArrayList<LifeLogLocation> locations = new ArrayList<>(limit);
        LifeLog.checkAuthentication(appContext, new LifeLog.OnAuthenticationChecked() {
            @Override
            public void onAuthChecked(boolean authenticated) {
                authToken = LifeLog.getAuthToken(appContext);
            }
        });
        String requestUrl = LOCATION_BASE_URL;
        String params = "";
        if (startTime != null) {
            params += "start_time="+startTime;
        }
        if (endTime != null) {
            params += "end_time="+endTime;
        }
        if (limit != null) {
            params += "limit="+limit;
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
                                    locations.add(new LifeLogLocation(resultArray.getJSONObject(i)));
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
