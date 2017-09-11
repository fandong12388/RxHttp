package com.jethon.http.core;


import com.jethon.http.bean.ErrorCode;

/**
 * time: 15/7/20
 * description:
 *
 * @author fandong
 */
public interface IFetchDataListener {

    void success();


    void error(ErrorCode errorCode);
}
