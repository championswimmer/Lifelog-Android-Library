package com.sonymobile.lifelog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sonymobile.lifelog.auth.GetAuthTokenTask;
import com.sonymobile.lifelog.auth.RefreshAuthTokenTask;
import com.sonymobile.lifelog.utils.SecurePreferences;

/**
 * Created by championswimmer on 21/4/15.
 */
public class LifeLog {

    public static final int LOGINACTIVITY_REQUEST_CODE = 2231;

    public static final String LIFELOG_PREFS = "lifelog_prefs";

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

    public static void initialise(String id, String secret, String callbackUrl) {
        client_id = id;
        client_secret = secret;
        callback_url = callbackUrl;
        setScope(Scopes.PROFILE_READ, Scopes.ACTIVITIES_READ, Scopes.LOCATIONS_READ);
    }

    public static void setScope(String... scopes) {
        login_scope = "";
        for (String scope : scopes) {
            login_scope += "+" + scope;
        }
    }

    public static String getScope() {
        return login_scope;
    }

    public static boolean doLogin(Activity activity) {
        Intent loginIntent = new Intent(activity, LoginActivity.class);
        activity.startActivityForResult(loginIntent, LOGINACTIVITY_REQUEST_CODE);
        return true; //TODO: return success of login
    }

    public static boolean isAuthenticated (Context context) {
        SecurePreferences securePreferences = new SecurePreferences(
                context,
                LIFELOG_PREFS,
                getClient_secret(),
                true
        );
        if (securePreferences.containsKey(GetAuthTokenTask.AUTH_ACCESS_TOKEN)) {
            long expires = Long.valueOf(securePreferences.getString(GetAuthTokenTask.AUTH_EXPIRES));
            long expires_in = expires - System.currentTimeMillis();
            if (expires_in > 120000) {
                return true;
            } else {
                RefreshAuthTokenTask ratt = new RefreshAuthTokenTask(context);
                ratt.refreshAuth(new RefreshAuthTokenTask.OnAuthenticatedListener() {
                    @Override
                    public void onAuthenticated() {

                    }
                });
            }
        }
        return false;
    }
}
