package com.idesade.mailru.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.text.Html;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

public class LinkLoader extends AsyncTaskLoader<Collection<LinkItem>> {

    public static String LOG_TAG = LinkLoader.class.getSimpleName();

    private LinkItem mParenLinkItem;

    public LinkLoader(Context context, LinkItem parenLinkItem) {
        super(context);
        mParenLinkItem = parenLinkItem;
    }

    @Override
    public Collection<LinkItem> loadInBackground() {
        InputStream is = null;
        Collection<LinkItem> result = new ArrayList<>();

        try {
            try {
                is = downloadURL(mParenLinkItem.getUrl());

                String data = inputStreamToString(is);

                Spanned spanned = Html.fromHtml(data);
                String spannedString = spanned.toString();
                URLSpan[] urlSpans = spanned.getSpans(0, spanned.length(), URLSpan.class);

                for (int i = 0; i < urlSpans.length; i++) {
                    URLSpan urlSpan = urlSpans[i];
                    String url = urlSpan.getURL();
                    if (url.startsWith("http")) {
                        int start = spanned.getSpanStart(urlSpan);
                        int end = spanned.getSpanEnd(urlSpan);
                        String str = spannedString.substring(start, end);
                        result.add(new LinkItem(mParenLinkItem.getId() + i + ".", str, null, urlSpan.getURL(), urlSpan.getURL()));
                    }
                }
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private InputStream downloadURL(String downloadUrl) throws IOException {
        Log.d(LOG_TAG, "Download data from: " + downloadUrl);
        URL url = new URL(downloadUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setDoInput(true);
        connection.setRequestMethod("GET");

        connection.connect();
        int response = connection.getResponseCode();
        Log.d(LOG_TAG, "The response is: " + response);

        return connection.getInputStream();
    }

    @NonNull
    private String inputStreamToString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try {
            byte[] data = new byte[1024];
            int numRead;
            while ((numRead = is.read(data)) >= 0) {
                sb.append(new String(data, 0, numRead));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
