package com.sonymobile.lifelog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.sonymobile.lifelog.auth.GetAuthTokenTask;
import com.sonymobile.lifelog.auth.RefreshAuthTokenTask;
import com.sonymobile.lifelog.utils.SecurePreferences;

/**
 * Created by championswimmer on 21/4/15.
 */
public class LifeLog {

    public static final int LOGINACTIVITY_REQUEST_CODE = 2231;

    public static final String API_BASE_URL = "https://platform.lifelog.sonymobile.com";

    // file name of preference
    private static final String LIFELOG_PREFS = "lifelog_prefs";
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

    @Nullable
    public static String getAuthToken(Context context) {
        SecurePreferences preference = getSecurePreference(context);
        return preference.getString(GetAuthTokenTask.AUTH_ACCESS_TOKEN);
    }

    public static void initialise(String id, String secret, String callbackUrl) {
        client_id = id;
        client_secret = secret;
        callback_url = callbackUrl;
        setScope(Scopes.PROFILE_READ, Scopes.ACTIVITIES_READ, Scopes.LOCATIONS_READ);
    }

    public static String getScope() {
        return login_scope;
    }

    public static void setScope(String... scopes) {
        login_scope = "";
        for (String scope : scopes) {
            if (TextUtils.isEmpty(login_scope)) {
                login_scope = scope;
            } else {
                login_scope += "+" + scope;
            }
        }
    }

    public static SecurePreferences getSecurePreference(Context context) {
        return new SecurePreferences(context, LifeLog.LIFELOG_PREFS, LifeLog.getClient_secret(), true);
    }

    /**
     * Initiate login operation with {@link com.sonymobile.lifelog.LifeLog#LOGINACTIVITY_REQUEST_CODE},
     * callback will be delivered to {@link Activity#onActivityResult(int, int, Intent)}.
     */
    public static void doLogin(Activity activity) {
        Intent loginIntent = new Intent(activity, LoginActivity.class);
        activity.startActivityForResult(loginIntent, LOGINACTIVITY_REQUEST_CODE);
    }

    /**
     * Refresh access token if it is about to expire
     */
    public static void checkAuthentication(Context context, final OnAuthenticationChecked oac) {
        context = context.getApplicationContext();
        SecurePreferences securePreferences = getSecurePreference(context);
        if (securePreferences.containsKey(GetAuthTokenTask.AUTH_ACCESS_TOKEN)) {
            long expiresIn = Long
                    .valueOf(securePreferences.getString(GetAuthTokenTask.AUTH_EXPIRES_IN));
            long issueAt = Long
                    .valueOf(securePreferences.getString(GetAuthTokenTask.AUTH_ISSUE_AT));

            // if access token expires in next 2 minutes
            if (issueAt + expiresIn - System.currentTimeMillis() / 1000 > 120) {
                oac.onAuthChecked(true);
            } else {
                RefreshAuthTokenTask ratt = new RefreshAuthTokenTask(context);
                ratt.refreshAuth(new RefreshAuthTokenTask.OnAuthenticatedListener() {
                    @Override
                    public void onAuthenticated(String token) {
                        oac.onAuthChecked(true);
                    }
                });
            }
        }
        oac.onAuthChecked(false);
    }

    public interface OnAuthenticationChecked {
        void onAuthChecked (boolean authenticated);
    }

    public static class Scopes {
        public static final String PROFILE_READ = "lifelog.profile.read";
        public static final String ACTIVITIES_READ = "lifelog.activities.read";
        public static final String LOCATIONS_READ = "lifelog.locations.read";
    }
}
