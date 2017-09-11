//package com.jethon.volley.core.response;
//
//import java.util.HashMap;
//
///**
// * time: 2015/8/19
// * description:
// *
// * @author fandong
// */
//public class CustomResponse {
//
//    private byte[] data;
//    private int code;
//    //ok  not found
//    private String msg;
//    private HashMap<String, String> headers;
//
//    public byte[] getData() {
//        return data;
//    }
//
//    public void setData(byte[] data) {
//        this.data = data;
//    }
//
//    public int getCode() {
//        return code;
//    }
//
//    public void setCode(int code) {
//        this.code = code;
//    }
//
//    public String getMsg() {
//        return msg;
//    }
//
//    public void setMsg(String msg) {
//        this.msg = msg;
//    }
//
//    public HashMap<String, String> getHeaders() {
//        return headers;
//    }
//
//    public void setHeaders(HashMap<String, String> headers) {
//        this.headers = headers;
//    }
//
//    public void addHeader(String key, String value) {
//        if (null == headers) {
//            this.headers = new HashMap<String, String>();
//        }
//        this.headers.put(key, value);
//    }
//
//    public String getHeader(String key) {
//        if (null != headers) {
//            return headers.get(key);
//        }
//        return null;
//    }
//}
