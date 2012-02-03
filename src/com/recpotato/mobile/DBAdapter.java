package com.recpotato.mobile;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter 
{
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_VALUE = "value";  
    public static final String KEY_DESCRIPTION = "description";
    
    private static final String TAG = "RP Database";
    
    private static final String DATABASE_NAME = "recpotato";
    private static final String DATABASE_TABLE = "tasks";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE =
        "create table " + DATABASE_TABLE + " (_id integer primary key autoincrement, "
        + KEY_NAME + " text not null, "
        + KEY_LOCATION + " text not null, "
        + KEY_VALUE + " text not null, " 
        + KEY_DESCRIPTION + " text not null);";
        
    private final Context context; 
    
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) 
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
        
    private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) 
        {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion 
                    + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }    
    
    //---opens the database---
    public DBAdapter open() throws SQLException 
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---    
    public void close() 
    {
        DBHelper.close();
    }
    
    /**
     * insert a task into the database
     * @param name
     * @param location
     * @param value
     * @param description
     * @return
     */
    public long insertTask(String name, String location, String value, String description) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_LOCATION, location);
        initialValues.put(KEY_VALUE, value);
        initialValues.put(KEY_DESCRIPTION, description);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }
    
    public boolean clearTasks() {
    	return db.delete(DATABASE_TABLE, KEY_ROWID + ">" + 0, null) > 0;
    }

    //---deletes a particular title---
    public boolean deleteTask(long rowId) 
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //---retrieves all the titles---
    public Cursor getAllTasks() {
        return db.query(DATABASE_TABLE, new String[] {
        		KEY_ROWID, 
        		KEY_NAME,
        		KEY_LOCATION,
                KEY_VALUE,
                KEY_DESCRIPTION}, 
                null, 
                null, 
                null, 
                null, 
                null);
    }

    //---retrieves a particular title---
    public Cursor getTask(long rowId) throws SQLException 
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {
                		KEY_ROWID,
                		KEY_NAME, 
                		KEY_LOCATION,
                		KEY_VALUE,
                		KEY_DESCRIPTION
                		}, 
                		KEY_ROWID + "=" + rowId, 
                		null,
                		null, 
                		null, 
                		null, 
                		null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a title---
    public boolean updateTask(long rowId, String name, 
    String location, String value, String description) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_LOCATION, location);
        args.put(KEY_VALUE, value);
        args.put(KEY_DESCRIPTION, description);
        return db.update(DATABASE_TABLE, args, 
                         KEY_ROWID + "=" + rowId, null) > 0;
    }
}