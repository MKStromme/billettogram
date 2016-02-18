package com.example.akam.billettogram;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class getJSON extends AsyncTask<String, Void, String> {

    private OnPostExecuteListener onPostExecuteListener;

    public interface OnPostExecuteListener {
        void onPostExecute(String output);
    }

    public void setOnPostExecuteListener(OnPostExecuteListener onPostExecuteListener) {
        this.onPostExecuteListener = onPostExecuteListener;
    }

    @Override
    protected String doInBackground(String... urls) {
        String s = "";
        String output = "";

        for (String url : urls) {
            try {
                URL urlen = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) urlen.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "applicaRon/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed:HTTP error code:" + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                System.out.println("Output from Server....\n");
                while ((s = br.readLine()) != null) {
                    output = output + s;
                }

                conn.disconnect();
                return output;
            } catch (Exception e) {
                return "Error!!!";
            }

        }
        return output;
    }

    protected void onPostExecute(String ss) {
        this.onPostExecuteListener.onPostExecute(ss);

    }
}