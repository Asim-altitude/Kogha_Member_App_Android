package asim.tgs_member_app.restclient;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.Arrays;

/**
 *Created by sohaibkhalid on 11/1/16.
 */

public class DataSource {
    // Database fields
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    public DataSource(Context context) {
        dbHelper = SQLiteHelper.getInstance(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void add(int response, String request, String timeStamp, byte[] model){
        try{
            //TODO Replace InsertHelper as its deprecated.
            DatabaseUtils.InsertHelper insertHelper = new DatabaseUtils.InsertHelper(database, SQLiteHelper.TABLE_RR);
            insertHelper.prepareForReplace();

            insertHelper.bind(insertHelper.getColumnIndex(SQLiteHelper.RESPONSE),response);
            insertHelper.bind(insertHelper.getColumnIndex(SQLiteHelper.REQUEST),request);
            insertHelper.bind(insertHelper.getColumnIndex(SQLiteHelper.TIMESTAMP),timeStamp);
            insertHelper.bind(insertHelper.getColumnIndex(SQLiteHelper.MODEL),model);

            insertHelper.execute();
            insertHelper.close();


        }catch(Exception e){

            e.printStackTrace();
            Log.e("database Error", Arrays.toString(e.getStackTrace()));
        }

    }

    public String getTimeStamp(String request){

        Cursor cursor = database.query(SQLiteHelper.TABLE_RR, new String[] { SQLiteHelper.RESPONSE,
                        SQLiteHelper.REQUEST, SQLiteHelper.TIMESTAMP, SQLiteHelper.MODEL, SQLiteHelper.KEY_ID }, SQLiteHelper.REQUEST + "=?",
                new String[] { request }, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0)
            cursor.moveToFirst();

        String timeStamp = "0000-00-00 00:00:00";
        try{
            assert cursor != null;
            if(cursor.moveToFirst()){
                timeStamp = cursor.getString(cursor.getColumnIndex(SQLiteHelper.TIMESTAMP));
                cursor.close();
                return timeStamp;
            }

        }catch (SQLiteException e){
            e.printStackTrace();
            cursor.close();
            Log.e("Database Error", e.toString());
            return timeStamp;
        }

        return timeStamp;
    }

    public byte[] getModel(String request){

        Cursor cursor = database.query(SQLiteHelper.TABLE_RR, new String[] { SQLiteHelper.RESPONSE,
                        SQLiteHelper.REQUEST, SQLiteHelper.TIMESTAMP, SQLiteHelper.MODEL, SQLiteHelper.KEY_ID }, SQLiteHelper.REQUEST + "=?",
                new String[] {request}, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0)
            cursor.moveToFirst();

        assert cursor != null;

        byte[] bytes = cursor.getBlob(cursor.getColumnIndex(SQLiteHelper.MODEL));
        cursor.close();
        return bytes;
    }
}
