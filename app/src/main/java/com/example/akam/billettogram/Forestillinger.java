package com.example.akam.billettogram;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Forestillinger extends AppCompatActivity {

    ListView dagensaktivitet;
    TextView txt;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    JSONParser jsonParser = new JSONParser();
    JSONObject hc = null;
    JSONArray fstilling;
    public String hk = "";
    int success;
    String msg;
    final Context c = this;
    StableArrayAdapter adb;
    private static final String url_Forestillinger = "http://barnestasjonen.no/test/db_get_forestillinger.php";
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
        setContentView(R.layout.activity_forestillinger);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        dagensaktivitet = (ListView) findViewById(R.id.forestillingerlist);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView txt = (TextView) view.findViewById(android.R.id.text1);
                txt.setTextColor(Color.WHITE);
                return view;
            }
        };
        dagensaktivitet.setAdapter(adapter);
        Log.d("i oncreate", adapter.toString());

        //if(!fromLocalDB())
        try {
            new getShows().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //JSONObject tickets = task.getJSon();
        adapter.notifyDataSetChanged();
        dagensaktivitet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                System.out.println("index: "+ position);
                frstlng y = frstlnglist.get(position);
                System.out.println("ID: "+y.getID()+ ". tittel: "+y.getTittel());

                Intent i = new Intent(getApplicationContext(), Forestilling.class);
                i.putExtra("selectedItem", y.getID());
                startActivity(i);

            }
        });

    }

    public void fromExtDB() throws JSONException {
        fstilling = hc.getJSONArray("forestillinger");
        for (int i = 0; i < fstilling.length(); i++) {
            String tittel = fstilling.getJSONObject(i).getString("tittel");
            String ID = fstilling.getJSONObject(i).getString("id");
            listItems.add(tittel);
            frstlng x = new frstlng(i, ID, tittel);
            frstlnglist.add(x);

        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forestillinger, menu);
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

    public void forestillingResult(){
        new AlertDialog.Builder(Forestillinger.this)
                .setTitle("OBS!!!")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        hk = "";
                        finish();
                    }
                })
                .show();
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
    class getShows extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            Log.d("TEST", "jeg er her");

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            //params.add(new BasicNameValuePair("id", "adam@gmail.com"));
            //params.add(new BasicNameValuePair("type", "android"));

            JSONObject json = jsonParser.makeHttpRequest(url_Forestillinger, "POST", params);

            try {
                if (json != null) {
                    success = Integer.parseInt(json.getString("success"));
                    msg = json.toString();
                    System.out.println("Vi er her:" + msg + "   " + success);

                }

                if (success == 1) {
                    hc = json;
                    System.out.println("jojo"+json.toString());

                }
                else
                {

                    msg=json.getString("message");
                    System.out.println(msg);
                    msg= Html.fromHtml(msg).toString();
                    hk = msg;

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            //run code
            try {
                Log.d("TAG23", "vi er in onpostExecute");
                if(success == 1) {
                    fromExtDB();
                }else{
                    forestillingResult();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
