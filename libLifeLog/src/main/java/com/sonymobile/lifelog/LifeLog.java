package com.sonymobile.lifelog;

import android.app.Activity;
import android.content.Intent;

import com.sonymobile.lifelog.login.LoginActivity;

/**
 * Created by championswimmer on 21/4/15.
 */
public class LifeLog {

    public static final int LOGIN_REQUEST_CODE = 2231;

    public static class Scopes {
        public static final String PROFILE_READ = "lifelog.profile.read";
        public static final String ACTIVITIES_READ = "lifelog.activities.read";
        public static final String LOCATIONS_READ = "lifelog.locations.read";
    }

    static String client_id = "";
    static String client_secret = "";
    static String login_scope = "";
    static String callback_url = "";

    public static String getClient_id() {
        return client_id;
    }

    public static String getClient_secret() {
        return client_secret;
    }

    public static String getCallback_url() {
        return callback_url;
    }

    public static void initialise (String id, String secret, String callbackUrl) {
        client_id = id;
        client_secret = secret;
        callback_url = callbackUrl;
        setScope(Scopes.PROFILE_READ, Scopes.ACTIVITIES_READ, Scopes.LOCATIONS_READ);
    }

    public static void setScope (String... scopes){
        login_scope = "";
        for (String scope : scopes){
            login_scope += "+" + scope;
        }
    }

    public static String getScope() {
        return login_scope;
    }

    public static boolean doLogin (Activity activity) {
        Intent loginIntent = new Intent(activity, LoginActivity.class);
        activity.startActivityForResult(loginIntent, LOGIN_REQUEST_CODE);
        return true; //TODO: return success of login
    }
}
