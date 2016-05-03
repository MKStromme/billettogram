package com.example.akam.billettogram;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.util.Patterns;
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
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    ListView dagensaktivitet;
    TextView txt;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    JSONParser jsonParser = new JSONParser();
    JSONObject hc= null;
    JSONArray tickets;
    JSONArray fstilling;
    DBAdapter db;
    int success;
    String msg;
    final Context c = this;
    StableArrayAdapter adb;
    public boolean ekstern=false;
    public static String email;

    private   String url_orderedTickets = "http://barnestasjonen.no/test/db_get_billetter.php";
    private   String url_getForestillinger= "http://barnestasjonen.no/test/db_get_frontforestillinger.php";

    private List<frstlng> frstlnglist= new ArrayList<frstlng>();


    private class frstlng{
        public String ID;
        public String tittel;
        public int listid;

        public frstlng(int listid, String ID, String tittel){
            this.listid = listid;
            this.ID = ID;
            this.tittel = tittel;
        }
        public String getID(){
            return this.ID;
        }
        public String getTittel()
        {
            return this.tittel;
        }
        public int getlistid(){
            return this.listid;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //********
        Pattern emailpattern = Patterns.EMAIL_ADDRESS;
        Account[]accounts= AccountManager.get(c).getAccounts();
        for(Account account:accounts){
            if(emailpattern.matcher(account.name).matches()){
                email=account.name;
                System.out.println("eeee" + email);
            }
        }

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
            try {
                new getShows().execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        if(!ekstern) {
            //JSONObject tickets = task.getJSon();
            adapter.notifyDataSetChanged();
            dagensaktivitet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("TAG2", "vi er in buttonclick");
                    Intent intent = new Intent(c, Billett.class);
                    intent.putExtra("TryThis", adb.getActualId(position));
                    startActivity(intent);
                }
            });
        }
        else {
            dagensaktivitet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    System.out.println("lll" + frstlnglist.toString());
                    System.out.println("index: " + position);
                    frstlng y = frstlnglist.get(position);
                    System.out.println("ID: " + y.getID() + ". tittel: " + y.getTittel());

                    Intent i = new Intent(getApplicationContext(), Forestilling.class);
                    i.putExtra("selectedItem", y.getID());
                    startActivity(i);

                }
            });
        }

    }

    public void fromExtDB() throws JSONException
    {
        fstilling = hc.getJSONArray("tickets");
        System.out.println("DDDD"+fstilling.toString());
        for (int i = 0; i < 3 && i < fstilling.length(); i++) {
            String tittel = fstilling.getJSONObject(i).getString("tittel");
            String ID = fstilling.getJSONObject(i).getString("id");
            listItems.add(tittel);
            frstlng x = new frstlng(i, ID, tittel);
            frstlnglist.add(x);
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

        switch (item.getItemId()) {
            case R.id.action_settings:
                new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                        .setTitle("Informasjon")
                        .setMessage("Denne appen er utviklet med tanke på artistene. Her kan du reservere plasser med enten kupponger eller betaling. \n\n" +
                                "De fleste forestillinger vil også inneholde et album som du kan laste ned og høre på inne i appen og utenfor. \n\n" +
                                "På første siden vil dine billetter bli vist, dersom du har noen. Hvis ikke vil det bli vist de tre førstkommende forestillingene.")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                        .show();
        }

        return super.onOptionsItemSelected(item);
    }
     //
    public void openCoupon(View view) {
                Intent slideactivity = new Intent(MainActivity.this, Coupon.class);
                startActivity(slideactivity);

    }
    public void openMineBilletter(View view) {
                Intent slideactivity = new Intent(MainActivity.this, MineBilletter.class);
                startActivity(slideactivity);

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
            Log.d("TEST", "jeg er her");

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("id", email));
            //params.add(new BasicNameValuePair("id", "Ole23@gmail.com"));
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
                            cv.put(db.SANG,tempjson.getString("sang"));

                        db.insert(cv);

                        System.out.println("etter if");
                    }
                    System.out.println("etter while");
                    db.close();
                }
                else{
                    List<NameValuePair> x = new ArrayList<>();
                    json = jsonParser.makeHttpRequest(url_getForestillinger,"POST",x);//hent forestillinger som brukeren kan kjope.
                    hc=json;
                    ekstern = true;
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


