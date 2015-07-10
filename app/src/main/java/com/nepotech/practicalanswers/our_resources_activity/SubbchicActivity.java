package com.nepotech.practicalanswers.our_resources_activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nepotech.practicalanswers.Global;
import com.nepotech.practicalanswers.R;
import com.nepotech.practicalanswers.community.Community;
import com.nepotech.practicalanswers.community.CommunityDBHelper;
import com.nepotech.practicalanswers.community.CommunityDataSource;
import com.nepotech.practicalanswers.items.SingleCommunityActivity;

import java.util.ArrayList;

public class SubbchicActivity extends AppCompatActivity {

    private Community mCommunity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subbchic);
        // Transition
        overridePendingTransition(Global.B_enter, Global.A_exit);

        String dspace = getIntent().getStringExtra(OurResourcesActivity.KEY_DSPACE);

        // get parent community
        CommunityDataSource dataSource = new CommunityDataSource(this);
        dataSource.open();
        mCommunity = dataSource.getFromDspaceId(CommunityDBHelper.TABLE_CHILD_COMMUNITY, dspace);

        setTitle(mCommunity.getTitle());

        final ArrayList<Community> subbcomms = dataSource.getSelectedCommunities(CommunityDBHelper.TABLE_CHILD_COMMUNITY,
                "lft > " + mCommunity.getLft() + " and rgt < " + mCommunity.getRgt(), CommunityDBHelper.COLUMN_TITLE);

        ListView listView = (ListView) findViewById(R.id.list_view);
        CustomLVAdapter adapter = new CustomLVAdapter(subbcomms);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SubbchicActivity.this, SingleCommunityActivity.class);
                intent.putExtra(CommunityDBHelper.COLUMN_DSPACE_ID,
                        subbcomms.get(position).getDspace_id());
                intent.putExtra(OurResourcesActivity.TABLE, CommunityDBHelper.TABLE_CHILD_COMMUNITY);
                intent.putExtra(OurResourcesActivity.TITLE, subbcomms.get(position).getTitle());
                startActivity(intent);
            }
        });

        dataSource.close();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        // Transition
        overridePendingTransition(Global.B_enter, Global.A_exit);
    }

    private class CustomLVAdapter extends BaseAdapter {

        ArrayList<Community> communities;

        public CustomLVAdapter(ArrayList<Community> communities) {
            this.communities = communities;
        }

        @Override
        public int getCount() {
            return communities.size();
        }

        @Override
        public Community getItem(int position) {
            return communities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.list_group, null);
            }
            TextView title = (TextView) convertView.findViewById(R.id.lblListHeader);
            TextView desc = (TextView) convertView.findViewById(R.id.groupDescription);
            SimpleDraweeView draweeView = (SimpleDraweeView) convertView.findViewById(R.id.imageView1);

            title.setText(getItem(position).getTitle());
            desc.setText(getItem(position).getDescription());
            Uri uri = Uri.parse(Global.baseUrl + getItem(position).getImageurl());
            draweeView.setImageURI(uri);

            return convertView;
        }
    }


}
