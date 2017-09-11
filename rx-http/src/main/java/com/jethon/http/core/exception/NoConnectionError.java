package com.jethon.http.core.exception;

/**
 * time: 2015/8/19
 * description:
 *
 * @author fandong
 */
public class NoConnectionError extends NetworkError {
    public NoConnectionError() {
    }

    public NoConnectionError(Throwable reason) {
        super(reason);
    }
}
