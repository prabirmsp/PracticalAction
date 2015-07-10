package com.nepotech.practicalanswers.items;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nepotech.practicalanswers.AnalyticsApplication;
import com.nepotech.practicalanswers.Global;
import com.nepotech.practicalanswers.PDFViewActivity;
import com.nepotech.practicalanswers.R;
import com.nepotech.practicalanswers.ServiceHandler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class SingleItemActivity extends AppCompatActivity {

    private static final String TAG = SingleItemActivity.class.getSimpleName();
    private static final String OPEN = "open";
    private Item mItem;
    private ItemsDataSource mDataSource;
    public static final String KEY_EXTRA_LINK = "document_href";

    private SimpleDraweeView mThumb;
    private TextView mDownloadTV;
    private ImageView mDownloadIV;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        Fresco.initialize(this);
        setContentView(R.layout.activity_single_item);

        // Transition
        overridePendingTransition(Global.B_enter, Global.A_exit);

        Intent thisIntent = getIntent();
        final String dspace_id = thisIntent.getStringExtra(ItemsDBHelper.COLUMN_DSPACE_ID);
        String title = thisIntent.getStringExtra(ItemsDBHelper.COLUMN_TITLE);

        // get item
        ItemsDataSource dataSource = new ItemsDataSource(this);
        dataSource.open();
        mItem = dataSource.getFromDspaceId(dspace_id);
        dataSource.close();

        setTitle(title);

        mThumb = (SimpleDraweeView) findViewById(R.id.doc_thumb);
        TextView mTitle = (TextView) findViewById(R.id.title);
        TextView mAuthor = (TextView) findViewById(R.id.author);
        TextView mPublisher = (TextView) findViewById(R.id.publisher);
        TextView mLanguage = (TextView) findViewById(R.id.language);
        TextView mYear = (TextView) findViewById(R.id.date);
        TextView mType = (TextView) findViewById(R.id.type);
        TextView mDescription = (TextView) findViewById(R.id.item_description);
        ImageView mTypeIcon = (ImageView) findViewById(R.id.type_icon);

        LinearLayout share_ll = (LinearLayout) findViewById(R.id.share_ll);

        mDownloadTV = (TextView) findViewById(R.id.download);
        mDownloadIV = (ImageView) findViewById(R.id.image_download);
        LinearLayout download_ll = (LinearLayout) findViewById(R.id.download_ll);

        final TextView star_tv = (TextView) findViewById(R.id.star);
        final ImageView star_iv = (ImageView) findViewById(R.id.image_star);
        LinearLayout star_ll = (LinearLayout) findViewById(R.id.star_ll);

        mTitle.setText(mItem.getTitle());
        mAuthor.setText(mItem.getCreator());
        mPublisher.setText(mItem.getPublisher());
        mLanguage.setText(mItem.getLanguage());
        mYear.setText(mItem.getDateIssued());
        double size = (double) Math.round(
                (double) Integer.parseInt(mItem.getDocumentSize()) / 102.4) / 10.0;
        mType.setText(mItem.getType() + " (" + size + " KB)");
        mDescription.setText(mItem.getDescription());

        // Picasso.with(this).load(URLDecoder.decode(mItem.getDocumentThumbHref())).into(mThumb);

        // set document thumb imageview
        String imgUrl = mItem.getDocumentThumbHref();
        Uri uri = Uri.parse(imgUrl);

        ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(
                    String id,
                    @Nullable ImageInfo imageInfo,
                    @Nullable Animatable anim) {
                if (imageInfo == null) {
                    return;
                }
                mThumb.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
            }

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
                if (imageInfo == null) {
                    return;
                }
                mThumb.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                FLog.e(getClass(), throwable, "Error loading %s", id);
            }
        };
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .setUri(uri)
                .build();
        mThumb.setController(controller);


        if (mItem.getType().equals("application/pdf")) {
            mTypeIcon.setImageResource(R.drawable.ic_picture_as_pdf_black_48dp);
        } else {
            mTypeIcon.setImageResource(R.drawable.ic_insert_drive_file_black_48dp);
        }


        // Bottom toolbar
        Toolbar bottomToolbar = (Toolbar) findViewById(R.id.toolbar_bottom);
        bottomToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        mDataSource = new ItemsDataSource(this);
        mDataSource.open();

        mThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDataSource.isPresent(ItemsDBHelper.TABLE_DOWNLOADED, dspace_id))
                    openFile();
                else
                    downloadFile(OPEN);
            }
        });
        if (mDataSource.isPresent(ItemsDBHelper.TABLE_STARRED, dspace_id)) {
            star_tv.setText("Starred");
            star_iv.setImageResource(R.drawable.ic_star_black_48dp);
        } else {
            star_tv.setText("Star");
            star_iv.setImageResource(R.drawable.ic_star_border_black_48dp);
        }

        if (mDataSource.isPresent(ItemsDBHelper.TABLE_DOWNLOADED, dspace_id)) {
            mDownloadTV.setText("Open");
            mDownloadIV.setImageResource(R.drawable.ic_offline_pin_black_48dp);
        } else {
            mDownloadTV.setText("Download");
            mDownloadIV.setImageResource(R.drawable.ic_file_download_black_48dp);
        }

        star_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDataSource.isPresent(ItemsDBHelper.TABLE_STARRED, dspace_id)) {
                    mDataSource.removeStar(dspace_id);
                    star_iv.setImageResource(R.drawable.ic_star_border_black_48dp);
                    star_tv.setText("Star");
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("Un-Star")
                            .setLabel("Un-star Item")
                            .build());
                } else {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("Star")
                            .setLabel("Star Item")
                            .build());
                    mDataSource.addStar(dspace_id);
                    star_iv.setImageResource(R.drawable.ic_star_black_48dp);
                    star_tv.setText("Starred");
                }
            }
        });

        download_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mDataSource.isPresent(ItemsDBHelper.TABLE_DOWNLOADED, dspace_id)) {
                    openFile();

                } else {
                    downloadFile(null);
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("Download")
                            .setLabel("Download Item")
                            .build());
                }

            }
        });

        download_ll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteFile();
                return false;
            }
        });

        share_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Share")
                        .setLabel("Share Item")
                        .build());
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Practical Action");
                intent.putExtra(Intent.EXTRA_TEXT, mItem.getTitle() +
                        "\nFind out more at: " +
                        mItem.getDocumentHref());
                startActivity(Intent.createChooser(intent, "Share via"));
            }
        });

    } // oncreate

    @Override
    protected void onResume() {
        super.onResume();

        // Tracker
        String name = "SingleItem";
        Log.i(TAG, "Setting screen name: " + name);
        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    private void downloadFile(final String openAfterDownload) {
        new AlertDialog.Builder(this)
                .setTitle("Download File")
                .setMessage("Are you sure you want to download the file to SDCard? (" +
                        (Integer.parseInt(mItem.getDocumentSize()) / 1024) + "KB)")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DownloadFileFromURL().execute(mItem.getDocumentHref(), openAfterDownload);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteFile() {
        new AlertDialog.Builder(SingleItemActivity.this)
                .setTitle("Delete File")
                .setMessage("Are you sure you want to delete the downloaded file?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDataSource.open();
                        File file = new File(
                                Global.ExtFolderPath + mDataSource.getFileName(mItem.getDspaceId()));
                        Log.d("DELETEFILE", file.getPath());
                        if (file.exists()) {
                            if (file.delete()) {
                                mDataSource.removeDowloaded(mItem.getDspaceId());
                                Toast.makeText(SingleItemActivity.this, "File Deleted Sucessfully", Toast.LENGTH_LONG).show();
                                finish();
                                overridePendingTransition(Global.A_enter, Global.B_exit);
                            }
                            snackbar("File could not be deleted.");
                        } else {
                            snackbar("File could not be found.");
                        }
                        mDataSource.close();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // cancel - do nothing
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        mDataSource.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (mDataSource.isPresent(ItemsDBHelper.TABLE_DOWNLOADED, mItem.getDspaceId()))
            getMenuInflater().inflate(R.menu.menu_main, menu);
        else
            getMenuInflater().inflate(R.menu.menu_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button_highlight, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.delete:
                deleteFile();
                break;
            case R.id.web_preview:
                openWebPreview();
                break;
            case R.id.preview:
                downloadPreview();
                break;
            case android.R.id.home:
                finish();
                overridePendingTransition(Global.A_enter, Global.B_exit);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(Global.A_enter, Global.B_exit);
    }

    private void downloadPreview() {

    }

    private void openWebPreview() {
        if (ServiceHandler.isOnline(this)) {
            Intent intent = new Intent(this, WebPreview.class);
            intent.putExtra(KEY_EXTRA_LINK, mItem.getDocumentHref());
            startActivity(intent);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Not Connected")
                    .setMessage("Connect to the internet to get a preview of the document.")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }


    /**
     * Background Async Task to download file
     */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        String fileName;
        int lengthOfFile;
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create Directory if not exist
            File dir = new File(Global.ExtFolderPath);
            if (!dir.exists()) {
                if (dir.mkdir()) {
                    Log.i("FileDownloader", "SD Directory Created");
                } else
                    cancel(true);
            }

            // Set up dialog for download
            pDialog = new ProgressDialog(SingleItemActivity.this);
            pDialog.setTitle("Downloading File");
            pDialog.setMessage("Please wait...");
            pDialog.setMax(Integer.parseInt(mItem.getDocumentSize()) / 1024);
            pDialog.setIndeterminate(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(false);
            pDialog.setProgressNumberFormat("%1d/%2d KB");
            // set cancel download button_highlight
            pDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancel(true);

                        }
                    });
            pDialog.show();
            if (!ServiceHandler.isOnline(SingleItemActivity.this)) {
                pDialog.dismiss();
                noConnectivityDialog();
                cancel(true);
            }
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... params) {
            int count;
            try {

                URL url = new URL(params[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                // progress bar
                lengthOfFile = connection.getContentLength();
                pDialog.setMax(lengthOfFile / 1024);

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);


                fileName = params[0].substring(params[0].lastIndexOf("/") + 1);

                // Output stream
                OutputStream output = new FileOutputStream(Global.ExtFolderPath + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) total / 1024);

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

            return params[1];
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String result) {
            // dismiss the dialog after the file was downloaded
            pDialog.dismiss();

            // Add to downloaded list
            mDataSource.addDownloaded(mItem.getDspaceId(), fileName);

            if (result == null || !result.equals(OPEN))
                Snackbar.make(findViewById(R.id.relative_layout), "Download Completed!",
                        Snackbar.LENGTH_LONG)
                        .setAction("OPEN", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openFile();
                            }
                        })
                        .show();
            else
                openFile();
            mDownloadTV.setText("Open");
            mDownloadIV.setImageResource(R.drawable.ic_offline_pin_black_48dp);
        }


        @Override
        protected void onCancelled() {
            super.onCancelled();
            snackbar("Download Cancelled");
        }
    }

    private void openFile() {
        String filePath = Global.ExtFolderPath + mDataSource.getFileName(mItem.getDspaceId());
        if (new File(filePath).exists()) {
            if (mItem.getType().equals("application/pdf")) {
                Intent intent = new Intent(SingleItemActivity.this, PDFViewActivity.class);
                intent.putExtra(PDFViewActivity.KEY_FILENAME, mDataSource.getFileName(mItem.getDspaceId()));
                intent.putExtra(PDFViewActivity.KEY_TYPE, mItem.getType());
                startActivity(intent);
            } else
                snackbar("File is not PDF.");
        } else {
            new AlertDialog.Builder(SingleItemActivity.this).setTitle("The file does not exist!")
                    .setMessage("Please delete it and download it again.")
                    .setPositiveButton("OK", null).show();
        }
    }

    private void noConnectivityDialog() {

        new AlertDialog.Builder(this).setTitle("No Connection")
                .setMessage("Please connect to the internet and try again.")
                .setPositiveButton("OK", null).show();
    }

    private void snackbar(String message) {
        Snackbar.make(findViewById(R.id.relative_layout), message, Snackbar.LENGTH_LONG)
                .show();
    }
}

