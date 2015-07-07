package com.nepotech.practicalanswers.home_activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nepotech.practicalanswers.R;
import com.nepotech.practicalanswers.items.Item;
import com.nepotech.practicalanswers.items.ItemsDataSource;
import com.nepotech.practicalanswers.items.ItemsRecyclerViewAdapter;

import java.util.ArrayList;

public class Downloaded extends AppCompatActivity {

    RecyclerView mRecyclerView;
    ItemsDataSource mDataSource;
    ArrayList<Item> mDownloadedItems;
    ItemsRecyclerViewAdapter mRecyclerViewAdapter;
    TextView mFillerTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_recycler_view);

        mFillerTV = (TextView) findViewById(R.id.fillerTextView);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerViewAdapter = new ItemsRecyclerViewAdapter(this, null, "Downloaded");
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mDataSource = new ItemsDataSource(this);


    }

    @Override
    protected void onResume() {
        super.onResume();

        mDataSource.open();

        mDownloadedItems = mDataSource.getAllDownloaded();
        mRecyclerViewAdapter.updateItems(mDownloadedItems);
        mRecyclerViewAdapter.notifyDataSetChanged();

        mDataSource.close();

        if (mDownloadedItems.size() < 1)
            mFillerTV.setVisibility(View.VISIBLE);
        else
            mFillerTV.setVisibility(View.INVISIBLE);

    }

}
