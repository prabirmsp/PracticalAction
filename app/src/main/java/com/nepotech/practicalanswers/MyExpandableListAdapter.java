package com.nepotech.practicalanswers;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.SimpleCursorTreeAdapter;

public class MyExpandableListAdapter extends SimpleCursorTreeAdapter {
	public CommunityDataSource datasource;
	Context context_me;
    public MyExpandableListAdapter(Cursor cursor, Context context,int groupLayout, 
        int childLayout, String[] groupFrom, int[] groupTo, String[] childrenFrom, 
        int[] childrenTo) {
            super(context, cursor, groupLayout, groupFrom, groupTo,
                  childLayout, childrenFrom, childrenTo);
            this.context_me = context;
            datasource = new CommunityDataSource(context);
            datasource.open();
        }
    

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
    	Log.e("childcurosr",""+groupCursor.getString(groupCursor.getColumnIndex("dspace_id")));
        Cursor childCursor = datasource.fetchChildren(groupCursor.getString(groupCursor.getColumnIndex("dspace_id")));            
        //MyExpandableListAdapter.this.startManagingCursor(childCursor);
        childCursor.moveToFirst();
        
        return childCursor;
    }
}