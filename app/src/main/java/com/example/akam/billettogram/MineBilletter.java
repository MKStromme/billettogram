package com.example.akam.billettogram;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MineBilletter extends AppCompatActivity {


    ListView dagensaktivitet;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    DBAdapter db;
    JSONObject hc= null;
    JSONArray tickets;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_billetter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




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

        db = new DBAdapter(this);
        db.open();

        Cursor cur = db.visAlle();
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

}
