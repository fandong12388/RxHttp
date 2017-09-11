package com.jethon.http.core.network;

import android.os.SystemClock;

import com.jethon.http.core.cache.Cache;
import com.jethon.http.core.exception.AuthFailureError;
import com.jethon.http.core.exception.NetworkError;
import com.jethon.http.core.exception.NoConnectionError;
import com.jethon.http.core.exception.TimeoutError;
import com.jethon.http.core.exception.VolleyError;
import com.jethon.http.core.request.Request;
import com.jethon.http.core.response.NetworkResponse;
import com.jethon.http.core.stack.HttpStack;
import com.jethon.http.log.HttpLogger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Locale;


/**
 * time: 15/7/10
 * description: 网络请求类型
 *
 * @author fandong
 */
public class NetworkExecutor extends BaseNetwork {

    public NetworkExecutor(HttpStack httpStack) {
        super(httpStack);
    }

    public NetworkResponse performRequest(Request<?> request) throws VolleyError {
        long requestStart = SystemClock.elapsedRealtime();
        while (true) {
            NetworkResponse resultResponse = null;
//            byte[] responseContent = null;
//            Map responseHeaders = Collections.emptyMap();
            try {
                HashMap<String, String> map = new HashMap<String, String>();
                this.addCacheHeaders(map, request.getCacheEntry());
                this.addGzipHeader(map);
                if (request.getHeaders() != null) {
                    map.putAll(request.getHeaders());
                }
                resultResponse = this.mHttpStack.performRequest(request, map);
                if (resultResponse == null) {
                    throw new IOException("resultResponse is null!");
                }
                //StatusLine statusLine = customResponse.getStatusLine();
                int statusCode = resultResponse.getStatusCode();
                //responseHeaders = convertHeaders(customResponse.getAllHeaders());
                if (statusCode != 304) {
                    resultResponse.setNetworkTimeMs(SystemClock.elapsedRealtime() - requestStart);
                    String responseBody = new String(resultResponse.getData(), "UTF-8");
                    if (statusCode >= 200 && statusCode <= 299) {
                        HttpLogger.printResponse(request, resultResponse);
                        return resultResponse;
                    }
                    throw new IOException(responseBody);
                }
                Cache.Entry requestLifetime = request.getCacheEntry();
                resultResponse.setStatusCode(304);
                resultResponse.setNetworkTimeMs(SystemClock.elapsedRealtime() - requestStart);
                if (requestLifetime != null) {
                    resultResponse.addHeaders(requestLifetime.responseHeaders);
                }
                return resultResponse;
            } catch (SocketTimeoutException e) {
                HttpLogger.e(e);
                attemptRetryOnException(request, new TimeoutError());
            } catch (MalformedURLException e) {
                throw new RuntimeException("Bad URL " + request.getUrl(), e);
            } catch (IOException e) {
                if (resultResponse == null) {
                    throw new NoConnectionError(e);
                }
                int statusCode = resultResponse.getStatusCode();
                HttpLogger.e(String.format(Locale.getDefault(), "Unexpected response code %d for %s", statusCode, request.getUrl()));
                HttpLogger.e(e);
                if (resultResponse.getData() == null || resultResponse.getData().length <= 0) {
                    throw new NetworkError(resultResponse);
                }
                if (statusCode != 401 && statusCode != 403) {
                    return resultResponse;
                }
                attemptRetryOnException(request, new AuthFailureError(resultResponse));
            }
        }
    }


}
