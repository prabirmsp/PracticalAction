package com.nepotech.practicalanswers;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

//set dspace like childs

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private ArrayList<Community> mCommunities = null;

    public ExpandableListAdapter(Context context, ArrayList<Community> communities) {
        mContext = context;
        mCommunities = communities;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.mCommunities.get(groupPosition).children.get(childPosition);
    }


    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        Community child = mCommunities.get(groupPosition).children.get(childPosition);
        String childTitle =  URLDecoder.decode(child.getTitle());
        String level_child = child.getLevel();

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        txtListChild.setText(childTitle);

        ImageView iv_child = (ImageView) convertView.findViewById(R.id.imageView1);
        int parentLevel = Integer.parseInt(mCommunities.get(groupPosition).getLevel());
        if ((Integer.parseInt(level_child) - parentLevel) == 2) {
            txtListChild.setPadding(10, 0, 0, 0);
            iv_child.setPadding(20, 0, 0, 0);
        } else {
            txtListChild.setPadding(0, 0, 0, 0);
            iv_child.setPadding(0, 0, 0, 0);
        }

        String imageurl_child = child.getImageurl();
        Log.d("CHILD-IMAGEURL", imageurl_child);

        Picasso.with(mContext).load(Global.baseUrl + URLDecoder.decode(imageurl_child)).into(iv_child);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
//    	Log.i("count",""+this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
//                .size());
        return this.mCommunities.get(groupPosition).getChildrenNumber();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mCommunities.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mCommunities.size();
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
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        Community headerCommunity = mCommunities.get(groupPosition);
        String headerTitle = URLDecoder.decode(headerCommunity.getTitle());
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        Log.i("header in group view", "" + headerTitle);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        ImageView iv = (ImageView) convertView.findViewById(R.id.imageView1);
        String imageurl = URLDecoder.decode(headerCommunity.getImageurl());

        // Load image
        Picasso.with(this.mContext).load(Global.baseUrl + imageurl).into(iv);

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

    public boolean loadImageFromURL(String fileUrl,
                                    ImageView iv) {
        try {

            URL myFileUrl = new URL(fileUrl);
            HttpURLConnection conn =
                    (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            //Log.e("lkjkkjkl",":"+ is);
            iv.setImageBitmap(BitmapFactory.decodeStream(is));

            return true;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}