package com.jethon.http.core.request;


import com.jethon.http.bean.HttpParams;
import com.jethon.http.core.HttpManager;
import com.jethon.http.core.exception.AuthFailureError;

/**
 * time: 15/6/15
 * description: 上传图片对应的请求实体
 *
 * @author fandong
 */
public  class HttpMultipartRequest<T> extends HttpGsonRequest<T> {

    public HttpMultipartRequest(String url, HttpParams params) {
        super(Method.POST, url, params);
    }


    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + HttpManager.BOUNDARY;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        byte[] body = HttpManager.buildPostParams(mHttpParams);
        if (body == null) {
            return super.getBody();
        } else {
            return body;
        }
    }

}
