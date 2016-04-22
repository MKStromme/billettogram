package com.example.akam.billettogram;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class Billett extends AppCompatActivity implements MediaController.MediaPlayerControl{

    DBAdapter db;
    Context context;
    int id;
    public JSONArray sangstring;
    File file;
    public MediaPlayer mpplayer;
    public ToggleButton tb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billett);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mpplayer = new MediaPlayer();
        tb=(ToggleButton)findViewById(R.id.playpause);
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

        tit.setText(cr.getString(cr.getColumnIndex(db.TITTEL)));
        dato.setText(cr.getString(cr.getColumnIndex(db.DATE)));
        varig.setText(cr.getString(cr.getColumnIndex(db.TIME)));
        antall.setText(cr.getString(cr.getColumnIndex(db.ANTALL)));
        try {
            sangstring = new JSONArray(cr.getString(cr.getColumnIndex(db.SANG)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void lastNedMusikk(View view) {

        File appfolder = new File(getFilesDir() + File.pathSeparator + "Music");
        if(!appfolder.exists()){
            appfolder.mkdir();
            System.out.println("kkkkk");
        }
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Download");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
            System.out.println("file make");
        }
        else {

            System.out.println("file unmake");
        }
        if (success) {
            // Do something on success
            System.out.println("file maked");
        } else {
            // Do something else on failure
            System.out.println("file not maked");
        }
        DownloadFile dlf = new DownloadFile();
        try {
            dlf.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        tbsetvisible();
    }

    public void startPause(View v){
        if(tb.getText().equals("❚❚")){
            start();
        }else if(tb.getText().equals("▶")){
            pause();
        }
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

    @Override
    public void start() {
        mpplayer.start();
    }

    @Override
    public void pause() {
        mpplayer.pause();
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int pos) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    public void tbsetvisible(){
        tb.setVisibility(View.VISIBLE);
    }

    private class DownloadFile extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... urlParams) {
            System.out.println("Magnus" + sangstring);
            int count;
            String x = null;
            try {
                x = Environment.getExternalStorageDirectory() +
                        File.separator + "Music" + File.separator + sangstring.getString(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for(int i = 0;i<sangstring.length();i++) {
                try {
                    System.out.println("Akam"+ sangstring.get(i));
                    String urlstring = "http://www.barnestasjonen.no/test/songs/"+sangstring.getString(i);
                    System.out.println("ggggg" + urlstring);
                    URL url = new URL(urlstring);
                    System.out.println("hhhhh");
                    URLConnection conexion = url.openConnection();
                    conexion.connect();
                    // this will be useful so that you can show a tipical 0-100% progress bar
                    int lenghtOfFile = conexion.getContentLength();

                    // downlod the file
                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() +
                            File.separator + "Music" + File.separator + sangstring.getString(i));


                    OutputStream localoutput = new FileOutputStream(getFilesDir() + File.pathSeparator + "Music" +
                            File.separator + sangstring.getString(i));

                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        publishProgress((int) (total * 100 / lenghtOfFile));
                        output.write(data, 0, count);
                        localoutput.write(data, 0, count);
                    }

                    output.flush();
                    output.close();
                    input.close();



                    //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                   // sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));

                    /*Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(file));
                    sendBroadcast(intent);*/

                } catch (Exception e) {
                    System.out.println("error: " + e.getLocalizedMessage());
                }
            }
            try {
                mpplayer.setDataSource(x);
                System.out.println("llllll1");
                mpplayer.prepare();
                System.out.println("llllll2");
                mpplayer.start();
                System.out.println("llllll3");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
