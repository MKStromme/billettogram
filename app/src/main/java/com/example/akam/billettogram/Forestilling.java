package com.example.akam.billettogram;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Forestilling extends AppCompatActivity {

    ListView dagensaktivitet;
    TextView txt;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    JSONParser jsonParser = new JSONParser();
    JSONObject hc=null;
    DBAdapter db;
    JSONArray arng;
    int success;
    int sumresult;
    String msg;
    final Context c = this;
    public String s;
    Spinner ant;
    TextView sumtext;
    public String bildet;
    public ImageView bildeplass;
    private String url_getForestilling= "http://barnestasjonen.no/test/db_get_forestilling.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forestilling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ant =(Spinner)findViewById(R.id.antplass);
        sumtext=(TextView)findViewById(R.id.sum);
        bildeplass = (ImageView)findViewById(R.id.imageplass);

        ant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sumtext.setText(String.valueOf(sumresult * (position + 1)) + " kr");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Bundle extras = getIntent().getExtras();
         s = extras.getString("selectedItem");

        try {
            new getShows().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        boolean notimage = true;

        if(notimage){

            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "Pictures" + File.separator + "Centertainment");
            if (!folder.exists()) {
                folder.mkdir();
                System.out.println("file img make");
            }

            boolean isimage = true;
            System.out.println("for file");
            String bildeTxt = Environment.getExternalStorageDirectory() + File.separator + "Pictures" + File.separator + "Centertainment" + File.separator + bildet;
            File image = new File(bildeTxt);
            System.out.println(bildeTxt);
            if(!image.exists()){

                System.out.println("xxxx");
                DownloadImage dli = new DownloadImage();
                try {
                    dli.execute().get();
                } catch (InterruptedException e) {
                    System.out.println("rrrrrrRR" + e.toString());
                } catch (ExecutionException e) {
                    System.out.println("rrrrrrRRTT" + e.toString());
                }
            }
        }
        Bitmap imagebit = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + File.separator + "Pictures" + File.separator + "Centertainment" + File.separator + bildet);
        bildeplass.setImageBitmap(imagebit);
    }

    public void fromExtDB() throws JSONException
    {

        arng = hc.getJSONArray("forestilling");
        TextView tit = (TextView)findViewById(R.id.tittel);
        TextView dato = (TextView)findViewById(R.id.dato);
        TextView varighet=(TextView)findViewById(R.id.varighet);
        TextView antall=(TextView)findViewById((R.id.antled));
        TextView plass=(TextView)findViewById((R.id.place));
        TextView pris =(TextView)findViewById(R.id.pris);
         


        for(int i=0;i<arng.length();i++) {
            arng.getJSONObject(i).getString("tittel");
            System.out.println("ggggggggg" + arng.getJSONObject(i).getString("tittel"));
            tit.append(arng.getJSONObject(i).getString("tittel"));
            dato.append(arng.getJSONObject(i).getString("dato"));
            antall.append(arng.getJSONObject(i).getString("ledigePlasser"));
            varighet.append(arng.getJSONObject(i).getString("varighet"));
            plass.append(arng.getJSONObject(i).getString("fylke"));
            pris.append(arng.getJSONObject(i).getString("pris"));
            sumresult = Integer.parseInt(arng.getJSONObject(i).getString("pris"));
            System.out.println("ggggggggg" + arng.getJSONObject(i).getString("bilde"));

            bildet = arng.getJSONObject(i).getString("bilde");
            System.out.println("11111"+bildet);
        }
        /*TextView tit = (TextView)findViewById(R.id.tittel);
        TextView dato = (TextView)findViewById(R.id.dato);
        TextView varighet=(TextView)findViewById(R.id.varighet);
        TextView antall=(TextView)findViewById((R.id.ant));

        tit.setText(hc.getJSONObject("forestilling").getString("tittel"));*/

    }
    public void betall(View view) {
        Intent intent = new Intent(Forestilling.this, Betaling.class);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forestilling, menu);
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

    class getShows extends AsyncTask<String,String,String>
    {

        @Override
        protected String doInBackground(String... args) {

            url_getForestilling += "?id=" + s;
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", s));
            JSONObject json = jsonParser.makeHttpRequest(url_getForestilling, "POST", params);

            try {
                if(json != null) {
                    success = Integer.parseInt(json.getString("success"));
                    msg = json.toString();
                    System.out.println("Vi er her:" + msg + "   " + success);
                }

                if (success == 1) {
                    hc = json;
                    bildet = hc.getString("bilde");
                    System.out.println(json.toString());
                }
                else{
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
                if(success == 1)
                fromExtDB();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    private class DownloadImage extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            System.out.println("22222"+bildet);
            String x = null;
            int count;
            x = Environment.getExternalStorageDirectory() +
                    File.separator + "Pictures" + File.separator + "Centertainment" + File.separator + bildet;
            System.out.println("aaasss"+x);
            try {
                System.out.println(bildet);
                String parsedstring = bildet;
                parsedstring = parsedstring.replaceAll(" ", "%20");
                String urlstring = "http://www.barnestasjonen.no/test/images/"+parsedstring;
                System.out.println("jjjjjllll" + urlstring);
                URL url = new URL(urlstring);
                System.out.println("hhhhh");
                URLConnection conexion = url.openConnection();
                conexion.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conexion.getContentLength();

                // downlod the file
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(x);

                System.out.println("ttttt");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    publishProgress((int) (total * 100 / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            }
            catch (Exception e){
                System.out.println("eerr" + e.toString());

            }
            System.out.println("not error");

            return null;
        }
    }


}
