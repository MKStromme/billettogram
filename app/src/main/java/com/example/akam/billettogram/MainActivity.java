package com.example.akam.billettogram;

import android.app.ActionBar;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
    DBAdapter db;


    private static final String url_orderedTickets = "http://barnestasjonen.no/test/db_get_billetter.php";
    private static final String url_getForestillinger= "http://barnestasjonen.no/test/db_get_frontforestillinger.php";

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

        if(!fromLocalDB())
            new getShows().execute();

        //JSONObject tickets = task.getJSon();
        adapter.notifyDataSetChanged();
        //task.execute(new String[]{"http://barnestasjonen.no/test/db_get_forestillinger.php"});



    }

    public void fromExtDB() throws JSONException
    {
        tickets = hc.getJSONArray("tickets");

        for (int i = 0; i < 3 && i < tickets.length(); i++) {
            listItems.add(tickets.getJSONObject(i).getString("tittel"));
        }
    }

    private boolean fromLocalDB()
    {

        db = new DBAdapter(this);
        db.open();

        Cursor cur = db.treSisteForestilling();
        if(cur.moveToFirst())
        {
            do{
                listItems.add(cur.getInt(cur.getColumnIndex(db.ID)),cur.getString(cur.getColumnIndex(db.TITTEL)));
            }while(cur.moveToNext());
            return true;
        }

        return false;
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
    public void openMineBilletter(View view) {
        // final Context context = this;
        Button mnbl = (Button) findViewById(R.id.show);
        mnbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent slideactivity = new Intent(MainActivity.this, MineBilletter.class);
                startActivity(slideactivity);
            }
        });
    }

    class getShows extends AsyncTask<String,String,String>
    {

        @Override
        protected String doInBackground(String... args) {
            Log.d("TEST","jeg er her");

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("id", "test12345@gmail.com"));
            params.add(new BasicNameValuePair("type", "android"));

            JSONObject json = jsonParser.makeHttpRequest(url_orderedTickets,"POST", params);

            try {
                int success= Integer.parseInt(json.getString("success"));
                String msg = json.toString();
                System.out.println("Vi er her:"+msg+"   "+success);



                if (success == 1) {hc = json;}

                else{
                    List<NameValuePair> x = new ArrayList<>();
                    json = jsonParser.makeHttpRequest(url_getForestillinger,"GET",x);
                    //hent forestillinger som brukeren kan kjope.
                    hc=json;
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
                fromExtDB();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}


