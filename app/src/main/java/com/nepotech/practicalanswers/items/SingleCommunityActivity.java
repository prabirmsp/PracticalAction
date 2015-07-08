package com.nepotech.practicalanswers.items;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.nepotech.practicalanswers.Global;
import com.nepotech.practicalanswers.R;
import com.nepotech.practicalanswers.ServiceHandler;
import com.nepotech.practicalanswers.community.Community;
import com.nepotech.practicalanswers.community.CommunityDBHelper;
import com.nepotech.practicalanswers.community.CommunityDataSource;
import com.nepotech.practicalanswers.home_activity.HomeActivity;
import com.nepotech.practicalanswers.our_resources_activity.OurResourcesActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SingleCommunityActivity extends AppCompatActivity {

    protected Community mCommunity;
    String tableForCommunity;
    ArrayList<Item> mItems;
    ItemsDataSource mItemsDataSource;
    String mWindowTitle;
    String mLangFilter;
    private static final String LANG_ALL = "None";

    RecyclerView mRecyclerView;
    ItemsRecyclerViewAdapter mRecyclerViewAdapter;
    SwipeRefreshLayout mSwipeRefresh;
    LinearLayout mLinearLayoutNoDocs;

    // JSON Nodes
    private static final String TAG_ITEMS = "community_items"; // wrapper object name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_singlecommunity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get from xml
        mRecyclerView = (RecyclerView) findViewById(R.id.items_list);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mLinearLayoutNoDocs = (LinearLayout) findViewById(R.id.ll_docs_not_found);

        mSwipeRefresh.setColorSchemeResources(R.color.primary);
        // fix setRefreshing(true)
        mSwipeRefresh.setProgressViewOffset(false,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -28, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));

        Intent thisIntent = getIntent();
        String dspace_id = thisIntent.getStringExtra(CommunityDBHelper.COLUMN_DSPACE_ID);
        tableForCommunity = thisIntent.getStringExtra(OurResourcesActivity.TABLE);

        // get parent community
        CommunityDataSource dataSource = new CommunityDataSource(this);
        dataSource.open();
        mCommunity = dataSource.getFromDspaceId(tableForCommunity, dspace_id);
        dataSource.close();

        mWindowTitle = mCommunity.getTitle();
        setTitle(mWindowTitle);

        mItems = new ArrayList<>();
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        // setting adapter
        mRecyclerViewAdapter = new ItemsRecyclerViewAdapter(SingleCommunityActivity.this, mItems, mWindowTitle);
        // setting list adapter
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mItemsDataSource = new ItemsDataSource(this);

        mSwipeRefresh.setRefreshing(true);

        SharedPreferences langPrefs = getSharedPreferences(HomeActivity.LANG_PREFS_NAME, 0);
        mLangFilter = langPrefs.getString(HomeActivity.KEY_LANGUAGE, LANG_ALL);

        mLinearLayoutNoDocs.setVisibility(View.INVISIBLE);
        findViewById(R.id.button_change_lang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog(true);
            }
        });

        if (getItemsFromDB(mLangFilter) != 0) {
            mLinearLayoutNoDocs.setVisibility(View.INVISIBLE);
            new GetItems().execute();
        } else {
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

    private void showFilterDialog(boolean addSelect) {
        /** Set up dialog for filter **/
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.filter_dialog);
        dialog.setTitle("Select Document Filter");
        ((TextView) dialog.findViewById(R.id.language)).setText("Languages\nin this Category");
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                getItemsFromDB(mLangFilter);
            }
        });
        Spinner lang_spinner = (Spinner) dialog.findViewById(R.id.spinner_lang);
        mItemsDataSource.open();
        final ArrayList<String> langList = mItemsDataSource.getLanguagesInCollection(mCommunity.getDspace_id());
        if (addSelect)
            langList.add(0, "Select..."); // add All to beginning
        langList.add(1, LANG_ALL); // add All to beginning
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
                SharedPreferences langPrefs = getSharedPreferences(HomeActivity.LANG_PREFS_NAME, 0);
                SharedPreferences.Editor editor = langPrefs.edit();
                editor.putString(HomeActivity.KEY_LANGUAGE, mLangFilter);
                editor.apply();
                mLinearLayoutNoDocs.setVisibility(View.INVISIBLE);
                dialog.dismiss();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mLangFilter = LANG_ALL;
            }
        });
        mItemsDataSource.close();
        dialog.show();
    }

    // Get items list from database
    private int getItemsFromDB(String lang) {
        String langQuery = ItemsDBHelper.COLUMN_LANGUAGE + " = '" + lang + "'";
        if (lang.equals(LANG_ALL))
            langQuery = null;
        mItemsDataSource.open();
        mItems = new ArrayList<>();
        ArrayList<Item> temp = mItemsDataSource.getItemsFromCollection(mCommunity.getDspace_id(),
                langQuery);
        mItemsDataSource.close();
        if (!temp.isEmpty()) { // Data found in DB
            Log.d("SingleComm.getMapFromDB", "Data Found!!!");
            mItems = temp;
            mItemsDataSource.close();
            mRecyclerViewAdapter.updateItems(mItems);
            mRecyclerViewAdapter.notifyDataSetChanged();
            return 0;
        } else { // Data not found in DB
            Log.d("Main.getMapFromDB", "Data not found in DB");
            mLinearLayoutNoDocs.setVisibility(View.VISIBLE);
            return 1;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reload:
                new GetItems().execute();
                break;
            case android.R.id.home:
                finish();
                break;
            case R.id.filter:
                showFilterDialog(false);
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
                if (!ServiceHandler.isOnline(SingleCommunityActivity.this))
                    cancel(true);
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
                    mItemsDataSource.createItem(jsonItem, mCommunity.getDspace_id());
                }
            } catch (JSONException | UnsupportedEncodingException e) {
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
                    .setMessage("Looks like we can't connect! Please check your internet connection.")
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
