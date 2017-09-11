package com.jethon.http.utils;

import static com.jethon.http.RxHttp.gContext;

/**
 * time: 2015/10/16 0016
 * description:
 *
 * @author fandong
 */
public class ResUtil {
    public static String getString(int resId) {
        return gContext.getResources().getString(resId);
    }
}
