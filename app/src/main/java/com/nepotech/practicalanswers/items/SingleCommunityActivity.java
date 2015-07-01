package com.nepotech.practicalanswers.items;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.nepotech.practicalanswers.Global;
import com.nepotech.practicalanswers.R;
import com.nepotech.practicalanswers.ServiceHandler;
import com.nepotech.practicalanswers.community.Community;
import com.nepotech.practicalanswers.community.CommunityDBHelper;
import com.nepotech.practicalanswers.community.CommunityDataSource;
import com.nepotech.practicalanswers.our_resources_activity.OurResourcesActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;

public class SingleCommunityActivity extends AppCompatActivity {

    protected Community mCommunity;
    String tableForCommunity;
    ArrayList<Item> mItems;
    ItemsDataSource mItemsDataSource;
    String mWindowTitle;
    String mLangFilter;
    private static final String LANG_ALL = "All";

    RecyclerView mRecyclerView;
    ItemsRecyclerViewAdapter mListViewAdapter;
    SwipeRefreshLayout mSwipeRefresh;
    FloatingActionButton mFab;

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
        Fresco.initialize(this);
        setContentView(R.layout.activity_singlecommunity);

        // get from xml
        mRecyclerView = (RecyclerView) findViewById(R.id.items_list);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        mSwipeRefresh.setColorSchemeResources(R.color.primary);
        // fix setRefreshing(true)
        mSwipeRefresh.setProgressViewOffset(false,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -28, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);

        Intent thisIntent = getIntent();
        String dspace_id = thisIntent.getStringExtra(CommunityDBHelper.COLUMN_DSPACE_ID);
        tableForCommunity = thisIntent.getStringExtra(OurResourcesActivity.TABLE);

        // get parent community
        CommunityDataSource dataSource = new CommunityDataSource(this);
        dataSource.open();
        mCommunity = dataSource.getFromDspaceId(tableForCommunity, dspace_id);
        dataSource.close();

        mWindowTitle = URLDecoder.decode(mCommunity.getTitle());
        setTitle(mWindowTitle);

        mItemsDataSource = new ItemsDataSource(this);

        mSwipeRefresh.setRefreshing(true);
        mFab.setClickable(false);

        mLangFilter = LANG_ALL;
        if (getItemsFromDB(mLangFilter) != 0)
            new GetItems().execute();
        else {
            // snackbar("Data Received from DB!");
            mSwipeRefresh.setRefreshing(false);
        }

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetItems().execute();
            }
        });
    }

    private void setupFilterDialog() {
        /** Set up dialog for filter **/
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.filter_dialog);
        dialog.setTitle("Select Filter");
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                getItemsFromDB(mLangFilter);
            }
        });
        Spinner lang_spinner = (Spinner) dialog.findViewById(R.id.spinner_lang);
        mItemsDataSource.open();
        final ArrayList<String> langList = mItemsDataSource.getLanguagesInCollection(mCommunity.getDspace_id());
        langList.add(0, LANG_ALL); // add All to beginning
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, langList);
        lang_spinner.setAdapter(adapter);
        lang_spinner.setSelection(langList.indexOf(mLangFilter));
        lang_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean firstSelect = true;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstSelect) {
                    firstSelect = false;
                    return;
                }
                mLangFilter = langList.get(position);
                dialog.dismiss();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mLangFilter = LANG_ALL;
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        mItemsDataSource.close();
    }

    // Get items list from database
    private int getItemsFromDB(String lang) {
        mFab.setClickable(true);
        String langQuery = ItemsDBHelper.COLUMN_LANGUAGE + " = '" + lang + "'";
        if (lang.equals(LANG_ALL))
            langQuery = null;
        mItemsDataSource.open();
        mItems = new ArrayList<>();
        ArrayList<Item> temp = mItemsDataSource.getItemsFromCollection(mCommunity.getDspace_id(),
                langQuery);
        if (!temp.isEmpty()) { // Data found in DB
            Log.d("SingleComm.getMapFromDB", "Data Found!!!");
            mItems = temp;
            mItemsDataSource.close();
            // setting adapter
            mListViewAdapter = new ItemsRecyclerViewAdapter(SingleCommunityActivity.this, mItems, mWindowTitle);
            // setting list adapter
            mRecyclerView.setAdapter(mListViewAdapter);
            setupFilterDialog();
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
            // Show loading indicator
            mSwipeRefresh.setRefreshing(true);
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

                    //String collection_id = jsonItem.getString(TAG_COLLECTION_ID);
                    String collection_id = mCommunity.getDspace_id();
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
            getItemsFromDB(mLangFilter);
            // Dismiss loading indicator
            mSwipeRefresh.setRefreshing(false);
            super.onPostExecute(aVoid);
        }


        @Override
        protected void onCancelled() {
            // Dismiss loading indicator
            mSwipeRefresh.setRefreshing(false);
            // Alert Dialog
            new AlertDialog.Builder(SingleCommunityActivity.this).setTitle("Oh no!")
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
        Snackbar.make(findViewById(R.id.coLa), message, Snackbar.LENGTH_LONG)
                .show();
    }

}
