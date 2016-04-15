package com.idesade.mailru.test;

import android.net.UrlQuerySanitizer;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LinkData {

    private final List<LinkItem> mData = new ArrayList<>();
    private String mNextQuery = "https://api.datamarket.azure.com/Bing/Search/Web?$format=json&Query=%27android%27";

    public String getNextQuery() {
        return mNextQuery;
    }

    public void setNextQuery(String nextQuery) {
        mNextQuery = nextQuery;
        if (!TextUtils.isEmpty(mNextQuery)) {
            UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(mNextQuery);
            if (sanitizer.getValue("$format") == null) {
                mNextQuery += "&$format=json";
            }
        }
    }

    public void addAll(Collection<LinkItem> items) {
        mData.addAll(items);
    }

    public List<LinkItem> getData() {
        return Collections.unmodifiableList(mData);
    }

    public boolean isEmpty() {
        return mData.isEmpty();
    }
}
