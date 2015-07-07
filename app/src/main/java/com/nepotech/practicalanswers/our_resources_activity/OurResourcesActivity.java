package com.nepotech.practicalanswers.our_resources_activity;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.nepotech.practicalanswers.Global;
import com.nepotech.practicalanswers.R;
import com.nepotech.practicalanswers.ServiceHandler;
import com.nepotech.practicalanswers.community.Community;
import com.nepotech.practicalanswers.community.CommunityDBHelper;
import com.nepotech.practicalanswers.community.CommunityDataSource;
import com.nepotech.practicalanswers.items.SingleCommunityActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

public class OurResourcesActivity extends AppCompatActivity {

    public static final String TABLE = "table";
    public static final String TITLE = "title";
    // JSON Node names
    private static final String TAG_COMMUNITY = "communities"; // wrapper object name
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_PARENT_ID = "parent_id";
    private static final String TAG_DSPACE_ID = "dspace_id";
    private static final String TAG_LEVEL = "level";
    private static final String TAG_LFT = "lft";
    private static final String TAG_RGT = "rgt";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_ALIAS = "alias";
    private static final String TAG_IMAGEURL = "imageurl";
    public static final String KEY_DSPACE = "dspace";
    protected ArrayList<Community> mParentCommunities;
    protected HashMap<Community, ArrayList<Community>> mChildrenMap;
    private ExpandableListView mExpandableListView;
    private TextView mTVNull;
    private SwipeRefreshLayout mSwipeRefresh;
    // Store Communities Data
    private CommunityDataSource mDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        // lint to xml
        mExpandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        mTVNull = (TextView) findViewById(R.id.fillerTextView);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        mDataSource = new CommunityDataSource(OurResourcesActivity.this);

        mTVNull.setVisibility(View.INVISIBLE);
        mSwipeRefresh.setColorSchemeResources(R.color.primary);
        // fix setRefreshing(true)
        mSwipeRefresh.setProgressViewOffset(false,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -28, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));

        mSwipeRefresh.setRefreshing(true);
        if (getMapFromDB() != 0) {
            refreshData();
        } else {
            // snackbar("Data Recieved from DB!");
            mSwipeRefresh.setRefreshing(false);
        }

        mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
/*	                Toast.makeText(getApplicationContext(),
                     "Group Clicked " + listDataChild.get(listDataHeader.get(groupPosition)),
	                Toast.LENGTH_SHORT).show();*/
                if (mChildrenMap.get(mParentCommunities.get(groupPosition)).size() == 0) {
                    Intent intent = new Intent(OurResourcesActivity.this, SingleCommunityActivity.class);
                    intent.putExtra(CommunityDBHelper.COLUMN_DSPACE_ID,
                            mParentCommunities.get(groupPosition).getDspace_id());
                    intent.putExtra(TABLE, CommunityDBHelper.TABLE_COMMUNITY);
                    intent.putExtra(TITLE, mParentCommunities.get(groupPosition).getTitle());
                    startActivity(intent);
                }
                return false;
            }
        });

        // Listview Group expanded listener
        mExpandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                    /*Toast.makeText(getApplicationContext(),
                            listDataHeader.get(groupPosition) + " Expanded",
	                        Toast.LENGTH_SHORT).show();*/
            }
        });

        // Listview Group collasped listener
        mExpandableListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                    /*Toast.makeText(getApplicationContext(),
                            listDataHeader.get(groupPosition) + " Collapsed",
	                        Toast.LENGTH_SHORT).show();*/

            }
        });

        // Listview on child click listener
        mExpandableListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Community comm = mChildrenMap.get(mParentCommunities.get(groupPosition)).get(childPosition);
                if ((Integer.parseInt(comm.getRgt()) - Integer.parseInt(comm.getLft())) > 1) {
                    // child community contains sub communities
                    snackbar("subbchic");

                    Intent intent = new Intent(OurResourcesActivity.this, SubbchicActivity.class);
                    intent.putExtra(KEY_DSPACE, mChildrenMap.get(mParentCommunities.get(groupPosition)).get(childPosition).getDspace_id());
                    startActivity(intent);










                } else {

                    Intent intent = new Intent(OurResourcesActivity.this, SingleCommunityActivity.class);
                    intent.putExtra(CommunityDBHelper.COLUMN_DSPACE_ID,
                            mChildrenMap.get(mParentCommunities.get(groupPosition)).get(childPosition).getDspace_id());
                    intent.putExtra(TABLE, CommunityDBHelper.TABLE_CHILD_COMMUNITY);
                    intent.putExtra(TITLE, mParentCommunities.get(groupPosition).getTitle());
                    startActivity(intent);
                }
                return false;
            }
        });

        // Swipe refresh listener
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    // Get map from database
    private int getMapFromDB() {
        mDataSource.open();
        mChildrenMap = new HashMap<>();
        ArrayList<Community> temp = mDataSource.getAllCommunities(
                CommunityDBHelper.TABLE_COMMUNITY, CommunityDBHelper.COLUMN_TITLE);
        if (!temp.isEmpty()) { // Data found in DB
            Log.d("Main.getMapFromDB", "Data Found!!!");
            mParentCommunities = temp;
            for (Community community : mParentCommunities) {
                String whereClause = CommunityDBHelper.COLUMN_PARENT_ID +
                        " = '" + community.getDspace_id() + "'";
                ArrayList<Community> arrayList = mDataSource.getSelectedCommunities(
                        CommunityDBHelper.TABLE_CHILD_COMMUNITY, whereClause,
                        CommunityDBHelper.COLUMN_TITLE);
                mChildrenMap.put(community, arrayList);
            }
            mDataSource.close();
            // setting adapter
            ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(OurResourcesActivity.this, mParentCommunities, mChildrenMap);
            // setting list adapter
            mExpandableListView.setAdapter(expandableListAdapter);
            mTVNull.setVisibility(View.INVISIBLE);
            return 0;
        } else { // Data not found in DB
            mDataSource.close();
            Log.d("Main.getMapFromDB", "Data not found in DB");
            return 1;
        }
    }


    private void refreshData() {
        new GetCommunities().execute();
    }

    @TargetApi(11)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_our_resources, menu);

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
                refreshData();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void snackbar(String message) {
        Snackbar.make(findViewById(R.id.linearLayout), message, Snackbar.LENGTH_LONG)
                .show();
    }

    /****************************************/
    private class GetCommunities extends AsyncTask<Void, Void, Void> {

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
                if (!ServiceHandler.isOnline(OurResourcesActivity.this)) {
                    cancel(true);
                }
                jsonStr = ServiceHandler.getText(Global.url);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ServiceHandler", "Could not get JSON Data");

                this.cancel(true);
            }

            Log.d("JSONSTRING", jsonStr);

            mDataSource.open();
            mDataSource.upgrade();

            try {
                // JSON Parsing
                JSONArray communities = new JSONObject(jsonStr).getJSONArray(TAG_COMMUNITY);
                int min = 999;
                int length = communities.length();
                // loop to find the minimum 'level'
                for (int i = 0; i < length; i++) {
                    JSONObject community = communities.getJSONObject(i);
                    int level = Integer.parseInt(community.getString(TAG_LEVEL));
                    if (level < min)
                        min = level;
                }

                for (int i = 0; i < length; i++) {
                    JSONObject jsonCommunity = communities.getJSONObject(i);
                    try {
                        String id = jsonCommunity.getString(TAG_ID); // id from server (not used)
                        String dspace_id = jsonCommunity.getString(TAG_DSPACE_ID);
                        String parent_id = jsonCommunity.getString(TAG_PARENT_ID);
                        String rgt = jsonCommunity.getString(TAG_RGT);
                        String lft = jsonCommunity.getString(TAG_LFT);
                        String level = jsonCommunity.getString(TAG_LEVEL);
                        String title = URLDecoder.decode(jsonCommunity.getString(TAG_TITLE), Global.CHARSET);
                        String description = URLDecoder.decode(jsonCommunity.getString(TAG_DESCRIPTION), Global.CHARSET);
                        String alias = jsonCommunity.getString(TAG_ALIAS);
                        String imageurl = URLDecoder.decode(jsonCommunity.getString(TAG_IMAGEURL), Global.CHARSET).replace(" ", "%20");

                        if (Integer.parseInt(level) == min) { // if parent group
                            mDataSource.createCommunity(
                                    CommunityDBHelper.TABLE_COMMUNITY, dspace_id, parent_id, rgt, lft,
                                    level, title, description, alias, imageurl);
                        } else { // if child
                            mDataSource.createCommunity(
                                    CommunityDBHelper.TABLE_CHILD_COMMUNITY, dspace_id, parent_id, rgt, lft,
                                    level, title, description, alias, imageurl);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mDataSource.close();


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            snackbar("Successfully updated!");
            getMapFromDB();
            // Dismiss loading indicator
            mSwipeRefresh.setRefreshing(false);
            super.onPostExecute(aVoid);
        }


        @Override
        protected void onCancelled() {
            // Dismiss the loading indicator
            mSwipeRefresh.setRefreshing(false);
            // Alert Dialog
            new AlertDialog.Builder(OurResourcesActivity.this).setTitle("Oh no!")
                    .setMessage("Looks like we can't connect!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
            if (getMapFromDB() == 0)
                mTVNull.setVisibility(View.INVISIBLE);
            else
                mTVNull.setVisibility(View.VISIBLE);
            super.onCancelled();
        }
    }
}
