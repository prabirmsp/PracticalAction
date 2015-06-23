package com.nepotech.practicalanswers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CommunityDBHelper extends SQLiteOpenHelper {

    private static final String DB_COMMUNITY = "dspace_community.db";
    private static final int DB_COMMUNITY_VERSION = 2;

    public static final String TABLE_COMMUNITY = "dspace_community";
    public static final String TABLE_CHILD_COMMUNITY = "dspace_community_child";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DSPACE_ID = "dspace_id";
    public static final String COLUMN_PARENT_ID = "parent_id";
    public static final String COLUMN_RGT = "rgt";
    public static final String COLUMN_LFT = "lft";
    public static final String COLUMN_LEVEL = "level";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESC = "description";
    public static final String COLUMN_ALIAS = "alias";
    public static final String COLUMN_IMAGEURL = "imageurl";


    // Database creation sql statement
    private static final String CREATE_TABLE_COMMUNITY =
            "create table " + TABLE_COMMUNITY + "(" +
            COLUMN_ID + " integer primary key autoincrement , " +
            COLUMN_DSPACE_ID + " text not null, " +
            COLUMN_PARENT_ID + " text not null, " +
            COLUMN_RGT + " text not null, " +
            COLUMN_LFT + " text not null, " +
            COLUMN_LEVEL + " text not null, " +
            COLUMN_TITLE + " text not null, " +
            COLUMN_DESC + " text not null, " +
            COLUMN_ALIAS + " text not null, " +
            COLUMN_IMAGEURL + " text not null)";

    private static final String CREATE_TABLE_CHILD_COMMUNITY =
            "create table " + TABLE_CHILD_COMMUNITY + "(" +
            COLUMN_ID + " integer primary key autoincrement , " +
            COLUMN_DSPACE_ID + " text not null, " +
            COLUMN_PARENT_ID + " text not null, " +
            COLUMN_RGT + " text not null, " +
            COLUMN_LFT + " text not null, " +
            COLUMN_LEVEL + " text not null, " +
            COLUMN_TITLE + " text not null, " +
            COLUMN_DESC + " text not null, " +
            COLUMN_ALIAS + " text not null, " +
            COLUMN_IMAGEURL + " text not null)";

    public CommunityDBHelper(Context context) {
        super(context, DB_COMMUNITY, null, DB_COMMUNITY_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(CREATE_TABLE_COMMUNITY);
        db.execSQL(CREATE_TABLE_CHILD_COMMUNITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        Log.w(CommunityDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMUNITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHILD_COMMUNITY);
        onCreate(db);
    }


}
