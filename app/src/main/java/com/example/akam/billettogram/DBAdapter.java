package com.example.akam.billettogram;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.Date;

public class DBAdapter {
    Context context;
    static final String TAG = "DbHelper";
    static final String DB_NAVN = "mydb.db";
    static final String TABELL = "billetter";
    static final String ID = BaseColumns._ID;
    static final String DATE = "dato";
    static final String FID = "fid";
    static final String TITTEL = "tittel";
    static final String PRIS = "pris";
    static final String BILDET = "bildet";
    static final String ANTALL = "antall";
    static final String KODE = "kode";
    static final String TIME = "time";
    static final String SANG = "sang";

    static final int ANTALLPAFORSIDEN = 3;

    static final int DB_VERSJON = 1;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;


    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {

            super(context, DB_NAVN, null, DB_VERSJON);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql="create table "+ TABELL+ "( "
                    + ID+ " integer primary key autoincrement,"
                    + DATE+ " date,"
                    + FID + " int,"
                    + TITTEL+" text,"
                    + PRIS+ " int,"
                    + BILDET+" text,"
                    + ANTALL+" int,"
                    + KODE+" text,"
                    + TIME+ " date,"
                    + SANG +" text);";
            Log.d(TAG, "oncreated sql" + sql);
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("drop table if exists " + TABELL);
            Log.d(TAG,"updated");
            onCreate(db);
        }
    }

    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }
    public void close()
    {
    }

    public void insert(ContentValues cv) {db.insert(TABELL, null, cv);}

    public Cursor treSisteForestilling()
    {
        String[] cols = {ID,TITTEL,DATE};

        Cursor cur = db.query(TABELL,cols,null,null,null,null,DATE + " DESC","" + ANTALLPAFORSIDEN);
        return cur;
    }

    public Cursor visAlle()
    {
        String[] cols = {ID,TITTEL,DATE};

        Cursor cur = db.query(TABELL,cols,null,null,null,null,ID);
        return cur;
    }


    public Cursor finnPersonMId(int id)
    {
        String[] cols = {ID,DATE,FID,TITTEL,PRIS,BILDET,ANTALL,KODE,TIME,SANG};
        //String sql = "SELECT * FROM " + TABELL + " WHERE " + ID + " = " + id;
        Log.d("ID", "!!!!!!!!!-------" + id);
        //Cursor cur = db.rawQuery(sql, null);
        Cursor cur = db.query(TABELL,cols,ID + " == " + id,null,null,null,ID);
        cur.moveToFirst();
        Log.d("DATA", "!!!!!!!!!-------" + id);
        return cur;
    }


    public String finnSang(int id)
    {
        String[] cols = {ID,DATE,FID,TITTEL,PRIS,BILDET,ANTALL,KODE,TIME,SANG};
        //String sql = "SELECT * FROM " + TABELL + " WHERE " + ID + " = " + id;
        Log.d("ID", "!!!!!!!!!-------" + id);
        //Cursor cur = db.rawQuery(sql, null);
        Cursor cur = db.query(TABELL,cols,ID + " == " + id,null,null,null,SANG);
        cur.moveToFirst();
        Log.d("DATA", "!!!!!!!!!-------" + id);
        return cur.toString();
    }

}
