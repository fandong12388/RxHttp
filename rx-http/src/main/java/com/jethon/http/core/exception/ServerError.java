package com.jethon.http.core.exception;


import com.jethon.http.core.response.NetworkResponse;

/**
 * time: 2015/8/19
 * description:
 *
 * @author fandong
 */
public class ServerError extends VolleyError {
    public ServerError(NetworkResponse networkResponse) {
        super(networkResponse);
    }

    public ServerError() {
    }
}
