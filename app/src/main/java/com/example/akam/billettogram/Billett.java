package com.example.akam.billettogram;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TableLayout;
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
    public String tittel;
    public Button dw;
    public String currentSong;
    public TextView songnavn;
    public TableLayout media;
    public String bildenavn;
    public ImageView bildeplass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billett);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //mpplayer = new MediaPlayer();
        songnavn = (TextView)findViewById(R.id.songtitle);
        media=(TableLayout)findViewById(R.id.mediatable);
        bildeplass = (ImageView)findViewById(R.id.imageplass);
        tb=(ToggleButton)findViewById(R.id.playpause);
        db = new DBAdapter(this);
        db.open();
        dw = (Button)findViewById(R.id.download);

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
        boolean notsang = true;
        System.out.println("QQQ "+cr.getString(cr.getColumnIndex(db.SANG)));

        if(cr.getString(cr.getColumnIndex(db.SANG)).equals("null")) {
            System.out.println("MMMM" + cr.getString(cr.getColumnIndex(db.SANG)));
            notsang = false;
            dw.setEnabled(false);
            dw.setText("Ingen sang");
        }
        tittel = cr.getString(cr.getColumnIndex(db.TITTEL));
        tit.setText(cr.getString(cr.getColumnIndex(db.TITTEL)));
        dato.setText(cr.getString(cr.getColumnIndex(db.DATE)));
        varig.setText(cr.getString(cr.getColumnIndex(db.TIME)));
        antall.setText(cr.getString(cr.getColumnIndex(db.ANTALL)));
        if(notsang){
            try {
                sangstring = new JSONArray(cr.getString(cr.getColumnIndex(db.SANG)));
                boolean issong = true;
                for(int i = 0; i < sangstring.length(); i++){
                    File sang = new File(Environment.getExternalStorageDirectory() + File.separator + "Music" + File.separator + "Centertainment" + File.separator + tittel + File.separator + sangstring.getString(i));
                    if(!sang.exists()){
                        issong = false;
                    }
                }
                if(issong){
                    dw.setVisibility(View.GONE);
                    dw.setEnabled(false);
                    tbsetvisible();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        boolean notimage = true;

        if(cr.getString(cr.getColumnIndex(db.BILDET)).equals("null")) {
            System.out.println("MMMM" + cr.getString(cr.getColumnIndex(db.BILDET)));
            notimage = false;
        }
        if(notimage){

            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "Pictures" + File.separator + "Centertainment");
            if (!folder.exists()) {
                folder.mkdir();
                System.out.println("file img make");
            }
            bildenavn = cr.getString(cr.getColumnIndex(db.BILDET));
            boolean isimage = true;
            System.out.println("for file");
            String bildeTxt = Environment.getExternalStorageDirectory() + File.separator + "Pictures" + File.separator + "Centertainment" + File.separator + bildenavn;
            File image = new File(bildeTxt);
            System.out.println(bildeTxt);
            if(!image.exists()){


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
        Bitmap imagebit = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + File.separator + "Pictures" + File.separator + "Centertainment" + File.separator + bildenavn);
        bildeplass.setImageBitmap(imagebit);

    }
    public void lastNedMusikk(View view) {
        File appfolder = new File(getFilesDir() + File.separator + "Music");
        if(!appfolder.exists()){
            appfolder.mkdir();
            System.out.println("kkkkk");
        }
        appfolder = new File(getFilesDir() + File.separator + "Music" + File.separator + "Centertainment");
        if(!appfolder.exists()){
            appfolder.mkdir();
            System.out.println("pppp");
        }
        appfolder = new File(getFilesDir() + File.separator + "Music" + File.separator + "Centertainment" + File.separator + tittel);
        if(!appfolder.exists()){
            appfolder.mkdir();
            System.out.println("pppp");
        }
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Music");
        if (!folder.exists()) {
            folder.mkdir();
            System.out.println("file make");
        }
        folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Music" + File.separator + "Centertainment");

        if (!folder.exists()) {
            folder.mkdir();
            System.out.println("file make");
        }
        folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Music" + File.separator + "Centertainment" + File.separator + tittel);

        if (!folder.exists()) {
            folder.mkdir();
            System.out.println("file make");
        }


        DownloadFile dlf = new DownloadFile();
        try {
            dlf.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        try {
            currentSong = sangstring.getString(0);
            songnavn.setText(currentSong);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tbsetvisible();
    }

    @Override
    public void onBackPressed(){
        if(mpplayer != null) {
            mpplayer.stop();
        }
        finish();
    }
    public void startPause(View v){
        if(tb.getText().equals("❚❚")){
            start();
        }else if(tb.getText().equals("▶")){
            pause();
        }
    }



    public void backsong(View v) throws JSONException, IOException {
        System.out.println("back");
        if(sangstring.getString(0).equals(currentSong)){
            System.out.println("back2");
            mpplayer.stop();
            mpplayer.reset();
            mpplayer.setDataSource(Environment.getExternalStorageDirectory() + File.separator + "Music" + File.separator + "Centertainment" + File.separator + tittel + File.separator + sangstring.getString(0));
            mpplayer.prepare();
            if(tb.getText().equals("❚❚")) {
                start();
            }
            return;
        }
        for(int i = 1; i < sangstring.length(); i++){
            if(sangstring.getString(i).equals(currentSong)) {
                System.out.println("back3");
                mpplayer.stop();
                mpplayer.reset();

                mpplayer.setDataSource(Environment.getExternalStorageDirectory() + File.separator + "Music" + File.separator + "Centertainment" + File.separator + tittel + File.separator + sangstring.getString(i - 1));
                System.out.println("pomfritt" + Environment.getExternalStorageDirectory() + File.separator + "Music" + File.separator + "Centertainment" + File.separator + sangstring.getString(i - 1));
                currentSong = sangstring.getString(i-1);
                System.out.println("pompom " + currentSong);
                songnavn.setText(currentSong);
                mpplayer.prepare();
                if(tb.getText().equals("❚❚")) {
                    start();
                }
                return;
            }
        }
    }
    public void nextsong(View v) throws JSONException, IOException {
        System.out.println("next");
        if(sangstring.getString(sangstring.length()-1).equals(currentSong)){
            System.out.println("next2");
            mpplayer.stop();
            return;
        }
        for(int i = 0; i < sangstring.length(); i++){
            System.out.println("next3 " + currentSong + "  -  " + sangstring.getString(i));
            if(sangstring.getString(i).equals(currentSong)) {
                mpplayer.stop();
                mpplayer.reset();
                mpplayer.setDataSource(Environment.getExternalStorageDirectory() + File.separator + "Music" + File.separator + "Centertainment" + File.separator + tittel + File.separator + sangstring.getString(i+1));
                System.out.println("pomfritt" + Environment.getExternalStorageDirectory() + File.separator + "Music" + File.separator + "Centertainment" + File.separator + sangstring.getString(i + 1));
                currentSong = sangstring.getString(i+1);
                System.out.println("pompom " + currentSong);
                songnavn.setText(currentSong);
                mpplayer.prepare();

                if(tb.getText().equals("❚❚")) {
                    start();
                }
                return;
            }
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

        try {
            mpplayer = new MediaPlayer();
            mpplayer.setDataSource(Environment.getExternalStorageDirectory() + File.separator + "Music" + File.separator + "Centertainment" + File.separator + tittel + File.separator + sangstring.getString(0));
            System.out.println("llllll1");
            mpplayer.prepare();
            currentSong = sangstring.getString(0);
            songnavn.setText(currentSong);
            System.out.println("llllll2"+currentSong);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        media.setVisibility(View.VISIBLE);
        dw.setVisibility(View.GONE);
    }

    private class DownloadFile extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... urlParams) {
            System.out.println("Magnus" + sangstring);
            int count;
            String x = null;
            try {
                x = Environment.getExternalStorageDirectory() +
                        File.separator + "Music" + File.separator + "Centertainment" + File.separator + tittel + File.separator + sangstring.getString(0);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            for(int i = 0;i<sangstring.length();i++) {
                try {
                    String parsedstring = sangstring.getString(i);
                    parsedstring = parsedstring.replaceAll(" ", "%20");
                    System.out.println("Akam"+ sangstring.get(i));
                    String urlstring = "http://www.barnestasjonen.no/test/songs/"+parsedstring;
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
                            File.separator + "Music" + File.separator + "Centertainment" + File.separator + tittel + File.separator + sangstring.getString(i));


                    OutputStream localoutput = new FileOutputStream(getFilesDir() + File.separator + "Music" +
                            File.separator + "Centertainment" + File.separator + tittel + File.separator + sangstring.getString(i));

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
                    localoutput.flush();
                    localoutput.close();
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

            return null;
        }
    }

    private class DownloadImage extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String x = null;
            int count;
            x = Environment.getExternalStorageDirectory() +
                    File.separator + "Pictures" + File.separator + "Centertainment" + File.separator + bildenavn;
            try {
            String parsedstring = bildenavn;
            parsedstring = parsedstring.replaceAll(" ", "%20");
            String urlstring = "http://www.barnestasjonen.no/test/images/"+parsedstring;
            System.out.println("ggggg" + urlstring);
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
