package com.sonymobile.lifelog.auth;

public interface OnAuthenticatedListener {
    void onAuthenticated(String authToken);

    void onError(Exception e);
}