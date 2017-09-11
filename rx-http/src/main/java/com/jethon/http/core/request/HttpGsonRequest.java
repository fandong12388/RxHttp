package com.jethon.http.core.request;

import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import com.jethon.http.bean.HttpParams;
import com.jethon.http.bean.parser.BaseParser;
import com.jethon.http.core.HttpHeaderParser;
import com.jethon.http.core.HttpManager;
import com.jethon.http.core.exception.AuthFailureError;
import com.jethon.http.core.response.HttpResponse;
import com.jethon.http.core.response.NetworkResponse;
import com.jethon.http.log.HttpLogger;
import com.jethon.http.utils.GsonUtil;
import com.jethon.http.utils.RequestHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

/**
 * time: 15/6/15
 * description: 网络请求返回的数据被封装到HttpResponse当中，这个类将HttpResponse当中的data封装到成一个T
 *
 * @author fandong
 */
public class HttpGsonRequest<T> extends BaseRequest<T> {

    protected HttpParams mHttpParams;

    protected String mNodeName;

    protected BaseParser<T> mParser;


    public HttpGsonRequest(String url) {
        super(url);
    }

    public HttpGsonRequest(String url, HttpParams params) {
        super(RequestHelper.buildUrl(url, params));

    }

    public Class<?> getClazz() {
        return null;
    }

    public HttpGsonRequest(int method, String url, HttpParams params) {
        super(method, url);
        this.mHttpParams = params;
        if (Method.GET == method) {
            this.mUrl = HttpManager.buildGetURL(url, params, getParamsEncoding());
        }
    }

    public HttpGsonRequest(int method, BaseParser<T> parser, String url, HttpParams params) {
        super(method, url);
        this.mParser = parser;
        this.mHttpParams = params;
        if (Method.GET == method) {
            this.mUrl = HttpManager.buildGetURL(url, params, getParamsEncoding());
        }
    }

    public HttpGsonRequest(int method, BaseParser<T> parser, String url, String mNodeName, HttpParams params) {
        super(method, url);
        this.mNodeName = mNodeName;
        this.mParser = parser;
        this.mHttpParams = params;
        if (Method.GET == method) {
            this.mUrl = HttpManager.buildGetURL(url, params, getParamsEncoding());
        }
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if (mHttpParams != null) {
            return mHttpParams.getTextParams();
        }
        return super.getParams();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (mHttpParams != null) {
            return mHttpParams.getHeaderParams();
        }
        return super.getHeaders();
    }

    @Override
    public HttpResponse<T> parseNetworkResponse(NetworkResponse response) {
        HttpResponse<T> mResponse = new HttpResponse<T>();
        if (response == null) {
            return mResponse;
        }
        String data;
        try {
            String charset = HttpHeaderParser.parseCharset(response.getHeaders());
            data = new String(response.getData(), charset);
        } catch (UnsupportedEncodingException e) {
            data = new String(response.getData());
        }
        try {
            JSONObject result = new JSONObject(data);
            if (!TextUtils.isEmpty(mNodeName)) {
                if (result.has(mNodeName)) {
                    result = result.getJSONObject(mNodeName);
                }
            }
            mResponse.status = response.getStatusCode();
            if (result.has("error_code")) {
                mResponse.status = result.optInt("error_code", HttpResponse.CODE_FAILED);
            }
            if (result.has("reason")) {
                mResponse.message = result.optString("reason", "");
            } else if (result.has("message")) {
                mResponse.message = result.optString("message", "");
            }

            if (result.has("serverTime")) {
                mResponse.serverTime = result.optDouble("serverTime");
            }
            //如果需要封装数据的nodeName不是"data"可以在此基础上扩展
            if (result.has("result")) {
                JSONObject result_ = result.optJSONObject("result");
                if (null != result_) {
                    data = result_.optString("data");
                }
            }
            Class<?> clazz = getClazz();
            if (!TextUtils.isEmpty(data)) {
                //1.解析器首先解析
                if (mParser != null) {
                    mResponse.data = mParser.parser(data);
                } else if (clazz != null) {
                    if (String.class == clazz) {
                        mResponse.data = (T) data;
                    } else {
                        mResponse.data = GsonUtil.fromJson(data, (Class<T>) clazz);
                    }
                } else {
                    Type type = ((ParameterizedType) ((Object) this).getClass()
                            .getGenericSuperclass()).getActualTypeArguments()[0];
                    mResponse.data = GsonUtil.fromJson(data, type);
                }
            }
        } catch (JsonSyntaxException e) {
            HttpLogger.e("gson解析错误：" + e.getMessage());
        } catch (Exception e) {
            HttpLogger.e("错误：" + e.getMessage());
        }
        return mResponse;
    }

    private void printJson(JSONObject obj) {
        try {
            Object o, object;
            JSONArray array;
            Iterator it = obj.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                o = obj.get(key);
                HttpLogger.e(key + ":" + o.toString() + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
