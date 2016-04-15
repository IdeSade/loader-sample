package com.idesade.mailru.test;

import android.support.annotation.NonNull;

public class LinkItem {

    private String mId;
    private String mTitle;
    private String mDescription;
    private String mDisplayUrl;
    private String mUrl;

    private int mLevel;

    public LinkItem(@NonNull String Id, String title, String description, String displayUrl, @NonNull String url) {
        mId = Id;
        mTitle = title;
        mDescription = description;
        mDisplayUrl = displayUrl;
        mUrl = url;

        mLevel = calcLevel(mId);
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getDisplayUrl() {
        return mDisplayUrl;
    }

    public String getUrl() {
        return mUrl;
    }

    public int getLevel() {
        return mLevel;
    }

    public int compareId(LinkItem other) {
        String[] ids = mId.split("\\.");
        String[] otherIds = other.getId().split("\\.");
        for (int i = 0; i < ids.length; i++) {
            int result = Integer.valueOf(ids[i]).compareTo(Integer.valueOf(otherIds[i]));
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    private int calcLevel(String id) {
        int level = -1;
        for (char c : id.toCharArray()) if (c == '.') level++;
        return level;
    }
}
