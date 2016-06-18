package com.sonymobile.lifelog.api.requests;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.sonymobile.lifelog.LifeLog;
import com.sonymobile.lifelog.api.models.Me;
import com.sonymobile.lifelog.utils.Debug;
import com.sonymobile.lifelog.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by championswimmer on 11/5/16.
 */
public class MeRequest {

    public static final String TAG = "LifeLog:MeRequest";
    static String API_URL = LifeLog.API_BASE_URL + "/users/me";

    Gson gson;

    private MeRequest() {

    }

    public static MeRequest prepareRequest() {
        return new MeRequest();
    }

    public void get(final Context context, final OnMeFetched omf) {
        if (Debug.isDebuggable(context)) {
            Log.v(TAG, "get called");
        }
        final Context appContext = context.getApplicationContext();

        LifeLog.checkAuthentication(appContext, new LifeLog.OnAuthenticationChecked() {
            @Override
            public void onAuthChecked(boolean authenticated) {
                if (authenticated) {
                    callMeApi(appContext, omf);
                }
            }
        });
    }

    private void callMeApi(final Context appContext, final OnMeFetched omf) {
        String requestUrl = API_URL;

        final JsonObjectRequest meRequest = new AuthedJsonObjectRequest(appContext,
                Request.Method.GET, requestUrl, (JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.v(TAG, jsonObject.toString());
                        try {
                            JSONArray resultArray = jsonObject.getJSONArray("result");
                            JSONObject meObject = resultArray.getJSONObject(0);

                            gson = new Gson();
                            Me meData = gson.fromJson(meObject.toString(), Me.class);

                            omf.onMeFetched(meData);
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
        );
        VolleySingleton.getInstance(appContext).addToRequestQueue(meRequest);

    }

    public interface OnMeFetched {
        void onMeFetched(Me meData);
    }


}
