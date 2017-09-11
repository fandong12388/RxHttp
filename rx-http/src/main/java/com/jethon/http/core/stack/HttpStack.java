package com.jethon.http.core.stack;

import com.jethon.http.core.exception.VolleyError;
import com.jethon.http.core.request.Request;
import com.jethon.http.core.response.NetworkResponse;

import java.io.IOException;
import java.util.Map;


/**
 * time: 2015/8/19
 * description:
 *
 * @author fandong
 */
public interface HttpStack {
    NetworkResponse performRequest(Request<?> request, Map<String, String> params) throws IOException, VolleyError;
}
