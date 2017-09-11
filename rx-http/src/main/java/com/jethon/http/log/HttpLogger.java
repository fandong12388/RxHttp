package com.jethon.http.log;


import android.util.Log;

import com.jethon.http.RxHttp;
import com.jethon.http.core.request.Request;
import com.jethon.http.core.response.NetworkResponse;

import java.util.Iterator;
import java.util.Map;


/**
 * time: 15/7/10
 * description: http请求的日志管理类
 *
 * @author fandong
 */
public class HttpLogger {
    public static final String TAG = "Request";

    /**
     * 请求发生之前，打印请求的相关信息
     *
     * @param request 请求
     */
    public static void printParams(Request<?> request) {
        if (RxHttp.sDebug) {
            StringBuilder builder = new StringBuilder();
            builder.append("\nHttp url : ")
                    .append(request.getUrl());
            String method;
            switch (request.getMethod()) {
                case Request.Method.GET:
                    method = "GET";
                    break;
                case Request.Method.POST:
                    method = "POST";
                    break;
                case Request.Method.PUT:
                    method = "PUT";
                    break;
                case Request.Method.DELETE:
                    method = "DELETE";
                    break;
                case Request.Method.HEAD:
                    method = "HEAD";
                    break;
                case Request.Method.OPTIONS:
                    method = "OPTIONS";
                    break;
                case Request.Method.TRACE:
                    method = "TRACE";
                    break;
                case Request.Method.PATCH:
                    method = "PATCH";
                    break;
                default:
                    method = "GET";
            }

            builder.append("\nHttp method : ").append(method);
            try {
                Map map = request.getHeaders();
                if (null != map && !map.isEmpty()) {
                    StringBuilder headerBuilder = new StringBuilder();
                    headerBuilder.append("\nHttp headers: ");
                    Iterator it = map.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry entry = (Map.Entry) it.next();
                        headerBuilder.append("[");
                        headerBuilder.append((String) entry.getKey());
                        headerBuilder.append(" = ");
                        headerBuilder.append((String) entry.getValue());
                        headerBuilder.append("] ");
                    }

                    builder.append(headerBuilder.toString());
                }
                byte[] params = request.getBody();
                if (params != null) {
                    builder.append("\nHttp Params: ");
                    builder.append(new String(params));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e(TAG, builder.toString());
        }
    }

    /**
     * 数据返回，尚未分发时的数据信息
     *
     * @param response 请求
     * @param response 响应
     */
    public static void printResponse(Request<?> request, NetworkResponse response) {
        if (RxHttp.sDebug) {
            try {
                String body = new String(response.getData(), "UTF-8");
                StringBuilder builder = new StringBuilder();
                builder.append("\nstatus:")
                        .append(response.getStatusCode())
                        .append("\nurl:")
                        .append(request.getOriginUrl())
                        .append("\ndata:")
                        .append(body)
                        .append("\nescapse time:")
                        .append(response.getNetworkTimeMs());
                Log.e(TAG, builder.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void e(String msg) {
        if (RxHttp.sDebug) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (RxHttp.sDebug) {
            Log.e(tag, msg);
        }
    }

    public static void e(Exception e) {
        if (RxHttp.sDebug) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public static void e(Throwable tr) {
        if (RxHttp.sDebug) {
            Log.e(TAG, Log.getStackTraceString(tr));
        }
    }

}
