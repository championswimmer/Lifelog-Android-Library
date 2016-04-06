package com.sonymobile.lifelog.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

public final class Debug {

    public static final boolean isDebuggable(Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

}
