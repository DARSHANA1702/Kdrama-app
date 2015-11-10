package com.example.amahan.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by amahan on 11/3/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Drama.db";

    public static final String DRAMA_TABLE_NAME = "drama";
    public static final String GENRE_TABLE_NAME = "genre";
    public static final String CAST_TABLE_NAME = "cast";

    public static final String DRAMA_COLUMN_ID = "id";
    public static final String DRAMA_COLUMN_NAME = "name";
    public static final String DRAMA_COLUMN_SYNOPSIS = "synopsis";
    public static final String DRAMA_COLUMN_DATE = "date";
    public static final String DRAMA_COLUMN_IMAGE = "image";
    public static final String DRAMA_COLUMN_RATING = "rating";

    public static final String CAST_COLUMN_DRAMAID = "dramaId";
    public static final String CAST_COLUMN_CASTNAME = "castName";

    public static final String GENRE_COLUMN_DRAMAID = "dramaId";
    public static final String GENRE_COLUMN_GENRENAME = "genreName";

    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table drama " +
                        "(id integer primary key AUTOINCREMENT, name text,synopsis text, date int, image text, rating int)"
        );

        db.execSQL(
                "create table cast " +
                        "(dramaId integer, castName text)"
        );
        db.execSQL(
                "create table genre " +
                        "(dramaId integer, genreName text)"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS drama");
        db.execSQL("DROP TABLE IF EXISTS cast");
        db.execSQL("DROP TABLE IF EXISTS genre");
        onCreate(db);
    }

    public boolean insertDrama(String name, String synopsis, int date, String image, int rating)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("synopsis", synopsis);
        contentValues.put("date", date);
        contentValues.put("image", image);
        contentValues.put("rating", rating);
        db.insert("drama", null, contentValues);
        return true;
    }
    public boolean insertCast(int dramaId, String castName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("dramaId", dramaId);
        contentValues.put("castName", castName);
        db.insert("cast", null, contentValues);
        return true;
    }
    public boolean insertGenre(int dramaId, String genreName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("dramaId", dramaId);
        contentValues.put("genreName", genreName);
        db.insert("genre", null, contentValues);
        return true;
    }
    public ArrayList<String> getAllDramaImages()
    {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from drama", null);
        res.moveToFirst();
        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(DRAMA_COLUMN_IMAGE)));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }


    public LinkedHashMap getAllDrama(String genre, String sort)
    {;
        String query ="";
        if (genre == "all")
        {
            if (sort == "all"){
                query = "select * from drama";
            }
            else if(sort == "date"){
                query = "select * from drama order by date desc";
            }
            else if(sort == "name"){
                query = "select * from drama order by name";
            }
            else if(sort == "rating"){
                query = "select * from drama order by rating desc";
            }
        }
        else
        {
            if (sort == "all"){
                query = "select * from drama where id in (select dramaId from genre where genreName = '" + genre + "')";
            }
            else if(sort == "date"){
                query = "select * from drama where id in (select dramaId from genre where genreName = '" + genre + "') order by date desc";
            }
            else if(sort == "name"){
                query = "select * from drama where id in (select dramaId from genre where genreName = '" + genre + "') order by name";
            }
            else if(sort == "rating"){
                query = "select * from drama where id in (select dramaId from genre where genreName = '" + genre + "') order by rating desc";
            }
        }


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(query, null);
        LinkedHashMap drama = new LinkedHashMap();
        res.moveToFirst();
        while(res.isAfterLast() == false)
        {
            LinkedHashMap dramaInfo = new LinkedHashMap();
            dramaInfo.put("id",res.getInt(res.getColumnIndex(DRAMA_COLUMN_ID)));
            dramaInfo.put("image",res.getString(res.getColumnIndex(DRAMA_COLUMN_IMAGE)));

            //TODO put the rest!

            drama.put(res.getString(res.getColumnIndex(DRAMA_COLUMN_NAME)),dramaInfo);
            res.moveToNext();

        }
        res.close();
        return drama;
    }

    public String getSynopsis(int id){

        String synopsis;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from drama where id="+id+"", null );
        res.moveToFirst();

        synopsis = res.getString(res.getColumnIndex(DRAMA_COLUMN_SYNOPSIS));
        res.close();
        return synopsis;
    }
    public String getName(int id){

        String name;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from drama where id="+id+"", null );
        res.moveToFirst();

        name = res.getString(res.getColumnIndex(DRAMA_COLUMN_NAME));
        res.close();
        return name;
    }
    public int getDate(int id){

        int date;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from drama where id="+id+"", null );
        res.moveToFirst();

        date = res.getInt(res.getColumnIndex(DRAMA_COLUMN_DATE));
        res.close();
        return date;
    }
    public int getRating(int id){

        int rating;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from drama where id="+id+"", null );
        res.moveToFirst();

        rating = res.getInt(res.getColumnIndex(DRAMA_COLUMN_RATING));
        res.close();
        return rating;
    }
    public String getImage(int id){

        String image;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from drama where id=" + id + "", null);
        res.moveToFirst();

        image = res.getString(res.getColumnIndex(DRAMA_COLUMN_IMAGE));
        res.close();
        return image;
    }

    public boolean checkDrama()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from drama", null);
        return res.moveToFirst();
    }


    public void destroy()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS drama");
        db.execSQL("DROP TABLE IF EXISTS cast");
        db.execSQL("DROP TABLE IF EXISTS genre");
        onCreate(db);
    }

    //TODO: cast and genre methods

}
