package com.example.akam.billettogram;

import android.app.ActionBar;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    ListView dagensaktivitet;
    TextView txt;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    JSONParser jsonParser = new JSONParser();
    JSONObject hc= null;
    JSONArray tickets;

    private static final String url_orderedTickets = "http://barnestasjonen.no/test/db_get_billetter.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        dagensaktivitet = (ListView) findViewById(R.id.todaylist);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView txt = (TextView) view.findViewById(android.R.id.text1);
                txt.setTextColor(Color.WHITE);
                return view;
            }
        };
        dagensaktivitet.setAdapter(adapter);
        getJSON task = new getJSON();
        task.setOnPostExecuteListener(new getJSON.OnPostExecuteListener() {
            @Override
            public void onPostExecute(String output) {

                // todo: prosessere output
                try {
                    JSONObject jsonObject = new JSONObject(output);

                    String[][] arr = new String[3][3];
                    tickets = hc.getJSONArray("tickets");
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < arr[0].length; j++) {
                            arr[j][i] = tickets.getString(0);
                        }
                    }

                    for (int i = 0; i < tickets.length(); i++) {
                        String forstedato = tickets.getJSONObject(i).getString("dato");
                        String tittel = tickets.getJSONObject(i).getString("tittel");
                        Log.d("Dagens", forstedato);
                        Log.d("Dagens", tittel);
                       // listItems.add(tittel + "\n" + forstedato);
                    }
                    adapter.notifyDataSetChanged();
                    int success = jsonObject.getInt("success");

                } catch (Exception e) {
                }

            }

        });

        new getShows().execute();

        //JSONObject tickets = task.getJSon();
        adapter.notifyDataSetChanged();
        task.execute(new String[]{"http://barnestasjonen.no/test/db_get_forestillinger.php"});
    }

    public void methodTest() throws JSONException {
        Log.d("TEST","etter postexecute");

        tickets = hc.getJSONArray("tickets");
        for(int i=0; i <3 && i < tickets.length() ;i++){
            listItems.add(tickets.getJSONObject(i).getString("kode"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
     //
    public void openCoupon(View view) {
        // final Context context = this;
        Button coupon = (Button) findViewById(R.id.koppong);
        coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent slideactivity = new Intent(MainActivity.this, Coupon.class);
                startActivity(slideactivity);
            }
        });
    }

    class getShows extends AsyncTask<String,String,String>
    {

        @Override
        protected String doInBackground(String... args) {

            Log.d("TEST", "do in background");

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("id", "arsho@gmail.com"));
            params.add(new BasicNameValuePair("type", "android"));

            JSONObject json = jsonParser.makeHttpRequest(url_orderedTickets,"POST", params);

            try {
                int success= Integer.parseInt(json.getString("success"));
                String msg = json.toString();
                System.out.println("Vi er her:"+msg);



                if (success == 1) {
                    hc = json;
                    // successfully created product
                    //Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    //startActivity(i);
                    Log.d("TEST","i IF");
                    // closing this screen
                    //finish();
                }
                else{
                    //dialogvindu med json message
                    //couponResult(json.getString("message"));
                    Log.d("TEST", "i else");
                    //msg=Html.fromHtml(msg).toString();
                    //hk = msg;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url)
        {
            //run code
            try {
                methodTest();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}


