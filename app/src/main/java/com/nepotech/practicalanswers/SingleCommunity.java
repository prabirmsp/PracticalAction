package com.nepotech.practicalanswers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class SingleCommunity extends AppCompatActivity implements OnItemClickListener{
	TextView tv_header;
	TextView tv_desc;
	ProgressDialog pDialog;
	String label;
	String dspace_id;
	String desc;
	ListView list_item;
	ArrayList<String> list;
    ListView listView;
    List<RowItem> rowItems;
    
    
    public String[] mStrings;
    //LazyImageLoadAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_singlecommunity);
		Intent in= SingleCommunity.this.getIntent();
		
		label = in.getStringExtra("label");
		dspace_id = label.substring(label.indexOf("|")+1,label.indexOf("*"));
		
		list_item=(ListView)findViewById(R.id.listView1);
		tv_header = (TextView) findViewById(R.id.textView1);
		tv_header.setText(label.substring(0,label.indexOf("|")));
		
		new GetCommunities().execute();
	
	}
	 private class GetCommunities extends AsyncTask<Void, Void, Void> {
		 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SingleCommunity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
 
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
             list = new ArrayList<String>();
 
            // Making a request to url and<a class="gnfmcezi" href="#" title="Click to Continue > by SaveLots"> GETTING RESPONSE<img src="http://cdncache-a.akamaihd.net/items/it/img/arrow-10x10.png"></a>
            String jsonStr = "";
            try {
                jsonStr = ServiceHandler.getText(Global.url + "?dspace_id=" + dspace_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int cnt = 0;
            //Log.d("Response:single ", "> " + jsonStr);
            list = new ArrayList<String>();
            rowItems = new ArrayList<RowItem>();
            if (jsonStr != null) {
               	try {
    					JSONObject jsonObj = new JSONObject(jsonStr);
    					JSONArray description = jsonObj.getJSONArray("community");
    					mStrings = new String[description.length()];
    					for (int i = 0; i < description.length(); i++) {
                        	
                        	
                            JSONObject c = description.getJSONObject(i);
                            list.add(java.net.URLDecoder.decode(c.getString("item_title"), "UTF-8"));
                            RowItem item = new RowItem(c.getString("thumb"), java.net.URLDecoder.decode(c.getString("item_title"), "UTF-8"), java.net.URLDecoder.decode(c.getString("description"), "UTF-8"),c.getString("alias"));
                            rowItems.add(item);
                            mStrings[i] = c.getString("thumb") + "|" + java.net.URLDecoder.decode(c.getString("item_title"), "UTF-8") + "*" + java.net.URLDecoder.decode(c.getString("description"), "UTF-8") + "^" + c.getString("alias");
    					}
    				} catch (JSONException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            } else {
            	
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                runOnUiThread(noInternet);
            }
 
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            CustomListViewAdapter adapter = new CustomListViewAdapter(SingleCommunity.this, R.layout.single_item, rowItems);
            list_item.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            list_item.setOnItemClickListener(SingleCommunity.this);
         
            
            
         // Create custom adapter for listview
//            adapter=new LazyImageLoadAdapter(SingleCommunity.this, mStrings);
//             
//            //Set adapter to listview
//            list_item.setAdapter(adapter);
        }
        Runnable noInternet = new Runnable() {
    		public void run() {
    			Toast.makeText(SingleCommunity.this, "connection error", Toast.LENGTH_LONG);
    		}
    	};
    	
 
    }
	@Override
	 public void onItemClick(AdapterView<?> parent, View view, int position,
	            long id) {
		Toast.makeText(SingleCommunity.this, "dfd", Toast.LENGTH_LONG);
	        Intent myIntent = new Intent(SingleCommunity.this, SingleItem.class);
            myIntent.putExtra("alias",rowItems.get(position).getAlias());
            startActivity(myIntent);
	}

}
