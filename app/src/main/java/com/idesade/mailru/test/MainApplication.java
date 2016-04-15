package com.idesade.mailru.test;

import android.app.Application;

public class MainApplication extends Application {

    private static MainApplication mApplication;
    private LinkData mLinkData = new LinkData();

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    public static MainApplication getApplication() {
        return mApplication;
    }

    public static LinkData getLinkData() {
        return mApplication.mLinkData;
    }
}
