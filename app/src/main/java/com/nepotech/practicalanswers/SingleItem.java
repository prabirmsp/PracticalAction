package com.nepotech.practicalanswers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.net.URLDecoder;

public class SingleItem extends FragmentActivity {

    Item mItem;

    TextView mTitle;
    TextView mAuthor;
    TextView mPublisher;
    TextView mLanguage;
    TextView mYear;
    TextView mType;
    TextView mDescription;
    ImageView mThumb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_item);

        Intent thisIntent = getIntent();
        String dspace_id = thisIntent.getStringExtra(ItemsDBHelper.COLUMN_DSPACE_ID);

        // get item
        ItemsDataSource dataSource = new ItemsDataSource(this);
        dataSource.open();
        mItem = dataSource.getFromDspaceId(ItemsDBHelper.TABLE_ITEMS, dspace_id);
        dataSource.close();

        setTitle(URLDecoder.decode(mItem.getTitle()));

        mThumb = (ImageView) findViewById(R.id.doc_thumb);
        mTitle = (TextView) findViewById(R.id.title);
        mAuthor = (TextView) findViewById(R.id.author);
        mPublisher = (TextView) findViewById(R.id.publisher);
        mLanguage = (TextView) findViewById(R.id.language);
        mYear = (TextView) findViewById(R.id.date);
        mType = (TextView) findViewById(R.id.type);
        mDescription = (TextView) findViewById(R.id.item_description);

        mTitle.setText(URLDecoder.decode(mItem.getTitle()));
        mAuthor.setText(URLDecoder.decode(mItem.getCreator()));
        mPublisher.setText(URLDecoder.decode(mItem.getPublisher()));
        mLanguage.setText(mItem.getLanguage());
        mYear.setText(mItem.getDateIssued());
        mType.setText(mItem.getType());
        mDescription.setText(URLDecoder.decode(mItem.getDescription()));

        Picasso.with(this).load(URLDecoder.decode(mItem.getDocumentThumbHref())).into(mThumb);



    }


}
