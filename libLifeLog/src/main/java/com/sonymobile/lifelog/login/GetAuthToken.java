package com.sonymobile.lifelog.login;

import android.os.AsyncTask;
import android.util.Log;

import com.sonymobile.lifelog.LifeLog;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by championswimmer on 21/4/15.
 */
public class GetAuthToken extends AsyncTask<String, Void, Void> {
    
    public static final String TAG = "LifeLog:GetAuthToken";

    static String PARAM_CLIENT_ID = "client_id";
    static String PARAM_CLIENT_SECRET = "client_secret";
    static String PARAM_GRANT_TYPE = "grant_type";
    static String PARAM_CODE = "code";


    @Override
    protected Void doInBackground(String... authCode) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("https://platform.lifelog.sonymobile.com/oauth/2/token");

        List<NameValuePair> postParams = new ArrayList<NameValuePair>(4);
        postParams.add(new BasicNameValuePair(PARAM_CLIENT_ID, LifeLog.getClient_id()));
        postParams.add(new BasicNameValuePair(PARAM_CLIENT_SECRET, LifeLog.getClient_secret()));
        postParams.add(new BasicNameValuePair(PARAM_GRANT_TYPE, "authorization_code"));
        postParams.add(new BasicNameValuePair(PARAM_CODE, authCode[0]));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(postParams));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpResponse httpResponse;

        try {
            httpResponse = httpClient.execute(httpPost);
            Log.d(TAG, httpResponse.getStatusLine().getStatusCode() + "");
            Log.d(TAG, httpResponse.getStatusLine().getReasonPhrase() + "");
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: Do something if cannot get auth token
        }



        return null;
    }


}
