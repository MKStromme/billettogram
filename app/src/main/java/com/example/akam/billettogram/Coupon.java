package com.example.akam.billettogram;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.text.Html;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Coupon extends AppCompatActivity {

    JSONParser jsonParser = new JSONParser();
    EditText qrFelt;
    Button qraccept;
    public String hk = "";
    DBAdapter db;
    //int success = jsonObject.getInt("success");
    private static String url_bestillCoupon = "http://barnestasjonen.no/test/db_validate_coupon.php";
    private static final String TAG_SUCCESS = "Success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_coupon, menu);
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

    public void acceptQr(View view) {
        final Context context = this;
        qrFelt = (EditText) findViewById(R.id.qrcode);
        qraccept =(Button)findViewById(R.id.accept);
        qraccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kode = qrFelt.getText().toString();
                new CreateNewProduct().execute(kode);
            }
        });
    }

    public void openCamera(View view) {
        final Context context = this;

        Button scannerButton = (Button) findViewById(R.id.scan);
        scannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), BarcodeScanner.class);
                startActivity(intent);
            }
        });
    }

    public void couponResult(){
        new AlertDialog.Builder(Coupon.this)
                .setTitle("OBS!!!")
                .setMessage(hk)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        hk="";
                    }
                })
                .show();
    }

    class CreateNewProduct extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {

            String msg = "";
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", "test@gmail.com"));
            params.add(new BasicNameValuePair("kode", args[0]));
            params.add(new BasicNameValuePair("type", "android"));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_bestillCoupon,"POST", params);

            // check log cat fro response
            // Log.d("Create Response", json.toString());
            // check for success tag
            try {
                int success= Integer.parseInt(json.getString("success"));
                msg=json.getString("message");

                if (success == 1) {
                    Log.d("test5",json.toString());
                    db.open();
                    db.oppdater(json.getString("date"),Integer.parseInt(json.getString("fid")),json.getString("tittel"),Integer.parseInt(json.getString("pris")),json.getString("bilde"),Integer.parseInt(json.getString("antall")),json.getString("kode"),json.getString("time"));

                    db.close();

                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else{
                    //dialogvindu med json message
                    //couponResult(json.getString("message"));
                    System.out.println(msg);
                    msg=Html.fromHtml(msg).toString();
                    hk = msg;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            //pDialog.dismiss();
            couponResult();
        }

    }
}
