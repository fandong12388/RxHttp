package com.jethon.http.core.request;

import android.graphics.Bitmap;

import com.jethon.http.RxHttp;
import com.jethon.http.bean.HttpParams;
import com.jethon.http.bean.parser.BaseParser;

import java.io.File;


/**
 * time: 2015/8/20
 * description:
 *
 * @author fandong
 */
public class RequestBuilder<T> {
    public static final int TYPE_NEED_BASE = 1;
    public static final int TYPE_NO_NEED_BASE = TYPE_NEED_BASE << 1;

    private int mParamsType = TYPE_NEED_BASE;
    private String mUrl;
    private HttpParams mHttpParams;
    private int mRequestMethod = Request.Method.GET;

    private boolean mIsMultipart = false;
    private BaseParser<T> mParser;
    private Class<T> mClazz;

    private RequestBuilder(Class<T> clazz) {
        this.mClazz = clazz;
        this.mHttpParams = new HttpParams();
    }

    public static final <T> RequestBuilder<T> create(Class<T> clazz) {
        return new RequestBuilder<T>(clazz);
    }

    public static final <T> RequestBuilder<T> create() {
        return new RequestBuilder<T>();
    }

    private RequestBuilder() {
        this.mHttpParams = new HttpParams();
    }

    public RequestBuilder<T> url(String url) {
        mUrl = url;
        return this;
    }


    public RequestBuilder<T> parser(BaseParser parser) {
        mParser = parser;
        return this;
    }

    public RequestBuilder<T> paramsType(int type) {
        mParamsType = type;
        return this;
    }

    public RequestBuilder<T> requestMethod(int method) {
        mRequestMethod = method;
        return this;
    }


    public RequestBuilder<T> header(String key, String value) {
        mHttpParams.header(key, value);
        return this;
    }

    public RequestBuilder<T> put(String key, String value) {
        mHttpParams.put(key, value);
        return this;
    }


    public RequestBuilder<T> put(String key, boolean value) {
        mHttpParams.put(key, value);
        return this;
    }

    public RequestBuilder<T> put(String key, long value) {
        mHttpParams.put(key, value);
        return this;
    }

    public RequestBuilder<T> put(String key, int value) {
        mHttpParams.put(key, value);
        return this;
    }

    public RequestBuilder<T> put(String key, File file) {
        mHttpParams.put(key, file);
        mIsMultipart = true;
        return this;
    }

    public RequestBuilder<T> put(String key, Bitmap bitmap) {
        mHttpParams.put(key, bitmap);
        mIsMultipart = true;
        return this;
    }


    public HttpGsonRequest<T> build() {
        //1.构建参数
        if (mParamsType == TYPE_NEED_BASE) {
            //1.1.添加基本参数
            RxHttp.sParametersGenerator.addBaseParameters(mHttpParams);
            //1.2.生成签名字段
            mHttpParams = RxHttp.sParametersGenerator.generateParameter(mUrl, mHttpParams);
        }
        //2 得到请求实体
        if (mIsMultipart) {
            return new HttpMultipartRequest<T>(mUrl, mHttpParams) {
                @Override
                public Class<?> getClazz() {
                    return mClazz;
                }
            };
        } else {
            if (mParser != null) {
                return new HttpGsonRequest<T>(mRequestMethod, mParser, mUrl, mHttpParams) {
                    @Override
                    public Class<?> getClazz() {
                        return mClazz;
                    }
                };
            } else {
                return new HttpGsonRequest<T>(mRequestMethod, mUrl, mHttpParams) {
                    @Override
                    public Class<?> getClazz() {
                        return mClazz;
                    }
                };
            }
        }
    }
}
