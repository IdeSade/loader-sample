package com.idesade.mailru.test;

import android.content.Context;
import android.net.UrlQuerySanitizer;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

public class SearchResultLoader extends AsyncTaskLoader<Collection<LinkItem>> {

    public static String LOG_TAG = SearchResultLoader.class.getSimpleName();

    private final LinkData mLinkData;

    public SearchResultLoader(Context context, LinkData linkData) {
        super(context);
        mLinkData = linkData;
    }

    @Override
    public Collection<LinkItem> loadInBackground() {
        InputStream is = null;
        Collection<LinkItem> result = new ArrayList<>();

        try {
            try {
                is = downloadURL(mLinkData.getNextQuery());

                String data = inputStreamToString(is);
                String nextQuery = null;
                try {
                    JSONObject jsonObject = new JSONObject(data).getJSONObject("d");
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonItem = jsonArray.getJSONObject(i);
                        String id = getItemId(jsonItem) + ".";
                        String title = jsonItem.getString("Title");
                        String desc = jsonItem.getString("Description");
                        String displayUrl = jsonItem.getString("DisplayUrl");
                        String url = jsonItem.getString("Url");
                        result.add(new LinkItem(id, title, desc, displayUrl, url));
                    }
                    nextQuery = jsonObject.optString("__next");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d(LOG_TAG, "Next query: " + nextQuery);
                mLinkData.setNextQuery(nextQuery);
                mLinkData.addAll(result);
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
        connection.setRequestProperty(
                "Authorization",
                "Basic WGRLcWhVOFRFRURVUkpRcGxtaHdrdWpQRHRDZkI3am83cklTMmhnQjBnaz06WGRLcWhVOFRFRURVUkpRcGxtaHdrdWpQRHRDZkI3am83cklTMmhnQjBnaz0=");

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

    private String getItemId(JSONObject item) throws JSONException {
        String uriString = item.getJSONObject("__metadata").getString("uri");
        UrlQuerySanitizer sanitizer = new UrlQuerySanitizer(uriString);
        return sanitizer.getValue("$skip");
    }
}
