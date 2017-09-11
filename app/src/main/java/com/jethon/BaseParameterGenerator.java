package com.jethon;

import com.jethon.http.bean.HttpParams;
import com.jethon.http.core.IParametersGenerator;

public class BaseParameterGenerator implements IParametersGenerator {
    private static final Object sLock = new Object();

    @Override
    public void addBaseParameters(HttpParams parameter) {
        //每一个接口都可能会添加基本参数
    }


    @Override
    public boolean isNeedURLEncode(String url) {
        //url是否需要进行URLEncode
        return false;
    }

    @Override
    public HttpParams generateParameter(String url, HttpParams parameters) {
        //2.对所有的非文件参数进行排序、加密，添加sig字段
        return parameters;
    }
}

