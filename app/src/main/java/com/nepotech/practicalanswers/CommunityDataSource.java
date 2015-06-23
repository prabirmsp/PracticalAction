package com.nepotech.practicalanswers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class CommunityDataSource {

    private SQLiteDatabase mDatabase;
    private CommunityDBHelper dbHelper;
    private String[] allColumns = {
            CommunityDBHelper.COLUMN_ID,
            CommunityDBHelper.COLUMN_DSPACE_ID,
            CommunityDBHelper.COLUMN_PARENT_ID,
            CommunityDBHelper.COLUMN_RGT,
            CommunityDBHelper.COLUMN_LFT,
            CommunityDBHelper.COLUMN_LEVEL,
            CommunityDBHelper.COLUMN_TITLE,
            CommunityDBHelper.COLUMN_DESC,
            CommunityDBHelper.COLUMN_ALIAS,
            CommunityDBHelper.COLUMN_IMAGEURL};


    public CommunityDataSource(Context context) {
        dbHelper = new CommunityDBHelper(context);
        // TODO Auto-generated constructor stub
    }

    public void open() throws SQLException {
        mDatabase = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // CREATE
    public long createCommunity(String TABLE_NAME, String dspace_id, String parent_id,
                                     String rgt, String lft, String level, String title,
                                     String description, String alias, String imageurl) {

        ContentValues values = new ContentValues();
        values.put(CommunityDBHelper.COLUMN_TITLE, title);
        values.put(CommunityDBHelper.COLUMN_PARENT_ID, parent_id);
        values.put(CommunityDBHelper.COLUMN_DSPACE_ID, dspace_id);
        values.put(CommunityDBHelper.COLUMN_LEVEL, level);
        values.put(CommunityDBHelper.COLUMN_LFT, lft);
        values.put(CommunityDBHelper.COLUMN_RGT, rgt);
        values.put(CommunityDBHelper.COLUMN_DESC, description);
        values.put(CommunityDBHelper.COLUMN_ALIAS, alias);
        values.put(CommunityDBHelper.COLUMN_IMAGEURL, imageurl);
        return mDatabase.insert(TABLE_NAME, null, values);
    }

    // read from cursor
    private Community cursorToCommunity(Cursor cursor) {
        return new Community(
                cursor.getString(cursor.getColumnIndex(CommunityDBHelper.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(CommunityDBHelper.COLUMN_DSPACE_ID)),
                cursor.getString(cursor.getColumnIndex(CommunityDBHelper.COLUMN_PARENT_ID)),
                cursor.getString(cursor.getColumnIndex(CommunityDBHelper.COLUMN_RGT)),
                cursor.getString(cursor.getColumnIndex(CommunityDBHelper.COLUMN_LFT)),
                cursor.getString(cursor.getColumnIndex(CommunityDBHelper.COLUMN_LEVEL)),
                cursor.getString(cursor.getColumnIndex(CommunityDBHelper.COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndex(CommunityDBHelper.COLUMN_DESC)),
                cursor.getString(cursor.getColumnIndex(CommunityDBHelper.COLUMN_ALIAS)),
                cursor.getString(cursor.getColumnIndex(CommunityDBHelper.COLUMN_IMAGEURL)));
    }

    // READ ALL
    public ArrayList<Community> getAllCommunities(String table, String orderBy) {
        ArrayList<Community> arrayList = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + table + " ORDER BY " + orderBy, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            arrayList.add(cursorToCommunity(cursor));
            cursor.moveToNext();
        }
        return arrayList;
    }

    // READ ALL
    public ArrayList<Community> getSelectedCommunities(String table, String whereClause, String orderBy) {
        ArrayList<Community> arrayList = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + table + " WHERE " + whereClause +
                " ORDER BY " + orderBy, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            arrayList.add(cursorToCommunity(cursor));
            cursor.moveToNext();
        }
        return arrayList;
    }

    // READ FROM DSPACE ID
    public Community getFromDspaceId(String table, String dspace_id) {
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + table + " WHERE " +
                CommunityDBHelper.COLUMN_DSPACE_ID + " = '" + dspace_id + "'" +
                " ORDER BY " + CommunityDBHelper.COLUMN_DSPACE_ID, null);
        cursor.moveToFirst();
        return cursorToCommunity(cursor);
    }

    // Delete and create new tables (for update)
    public void upgrade() {
        mDatabase = dbHelper.getWritableDatabase();
        dbHelper.onUpgrade(mDatabase, mDatabase.getVersion(), mDatabase.getVersion() + 1);
    }
}