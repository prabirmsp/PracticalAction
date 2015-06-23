package com.nepotech.practicalanswers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.util.ArrayList;

public class ItemsListViewAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<Item> mItems;


    public ItemsListViewAdapter(Context context, ArrayList<Item> items) {
        super();
        this.mItems = items;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Item getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtDesc;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item rowItem = getItem(position);
        ViewHolder holder;

        LayoutInflater mInflater = (LayoutInflater) mContext
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.single_item, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.item_description);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.item_title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.doc_thumb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // set textviews
        holder.txtDesc.setText(URLDecoder.decode(rowItem.getDescription()));
        holder.txtTitle.setText(URLDecoder.decode(rowItem.getTitle()));
        // set document thumb imageview
        Picasso.with(mContext).load(rowItem.getDocumentThumbHref()).into(holder.imageView);

        return convertView;
    }
}
