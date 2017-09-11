package com.jethon.http.bean;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * time: 15/6/15
 * description: 封装了请求参数
 *
 * @author fandong
 */
public class HttpParams implements Serializable {
    private static final String PARAMS_BLANK = "params_blank";

    private static final long serialVersionUID = -6376078803512393464L;

    private Map<String, String> mTextParams;

    private Map<String, Object> mMultiParams;

    private Map<String, String> mHeaders;

    public HttpParams() {
    }

    private void checkTextMap() {
        if (mTextParams == null) {
            mTextParams = new HashMap<String, String>();
        }
    }

    private void checkMultiMap() {
        if (mMultiParams == null) {
            mMultiParams = new HashMap<String, Object>();
        }
    }

    private void checkHeaders() {
        if (mHeaders == null) {
            mHeaders = new HashMap<String, String>();
        }
    }

    public HttpParams put(String key, String value) {
        try {
            checkTextMap();
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                if (PARAMS_BLANK.equals(value)) {
                    value = "";
                }
                mTextParams.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public HttpParams header(String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            checkHeaders();
            mHeaders.put(key, value);
        }
        return this;
    }

    public HttpParams put(String key, float value) {
        checkTextMap();
        put(key, value + "");
        return this;
    }

    public HttpParams put(String key, int value) {
        checkTextMap();
        put(key, value + "");
        return this;
    }

    public HttpParams put(String key, double value) {
        checkTextMap();
        put(key, value + "");
        return this;
    }

    public HttpParams put(String key, boolean value) {
        checkTextMap();
        put(key, value + "");
        return this;
    }

    public HttpParams put(String key, File file) {
        checkMultiMap();
        mMultiParams.put(key, file);
        return this;
    }

    public HttpParams put(String key, Bitmap bitmap) {
        checkMultiMap();
        mMultiParams.put(key, bitmap);
        return this;
    }

    public HttpParams put(String key, ByteArrayOutputStream bis) {
        checkMultiMap();
        mMultiParams.put(key, bis);
        return this;
    }

    public HttpParams put(Map<String, String> map) {
        if (map != null && !map.isEmpty()) {
            checkTextMap();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public Map<String, String> getTextParams() {
        return mTextParams;
    }

    public Map<String, String> getHeaderParams() {
//        return Collections.unmodifiableMap(mHeaders);
        return mHeaders;
    }

    public Map<String, Object> getMutiParams() {
//        if (mMultiParams == null) {
//            return null;
//        }
//        return Collections.unmodifiableMap(mMultiParams);
        return mMultiParams;
    }


    public void clear() {
        this.mTextParams.clear();
        this.mMultiParams.clear();
    }
}
