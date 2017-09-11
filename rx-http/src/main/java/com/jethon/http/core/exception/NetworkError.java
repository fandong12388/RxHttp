package com.jethon.http.core.exception;


import com.jethon.http.bean.ErrorCode;
import com.jethon.http.core.response.NetworkResponse;

/**
 * time: 2015/8/19
 * description:
 *
 * @author fandong
 */
public class NetworkError extends VolleyError {
    private ErrorCode mErrorCode;

    public NetworkError() {
    }

    public ErrorCode getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(ErrorCode code) {
        this.mErrorCode = code;
    }

    public NetworkError(Throwable cause) {
        super(cause);
    }

    public NetworkError(NetworkResponse networkResponse) {
        super(networkResponse);
    }
}
