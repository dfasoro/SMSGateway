package org.macgrenor.smsgateway.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

public class DataProvider {

    private static final String DATABASE_NAME = "smsgateway.db";
    private static final int DATABASE_VERSION = 1;
    
    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE messages ( " +
            		"ID INTEGER PRIMARY KEY, " +
            		"Sender VARCHAR NOT NULL, "  +
            		"ServiceCenter VARCHAR NULL, " +
            		"Message TEXT NOT NULL, "  +
            		"Pages INTEGER NOT NULL DEFAULT 0, "  +

            		"Processed INTEGER NOT NULL DEFAULT 0, "  +
            		"Valid INTEGER NOT NULL DEFAULT 0, "  +
            		"Responded INTEGER NOT NULL DEFAULT 0, "  +
            		"Posted INTEGER NOT NULL DEFAULT 0, "  +
           		          		
            		"MessageDate VARCHAR, "  +
            		"StoreDate VARCHAR, "  +
            		"Exported INTEGER NOT NULL DEFAULT 0);");
            
            db.execSQL("CREATE TABLE rewards ( " +
            		"Sender VARCHAR NOT NULL PRIMARY KEY, " +
            		"Pages INTEGER NOT NULL, " +
            		"Rewarded INTEGER NOT NULL, " +
            		"Modified VARCHAR);");
            
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        	/*Log.w("Maps", "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
        	if (oldVersion == 1 && newVersion == 2) {               
                db.execSQL("DROP TABLE incident;"); 
                db.execSQL("DROP TABLE media;");            
        	}
            
        	onCreate(db);*/
        }
    }

    private DatabaseHelper mOpenHelper;

    private DataProvider(Context cxt) {
        mOpenHelper = new DatabaseHelper(cxt);
    }
    
    private static DataProvider mDataProvider = null;
    
    public static synchronized void initDataProvider(Context cxt) {
    	mDataProvider = new DataProvider(cxt);
    }

    public static DataProvider getDataProvider() {
    	return mDataProvider;
    }

    public Cursor query(SQLiteQueryBuilder qb, String[] projection, String selection, 
    		String[] selectionArgs, String sortOrder, String limit) {        
        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder, limit);
        return c;
    }
    public long insert(String tbl, ContentValues values) {     	
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();        
        return db.insertOrThrow(tbl, null, values);
    }

    public long delete(String tbl, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return db.delete(tbl, where, whereArgs);
    }

    public int update(String tbl, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return db.update(tbl, values, where, whereArgs);
    }
    
    public void close() {
    	mOpenHelper.close();
    }
}
