package com.nepotech.practicalanswers.our_resources_activity;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nepotech.practicalanswers.Global;
import com.nepotech.practicalanswers.R;
import com.nepotech.practicalanswers.community.Community;

import java.util.ArrayList;
import java.util.HashMap;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private ArrayList<Community> mParentCommunities;
    private HashMap<Community, ArrayList<Community>> mChildrenMap;


    public ExpandableListAdapter(Context context,
                                 ArrayList<Community> parentList,
                                 HashMap<Community, ArrayList<Community>> childMap) {
        mContext = context;
        mParentCommunities = parentList;
        mChildrenMap = childMap;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.mChildrenMap.get(mParentCommunities.get(groupPosition)).get(childPosition);
    }


    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        //Fresco.initialize(mContext);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_child_item, null);
        }

        Community child = (Community) getChild(groupPosition, childPosition);
        String childTitle = child.getTitle();

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        txtListChild.setText(childTitle);

        SimpleDraweeView draweeView = (SimpleDraweeView) convertView.findViewById(R.id.imageView1);
        if (Math.abs(Integer.parseInt(child.getLft()) - Integer.parseInt(child.getRgt())) > 1) {
            Log.d("SUBBCHICK found", child.getTitle());
            txtListChild.setPadding(10, 0, 0, 0);
            txtListChild.setTypeface(null, Typeface.BOLD);
            draweeView.setPadding(20, 0, 0, 0);
        } else {
            txtListChild.setPadding(0, 0, 0, 0);
            draweeView.setPadding(0, 0, 0, 0);
        }

        String imageurl_child = child.getImageurl();
        //Log.d("CHILD-IMAGEURL", imageurl_child);
        Uri uri = Uri.parse(Global.baseUrl + imageurl_child);
        draweeView.setImageURI(uri);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mChildrenMap.get(mParentCommunities.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mParentCommunities.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mParentCommunities.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        //Fresco.initialize(mContext);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }

        Community headerCommunity = mParentCommunities.get(groupPosition);
        String headerTitle = headerCommunity.getTitle();
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        //Log.i("header in group view", "" + headerTitle);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        //ImageView iv = (ImageView) convertView.findViewById(R.id.imageView1);
        String imageurl = headerCommunity.getImageurl();
        //Log.d("GROUPPICURL", imageurl);

        TextView groupDescription = (TextView) convertView.findViewById(R.id.groupDescription);
        String descriptionText = headerCommunity.getDescription();
        descriptionText = Html.fromHtml(descriptionText).toString();
        groupDescription.setText(descriptionText);

        // Load image
        //Picasso.with(this.mContext).load(Global.baseUrl + imageurl).into(iv);
        Uri uri = Uri.parse(Global.baseUrl + imageurl);
        SimpleDraweeView draweeView = (SimpleDraweeView) convertView.findViewById(R.id.imageView1);
        draweeView.setImageURI(uri);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}