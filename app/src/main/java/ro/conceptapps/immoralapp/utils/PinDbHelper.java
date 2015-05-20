package ro.conceptapps.immoralapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class PinDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "PinDbHelper";

    private static PinDbHelper mInstance;
    private static SQLiteDatabase db;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "pins.db";
    private static final String TABLE_PINS = "table_pins";
    private static final String TABLE_COL_PIN_ID = "table_col_pin_id";
    private static final String TABLE_COL_USER_ID = "table_col_user_id";
    private static final String TABLE_COL_TYPE = "table_col_type";
    private static final String TABLE_COL_DESCRIPTION = "table_col_description";
    private static final String TABLE_COL_LATITUDE = "table_col_latitude";
    private static final String TABLE_COL_LONGITUDE = "table_col_longitude";
    private static final String[] TABLE_ALL_COLS_PINS = {TABLE_COL_PIN_ID, TABLE_COL_USER_ID, TABLE_COL_TYPE, TABLE_COL_DESCRIPTION, TABLE_COL_LATITUDE, TABLE_COL_LONGITUDE};
    private static final String DATABASE_CREATE = ""
            + "CREATE TABLE "
            + TABLE_PINS
            + " ( "
            + TABLE_COL_PIN_ID
            + " INTEGER, "
            + TABLE_COL_USER_ID
            + " INTEGER, "
            + TABLE_COL_TYPE
            + " TEXT, "
            + TABLE_COL_DESCRIPTION
            + " TEXT, "
            + TABLE_COL_LATITUDE
            + " REAL, "
            + TABLE_COL_LONGITUDE
            + " REAL"
            + " );";


    private PinDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DATABASE_CREATE);
    }

    private static PinDbHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PinDbHelper(context);
            db = mInstance.getWritableDatabase();
        }
        return mInstance;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private static int getLastID(Context context) {
        getInstance(context);

        Cursor cursor = db.query(TABLE_PINS, TABLE_ALL_COLS_PINS, null, null, null, null, null);
        int id = cursor.getCount() + 1;
        cursor.close();
        return id;
    }

    public static void addPinToDatabase(Context context,int userId, String type, String description, double lat, double lng ) {
        getInstance(context);
        ContentValues values = new ContentValues();
        values.put(TABLE_COL_PIN_ID, getLastID(context));
        values.put(TABLE_COL_USER_ID, userId);
        values.put(TABLE_COL_TYPE, type);
        values.put(TABLE_COL_DESCRIPTION, description);
        values.put(TABLE_COL_LATITUDE, lat);
        values.put(TABLE_COL_LONGITUDE, lng);
        db.insert(TABLE_PINS, null, values);
        Log.d(TAG, values.toString());
    }

    public static ArrayList<Pin> getPinsFromDatabase(Context context){
        ArrayList<Pin> pins = new ArrayList<>();
        getInstance(context);

        Cursor cursor = db.query(TABLE_PINS, null,null,null,null,null,null );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Pin pin = new Pin();
            pin.id = cursor.getInt(0);
            pin.userId = cursor.getInt(1);
            pin.type = cursor.getString(2);
            pin.description = cursor.getString(3);
            pin.lat = cursor.getFloat(4);
            pin.lng = cursor.getFloat(5);
            pins.add(pin);
            cursor.moveToNext();
        }
        cursor.close();

        return pins;
    }

    public static ArrayList<Pin> selectPinsFromDatabase(Context context, String type){
        getInstance(context);
        ArrayList<Pin> pins = new ArrayList<>();

        Cursor cursor = db.query(TABLE_PINS, TABLE_ALL_COLS_PINS, TABLE_COL_TYPE +"='"+type+"'",null, null, null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Pin pin = new Pin();
            pin.id = cursor.getInt(0);
            pin.userId = cursor.getInt(1);
            pin.type = cursor.getString(2);
            pin.description = cursor.getString(3);
            pin.lat = cursor.getFloat(4);
            pin.lng = cursor.getFloat(5);
            pins.add(pin);
            cursor.moveToNext();
        }
        cursor.close();

        return pins;
    }


    public static void deletePin(Context context, int id) {
        getInstance(context);
        db.delete(TABLE_PINS, TABLE_COL_PIN_ID + " = '" + id + "'", null);
    }
}
