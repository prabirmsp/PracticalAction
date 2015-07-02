package com.nepotech.practicalanswers.home_activity;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.nepotech.practicalanswers.R;
import com.nepotech.practicalanswers.items.Item;
import com.nepotech.practicalanswers.items.ItemsDataSource;
import com.nepotech.practicalanswers.our_resources_activity.OurResourcesActivity;

import java.net.URLDecoder;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    ArrayList<HomeRecyclerItem> mContent;
    ItemsDataSource mItemsDataSource;
    private String mLangFilter;
    public static final String LANG_ALL = "All";
    public static final String LANG_PREFS_NAME = "LanguagePrefs";
    public static final String KEY_LANGUAGE = "language";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_home);

        setTitle("Practical Action");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
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
        mRecyclerView.setLayoutManager(gridLayoutManager);

        SharedPreferences langSettings = getSharedPreferences(LANG_PREFS_NAME, 0);
        mLangFilter = langSettings.getString(KEY_LANGUAGE, LANG_ALL);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<Item> arrayList;

        /** Add content **/
        mContent = new ArrayList<>();
        // add banner
        mContent.add(new HomeRecyclerItem(HomeRecyclerItem.BANNER, 0, true));
        mItemsDataSource = new ItemsDataSource(this);
        mItemsDataSource.open();

        // add starred header
        mContent.add(new HomeRecyclerItem(
                HomeRecyclerItem.HEADER, "Starred Items",
                new Intent(HomeActivity.this, Starred.class)));
        // add starred content
        arrayList = mItemsDataSource.getAllStarred();
        for (int i = 0; i < 3; i++) {
            if (i < arrayList.size()) {
                Item item = arrayList.get(i);
                mContent.add(new HomeRecyclerItem(HomeRecyclerItem.ITEM_CARD,
                        URLDecoder.decode(item.getTitle()), item.getDocumentThumbHref(),
                        "Starred", item.getDspaceId(), i + 1, true));
            } else
                mContent.add(new HomeRecyclerItem(HomeRecyclerItem.ITEM_CARD, i + 1, false));
        }

        // add downloaded header
        mContent.add(new HomeRecyclerItem(
                HomeRecyclerItem.HEADER, "Downloaded Files",
                new Intent(HomeActivity.this, Downloaded.class)));
        // add downloaded content
        arrayList = mItemsDataSource.getAllDownloaded();
        for (int i = 0; i < 3; i++) {
            if (i < arrayList.size()) {
                Item item = arrayList.get(i);
                mContent.add(new HomeRecyclerItem(HomeRecyclerItem.ITEM_CARD,
                        URLDecoder.decode(item.getTitle()), item.getDocumentThumbHref(),
                        "Downloaded", item.getDspaceId(), i + 1, true));
            } else
                mContent.add(new HomeRecyclerItem(HomeRecyclerItem.ITEM_CARD, i + 1, false));
        }
        mItemsDataSource.close();

        HomeRecyclerAdapter adapter = new HomeRecyclerAdapter(this, mContent);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.filter) {
            showFilterDialog();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        /** Set up dialog for filter **/
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.filter_dialog);
        dialog.setTitle("Select Filter");
        Spinner lang_spinner = (Spinner) dialog.findViewById(R.id.spinner_lang);
        mItemsDataSource.open();
        final ArrayList<String> langList = mItemsDataSource.getAllLanguages();
        langList.add(0, LANG_ALL); // add All to beginning
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, langList);
        lang_spinner.setAdapter(adapter);
        lang_spinner.setSelection(langList.indexOf(mLangFilter));
        lang_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean firstSelect = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstSelect) {
                    firstSelect = false;
                    return;
                }
                mLangFilter = langList.get(position);
                SharedPreferences langPrefs = getSharedPreferences(LANG_PREFS_NAME, 0);
                SharedPreferences.Editor editor = langPrefs.edit();
                editor.putString(KEY_LANGUAGE, mLangFilter);
                editor.apply();
                dialog.dismiss();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mLangFilter = LANG_ALL;
            }
        });
        mItemsDataSource.close();
        dialog.show();
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
        String category;
        String imageHref;
        String dspace_id;
        boolean visible;
        Intent intent;
        int col;

        public HomeRecyclerItem(int viewType, int col, boolean visible) {
            this.viewType = viewType;
            this.col = col;
            this.visible = visible;
        }

        public HomeRecyclerItem(int viewType, @Nullable String text, @Nullable String imageHref,
                                String category, String dspace_id, int col, boolean visible) {
            this.viewType = viewType;
            this.text = text;
            this.imageHref = imageHref;
            this.category = category;
            this.col = col;
            this.dspace_id = dspace_id;
            this.visible = visible;
        }

        public HomeRecyclerItem(int viewType, String text, Intent intent) {
            this.viewType = viewType;
            this.text = text;
            this.intent = intent;
            this.visible = true;
        }
    }
}
