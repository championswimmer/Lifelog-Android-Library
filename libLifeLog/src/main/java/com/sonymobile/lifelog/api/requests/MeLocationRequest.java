package com.sonymobile.lifelog.api.requests;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sonymobile.lifelog.LifeLog;
import com.sonymobile.lifelog.api.models.MeLocation;
import com.sonymobile.lifelog.utils.Debug;
import com.sonymobile.lifelog.utils.ISO8601Date;
import com.sonymobile.lifelog.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by championswimmer on 21/4/15.
 */
public class MeLocationRequest {

    private static final String TAG = "LifeLog:LocationAPI";
    private String startTime, endTime;
    private Integer limit;

    private static final Uri LOCATION_BASE_URL =
            Uri.parse(LifeLog.API_BASE_URL).buildUpon().appendEncodedPath("users/me/locations").build();

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

    private void callLocationApi(final Context appContext, final OnLocationFetched olf) {
        final ArrayList<MeLocation> locations = new ArrayList<>(limit);

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

        final JsonObjectRequest locationRequest = new AuthedJsonObjectRequest(appContext,
                Request.Method.GET, uriBuilder.toString(), (JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if (Debug.isDebuggable(appContext)) {
                            Log.v(TAG, jsonObject.toString());
                        }
                        try {
                            JSONArray resultArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < resultArray.length(); i++) {
                                locations.add(new MeLocation(resultArray.getJSONObject(i)));
                            }
                            olf.onLocationFetched(locations);
                        } catch (JSONException e) {
                            if (Debug.isDebuggable(appContext)) {
                                Log.w(TAG, "JSONException", e);
                            }
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (Debug.isDebuggable(appContext)) {
                            Log.w(TAG, "VolleyError: " + new String(volleyError.networkResponse.data), volleyError);
                        }
                    }
                }
        );
        VolleySingleton.getInstance(appContext).addToRequestQueue(locationRequest);
    }

    public interface OnLocationFetched {
        void onLocationFetched(ArrayList<MeLocation> locations);
    }

}
