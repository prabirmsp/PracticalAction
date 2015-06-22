package com.nepotech.practicalanswers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CommunityDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_TITLE,
            MySQLiteHelper.COLUMN_PARENT_ID,
            MySQLiteHelper.COLUMN_DSPACE_ID,
            MySQLiteHelper.COLUMN_LEVEL,
            MySQLiteHelper.COLUMN_LFT,
            MySQLiteHelper.COLUMN_RGT,
            MySQLiteHelper.COLUMN_IMAGEURL};

    public CommunityDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
        // TODO Auto-generated constructor stub
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Community createCommunity(String TABLE_NAME, String id, String title, String parent_id, String dspace_id, String level, String lft, String rgt, String imageurl) {

        ContentValues values = new ContentValues();
        //Log.e("all values->",""+ title + ":"+ parent_id + ":" + dspace_id + ":" + level+ ":"+lft +":"+ rgt );
        values.put(MySQLiteHelper.COLUMN_TITLE, title);
        values.put(MySQLiteHelper.COLUMN_PARENT_ID, parent_id);
        values.put(MySQLiteHelper.COLUMN_DSPACE_ID, dspace_id);
        values.put(MySQLiteHelper.COLUMN_LEVEL, level);
        values.put(MySQLiteHelper.COLUMN_LFT, lft);
        values.put(MySQLiteHelper.COLUMN_RGT, rgt);
        values.put(MySQLiteHelper.COLUMN_IMAGEURL, imageurl);
        long insertId = database.insert(TABLE_NAME, null, values);
        Cursor cursor = database.query(TABLE_NAME, allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, MySQLiteHelper.COLUMN_TITLE);
        cursor.moveToFirst();
        Community newCommunity = cursorToCommunity(cursor);
        cursor.close();
        return newCommunity;

    }

    private Community cursorToCommunity(Cursor cursor) {
        Community comm = new Community();
        comm.setId("" + cursor.getLong(0));
        comm.setTitle(cursor.getString(1));
        return comm;
    }

    public List<Community> getAllCommunities() {
        List<Community> communities = new ArrayList<Community>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMUNITY, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Community community = cursorToCommunity(cursor);
            communities.add(community);
            cursor.moveToNext();

        }
        cursor.close();
        return communities;

    }

    public Cursor getAllCommunitiesCursor() {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMUNITY, allColumns, null, null, null, null, null);
        return cursor;

    }

    public void removedb() {
        database = dbHelper.getWritableDatabase();
        database.delete(MySQLiteHelper.TABLE_COMMUNITY, null, null);
    }

    public Cursor fetchGroup() {
            /*String query = "SELECT * FROM rooms"
		    return mDb.rawQuery(query, null);*/
        Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMUNITY, allColumns, null, null, null, null, null);
        return cursor;
    }

    public Cursor fetchChildren(String parent_id) {
        Log.d("parent", "" + parent_id);
        Cursor cu = database.rawQuery("SELECT * FROM dspace_community_child WHERE parent_id='" + parent_id + "' ORDER BY " + MySQLiteHelper.COLUMN_TITLE, null);
        Log.e("get child", "" + cu.getCount());
        return cu;
    }

    public List<String> retrievevalue(int value)

    {
        ArrayList<String> dataList = new ArrayList<String>();
        //Cursor cu = null;
        Cursor cu;
        if (value != 0) {
            cu = database.query(MySQLiteHelper.TABLE_CHILD_COMMUNITY, allColumns, "dspace_id=?", new String[]{"" + value}, null, null, null);
        } else {
            cu = database.query(MySQLiteHelper.TABLE_COMMUNITY, allColumns, null, null, null, null, null);
        }
        //Cursor cu=DB1.rawQuery("SELECT NAME from products where CATEGORY ="+value,null);
        cu.moveToFirst();


        if (!cu.isAfterLast()) {
            do {
                //Log.i("in cursor","dfd"+ cu.getString(3));


                //              dataList.add(cu.getLong(0));
                //              dataList.add(cu.getString(1));
                //              dataList.add(cu.getString(2));
                //              dataList.add(cu.getString(3));
                //              dataList.add(cu.getString(4));
                //              dataList.add(cu.getString(5));
                //              dataList.add(cu.getString(6));

                dataList.add(cu.getString(0));

            }

            while (cu.moveToNext());
            cu.close();
        }

        // return the ArrayList that holds the data collected from
        // the database.
        return dataList;
        //
    }

////////////////////////
    ///////////////////////
    //////////////////////////////////
    ////////////


    public Cursor retrievevalueincursor(String string)

    {
        //ArrayList<String> dataList = new ArrayList<String>();
        //Cursor cu = null;

        // Log.e("value in datasourcemsdm",""+ string);
        if (string != null) {
            // Cursor cu = database.query(MySQLiteHelper.TABLE_CHILD_COMMUNITY, allColumns, "dspace_id=?", new String[] {string}, null, null, null);
            Cursor cu = database.rawQuery("SELECT * FROM dspace_community_child WHERE parent_id=" + string, null);
            Log.i("child chekcdf", "" + cu.getCount());
            return cu;
        } else {
            Cursor cu1 = database.query(MySQLiteHelper.TABLE_COMMUNITY, allColumns, null, null, null, null, MySQLiteHelper.COLUMN_TITLE);

            return cu1;
        }

        //Cursor cu=DB1.rawQuery("SELECT NAME from products where CATEGORY ="+value,null);


        //
    }


}
