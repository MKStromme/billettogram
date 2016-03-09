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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //public static final string "";

        dagensaktivitet = (ListView) findViewById(R.id.todaylist);
        //txt=(TextView)findViewById(R.id.pid);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems) {
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                // Initialize a TextView for ListView each Item
                TextView txt = (TextView) view.findViewById(android.R.id.text1);

                // Set the text color of TextView (ListView Item)
                txt.setTextColor(Color.WHITE);

                // Generate ListView Item using TextView
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
                    JSONArray tickets = jsonObject.getJSONArray("tickets");
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
                        listItems.add(tittel + "\n" + forstedato);
                    }
                    adapter.notifyDataSetChanged();
                    int success = jsonObject.getInt("success");

                } catch (Exception e) {
                }

            }

        });
        JSONObject tickets = task.getJSon();

        /*for (int i = 0; i < 3; i++) {
            String tittel = null;
            try {
                tittel = tickets.getJSONObject("tittel").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("Dagens", tittel);
            listItems.add(tittel + "\n");
        }*/
        adapter.notifyDataSetChanged();

        /*JSONParser jsonParser = new JSONParser();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", "arsho@gmail.com"));
        params.add(new BasicNameValuePair("type", "android"));

        // getting JSON Object
        // Note that create product url accepts POST method
        JSONObject json = jsonParser.makeHttpRequest("http://barnestasjonen.no/test/db_get_billetter.php","POST", params);*/

        task.execute(new String[]{"http://barnestasjonen.no/test/db_get_forestillinger.php"});
    }

    public void methodTest()
    {
        Log.d("TEST","etter postexecute");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openCoupon(View view) {
        // final Context context = this;
        Button coupon = (Button) findViewById(R.id.koppong);
        coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent slideactivity = new Intent(MainActivity.this, Coupon.class);
                startActivity(slideactivity);
                //overridePendingTransition(android.R.anim., android.R.anim.slide_in_left);
            }
        });
    }

   /* public class getJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... args) {
            //String description = inputDesc.getText().toString();

            String msg = "";
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", "arsho@gmail.com"));
            params.add(new BasicNameValuePair("type", "android"));

            JSONObject json = jsonParser.makeHttpRequest("http://barnestasjonen.no/test/db_get_billetter.php", "POST", params);

            try {
                int success = Integer.parseInt(json.getString("success"));
                msg = json.toString();
                System.out.println(msg);

                if (success == 1) {

                } else {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }*/
}


