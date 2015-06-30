package com.nepotech.practicalanswers.home_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nepotech.practicalanswers.R;
import com.nepotech.practicalanswers.items.Item;
import com.nepotech.practicalanswers.items.ItemsDataSource;
import com.nepotech.practicalanswers.items.ItemsRecyclerViewAdapter;
import com.nepotech.practicalanswers.our_resources_activity.OurResourcesActivity;

import java.net.URLDecoder;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    GridLayoutManager mGlm;
    ArrayList<HomeRecyclerItem> mContent;
    ItemsDataSource mItemsSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mGlm = new GridLayoutManager(this, 3);
        mGlm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (position) {
                    case 0:
                    case 1:
                    case 5:
                    case 9:
                        return 3;
                    default:
                        return 1;
                }
            }
        });
        mRecyclerView.setLayoutManager(mGlm);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        ArrayList<Item> arrayList = new ArrayList<>();

        // Add content
        mContent = new ArrayList<>();
        mContent.add(new HomeRecyclerItem(HomeRecyclerItem.BANNER, true)); // add banner
        mItemsSource = new ItemsDataSource(this);
        mItemsSource.open();
        // add downloaded header
        mContent.add(new HomeRecyclerItem(HomeRecyclerItem.HEADER, "Downloaded Files", null, true));
        // add downloaded content
        arrayList = mItemsSource.getAllDownloaded();
        for (int i = 0; i < 3; i++) {
            if (i < arrayList.size()) {
                Item item = arrayList.get(i);
                mContent.add(new HomeRecyclerItem(HomeRecyclerItem.ITEM_CARD,
                        URLDecoder.decode(item.getTitle()), item.getDocumentThumbHref(), true));
            }
            else
                mContent.add(new HomeRecyclerItem(HomeRecyclerItem.ITEM_CARD, false));
        }
        // add starred header
        mContent.add(new HomeRecyclerItem(HomeRecyclerItem.HEADER, "Starred Items", null, true));
        // add starred content
        arrayList = mItemsSource.getAllStarred();
        for (int i = 0; i < 3; i++) {
            if (i < arrayList.size()) {
                Item item = arrayList.get(i);
                mContent.add(new HomeRecyclerItem(HomeRecyclerItem.ITEM_CARD,
                        URLDecoder.decode(item.getTitle()), item.getDocumentThumbHref(), true));
            }
            else
                mContent.add(new HomeRecyclerItem(HomeRecyclerItem.ITEM_CARD, false));
        }

        HomeRecyclerAdapter adapter = new HomeRecyclerAdapter(this, mContent);
        mRecyclerView.setAdapter(adapter);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void resources(View view) {
        Intent intent = new Intent(this, OurResourcesActivity.class);
        startActivity(intent);
    }

    public void starred(View view) {
        Intent intent = new Intent(this, Starred.class);
        startActivity(intent);
    }

    public void downloaded(View view) {
        Intent intent = new Intent(this, Downloaded.class);
        startActivity(intent);
    }

    public static class HomeRecyclerItem {
        protected static final int BANNER = 0;
        protected static final int HEADER = 1;
        protected static final int ITEM_CARD = 2;

        int viewType;
        String text;
        String imageHref;
        int drawable;
        boolean visible;

        public HomeRecyclerItem(int viewType, @Nullable String text, int drawable, boolean visible) {
            this.viewType = viewType;
            this.text = text;
            this.drawable = drawable;
            this.visible = visible;
        }

        public HomeRecyclerItem(int viewType, @Nullable String text, @Nullable String imageHref,
                                boolean visible) {
            this.viewType = viewType;
            this.text = text;
            this.imageHref = imageHref;
            this.visible = visible;
        }

        public HomeRecyclerItem(int viewType, boolean visible) {
            this.viewType = viewType;
            this.visible = visible;
        }


    }
}
