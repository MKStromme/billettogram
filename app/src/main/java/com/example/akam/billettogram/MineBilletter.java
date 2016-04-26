package com.example.akam.billettogram;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MineBilletter extends AppCompatActivity {


    ListView dagensaktivitet;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    DBAdapter db;
    JSONObject hc= null;
    JSONArray tickets;
    final Context c = this;
    StableArrayAdapter adb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_billetter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Gjort nå
        db = new DBAdapter(this);
        db.open();



        dagensaktivitet = (ListView) findViewById(R.id.mytickets);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView txt = (TextView) view.findViewById(android.R.id.text1);
                txt.setTextColor(Color.WHITE);
                return view;
            }
        };
        dagensaktivitet.setAdapter(adapter);

        fromLocalDB();
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

    public void fromExtDB() throws JSONException
    {
        tickets = hc.getJSONArray("tickets");

        for (int i = 0; i < tickets.length(); i++) {
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

        Cursor cur = db.visAlle();
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
        getMenuInflater().inflate(R.menu.menu_minebilletter, menu);
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

}
