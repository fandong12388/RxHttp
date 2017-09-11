package com.jethon.http.core.network;


import com.jethon.http.core.exception.VolleyError;
import com.jethon.http.core.request.Request;
import com.jethon.http.core.response.NetworkResponse;

/**
 * time: 2015/8/19
 * description:
 *
 * @author fandong
 */
public interface Network {

    /**
     * 发送请求，包含了retry机制
     *
     * @param request 请求实体
     * @return
     * @throws VolleyError
     */
    NetworkResponse performRequest(Request<?> request) throws VolleyError;
}
