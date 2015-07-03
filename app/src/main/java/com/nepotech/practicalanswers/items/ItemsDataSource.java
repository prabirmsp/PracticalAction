package com.nepotech.practicalanswers.items;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.util.ArrayList;

public class ItemsDataSource {

    private SQLiteDatabase mDatabase;
    private ItemsDBHelper dbHelper;


    public ItemsDataSource(Context context) {
        dbHelper = new ItemsDBHelper(context);
    }

    public void open() throws SQLException {
        mDatabase = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Item cursorToItem(Cursor cursor) {
        return new Item(
                cursor.getString(cursor.getColumnIndex(ItemsDBHelper.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(ItemsDBHelper.COLUMN_DSPACE_ID)),
                cursor.getString(cursor.getColumnIndex(ItemsDBHelper.COLUMN_COLLECTION_ID)),
                cursor.getString(cursor.getColumnIndex(ItemsDBHelper.COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndex(ItemsDBHelper.COLUMN_CREATOR)),
                cursor.getString(cursor.getColumnIndex(ItemsDBHelper.COLUMN_PUBLISHER)),
                cursor.getString(cursor.getColumnIndex(ItemsDBHelper.COLUMN_DESC)),
                cursor.getString(cursor.getColumnIndex(ItemsDBHelper.COLUMN_LANGUAGE)),
                cursor.getString(cursor.getColumnIndex(ItemsDBHelper.COLUMN_DATE_ISSUED)),
                cursor.getString(cursor.getColumnIndex(ItemsDBHelper.COLUMN_TYPE)),
                cursor.getString(cursor.getColumnIndex(ItemsDBHelper.COLUMN_BITSTREAM_ID)),
                cursor.getString(cursor.getColumnIndex(ItemsDBHelper.COLUMN_DOCUMENT_THUMB_HREF)),
                cursor.getString(cursor.getColumnIndex(ItemsDBHelper.COLUMN_DOCUMENT_HREF)),
                cursor.getString(cursor.getColumnIndex(ItemsDBHelper.COLUMN_DOCUMENT_SIZE)));
    }

    // CREATE
    public long createItem(String dspace_id, String collection_id, String title,
                           String creator, String publisher, String description, String language,
                           String dateIssued, String type, String bitstreamId,
                           String documentThumbHref, String documentHref,
                           String documentSize) {
        ContentValues values = new ContentValues();
        values.put(ItemsDBHelper.COLUMN_DSPACE_ID, dspace_id);
        values.put(ItemsDBHelper.COLUMN_COLLECTION_ID, collection_id);
        values.put(ItemsDBHelper.COLUMN_TITLE, title);
        values.put(ItemsDBHelper.COLUMN_CREATOR, creator);
        values.put(ItemsDBHelper.COLUMN_PUBLISHER, publisher);
        values.put(ItemsDBHelper.COLUMN_DESC, description);
        values.put(ItemsDBHelper.COLUMN_LANGUAGE, language);
        values.put(ItemsDBHelper.COLUMN_DATE_ISSUED, dateIssued);
        values.put(ItemsDBHelper.COLUMN_TYPE, type);
        values.put(ItemsDBHelper.COLUMN_BITSTREAM_ID, bitstreamId);
        values.put(ItemsDBHelper.COLUMN_DOCUMENT_THUMB_HREF, documentThumbHref);
        values.put(ItemsDBHelper.COLUMN_DOCUMENT_HREF, documentHref);
        values.put(ItemsDBHelper.COLUMN_DOCUMENT_SIZE, documentSize);

        // Add language to index
        Cursor cu = mDatabase.rawQuery("SELECT * FROM " + ItemsDBHelper.TABLE_LANGUAGES, null);
        cu.moveToFirst();
        boolean indexed = false;
        while (!cu.isAfterLast()) {
            String lang = cu.getString(cu.getColumnIndex(ItemsDBHelper.COLUMN_LANGUAGE));
            if (lang.equalsIgnoreCase(language)) {
                indexed = true;
                break;
            }
            cu.moveToNext();
        }
        cu.close();
        if (!indexed) {
            ContentValues v = new ContentValues();
            v.put(ItemsDBHelper.COLUMN_LANGUAGE, language);
            mDatabase.insert(ItemsDBHelper.TABLE_LANGUAGES, null, v);
        }

        return mDatabase.insert(ItemsDBHelper.TABLE_ITEMS, null, values);
    }

    // READ items
    public ArrayList<Item> getItemsFromCollection(String collectionId, @Nullable String whereClause) {
        ArrayList<Item> arrayList = new ArrayList<>();
        String query;
        if (whereClause != null)
            query = "SELECT * FROM " + ItemsDBHelper.TABLE_ITEMS +
                    " WHERE " + ItemsDBHelper.COLUMN_COLLECTION_ID + " = '" + collectionId +
                    "' and " + whereClause + " ORDER BY " + ItemsDBHelper.COLUMN_TITLE;
        else
            query = "SELECT * FROM " + ItemsDBHelper.TABLE_ITEMS +
                    " WHERE " + ItemsDBHelper.COLUMN_COLLECTION_ID + " = '" + collectionId +
                    "' ORDER BY " + ItemsDBHelper.COLUMN_TITLE;
        Cursor cursor = mDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            arrayList.add(cursorToItem(cursor));
            cursor.moveToNext();
        }
        return arrayList;
    }

    // DELETE rows by collection (on refresh)
    public void deleteItemsByCollection(String collection_id) {
        mDatabase.delete(ItemsDBHelper.TABLE_ITEMS,
                ItemsDBHelper.COLUMN_COLLECTION_ID + " = '" + collection_id + "'", null);
    }

    // Upgrade
    public void upgrade() {
        dbHelper.onUpgrade(mDatabase, ItemsDBHelper.DB_ITEMS_VERSION, ItemsDBHelper.DB_ITEMS_VERSION + 1);

    }


    public Item getFromDspaceId(String dspace_id) {
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + ItemsDBHelper.TABLE_ITEMS + " WHERE " +
                ItemsDBHelper.COLUMN_DSPACE_ID + " = '" + dspace_id + "'" +
                " ORDER BY " + ItemsDBHelper.COLUMN_DSPACE_ID, null);
        cursor.moveToFirst();
        return cursorToItem(cursor);
    }

    public boolean isPresent(String table, String dspace_id) {
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + table +
                " WHERE " + ItemsDBHelper.COLUMN_DSPACE_ID + " = '" + dspace_id + "'", null);
        boolean present = cursor.getCount() > 0;
        cursor.close();
        return present;
    }

    public boolean isEmpty(String table) {
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + table , null);
        boolean empty = cursor.getCount() == 0;
        cursor.close();
        return empty;
    }

    /**
     * Starring operations
     **/
    public void addStar(String dspace_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemsDBHelper.COLUMN_DSPACE_ID, dspace_id);
        mDatabase.insert(ItemsDBHelper.TABLE_STARRED, null, contentValues);
    }

    public void removeStar(String dspace_id) {
        mDatabase.delete(ItemsDBHelper.TABLE_STARRED,
                ItemsDBHelper.COLUMN_DSPACE_ID + " = '" + dspace_id + "'", null);
    }

    public ArrayList<Item> getAllStarred() {
        ArrayList<Item> arrayList = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + ItemsDBHelper.TABLE_STARRED +
                " ORDER BY " + ItemsDBHelper.COLUMN_ID + " DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String dspace_id = cursor.getString(cursor.getColumnIndex(ItemsDBHelper.COLUMN_DSPACE_ID));
            arrayList.add(getFromDspaceId(dspace_id));
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;
    }

    /**
     * Downloaded operations
     **/
    public void addDownloaded(String dspace_id, String fileName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemsDBHelper.COLUMN_DSPACE_ID, dspace_id);
        contentValues.put(ItemsDBHelper.COLUMN_FILENAME, fileName);
        mDatabase.insert(ItemsDBHelper.TABLE_DOWNLOADED, null, contentValues);
    }

    public void removeDowloaded(String dspace_id) {
        mDatabase.delete(ItemsDBHelper.TABLE_DOWNLOADED,
                ItemsDBHelper.COLUMN_DSPACE_ID + " = '" + dspace_id + "'", null);
    }

    public ArrayList<Item> getAllDownloaded() {
        ArrayList<Item> arrayList = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + ItemsDBHelper.TABLE_DOWNLOADED +
                " ORDER BY " + ItemsDBHelper.COLUMN_ID + " DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String dspace_id = cursor.getString(cursor.getColumnIndex(ItemsDBHelper.COLUMN_DSPACE_ID));
            arrayList.add(getFromDspaceId(dspace_id));
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;
    }

    public String getFileName(String dspace_id) {
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + ItemsDBHelper.TABLE_DOWNLOADED +
                " WHERE " + ItemsDBHelper.COLUMN_DSPACE_ID + " = '" + dspace_id + "'", null);
        cursor.moveToFirst();
        String fileName = cursor.getString(cursor.getColumnIndex(ItemsDBHelper.COLUMN_FILENAME));
        cursor.close();
        return fileName;

    }

    /**
     * language filter operations
     **/
    public ArrayList<String> getAllLanguages() {
        ArrayList<String> arrayList = new ArrayList<String>();
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + ItemsDBHelper.TABLE_LANGUAGES +
                " ORDER BY " + ItemsDBHelper.COLUMN_ID + " DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String language = cursor.getString(cursor.getColumnIndex(ItemsDBHelper.COLUMN_LANGUAGE));
            arrayList.add(language);
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;
    }

    public ArrayList<String> getLanguagesInCollection(String collectionId) {
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor cu = mDatabase.rawQuery("SELECT * FROM " + ItemsDBHelper.TABLE_ITEMS +
                " WHERE " + ItemsDBHelper.COLUMN_COLLECTION_ID + " = '" + collectionId +
                "' ORDER BY " + ItemsDBHelper.COLUMN_LANGUAGE, null);
        cu.moveToFirst();
        String current, previous = null;
        while (!cu.isAfterLast()) {
            current = cu.getString(cu.getColumnIndex(ItemsDBHelper.COLUMN_LANGUAGE));
            if (!current.equals(previous)) {
                arrayList.add(current);
            }
            previous = current;
            cu.moveToNext();
        }
        cu.close();
        return arrayList;
    }

}
