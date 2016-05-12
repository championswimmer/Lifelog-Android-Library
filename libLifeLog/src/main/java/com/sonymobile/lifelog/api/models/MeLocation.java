package com.sonymobile.lifelog.api.models;

import com.sonymobile.lifelog.utils.ISO8601Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;

/**
 * Created by championswimmer on 11/5/16.
 */
public class MeLocation {

    String id = "";
    sourceClass source = new sourceClass();
    String startTime = "";
    String endTime = "";
    positionClass position = new positionClass();
    int altitude = 0;
    int accuracy = 0;

    public MeLocation(JSONObject jobj) throws JSONException {
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

    public static class sourceClass {
        String name = "";
        String type = "";
        String id = "";
    }

    static class positionClass {
        double latitude = 0;
        double longitude = 0;
    }

}
