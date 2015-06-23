package com.nepotech.practicalanswers;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ExpandableListAdapter mExpandableListAdapter;
    private ExpandableListView mExpandableListView;
    private ProgressBar mProgressBar;
    private TextView mTVNull;

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

    protected static final String TABLE = "table";
    protected static final String TITLE = "title";

    // Store Communities Data
    private CommunityDataSource mDataSource;
    protected ArrayList<Community> mParentCommunities;
    protected HashMap<Community, ArrayList<Community>> mChildrenMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mExpandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        mDataSource = new CommunityDataSource(MainActivity.this);

        mTVNull = (TextView) findViewById(R.id.fillerTextView);
        mTVNull.setVisibility(View.INVISIBLE);

        mProgressBar.setVisibility(View.VISIBLE);
        if (getMapFromDB() != 0) {
            refreshData();
        } else {
            snackbar("Data Recieved from DB!");
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
/*	                Toast.makeText(getApplicationContext(),
                     "Group Clicked " + listDataChild.get(listDataHeader.get(groupPosition)),
	                Toast.LENGTH_SHORT).show();*/
                if (mChildrenMap.get(mParentCommunities.get(groupPosition)).size() == 0) {
                    Intent intent = new Intent(MainActivity.this, SingleCommunity.class);
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
                Intent intent = new Intent(MainActivity.this, SingleCommunity.class);
                intent.putExtra(CommunityDBHelper.COLUMN_DSPACE_ID,
                        mChildrenMap.get(mParentCommunities.get(groupPosition)).get(childPosition).getDspace_id());
                intent.putExtra(TABLE, CommunityDBHelper.TABLE_CHILD_COMMUNITY);
                intent.putExtra(TITLE, mParentCommunities.get(groupPosition).getTitle());
                startActivity(intent);
                return false;
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
            mExpandableListAdapter = new ExpandableListAdapter(MainActivity.this, mParentCommunities, mChildrenMap);
            // setting list adapter
            mExpandableListView.setAdapter(mExpandableListAdapter);
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
                refreshData();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /****************************************/
    private class GetCommunities extends AsyncTask<Void, Void, Void> {

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

                    String id = jsonCommunity.getString(TAG_ID); // id from server (not used)
                    String dspace_id = jsonCommunity.getString(TAG_DSPACE_ID);
                    String parent_id = jsonCommunity.getString(TAG_PARENT_ID);
                    String rgt = jsonCommunity.getString(TAG_RGT);
                    String lft = jsonCommunity.getString(TAG_LFT);
                    String level = jsonCommunity.getString(TAG_LEVEL);
                    String title = jsonCommunity.getString(TAG_TITLE);
                    String description = jsonCommunity.getString(TAG_DESCRIPTION);
                    String alias = jsonCommunity.getString(TAG_ALIAS);
                    String imageurl = jsonCommunity.getString(TAG_IMAGEURL);

                    if (Integer.parseInt(level) == min) { // if parent group
                        mDataSource.createCommunity(
                                CommunityDBHelper.TABLE_COMMUNITY, dspace_id, parent_id, rgt, lft,
                                level, title, description, alias, imageurl);
                    } else { // if child
                        mDataSource.createCommunity(
                                CommunityDBHelper.TABLE_CHILD_COMMUNITY, dspace_id, parent_id, rgt, lft,
                                level, title, description, alias, imageurl);
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
            // Dismiss the progress bar
            mProgressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(aVoid);
        }


        @Override
        protected void onCancelled() {
            // Dismiss the progress dialog
            mProgressBar.setVisibility(View.INVISIBLE);
            // Alert Dialog
            new AlertDialog.Builder(MainActivity.this).setTitle("Oh no!")
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

    private void snackbar(String message) {
        Snackbar.make(findViewById(R.id.linearLayout), message, Snackbar.LENGTH_LONG)
                .show();
    }
}
