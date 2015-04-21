package in.championswimmer.lifelog;

import android.app.Application;

import com.sonymobile.lifelog.LifeLog;

/**
 * Created by championswimmer on 21/4/15.
 */
public class ApplicationWrapper extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LifeLog.initialise("5tAGGgWgda3fgIBAn6PGXm7SnmOhjAfv", "t0WZH49DUvQrbYGP", "https://tosc.in/championswimmer");
    }
}
