package com.nepotech.practicalanswers.home_activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nepotech.practicalanswers.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder> {

    Context mContext;
    ArrayList<HomeActivity.HomeRecyclerItem> mContent;

    private final int BANNER = HomeActivity.HomeRecyclerItem.BANNER;
    private final int HEADER = HomeActivity.HomeRecyclerItem.HEADER;
    private final int ITEM_CARD = HomeActivity.HomeRecyclerItem.ITEM_CARD;

    public HomeRecyclerAdapter(Context context, ArrayList<HomeActivity.HomeRecyclerItem> content) {
        mContext = context;
        mContent = content;
    }

    @Override
    public int getItemViewType(int position) {
        return mContent.get(position).viewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        switch (viewType) {
            case BANNER:
                v = inflater.inflate(R.layout.home_rv_banner, parent, false);
                break;
            case HEADER:
                v = inflater.inflate(R.layout.home_rv_header, parent, false);
                break;
            case ITEM_CARD:
                v = inflater.inflate(R.layout.home_rv_item, parent, false);
                break;
            default:
                throw new IllegalStateException("Unexpected viewType (= " + viewType + ")");
        }
        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HomeActivity.HomeRecyclerItem recyclerItem = mContent.get(position);
        switch (recyclerItem.viewType) {
            case BANNER:
                break;
            case HEADER:
                onBindHeader(holder, position, recyclerItem);
                break;
            case ITEM_CARD:
                onBindItem(holder, position, recyclerItem);
                break;
            default:
                break;
        }
    }

    private void onBindHeader(ViewHolder holder, int position, HomeActivity.HomeRecyclerItem item) {
        holder.textView.setText(item.text);
        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



    private void onBindItem(ViewHolder holder, int position, HomeActivity.HomeRecyclerItem item) {
        if (item.visible) {
            holder.textView.setText(item.text);
            Picasso.with(mContext).load(item.imageHref).into(holder.imageView);
        }
        else
            holder.itemView.setVisibility(View.INVISIBLE);
    }


    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        Button moreButton;
        View itemView;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            if (viewType == HEADER)
                moreButton = (Button) itemView.findViewById(R.id.button);
            this.itemView = itemView;
        }
    }
}
