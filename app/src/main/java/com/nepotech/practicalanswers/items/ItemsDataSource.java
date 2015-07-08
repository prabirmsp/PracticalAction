package com.nepotech.practicalanswers.items;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.nepotech.practicalanswers.Global;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

public class ItemsDataSource {

    private SQLiteDatabase mDatabase;
    private ItemsDBHelper dbHelper;

    // JSON Nodes
    public static final String TAG_COLLECTION_ID = "collection_id";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_DSPACE_ID = "item_dspace_id";
    private static final String TAG_CREATOR = "creator";
    private static final String TAG_PUBLISHER = "publisher";
    private static final String TAG_LANGUAGE = "language";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_DATE = "date_issued";
    private static final String TAG_TYPE = "type";
    private static final String TAG_DOC_THUMB_HREF = "document_thumb_href";
    private static final String TAG_DOC_HREF = "document_href";
    private static final String TAG_DOC_SIZE = "document_size";
    private static final String TAG_BITSTREAM_ID = "bitstream_id";


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
        Item item = null;
        if (cursor != null) {
            item = new Item(
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
        return item;

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

    public long createItem(JSONObject jsonItem, String collection_id) throws JSONException, UnsupportedEncodingException {

        String dspace_id = jsonItem.getString(TAG_DSPACE_ID);
        String creator = URLDecoder.decode(jsonItem.getString(TAG_CREATOR), Global.CHARSET);
        String publisher = URLDecoder.decode(jsonItem.getString(TAG_PUBLISHER), Global.CHARSET);
        String language = jsonItem.getString(TAG_LANGUAGE).toUpperCase();
        String title = URLDecoder.decode(jsonItem.getString(TAG_TITLE), Global.CHARSET);
        String description = URLDecoder.decode(jsonItem.getString(TAG_DESCRIPTION), Global.CHARSET);
        String date = jsonItem.getString(TAG_DATE);
        String bitstream_id = jsonItem.getString(TAG_BITSTREAM_ID);
        String size = jsonItem.getString(TAG_DOC_SIZE);
        String thumb_href = URLDecoder.decode(jsonItem.getString(TAG_DOC_THUMB_HREF), Global.CHARSET).replace(" ", "%20");
        String href = URLDecoder.decode(jsonItem.getString(TAG_DOC_HREF), Global.CHARSET).replace(" ", "%20");
        String type = jsonItem.getString(TAG_TYPE);

        return createItem(
                dspace_id, collection_id, title, creator, publisher,
                description, language, date, type, bitstream_id, thumb_href,
                href, size);
    }

    public ArrayList<Item> search(String searchText) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT * FROM " + ItemsDBHelper.TABLE_ITEMS + " WHERE (");
        String[] searchColumns = {ItemsDBHelper.COLUMN_TITLE, ItemsDBHelper.COLUMN_DESC};
        for (int i = 0; i < searchColumns.length; i++) {
            builder.append(searchColumns[i] + " LIKE '%" + searchText + "%'");
            if (i + 1 < searchColumns.length)
                builder.append(") OR (");
        }
        builder.append(")");
        String query = builder.toString();
        Cursor cursor = mDatabase.rawQuery(query, null);
        ArrayList<Item> arrayList = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            arrayList.add(cursorToItem(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;
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
        if (cursor.getCount() < 1) {
            cursor.close();
            return null;
        }
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
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + table, null);
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
        ArrayList<String> arrayList = new ArrayList<>();
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
