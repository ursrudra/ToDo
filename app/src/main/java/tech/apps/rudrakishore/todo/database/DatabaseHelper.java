package tech.apps.rudrakishore.todo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tech.apps.rudrakishore.todo.database.model.Item;

/**
 * Created by Rudra Kishore on 21-03-2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "todo_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Item.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Item.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertItem(String item, double qty, String metric) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Item.COLUMN_NOTE, item);
        values.put(Item.COLUMN_QTY, qty);
        values.put(Item.COULMN_QTY_METRIC, metric);
        // insert row
        long id = db.insert(Item.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }


    public Item getItem(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Item.TABLE_NAME,
                new String[]{Item.COLUMN_ID, Item.COLUMN_NOTE, Item.COLUMN_QTY, Item.COULMN_QTY_METRIC, Item.COLUMN_TIMESTAMP, Item.COLUMN_STATUS},
                Item.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        Item note = new Item(
                cursor.getInt(cursor.getColumnIndex(Item.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Item.COLUMN_NOTE)),
                cursor.getDouble(cursor.getColumnIndex(Item.COLUMN_QTY)),
                cursor.getString(cursor.getColumnIndex(Item.COULMN_QTY_METRIC)),
                cursor.getString(cursor.getColumnIndex(Item.COLUMN_TIMESTAMP)),
                cursor.getInt(cursor.getColumnIndex(Item.COLUMN_STATUS)));

        // close the db connection
        cursor.close();

        return note;
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Item.TABLE_NAME + " GROUP BY " + Item.COLUMN_TIMESTAMP + " ORDER BY " +
                Item.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(cursor.getInt(cursor.getColumnIndex(Item.COLUMN_ID)));
                item.setItem(cursor.getString(cursor.getColumnIndex(Item.COLUMN_NOTE)));
                item.setQty(cursor.getDouble(cursor.getColumnIndex(Item.COLUMN_QTY)));
                item.setMetric(cursor.getString(cursor.getColumnIndex(Item.COULMN_QTY_METRIC)));
                item.setTimestamp(cursor.getString(cursor.getColumnIndex(Item.COLUMN_TIMESTAMP)));
                item.setStatus(cursor.getInt(cursor.getColumnIndex(Item.COLUMN_STATUS)));

                items.add(item);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return items;
    }

    public int getItemsCount() {
        String countQuery = "SELECT  * FROM " + Item.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int getItemsCountParm(String da) {

        String parm = formatDate(da);
        String countQuery = "SELECT * FROM " + Item.TABLE_NAME + " WHERE strftime('%Y%m%d'," + Item.COLUMN_TIMESTAMP + ") = '" + parm + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int getAvailbleItemsCount(String da) {

        String parm = formatDate(da);
        String countQuery = "SELECT * FROM " + Item.TABLE_NAME + " WHERE strftime('%Y%m%d'," + Item.COLUMN_TIMESTAMP + ") = '" + parm + "' AND " + Item.COLUMN_STATUS + " = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Item.COLUMN_NOTE, item.getItem());
        values.put(Item.COLUMN_QTY, item.getQty());
        values.put(Item.COULMN_QTY_METRIC, item.getMetric());
        values.put(Item.COLUMN_STATUS, item.getStatus());


        // updating row
        return db.update(Item.TABLE_NAME, values, Item.COLUMN_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    public void deleteItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Item.TABLE_NAME, Item.COLUMN_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
        db.close();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("yyyyMMdd");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }

}
