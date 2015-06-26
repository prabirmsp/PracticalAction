package com.nepotech.practicalanswers;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

public class SingleItem extends AppCompatActivity {

    private Item mItem;
    private ItemsDataSource mDataSource;

    private TextView mTitle;
    private TextView mAuthor;
    private TextView mPublisher;
    private TextView mLanguage;
    private TextView mYear;
    private TextView mType;
    private TextView mDescription;
    private ImageView mThumb;
    private ImageView mTypeIcon;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_item);

        Intent thisIntent = getIntent();
        final String dspace_id = thisIntent.getStringExtra(ItemsDBHelper.COLUMN_DSPACE_ID);
        String title = thisIntent.getStringExtra(ItemsDBHelper.COLUMN_TITLE);

        // get item
        ItemsDataSource dataSource = new ItemsDataSource(this);
        dataSource.open();
        mItem = dataSource.getFromDspaceId(ItemsDBHelper.TABLE_ITEMS, dspace_id);
        dataSource.close();

        setTitle(title);

        mThumb = (ImageView) findViewById(R.id.doc_thumb);
        mTitle = (TextView) findViewById(R.id.title);
        mAuthor = (TextView) findViewById(R.id.author);
        mPublisher = (TextView) findViewById(R.id.publisher);
        mLanguage = (TextView) findViewById(R.id.language);
        mYear = (TextView) findViewById(R.id.date);
        mType = (TextView) findViewById(R.id.type);
        mDescription = (TextView) findViewById(R.id.item_description);
        mTypeIcon = (ImageView) findViewById(R.id.type_icon);

        LinearLayout share_ll = (LinearLayout) findViewById(R.id.share_ll);

        TextView download_tv = (TextView) findViewById(R.id.download);
        ImageView download_iv = (ImageView) findViewById(R.id.image_download);
        LinearLayout download_ll = (LinearLayout) findViewById(R.id.download_ll);

        final TextView star_tv = (TextView) findViewById(R.id.star);
        final ImageView star_iv = (ImageView) findViewById(R.id.image_star);
        LinearLayout star_ll = (LinearLayout) findViewById(R.id.star_ll);

        mTitle.setText(URLDecoder.decode(mItem.getTitle()));
        mAuthor.setText(URLDecoder.decode(mItem.getCreator()));
        mPublisher.setText(URLDecoder.decode(mItem.getPublisher()));
        mLanguage.setText(mItem.getLanguage());
        mYear.setText(mItem.getDateIssued());
        mType.setText(mItem.getType());
        mDescription.setText(URLDecoder.decode(mItem.getDescription()));

        Picasso.with(this).load(URLDecoder.decode(mItem.getDocumentThumbHref())).into(mThumb);
        if (mItem.getType() == "application/pdf") {
            mTypeIcon.setImageResource(R.drawable.ic_picture_as_pdf_black_48dp);
        } else {
            mTypeIcon.setImageResource(R.drawable.ic_insert_drive_file_black_48dp);
        }

        // Set up dialog for download
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Downloading file. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(false);

        mDataSource = new ItemsDataSource(this);
        mDataSource.open();

        if (mDataSource.isPresent(ItemsDBHelper.TABLE_STARRED, dspace_id)) {
            star_tv.setText("Starred");
            star_iv.setImageResource(R.drawable.ic_star_black_48dp);
        } else {
            star_tv.setText("Star");
            star_iv.setImageResource(R.drawable.ic_star_border_black_48dp);
        }

        if (mDataSource.isPresent(ItemsDBHelper.TABLE_DOWNLOADED, dspace_id)) {
            download_tv.setText("Downloaded");
            download_iv.setImageResource(R.drawable.ic_offline_pin_black_48dp);
        } else {
            download_tv.setText("Download");
            download_iv.setImageResource(R.drawable.ic_file_download_black_48dp);
        }

        star_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDataSource.isPresent(ItemsDBHelper.TABLE_STARRED, dspace_id)) {
                    mDataSource.removeStar(dspace_id);
                    star_iv.setImageResource(R.drawable.ic_star_border_black_48dp);
                    star_tv.setText("Star");
                } else {
                    mDataSource.addStar(dspace_id);
                    star_iv.setImageResource(R.drawable.ic_star_black_48dp);
                    star_tv.setText("Starred");
                }
            }
        });

        download_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadFileFromURL().execute(URLDecoder.decode(mItem.getDocumentHref()));
            }
        });


    }

    @Override
    protected void onDestroy() {
        mDataSource.close();
        super.onDestroy();
    }


    /**
     * Background Async Task to download file
     */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        String fileName;
        String dirPath;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Create Directory if not exist
            File dir = new File(Environment.getExternalStorageDirectory().toString()
                    + File.separator + Global.SDFolderName);
            if (!dir.exists()) {
                if (dir.mkdir()) {
                    Log.i("FileDownloader", "SD Directory Created");
                } else
                    cancel(true);
            }
            // set cancel download button
            pDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancel(true);

                        }
                    });
            pDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                // progress bar
                int lengthOfFile = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);


                fileName = f_url[0].substring(f_url[0].lastIndexOf("/") + 1);
                dirPath = Environment
                        .getExternalStorageDirectory().toString() + File.separator +
                        Global.SDFolderName + File.separator;
                // Output stream
                OutputStream output = new FileOutputStream(dirPath + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            pDialog.dismiss();
            Snackbar.make(findViewById(R.id.relative_layout), "Download Completed!",
                    Snackbar.LENGTH_LONG)
                    .setAction("OPEN", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            File file = new File(dirPath + fileName);

                            // Just example, you should parse file name for extension
                            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                                    fileName.substring(fileName.lastIndexOf(".")));

                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(file), mime);
                            startActivityForResult(intent, 10);

                        }
                    })
                    .show();

            // Add to downloaded list
            mDataSource.addDownloaded(mItem.getDspaceId(), fileName);

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            snackbar("Download Cancelled");
        }
    }

    private void snackbar(String message) {
        Snackbar.make(findViewById(R.id.relative_layout), message, Snackbar.LENGTH_LONG)
                .show();
    }
}

