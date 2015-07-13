package com.nepotech.practicalanswers;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.nepotech.practicalanswers.items.Item;
import com.nepotech.practicalanswers.items.ItemsDataSource;
import com.nepotech.practicalanswers.items.ItemsRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    ItemsRecyclerViewAdapter mAdapter;
    TextView mOfflineTV;
    TextView mFillerTV;
    SwipeRefreshLayout mSwipeRefresh;
    Runnable notifyUpdate;

    private boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_search_results);

        setTitle("Search Results");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get xml
        mRecyclerView = (RecyclerView) findViewById(R.id.items_list);
        mOfflineTV = (TextView) findViewById(R.id.offline_tv);
        mFillerTV = (TextView) findViewById(R.id.fillerTextView);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        // fix setRefreshing(true)
        mSwipeRefresh.setEnabled(false);
        mSwipeRefresh.setColorSchemeResources(R.color.primary);
        mSwipeRefresh.setProgressViewOffset(false,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -48, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));

        mRecyclerView.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mAdapter = new ItemsRecyclerViewAdapter(this, new ArrayList<Item>(), "Search Results");
        mRecyclerView.setAdapter(mAdapter);

        mOfflineTV.setVisibility(View.INVISIBLE);
        mFillerTV.setVisibility(View.INVISIBLE);

        notifyUpdate = new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        };

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        isOnline = ServiceHandler.isOnline(this);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            setTitle("Search Results for '" + query + "'");
            new QuerySearch().execute(query);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(Global.A_enter, Global.B_exit);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(Global.A_enter, Global.B_exit);
    }

    public class QuerySearch extends AsyncTask<String, Void, ArrayList<Item>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isOnline) {
                mOfflineTV.setVisibility(View.INVISIBLE);
                mRecyclerView.setPadding(0, 0, 0, 0);
            } else {
                mOfflineTV.setVisibility(View.VISIBLE);
                mRecyclerView.setPadding(0, 0, 0, dp_px(32));
            }
            mFillerTV.setVisibility(View.INVISIBLE);
            mSwipeRefresh.setRefreshing(true);
        }

        @Override
        protected ArrayList<Item> doInBackground(String... params) {

            Integer queryItems = 0;
            ArrayList<Item> results = new ArrayList<>();
            ItemsDataSource itemsDataSource = new ItemsDataSource(SearchResultsActivity.this);
            itemsDataSource.open();

            if (isOnline) {
                // is online - query server
                String query, jsonStr = "";
                try {
                    query = URLEncoder.encode(params[0].trim(), Global.CHARSET);
                    jsonStr = ServiceHandler.getText(Global.url + "?search=" + query);
                } catch (UnsupportedCharsetException e) {
                    Log.e("URLEncoder", "UnsupportedCharsetException");
                    e.printStackTrace();
                    this.cancel(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ServiceHandler", "Could not get JSON Data");
                    this.cancel(true);
                }

                try {
                    JSONArray jsonArray = new JSONObject(jsonStr).getJSONArray("search_items");
                    queryItems = jsonArray.length();

                    for (int i = 0; i < queryItems; i++) {
                        String dspace_id = jsonArray.getJSONObject(i).getString("dspace_id");
                        Item item;
                        if ((item = itemsDataSource.getFromDspaceId(dspace_id)) != null) {
                            // item is already saved in db
                            results.add(item);
                            mAdapter.addToItems(item);
                            runOnUiThread(notifyUpdate);
                        } else {
                            // get item from server
                            try {
                                String jsonItemStr = ServiceHandler.getText(Global.url + "?get_single_item=" + dspace_id);
                                JSONObject jsonObject = new JSONObject(jsonItemStr);
                                itemsDataSource.createItem(jsonObject, jsonObject.getString(ItemsDataSource.TAG_COLLECTION_ID));
                                item = itemsDataSource.getFromDspaceId(dspace_id);
                                results.add(item);
                                mAdapter.addToItems(item);
                                runOnUiThread(notifyUpdate);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                this.cancel(true);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("ServiceHandler", "Could not get JSON Data");
                                this.cancel(true);
                            }
                        }
                    }
                } catch (JSONException e) {
                    Log.e("SearchResults", "JSON Exception");
                }
                Log.d("SEARCHQURYITEMS", queryItems + "");

            } else { // not online - query local db
                Log.d("Qry", "NOT ONLINE");
                results = itemsDataSource.search(params[0].trim());
            }
            itemsDataSource.close();
            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<Item> items) {
            super.onPostExecute(items);
            //mAdapter.updateItems(items);
            //mAdapter.notifyDataSetChanged();
            if (!(items.size() > 0)) {
                mFillerTV.setVisibility(View.VISIBLE);
            }
            mSwipeRefresh.setRefreshing(false);
        }
    }

    private int dp_px(int dp) {
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        return Math.round(pixels);
    }

}
