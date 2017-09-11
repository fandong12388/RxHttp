package com.jethon.http.core.request;


import com.jethon.http.core.exception.VolleyError;

/**
 * time: 2015/8/19
 * description:
 *
 * @author fandong
 */
public interface RetryPolicy {
    int getCurrentTimeout();

    int getCurrentRetryCount();

    void retry(VolleyError var1) throws VolleyError;
}
