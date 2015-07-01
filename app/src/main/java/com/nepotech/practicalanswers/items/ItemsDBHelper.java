package com.nepotech.practicalanswers.items;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemsDBHelper extends SQLiteOpenHelper {


    private static final String DB_ITEMS = "dspace_items.db";
    protected static final int DB_ITEMS_VERSION = 2;

    protected final static String TABLE_ITEMS = "dspace_items";
    protected final static String TABLE_STARRED = "starred_items";
    protected final static String TABLE_DOWNLOADED = "downloaded_items";
    protected final static String TABLE_RECENTS= "recent_items";
    protected final static String TABLE_LANGUAGES= "languages";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DSPACE_ID = "dspace_id";
    public static final String COLUMN_COLLECTION_ID = "parent_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CREATOR = "creator";
    public static final String COLUMN_PUBLISHER = "publisher";
    public static final String COLUMN_DESC = "description";
    public static final String COLUMN_LANGUAGE = "language";
    public static final String COLUMN_DATE_ISSUED = "date_issued";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_BITSTREAM_ID = "bitstream_id";
    public static final String COLUMN_DOCUMENT_THUMB_HREF = "document_thumb_href";
    public static final String COLUMN_DOCUMENT_HREF = "document_href";
    public static final String COLUMN_DOCUMENT_SIZE = "document_size";
    public static final String COLUMN_FILENAME = "local_file_name";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    // Table create SQL Statement
    private static final String CREATE_TABLE_COMMUNITY =
            "create table " + TABLE_ITEMS + "(" +
                    COLUMN_ID + " integer primary key autoincrement , " +
                    COLUMN_DSPACE_ID + " text not null, " +
                    COLUMN_COLLECTION_ID + " text not null, " +
                    COLUMN_TITLE + " text not null, " +
                    COLUMN_CREATOR + " text not null, " +
                    COLUMN_PUBLISHER + " text not null, " +
                    COLUMN_DESC + " text not null, " +
                    COLUMN_LANGUAGE + " text not null, " +
                    COLUMN_DATE_ISSUED + " text not null, " +
                    COLUMN_TYPE + " text not null, " +
                    COLUMN_BITSTREAM_ID + " text not null, " +
                    COLUMN_DOCUMENT_THUMB_HREF + " text not null, " +
                    COLUMN_DOCUMENT_HREF + " text not null, " +
                    COLUMN_DOCUMENT_SIZE + " text not null)";

    private static final String CREATE_TABLE_STARRED =
            "create table " + TABLE_STARRED + "(" +
                    COLUMN_ID + " integer primary key autoincrement , " +
                    COLUMN_DSPACE_ID + " text not null)";
    private static final String CREATE_TABLE_DOWNLOADED =
            "create table " + TABLE_DOWNLOADED + "(" +
                    COLUMN_ID + " integer primary key autoincrement , " +
                    COLUMN_DSPACE_ID + " text not null, " +
                    COLUMN_FILENAME + " text not null)";
    private static final String CREATE_TABLE_RECENTS =
            "create table " + TABLE_RECENTS + "(" +
                    COLUMN_ID + " integer primary key autoincrement , " +
                    COLUMN_DSPACE_ID + " text not null)";
    private static final String CREATE_TABLE_LANGUAGES =
            "create table " + TABLE_LANGUAGES + "(" +
                    COLUMN_ID + " integer primary key autoincrement , " +
                    COLUMN_LANGUAGE + " text not null)";

    public ItemsDBHelper(Context context) {
        super(context, DB_ITEMS, null, DB_ITEMS_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_COMMUNITY);
        db.execSQL(CREATE_TABLE_STARRED);
        db.execSQL(CREATE_TABLE_DOWNLOADED);
        db.execSQL(CREATE_TABLE_LANGUAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL(CREATE_TABLE_COMMUNITY);
    }
}
