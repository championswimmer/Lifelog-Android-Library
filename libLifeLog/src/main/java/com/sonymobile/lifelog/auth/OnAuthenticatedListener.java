package com.sonymobile.lifelog.auth;

public interface OnAuthenticatedListener {
    void onAuthenticated();

    void onError(Exception e);
}