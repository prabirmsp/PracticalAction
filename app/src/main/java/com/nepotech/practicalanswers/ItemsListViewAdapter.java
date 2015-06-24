package com.nepotech.practicalanswers;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.util.ArrayList;

public class ItemsListViewAdapter extends RecyclerView.Adapter<ItemsListViewAdapter.ViewHolder> {

    Context mContext;
    ArrayList<Item> mItems;


    public ItemsListViewAdapter(Context context, ArrayList<Item> items) {
        super();
        this.mItems = items;
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.single_item, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Item rowItem = mItems.get(i);

        // set textviews
        viewHolder.txtDesc.setText(URLDecoder.decode(rowItem.getDescription()));
        viewHolder.txtTitle.setText(URLDecoder.decode(rowItem.getTitle()));
        // set document thumb imageview
        Picasso.with(mContext).load(rowItem.getDocumentThumbHref()).into(viewHolder.imageView);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    // ViewHolder Class //
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtDesc;

        public ViewHolder(View v) {
            super(v);
            txtDesc = (TextView) v.findViewById(R.id.item_description);
            txtTitle = (TextView) v.findViewById(R.id.item_title);
            imageView = (ImageView) v.findViewById(R.id.doc_thumb);

        }
    }
}
