package com.jethon.http.core.network;


import com.jethon.http.core.cache.Cache;
import com.jethon.http.core.exception.VolleyError;
import com.jethon.http.core.pool.ByteArrayPool;
import com.jethon.http.core.request.Request;
import com.jethon.http.core.request.RetryPolicy;
import com.jethon.http.core.stack.HttpStack;
import com.jethon.http.utils.DateUtils;

import java.util.Date;
import java.util.Map;


/**
 * time: 15/7/10
 * description: 对网络进行配置
 *
 * @author fandong
 */
public abstract class BaseNetwork implements Network {
    protected static int SLOW_REQUEST_THRESHOLD_MS = 5000;
    protected static int DEFAULT_POOL_SIZE = 4096;
    protected HttpStack mHttpStack;
    protected ByteArrayPool mPool;

    public BaseNetwork(HttpStack httpStack) {
        this(httpStack, new ByteArrayPool(DEFAULT_POOL_SIZE));
    }

    public BaseNetwork(HttpStack httpStack, ByteArrayPool pool) {
        this.mHttpStack = httpStack;
        this.mPool = pool;
    }


//    protected void logSlowRequests(long requestLifetime, Request<?> request, byte[] responseContents, StatusLine statusLine) {
//        if (requestLifetime > (long) SLOW_REQUEST_THRESHOLD_MS) {
//            HttpLogger.e(String.format("HTTP response for request=<%s> [lifetime=%d], [size=%s], [rc=%d], [retryCount=%s]",
//                    new Object[]{request, Long.valueOf(requestLifetime), responseContents != null ? Integer.valueOf(responseContents.length) : "null", Integer.valueOf(statusLine.getStatusCode()), Integer.valueOf(request.getRetryPolicy().getCurrentRetryCount())}));
//        }
//
//    }

    protected void attemptRetryOnException(Request<?> request,
                                           VolleyError exception) throws VolleyError {
        RetryPolicy retryPolicy = request.getRetryPolicy();
        try {
            retryPolicy.retry(exception);
        } catch (VolleyError e) {
            throw e;
        }
    }

    protected void addCacheHeaders(Map<String, String> headers, Cache.Entry entry) {
        if (entry != null) {
            if (entry.etag != null) {
                headers.put("If-None-Match", entry.etag);
            }
            if (entry.serverDate > 0L) {
                Date refTime = new Date(entry.serverDate);
                headers.put("If-Modified-Since", DateUtils.formatDate(refTime));
            }
        }
    }

    protected void addGzipHeader(Map<String, String> headers) {
        headers.put("Accept-Encoding", "gzip");
    }

//
//    protected static Map<String, String> convertHeaders(Header[] headers) {
//        TreeMap<String, String> result = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
//        for (int i = 0; i < headers.length; ++i) {
//            result.put(headers[i].getName(), headers[i].getValue());
//        }
//        return result;
//    }
}

