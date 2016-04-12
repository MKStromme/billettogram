package com.example.akam.billettogram;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class Billett extends AppCompatActivity {

    DBAdapter db;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billett);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DBAdapter(this);
        db.open();

        Bundle extras = getIntent().getExtras();
        Log.d("TAG:", "" + extras.getInt("TryThis"));

        Intent intent = getIntent();
        id = extras.getInt("TryThis");
        //id++;

        TextView tit = (TextView)findViewById(R.id.tittel);
        TextView dato = (TextView)findViewById(R.id.dato);
        TextView varig = (TextView) findViewById(R.id.varighet);
        TextView antall=(TextView)findViewById(R.id.ant);
        Cursor cr = db.finnPersonMId(id);

        tit.setText("Tittel:    "+cr.getString(cr.getColumnIndex(db.TITTEL)));
        dato.setText("Dato:     "+cr.getString(cr.getColumnIndex(db.DATE)));
        varig.setText("Kjøpsdato:   "+cr.getString(cr.getColumnIndex(db.TIME)));
        antall.setText("Antall billetter:    "+cr.getString(cr.getColumnIndex(db.ANTALL)));







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
