package asim.tgs_member_app.restclient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by sohaibkhalid on 11/1/16.
 *
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "clindoc_cache_v.db";

    // Request, Response table name
    public static final String TABLE_RR = "clindoc_cache_v_rrtable";

    // Request, Response Table Column names
    public static final String KEY_ID = "id";
    public static final String RESPONSE = "response";
    public static final String REQUEST =  "request";
    public static final String TIMESTAMP = "timestamp";
    public static final String MODEL = "model";
    private static SQLiteHelper sInstance;


    String DATABASE_CREATE = " CREATE TABLE  " + TABLE_RR + "("
            + RESPONSE + " INTEGER ,"
            + REQUEST + " TEXT NOT NULL UNIQUE ,"
            + TIMESTAMP+ " TEXT ,"
            + MODEL+ " BLOB , "
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " + ")";


    public static SQLiteHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new SQLiteHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RR);
        onCreate(db);
    }
}