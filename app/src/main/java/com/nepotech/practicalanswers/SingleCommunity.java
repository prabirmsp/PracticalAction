package com.nepotech.practicalanswers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;

public class SingleCommunity extends AppCompatActivity {

    Community mCommunity;
    ArrayList<Item> mItems;
    ItemsDataSource mItemsDataSource;

    ProgressBar mProgressBar;
    ListView mListView;
    ItemsListViewAdapter mListViewAdapter;

    // JSON Nodes
    private static final String TAG_ITEMS = "community_items"; // wrapper object name
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_DSPACE_ID = "item_dspace_id";
    private static final String TAG_COLLECTION_ID = "collection_id";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_singlecommunity);
        mListView = (ListView) findViewById(R.id.items_list);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        Intent thisIntent = getIntent();
        String dspace_id = thisIntent.getStringExtra(CommunityDBHelper.COLUMN_DSPACE_ID);
        String table = thisIntent.getStringExtra(MainActivity.TABLE);
        String parentTitle = thisIntent.getStringExtra(MainActivity.TITLE);
        setTitle(URLDecoder.decode(parentTitle));

        CommunityDataSource dataSource = new CommunityDataSource(this);
        dataSource.open();
        mCommunity = dataSource.getFromDspaceId(table, dspace_id);
        dataSource.close();

        mItemsDataSource = new ItemsDataSource(this);

        if (getItemsFromDB() != 0)
            new GetItems().execute();
        else {
            snackbar("Data Received from DB!");
            mProgressBar.setVisibility(View.INVISIBLE);
        }


    }
    // Get items list from database
    private int getItemsFromDB() {
        mItemsDataSource.open();
        mItems = new ArrayList<>();
        ArrayList<Item> temp = mItemsDataSource.getItemsFromCollection(mCommunity.getDspace_id());
        if (!temp.isEmpty()) { // Data found in DB
            Log.d("SingleComm.getMapFromDB", "Data Found!!!");
            mItems = temp;
            mItemsDataSource.close();
            // setting adapter
            mListViewAdapter = new ItemsListViewAdapter(SingleCommunity.this, mItems);
            // setting list adapter
            mListView.setAdapter(mListViewAdapter);
            return 0;
        } else { // Data not found in DB
            mItemsDataSource.close();
            Log.d("Main.getMapFromDB", "Data not found in DB");
            return 1;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reload:
                new GetItems().execute();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private class GetItems extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // Show progress bar
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            // GET FROM WEB
            // get JSON data
            String jsonStr = "";
            try {
                jsonStr = ServiceHandler.getText(Global.url + "?get_items_from_dspace_id=" +
                        mCommunity.getDspace_id());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ServiceHandler", "Could not get JSON Data");

                this.cancel(true);
            }

            Log.d("JSONSTRING", jsonStr);

            mItemsDataSource.open();
            // Delete old data from db
            mItemsDataSource.deleteItemsByCollection(mCommunity.getDspace_id());
            try {
                // JSON Parsing
                JSONArray items = new JSONObject(jsonStr).getJSONArray(TAG_ITEMS);

                for (int i = 0; i < items.length(); i++) {
                    JSONObject jsonItem = items.getJSONObject(i);

                    String collection_id = jsonItem.getString(TAG_COLLECTION_ID);
                    String dspace_id = jsonItem.getString(TAG_DSPACE_ID);
                    String creator = jsonItem.getString(TAG_CREATOR);
                    String publisher = jsonItem.getString(TAG_PUBLISHER);
                    String language = jsonItem.getString(TAG_LANGUAGE);
                    String title = jsonItem.getString(TAG_TITLE);
                    String description = jsonItem.getString(TAG_DESCRIPTION);
                    String date = jsonItem.getString(TAG_DATE);
                    String bitstream_id = jsonItem.getString(TAG_BITSTREAM_ID);
                    String size = jsonItem.getString(TAG_DOC_SIZE);
                    String thumb_href = jsonItem.getString(TAG_DOC_THUMB_HREF);
                    String href = jsonItem.getString(TAG_DOC_HREF);
                    String type = jsonItem.getString(TAG_TYPE);

                    mItemsDataSource.createItem(
                            dspace_id, collection_id, title, creator, publisher,
                            description, language, date, type, bitstream_id, thumb_href,
                            href, size);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mItemsDataSource.close();


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            snackbar("Successfully updated!");
            getItemsFromDB();
            // Dismiss the progress bar
            mProgressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(aVoid);
        }


        @Override
        protected void onCancelled() {
            // Dismiss the progress dialog
            mProgressBar.setVisibility(View.INVISIBLE);
            // Alert Dialog
            new AlertDialog.Builder(SingleCommunity.this).setTitle("Oh no!")
                    .setMessage("Looks like we can't connect!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
            super.onCancelled();
        }
    }

    void snackbar(String message) {
        Snackbar.make(findViewById(R.id.linearLayout), message, Snackbar.LENGTH_LONG)
                .show();
    }

}
