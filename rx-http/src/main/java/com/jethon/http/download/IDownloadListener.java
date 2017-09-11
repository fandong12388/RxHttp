package com.jethon.http.download;

/**
 * time: 15/7/15
 * description:
 *
 * @author fandong
 */

public interface IDownloadListener {
    void onDownloadStarted();

    void onDownloadFinished(DownloadResult result);

    void onProgressUpdate(Float... value);
}

