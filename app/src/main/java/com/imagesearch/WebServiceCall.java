package com.imagesearch;


import android.graphics.Bitmap;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class WebServiceCall extends AsyncTask<String, Void, Void> {

    String jSONResp = "";
    MainActivity mainActivity;

    public WebServiceCall(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    protected Void doInBackground(String... gpssearch) {

        try {

            URL url = new URL("https://en.wikipedia.org/w/api.php?action=query&prop=pageimages&format=json&piprop=thumbnail&pithumbsize=50&pilimit=50&generator=prefixsearch&gpssearch=" + gpssearch[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String line;
            while ((line = br.readLine()) != null) {
                jSONResp += line;
            }
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {

        mainActivity.showProgressbar(false);
        if (isCancelled())
        {
            return;
        }

        ArrayList<ListItemHolder> listData = new ArrayList<ListItemHolder>();
        try {
            if (!jSONResp.equals("")) {
                JSONObject jsonObject = new JSONObject(jSONResp);
                JSONObject query = jsonObject.getJSONObject("query");
                JSONObject pages = query.getJSONObject("pages");

                Iterator<String> iter = pages.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        JSONObject value = (JSONObject) pages.get(key);
                        String title = value.getString("title");
                        JSONObject thumbnail = value.getJSONObject("thumbnail");
                        String source = thumbnail.getString("source");

                        ListItemHolder listItemHolder = new ListItemHolder();
                        listItemHolder.setTitle(title);
                        listItemHolder.setUrl(source);
                        listData.add(listItemHolder);


                    } catch (JSONException e) {

                    }
                }
            }
            mainActivity.getSearchListAdapter().setListData(listData);
            mainActivity.getSearchListAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}



