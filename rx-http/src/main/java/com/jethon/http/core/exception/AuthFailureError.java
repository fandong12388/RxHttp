package com.jethon.http.core.exception;

import android.content.Intent;

import com.jethon.http.core.response.NetworkResponse;


/**
 * time: 2015/8/19
 * description:
 *
 * @author fandong
 */
public class AuthFailureError extends VolleyError {
    private Intent mResolutionIntent;

    public AuthFailureError() {
    }

    public AuthFailureError(Intent intent) {
        this.mResolutionIntent = intent;
    }

    public AuthFailureError(NetworkResponse response) {
        super(response);
    }

    public AuthFailureError(String message) {
        super(message);
    }

    public AuthFailureError(String message, Exception reason) {
        super(message, reason);
    }

    public Intent getResolutionIntent() {
        return this.mResolutionIntent;
    }

    public String getMessage() {
        return this.mResolutionIntent != null ? "User needs to (re)enter credentials." : super.getMessage();
    }
}