package com.jethon.http;

import android.content.Context;

import com.jethon.http.core.IParametersGenerator;

/**
 * time: 2015/10/16
 * description:同步网络请求框架Rx-sync-volley的入口
 *
 * @author fandong
 */
public class RxHttp {

    public static Context gContext;
    public static boolean sDebug;
    public static IParametersGenerator sParametersGenerator;
    public static String sValidateHost;

    /**
     * 同步Volley的初始化
     *
     * @param context      必须是Application的子类
     * @param debug        是否是调试模式
     * @param generator    请求参数的生成器(可能需要添加基本字段和签名字段)
     * @param validateHost https请求模式下需要与服务器进行双向认证的主机名
     */
    public static void init(Context context, boolean debug, IParametersGenerator generator
            , String validateHost) {
        RxHttp.gContext = context;
        RxHttp.sDebug = debug;
        RxHttp.sParametersGenerator = generator;
        RxHttp.sValidateHost = validateHost;
    }

    public static void release() {
        RxHttp.gContext = null;
        RxHttp.sParametersGenerator = null;
        RxHttp.sValidateHost = null;
    }

    public static void setDebug(boolean sDebug) {
        RxHttp.sDebug = sDebug;
    }

    public static void setParametersGenerator(IParametersGenerator sParametersGenerator) {
        RxHttp.sParametersGenerator = sParametersGenerator;
    }

    public static void setValidateHost(String sValidateHost) {
        RxHttp.sValidateHost = sValidateHost;
    }

}
