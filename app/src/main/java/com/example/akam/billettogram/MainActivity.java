package com.example.akam.billettogram;

import android.app.ActionBar;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class MainActivity extends AppCompatActivity {

      ListView dagensaktivitet;
      TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dagensaktivitet=(ListView)findViewById(R.id.todaylist);
        txt=(TextView)findViewById(R.id.pid);

        getJSON task= new getJSON();
        task.execute(new String[]{"http://student.cs.hioa.no/~s198518/hovedprosjekt/admin/db_get_forestillinger.php"});
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

    public void openCoupon(View view){
       // final Context context = this;
        Button coupon = (Button)findViewById(R.id.koppong);
        coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent slideactivity = new Intent(MainActivity.this, Coupon.class);
                startActivity(slideactivity);
                //overridePendingTransition(android.R.anim., android.R.anim.slide_in_left);
            }
        });
    }

    public class getJSON extends AsyncTask<String,	Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String s="";
            String output="";

            for (String url :urls){
                try{
                    URL urlen=new URL(urls[0]);
                    HttpURLConnection conn=(HttpURLConnection)urlen.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "applicaRon/json");

                    if	(conn.getResponseCode()	!=	200)
                    {
                        throw new RuntimeException("Failed:HTTP error code:"+conn.getResponseCode());
                    }

                    BufferedReader br=	new BufferedReader(new InputStreamReader( (conn.getInputStream())));

                    System.out.println("Output from Server....\n");
                    while	((s=br.readLine())	!=	null)
                    {
                        output=output+s;

                    }

                    conn.disconnect();
                    return	output;
                }
                catch(Exception e){
                    return "Error!!!";
                }

            }
            return output;
        }
        protected void	onPostExecute(String ss){
            try {
                JSONObject jsonObject = new JSONObject(ss);

                JSONArray forestillinger = jsonObject.getJSONArray("forestillinger");

                for(int i=0;i<forestillinger.length();i++) {
                    String forstedato = forestillinger.getJSONObject(i).getString("dato");
                }

                int success = jsonObject.getInt("success");

            }
            catch (Exception e)
            {
            }
        }
    }

}
