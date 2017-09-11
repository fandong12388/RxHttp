package com.jethon.http.core.response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * time: 2015/8/19
 * description:
 *
 * @author fandong
 */
public class NetworkResponse implements Serializable {
    private static final long serialVersionUID = -20150728102000L;
    private int statusCode;
    private byte[] data;
    private String msg;
    private Map<String, String> headers;
    private boolean notModified;
    private long networkTimeMs;

    public NetworkResponse(int statusCode, byte[] data, Map<String, String> headers, boolean notModified, long networkTimeMs) {
        this.statusCode = statusCode;
        this.data = data;
        this.headers = headers;
        this.notModified = notModified;
        this.networkTimeMs = networkTimeMs;
    }

    public NetworkResponse(int statusCode, byte[] data, Map<String, String> headers, boolean notModified) {
        this(statusCode, data, headers, notModified, 0L);
    }

    public NetworkResponse(byte[] data) {
        this(200, data, null, false, 0L);
    }

    public NetworkResponse(int statusCode) {
        this(statusCode, null, null, false, 0L);
    }

    public NetworkResponse(byte[] data, Map<String, String> headers) {
        this(200, data, headers, false, 0L);
    }

    public void addHeader(String key, String value) {
        if (null == headers) {
            this.headers = new HashMap<String, String>();
        }
        this.headers.put(key, value);
    }

    public void addHeaders(Map<String, String> headers) {
        if (null == headers) {
            this.headers = new HashMap<String, String>();
        }
        this.headers.putAll(headers);
    }

    public String getHeader(String key) {
        if (null != headers) {
            return headers.get(key);
        }
        return null;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public boolean isNotModified() {
        return notModified;
    }

    public void setNotModified(boolean notModified) {
        this.notModified = notModified;
    }

    public long getNetworkTimeMs() {
        return networkTimeMs;
    }

    public void setNetworkTimeMs(long networkTimeMs) {
        this.networkTimeMs = networkTimeMs;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
