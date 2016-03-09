package com.example.akam.billettogram;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class getJSON extends AsyncTask<String, Void, String> {
    JSONParser jsonParser = new JSONParser();


    private OnPostExecuteListener onPostExecuteListener;
    private JSONObject jsonObject = null;

    public interface OnPostExecuteListener {
        void onPostExecute(String output);
    }

    public void setOnPostExecuteListener(OnPostExecuteListener onPostExecuteListener) {
        this.onPostExecuteListener = onPostExecuteListener;
    }

    @Override
    protected String doInBackground(String... urls) {

        String msg = "";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", "arsho@gmail.com"));
        params.add(new BasicNameValuePair("type", "android"));

        JSONObject json = jsonParser.makeHttpRequest("http://barnestasjonen.no/test/db_get_billetter.php", "POST", params);

        try {
            int success = Integer.parseInt(json.getString("success"));
            msg = json.toString();
            //System.out.println(msg);

            if (success == 1) {

                jsonObject = json;

            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    protected void onPostExecute(String ss) {
        this.onPostExecuteListener.onPostExecute(ss);

    }
    public JSONObject getJSon(){
        return jsonObject;
    }
}