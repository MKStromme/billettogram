package com.example.akam.billettogram;

import android.app.AlertDialog;
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
import android.widget.ListView;
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
    //StableArrayAdapter adb;
    private static final String url_Forestillinger = "http://barnestasjonen.no/test/db_get_forestillinger.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forestillinger);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        dagensaktivitet = (ListView) findViewById(R.id.forestillinglist);
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
        /*final Context c = this;*/

        dagensaktivitet.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(getApplicationContext(), Forestilling.class);
                i.putExtra("selectedItem", listItems.get(position));
                startActivity(i);
            }
        });

    }

    public void fromExtDB() throws JSONException {
        fstilling = hc.getJSONArray("forestillinger");

        for (int i = 0; i < fstilling.length(); i++) {
            listItems.add(fstilling.getJSONObject(i).getString("id"));
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
