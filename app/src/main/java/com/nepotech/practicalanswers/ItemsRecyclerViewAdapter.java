package com.nepotech.practicalanswers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.util.ArrayList;

public class ItemsRecyclerViewAdapter extends RecyclerView.Adapter<ItemsRecyclerViewAdapter.ViewHolder> {

    Context mContext;
    ArrayList<Item> mItems;
    String mWindowTitle;


    public ItemsRecyclerViewAdapter(Context context, ArrayList<Item> items, String windowTitle) {
        super();
        this.mItems = items;
        this.mContext = context;
        this.mWindowTitle = windowTitle;
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
        final Item rowItem = mItems.get(i);

        viewHolder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SingleItem.class);
                intent.putExtra(ItemsDBHelper.COLUMN_DSPACE_ID, rowItem.getDspaceId());
                intent.putExtra(ItemsDBHelper.COLUMN_TITLE, mWindowTitle);
                mContext.startActivity(intent);


            }
        });
        // set textviews
        viewHolder.txtDesc.setText(URLDecoder.decode(rowItem.getDescription()));
        viewHolder.txtTitle.setText(URLDecoder.decode(rowItem.getTitle()));
        String imageUrl = "" + URLDecoder.decode(rowItem.getDocumentThumbHref());
        // set document thumb imageview
        Picasso.with(mContext).load(imageUrl).into(viewHolder.imageView);
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
        View v;

        public ViewHolder(View v) {
            super(v);
            this.v = v;
            txtDesc = (TextView) v.findViewById(R.id.item_description);
            txtTitle = (TextView) v.findViewById(R.id.item_title);
            imageView = (ImageView) v.findViewById(R.id.doc_thumb);

        }
    }
}
