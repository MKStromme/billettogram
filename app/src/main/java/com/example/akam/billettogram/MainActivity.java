package com.example.akam.billettogram;

import android.app.ActionBar;
import android.app.LauncherActivity;
import android.content.ContentValues;
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
import android.widget.AdapterView;
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
import java.util.Iterator;
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
    int success;
    String msg;
    final Context c = this;
    StableArrayAdapter adb;

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
                Log.d("dritt","drittttt");
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


        //final ArrayList<String> list = new ArrayList<String>();
        //final ArrayList<Integer> listId = new ArrayList<>();

        /*if (cur.moveToFirst()) {
            do {
                list.add(cur.getString(0));
                listId.add(cur.getInt(cur.getColumnIndex(db.ID)));
            } while (cur.moveToNext());
        }
        cur.close();

        final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, list, listId);
        dagensaktivitet.setAdapter(adapter);*/
        /*final Context c = this;

        dagensaktivitet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(c, Billett.class);
                intent.putExtra("TryThis", adapter.getActualId(position));
                startActivity(intent);
            }
        });*/
        dagensaktivitet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TAG2","vi er in buttonclick");
                Intent intent = new Intent(c,Billett.class);
                intent.putExtra("TryThis", adb.getActualId(position));
                startActivity(intent);
            }
        });


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
        Log.d("TAG:","Vi er i localDB");
        db = new DBAdapter(this);
        db.open();

        final ArrayList<String> list = new ArrayList<String>();
        final ArrayList<Integer> listId = new ArrayList<>();

        Cursor cur = db.treSisteForestilling();
        if(cur.moveToFirst())
        {
            do{
                list.add(cur.getString(cur.getColumnIndex(db.TITTEL)));
                listId.add(cur.getInt(cur.getColumnIndex(db.ID)));
                Log.d("tests", ""+cur.getColumnIndex(db.TITTEL));
                Log.d("tests", "test 2 " + cur.getString(cur.getColumnIndex(db.TITTEL)));
                listItems.add(cur.getString(cur.getColumnIndex(db.TITTEL)));
            }while(cur.moveToNext());
            adb = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, list, listId);
            dagensaktivitet.setAdapter(adapter);
            return true;
        }
        cur.close();
        //tl.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {});
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

    public void buyForestilling(View view) {
        // final Context context = this;
        Intent intent = new Intent(MainActivity.this, Forestillinger.class);
        startActivity(intent);
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {
        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
        List<Integer> trialId;

        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects, List<Integer> objectId) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); i++)
                mIdMap.put(objects.get(i), i);
            trialId = objectId;
        }

        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        public int getActualId(int position)
        {
            return trialId.get(position);
        }
    }

    class getShows extends AsyncTask<String,String,String>
    {

        @Override
        protected String doInBackground(String... args) {
            Log.d("TEST","jeg er her");

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("id", "adam@gmail.com"));
            params.add(new BasicNameValuePair("type", "android"));

            JSONObject json = jsonParser.makeHttpRequest(url_orderedTickets,"POST", params);

            try {
                if(json != null) {
                    success = Integer.parseInt(json.getString("success"));
                    msg = json.toString();
                    System.out.println("Vi er her:" + msg + "   " + success);

                }



                if (success == 1) {
                    hc = json;

                    System.out.println(json.toString());
                    db.open();
                    JSONArray keys = new JSONArray(json.get("tickets").toString());
                    System.out.println("før while");
                    for(int i=0; i <keys.length(); i++){
                        JSONObject tempjson =keys.getJSONObject(i);

                            System.out.println("inne i if");
                            ContentValues cv = new ContentValues();
                            cv.put(db.DATE,tempjson.getString("date"));
                            cv.put(db.FID,Integer.parseInt(tempjson.getString("fid")));
                            cv.put(db.TITTEL,tempjson.getString("tittel"));
                            cv.put(db.PRIS,Integer.parseInt(tempjson.getString("pris")));
                            cv.put(db.BILDET,tempjson.getString("bilde"));
                            cv.put(db.KODE,tempjson.getString("kode"));
                            cv.put(db.ANTALL,Integer.parseInt(tempjson.getString("antall")));
                            cv.put(db.TIME,tempjson.getString("time"));
                            db.insert(cv);

                        System.out.println("etter if");
                    }
                    System.out.println("etter while");
                    db.close();
                }
                else{
                    List<NameValuePair> x = new ArrayList<>();
                    json = jsonParser.makeHttpRequest(url_getForestillinger,"POST",x);
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


