package com.sonymobile.lifelog.api.requests;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

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
    private String mStartTime, mEndTime;
    private Integer mLimit;

    private String mNextPage;

    private static final Uri LOCATION_BASE_URL =
            Uri.parse(LifeLog.API_BASE_URL).buildUpon().appendEncodedPath("users/me/locations").build();

    public MeLocationRequest(Calendar start, Calendar end, Integer lim) {
        if (start != null) mStartTime = ISO8601Date.fromCalendar(start);
        if (end != null) mEndTime = ISO8601Date.fromCalendar(end);
        mLimit = lim;
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
        Uri.Builder uriBuilder = LOCATION_BASE_URL.buildUpon();
        if (!TextUtils.isEmpty(mStartTime)) {
            uriBuilder.appendQueryParameter("start_time", mStartTime);
        }
        if (!TextUtils.isEmpty(mEndTime)) {
            uriBuilder.appendQueryParameter("end_time", mEndTime);
        }
        if (mLimit > 0) {
            uriBuilder.appendQueryParameter("limit", String.valueOf(mLimit));
        }

        final JsonObjectRequest locationRequest =
                new LocationApiRequest(appContext, uriBuilder.toString(), olf);
        VolleySingleton.getInstance(appContext).addToRequestQueue(locationRequest);
    }

    private class LocationApiRequest extends AuthedJsonObjectRequest {
        private LocationApiRequest(final Context context,
                                   final String url,
                                   final OnLocationFetched olf) {
            super(context,
                  Request.Method.GET,
                  url,
                  (JSONObject) null,
                  new Response.Listener<JSONObject>() {
                      @Override
                      public void onResponse(JSONObject jsonObject) {
                          ArrayList<MeLocation> locations = new ArrayList<>(mLimit);
                          if (Debug.isDebuggable(context)) {
                              Log.v(TAG, jsonObject.toString());
                          }
                          try {
                              JSONArray resultArray = jsonObject.getJSONArray("result");
                              for (int i = 0; i < resultArray.length(); i++) {
                                  locations.add(new MeLocation(resultArray.getJSONObject(i)));
                              }

                              JSONArray links = jsonObject.optJSONArray("links");
                              if (links != null) {
                                  for (int i = 0; i < links.length(); i++) {
                                      JSONObject object = links.getJSONObject(i);
                                      if (TextUtils.equals("next", object.optString("rel"))) {
                                          String href = object.optString("href");
                                          if (!TextUtils.isEmpty(href) && URLUtil
                                                  .isNetworkUrl(href)) {
                                              mNextPage = href;
                                          }
                                      }
                                  }
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
                  });
        }
    }

    public interface OnLocationFetched {
        void onLocationFetched(ArrayList<MeLocation> locations);
    }

}
