package com.nepotech.practicalanswers.home_activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nepotech.practicalanswers.R;

import java.util.List;

/**
 * Created by prabir on 6/30/15.
 */
public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder> {

    Context mContext;
    List<HomeActivity.HomeRecyclerItem> mContent;

    public HomeRecyclerAdapter (Context context, List<HomeActivity.HomeRecyclerItem> content) {
        mContext = context;
        mContent = content;
    }

    @Override
    public int getItemViewType(int position) {
        return mContent.get(position).viewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(parent.getContext());
        View v;
        switch (viewType) {
            case BANNER:
                v = inflater.inflate(R.id.home_rv_banner);
                break;
            case HEADER:
                v = inflater.inflate(R.id.home_rv_header);
                break;
            case ITEM_CARD:
                v = inflater.inflate(R.id.home_rv_item);
                break;
            default:
        }
        return ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);

        }
    }
}
