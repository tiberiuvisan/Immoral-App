package licenta.fastbanking.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import licenta.fastbanking.Objects.Bank;


public class BankDbHelper extends SQLiteOpenHelper {


    /*
    * Baze de date pentru pini cu functiile de adaugare pin
    *
    * */
    private static final String TAG = "BankDbHelper";

    private static BankDbHelper mInstance;
    private static SQLiteDatabase db;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "banks.db";
    private static final String TABLE_BANKS = "table_banks";
    private static final String TABLE_COL_BANK_ID = "table_col_bank_id";
    private static final String TABLE_COL_BANK_NAME = "table_col_bank_name";
    private static final String TABLE_COL_BANK_COUNTERS_NUMBER = "table_col_bank_counter_numbers";
    private static final String TABLE_COL_BANK_WAIT_TIME = "table_col_bank_wait_time";
    private static final String TABLE_COL_BANK_TOTAL_PEOPLE = "table_col_bank_total_people";
    private static final String TABLE_COL_LATITUDE = "table_col_latitude";
    private static final String TABLE_COL_LONGITUDE = "table_col_longitude";
    private static final String[] TABLE_ALL_COLS_BANKS = {TABLE_COL_BANK_ID, TABLE_COL_BANK_NAME, TABLE_COL_BANK_COUNTERS_NUMBER,
            TABLE_COL_BANK_WAIT_TIME, TABLE_COL_BANK_TOTAL_PEOPLE, TABLE_COL_LATITUDE, TABLE_COL_LONGITUDE};
    private static final String DATABASE_CREATE = ""
            + "CREATE TABLE "
            + TABLE_BANKS
            + " ( "
            + TABLE_COL_BANK_ID
            + " INTEGER, "
            + TABLE_COL_BANK_NAME
            + " TEXT, "
            + TABLE_COL_BANK_COUNTERS_NUMBER
            + " INTEGER, "
            + TABLE_COL_BANK_WAIT_TIME
            + " INTEGER, "
            + TABLE_COL_BANK_TOTAL_PEOPLE
            + " INTEGER, "
            + TABLE_COL_LATITUDE
            + " REAL, "
            + TABLE_COL_LONGITUDE
            + " REAL"
            + " );";


    private BankDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DATABASE_CREATE);
    }

    private static BankDbHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new BankDbHelper(context);
            db = mInstance.getWritableDatabase();
        }
        return mInstance;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private static int getLastID(Context context) {
        getInstance(context);

        Cursor cursor = db.query(TABLE_BANKS, TABLE_ALL_COLS_BANKS, null, null, null, null, null);
        int id = cursor.getCount() + 1;
        cursor.close();
        return id;
    }

    public static void addBankToDatabase(Context context, String bankName, int countersNumber,  int waitTime, int totalPeople, double lat, double lng ) {
        getInstance(context);
        ContentValues values = new ContentValues();
        values.put(TABLE_COL_BANK_ID, getLastID(context));
        values.put(TABLE_COL_BANK_NAME, bankName);
        values.put(TABLE_COL_BANK_COUNTERS_NUMBER, countersNumber);
        values.put(TABLE_COL_BANK_WAIT_TIME, waitTime);
        values.put(TABLE_COL_BANK_TOTAL_PEOPLE, totalPeople);
        values.put(TABLE_COL_LATITUDE, lat);
        values.put(TABLE_COL_LONGITUDE, lng);
        db.insert(TABLE_BANKS, null, values);
        Log.d(TAG, values.toString());
    }

    public static ArrayList<Bank> getBanksFromDatabase(Context context){
        ArrayList<Bank> banks = new ArrayList<>();
        getInstance(context);

        Cursor cursor = db.query(TABLE_BANKS, null,null,null,null,null,null );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Bank bank = new Bank();
            bank.id = cursor.getInt(0);
            bank.name = cursor.getString(1);
            bank.countersNumber = cursor.getInt(2);
            bank.waitTime = cursor.getInt(3);
            bank.totalPeople = cursor.getInt(4);
            bank.lat = cursor.getFloat(5);
            bank.lng = cursor.getFloat(6);
            banks.add(bank);
            cursor.moveToNext();
        }
        cursor.close();

        return banks;
    }



    public static void deleteBank(Context context, int id) {
        getInstance(context);
        db.delete(TABLE_BANKS, TABLE_COL_BANK_ID + " = '" + id + "'", null);
    }
}
