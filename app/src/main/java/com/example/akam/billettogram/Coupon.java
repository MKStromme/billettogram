package com.example.akam.billettogram;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
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
import android.widget.Spinner;
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
    Spinner ant;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DBAdapter(this);
        //db.open();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_coupon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                        .setTitle("Informasjon")
                        .setMessage("Her kan du skrive inn en kode som vil gi deg en billett. Denne billeten fungerer på akkuratt samme måte som andre billetter. \n\n" +
                                "Du kan velge hvor mange personer du ønsker å ta med opp til 10. \n\n" +
                                "Du kan kun bruke hver kupong én gang")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                        .show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void acceptQr(View view) {
        final Context context = this;
        qrFelt = (EditText) findViewById(R.id.qrcode);
        ant =(Spinner)findViewById(R.id.antplass);
        qraccept =(Button)findViewById(R.id.accept);
                String kode = qrFelt.getText().toString();
                String antpl = ant.getSelectedItem().toString();
                new CreateNewProduct().execute(kode,antpl);
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
            params.add(new BasicNameValuePair("id", MainActivity.email));
            params.add(new BasicNameValuePair("kode", args[0].toString()));
            params.add(new BasicNameValuePair("type", "android"));
            params.add(new BasicNameValuePair("antall", args[1].toString()));

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
                    Log.d("test5", json.toString());

                    db.open();
                    ContentValues cv = new ContentValues();

                    cv.put(db.DATE,json.getString("date"));
                    cv.put(db.FID,Integer.parseInt(json.getString("fid")));
                    cv.put(db.TITTEL,json.getString("tittel"));
                    cv.put(db.PRIS,Integer.parseInt(json.getString("pris")));
                    cv.put(db.BILDET,json.getString("bilde"));
                    cv.put(db.KODE,json.getString("kode"));
                    cv.put(db.ANTALL,Integer.parseInt(json.getString("antall")));
                    cv.put(db.TIME,json.getString("time"));
                    cv.put(db.SANG,json.getString("sang"));


                    db.insert(cv);

                    //successfully created product
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