package com.nepotech.practicalanswers;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ExpandableListAdapter mExpandableListAdapter;
    private ExpandableListView mExpandableListView;
    private ProgressDialog pDialog;

    // Store communities data
    ArrayList<Community> mCommunities;

    // JSON Node names
    private static final String TAG_COMMUNITY = "communities"; //my first object name

    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_PARENT_ID = "parent_id";
    private static final String TAG_DSPACE_ID = "dspace_id";
    private static final String TAG_LEVEL = "level";
    private static final String TAG_LFT = "lft";
    private static final String TAG_RGT = "rgt";
    private static final String TAG_DESCRIPTION= "description";
    private static final String TAG_ALIAS= "alias";
    private static final String TAG_IMAGEURL = "imageurl";

    public CommunityDataSource mDatasource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mExpandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        mDatasource = new CommunityDataSource(MainActivity.this);
        mDatasource.open();
        refreshData();


        mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
/*	                Toast.makeText(getApplicationContext(),
                     "Group Clicked " + listDataChild.get(listDataHeader.get(groupPosition)),
	                Toast.LENGTH_SHORT).show();*/
                if (mCommunities.get(groupPosition).getChildrenNumber() == 0) {
                    Intent singlecomm = new Intent(MainActivity.this, SingleCommunity.class);
         //           singlecomm.putExtra("label", listDataHeader.get(groupPosition));
                    startActivity(singlecomm);
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
                Intent myIntent = new Intent(MainActivity.this, SingleCommunity.class);
     //           myIntent.putExtra("label", listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition));
                startActivity(myIntent);
                return false;
            }
        });
    }

    /*
     * Preparing the list data
     */
    private void refreshData() {
        mCommunities = new ArrayList<>();

        // Showing progress dialog
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        new GetCommunities().execute();

        // Dismiss the progress dialog
        if (pDialog.isShowing())
            pDialog.dismiss();



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
    private class GetCommunities extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            // GET FROM WEB
            // get JSON data
            String jsonStr = "";
            try {
                jsonStr = ServiceHandler.getText(Global.url);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ServiceHandler", "Could not get JSON Data");
            }

            Log.d("JSONSTRING", jsonStr);

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

                // Create new ArrayList of Communities
                mCommunities = new ArrayList<>();

                int count = 0;
                for (int i = 0; i < length; i++) {
                    JSONObject jsonCommunity = communities.getJSONObject(i);

                    String id = jsonCommunity.getString(TAG_ID);
                    String dspace_id = jsonCommunity.getString(TAG_DSPACE_ID);
                    String parent_id = jsonCommunity.getString(TAG_PARENT_ID);
                    String rgt = jsonCommunity.getString(TAG_RGT);
                    String lft = jsonCommunity.getString(TAG_LFT);
                    String level = jsonCommunity.getString(TAG_LEVEL);
                    String title = jsonCommunity.getString(TAG_TITLE);
                    String description = jsonCommunity.getString(TAG_DESCRIPTION);
                    String alias = jsonCommunity.getString(TAG_ALIAS);
                    String imageurl = jsonCommunity.getString(TAG_IMAGEURL);

                    Community community = new Community(id, dspace_id, parent_id, rgt, lft, level, title,
                            description, alias, imageurl);

                    if (Integer.parseInt(level) == min) { // if parent group
                        mCommunities.add(community);
                        count++;
                    }
                    else { // if child
                        mCommunities.get(count - 1).addChild(community);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            // setting adapter
            mExpandableListAdapter = new ExpandableListAdapter(MainActivity.this, mCommunities);

            // setting list adapter
            mExpandableListView.setAdapter(mExpandableListAdapter);

            super.onPostExecute(o);
        }
    }
}
