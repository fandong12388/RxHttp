package com.jethon.http.core.stack;


import android.text.TextUtils;

import com.facebook.stetho.urlconnection.ByteArrayRequestEntity;
import com.facebook.stetho.urlconnection.SimpleRequestEntity;
import com.facebook.stetho.urlconnection.StethoURLConnectionManager;
import com.jethon.http.RxHttp;
import com.jethon.http.core.PoolingByteArrayOutputStream;
import com.jethon.http.core.exception.AuthFailureError;
import com.jethon.http.core.exception.ServerError;
import com.jethon.http.core.exception.VolleyError;
import com.jethon.http.core.pool.ByteArrayPool;
import com.jethon.http.core.request.BaseRequest;
import com.jethon.http.core.request.Request;
import com.jethon.http.core.response.NetworkResponse;
import com.jethon.http.core.ssl.SSLContextFactory;
import com.jethon.http.log.HttpLogger;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;


/**
 * time: 15/7/10
 * description: HttpUrlConnection方式请求数据
 *
 * @author fandong
 */
public class HttpUrlStack implements HttpStack {
    private StethoURLConnectionManager stethoManager;
    protected static int DEFAULT_POOL_SIZE = 4096;
    protected ByteArrayPool mPool;

    public HttpUrlStack() {
        this(new ByteArrayPool(DEFAULT_POOL_SIZE));
    }

    public HttpUrlStack(ByteArrayPool pool) {
        this.mPool = pool;
    }

    public NetworkResponse performRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, VolleyError {
        //1.创建StethoManager,用于stetho调试
        if (RxHttp.sDebug) {
            stethoManager = new StethoURLConnectionManager(((BaseRequest) request).getFriendlyName());
        }
        //2.打印请求的头部字段和参数信息
        HttpLogger.printParams(request);
        //3.创建URL并处理https
        URL parsedUrl = new URL(request.getUrl());
        if ("https".equals(parsedUrl.getProtocol())) {
            String host = parsedUrl.getHost();
            boolean auth = true;
            if (!TextUtils.isEmpty(host) && !host.endsWith(RxHttp.sValidateHost)) {
                HttpLogger.e("Request", "https not oauth!");
                auth = false;
            }
            handleHttps(auth);
        }
        //5.连接网咯
        HttpURLConnection mConnection = this.openConnection(parsedUrl, request);
        try {
            //6.stetho保存访问前的状态
            if (RxHttp.sDebug) {
                SimpleRequestEntity requestEntity = null;
                if (request.getBody() != null) {
                    requestEntity = new ByteArrayRequestEntity(request.getBody());
                }
                stethoManager.preConnect(mConnection, requestEntity);
            }
            //7.将头字段的信息加入到请求信息当中
            if (additionalHeaders != null && !additionalHeaders.isEmpty()) {
                Iterator iterator = additionalHeaders.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    mConnection.addRequestProperty(key, additionalHeaders.get(key));
                }
            }
            //8.设置请求方式
            setConnectionParametersForRequest(mConnection, request);
            //ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
            int responseCode = mConnection.getResponseCode();
            if (responseCode < 0) {
                IOException e = new IOException("Could not retrieve response code from HttpUrlConnection.");
                if (RxHttp.sDebug) {
                    stethoManager.httpExchangeFailed(e);
                }
                throw e;
            } else {
                NetworkResponse response = new NetworkResponse(mConnection.getResponseCode());
                response.setMsg(mConnection.getResponseMessage());

                Iterator it = mConnection.getHeaderFields().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry header = (Map.Entry) it.next();
                    if (header.getKey() != null) {
                        String key = (String) header.getKey();
                        List list = (List) header.getValue();
                        if (list != null && !list.isEmpty()) {
                            String value = String.valueOf(list.get(0));
                            response.addHeader(key, value);
                        }

                    }
                }
                entityFromConnection(mConnection, response);
                if (RxHttp.sDebug) {
                    stethoManager.postConnect();
                }
                return response;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (mConnection != null) {
                mConnection.disconnect();
            }
        }
    }

    /**
     * 对https请求添加双向认证的机制
     *
     * @param auth 是否需要双向认证
     */
    private void handleHttps(boolean auth) {
        try {
            //1.添加hostnameVerifier
            HostnameVerifier e = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(e);
            //2.处理SSL
            SSLContextFactory factory = SSLContextFactory.getInstance();
            SSLContext context = null;
            if (auth) {
                context = factory.getSSLContext();
            }
            if (context == null) {
                context = factory.makeContext();
            }
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            HttpLogger.e(e);
        }

    }

    public byte[] getBytes(InputStream is, boolean hasGzip) throws Exception {
        if (hasGzip) {
            is = new GZIPInputStream(is);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        is.close();
        bos.flush();
        byte[] result = bos.toByteArray();
        bos.close();
        return result;
    }


    private void entityFromConnection(HttpURLConnection connection, NetworkResponse response) throws VolleyError {
        boolean hasGzip = hasGzip(response.getHeader("Content-Encoding"));
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
            if (RxHttp.sDebug) {
                inputStream = stethoManager.interpretResponseStream(inputStream);
            }
        } catch (IOException e) {
            inputStream = connection.getErrorStream();
            if (RxHttp.sDebug) {
                stethoManager.httpExchangeFailed(e);
            }
        }
        PoolingByteArrayOutputStream bytes = new PoolingByteArrayOutputStream(this.mPool);
        byte[] buffer = null;
        try {
            if (hasGzip) {
                inputStream = new GZIPInputStream(inputStream);
            }
            if (inputStream == null) {
                throw new ServerError();
            } else {
                buffer = this.mPool.getBuf(1024);
                int count;
                while ((count = inputStream.read(buffer)) != -1) {
                    bytes.write(buffer, 0, count);
                }
                byte[] buffers = bytes.toByteArray();
                response.setData(buffers);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                this.mPool.returnBuf(buffer);
                if (inputStream != null) {
                    inputStream.close();
                }
                bytes.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private HttpURLConnection createConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    private HttpURLConnection openConnection(URL url, Request<?> request) throws IOException {
        HttpURLConnection connection = this.createConnection(url);
        int timeoutMs = request.getTimeoutMs();
        connection.setConnectTimeout(timeoutMs);
        connection.setReadTimeout(timeoutMs);
        connection.setUseCaches(false);
        connection.setDoInput(true);
        return connection;
    }

    private void setConnectionParametersForRequest(HttpURLConnection connection, Request<?> request) throws IOException, AuthFailureError {
        switch (request.getMethod()) {
            case Request.Method.DEPRECATED_GET_OR_POST:
                byte[] postBody = request.getBody();
                if (postBody != null) {
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.addRequestProperty("Content-Type", request.getBodyContentType());
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.write(postBody);
                    out.close();
                }
                break;
            case Request.Method.GET:
                connection.setRequestMethod("GET");
                break;
            case Request.Method.POST:
                connection.setRequestMethod("POST");
                addBodyIfExists(connection, request);
                break;
            case Request.Method.PUT:
                connection.setRequestMethod("PUT");
                addBodyIfExists(connection, request);
                break;
            case Request.Method.DELETE:
                connection.setRequestMethod("DELETE");
                break;
            case Request.Method.HEAD:
                connection.setRequestMethod("HEAD");
                break;
            case Request.Method.OPTIONS:
                connection.setRequestMethod("OPTIONS");
                break;
            case Request.Method.TRACE:
                connection.setRequestMethod("TRACE");
                break;
            case Request.Method.PATCH:
                connection.setRequestMethod("PATCH");
                addBodyIfExists(connection, request);
                break;
            default:
                throw new IllegalStateException("Unknown request method.");

        }

    }

    private boolean hasGzip(String headerValue) {
        if (TextUtils.isEmpty(headerValue)) {
            return false;
        }
        return headerValue.contains("gzip")
                || headerValue.contains("GZIP");
    }

    private static void addBodyIfExists(HttpURLConnection connection, Request<?> request) throws IOException, AuthFailureError {
        byte[] body = request.getBody();
        if (body != null) {
            connection.setDoOutput(true);
            connection.addRequestProperty("Content-Type", request.getBodyContentType());
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(body);
            out.close();
        }
    }
}

