package licenta.fastbanking.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tiberiu Visan on 4/18/2016.
 * Project: FastBanking
 */
public class UserDbHelper extends SQLiteOpenHelper {
    private static UserDbHelper mInstance;
    private static SQLiteDatabase db;
    private static String TAG = "UserDbHelper";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "user.db";
    private static final String TABLE_USERS = "table_users";
    private static final String TABLE_COL_ID = "table_col_id";
    private static final String TABLE_COL_USER = "table_col_user";
    private static final String TABLE_COL_PASSWORD = "table_col_pass";
    private static final String TABLE_COL_PHONENUMBER = "table_col_phone";

    private static final String[] TABLE_ALL_COLS_USERS = {TABLE_COL_ID, TABLE_COL_USER, TABLE_COL_PASSWORD, TABLE_COL_PHONENUMBER};
    private static final String DATABASE_CREATE = ""
            + "CREATE TABLE "
            + TABLE_USERS
            + " ( "
            + TABLE_COL_ID
            + " INTEGER, "
            + TABLE_COL_USER
            + " TEXT, "
            + TABLE_COL_PASSWORD
            + " TEXT, "
            + TABLE_COL_PHONENUMBER
            + " TEXT"
            + " );";

    public UserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static UserDbHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new UserDbHelper(context);
            db = mInstance.getWritableDatabase();
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static int getLastID(Context context) {
        getInstance(context);


        Cursor cursor = db.query(TABLE_USERS, TABLE_ALL_COLS_USERS, null, null, null, null, null);
        int id = cursor.getCount() + 1;
        cursor.close();
        return id;
    }

    public static void addUserToDatabase(Context context, String user, String password, String phoneNumber) {
        getInstance(context);
        ContentValues values = new ContentValues();
        values.put(TABLE_COL_ID, getLastID(context));
        values.put(TABLE_COL_USER, user);
        values.put(TABLE_COL_PASSWORD, password);
        values.put(TABLE_COL_PHONENUMBER, phoneNumber);
        db.insert(TABLE_USERS, null, values);
    }
}
