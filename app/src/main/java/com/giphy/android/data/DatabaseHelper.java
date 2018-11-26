package com.giphy.android.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database to store favourite images in a SQLite table
 * The SQLite table includes 2 columns: Image URL and Image data ( bitmap)
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "image_database";

    // Table Name
    public static final String DB_TABLE = "image_table";

    // column to store image URL
    public static final String KEY_URL = "image_url";
    // column to store image data ( bitmap)
    public  static final String KEY_IMAGE = "image_data";

    // Table create
    private static final String CREATE_TABLE_IMAGE = "CREATE TABLE " + DB_TABLE + "("+
            KEY_URL + " TEXT," +
            KEY_IMAGE + " BLOB);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Table create
     */
    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL(CREATE_TABLE_IMAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);

        // create new table
        onCreate(db);
    }
}