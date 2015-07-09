package com.nepotech.practicalanswers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;

public class PDFViewActivity extends AppCompatActivity {

    public static final String KEY_FILENAME = "fileName";
    public static final String KEY_TYPE = "type";

    private String mType;
    private String fileName;
    private PDFView pdfView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        // Transition
        overridePendingTransition(Global.B_enter, Global.A_exit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("View Document");

        pdfView = (PDFView) findViewById(R.id.pdfview);
        textView = (TextView) findViewById(R.id.page_number);
        fileName = getIntent().getStringExtra(KEY_FILENAME);
        mType = getIntent().getStringExtra(KEY_TYPE);
        setupPDFView(1);

    }

    private void setupPDFView(int defaultPage) {
        File pdf = new File(Global.ExtFolderPath + fileName);
        pdfView.fromFile(pdf)
                .defaultPage(defaultPage)
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int i, int i1) {
                        textView.setText("Page " + i +
                                " of " + i1);
                    }
                })
                .showMinimap(true)
                .enableSwipe(true)
                .onLoad(null)
                .load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pdfview, menu);
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
            case android.R.id.home:
                finish();
                overridePendingTransition(Global.A_enter, Global.B_exit);
                return true;
            case R.id.go_to_page:
                goToPage();
                return true;
            case R.id.open_in:
                openFile(fileName);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void goToPage() {
        final LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_go_to_page, null);
        TextView tv = (TextView) view.findViewById(R.id.page_of_xxx);
        tv.setText("of " + pdfView.getPageCount());
        new AlertDialog.Builder(this)
                .setTitle("Go to...")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText et = (EditText) view.findViewById(R.id.page_number);
                        String input = et.getText().toString();
                        if (!input.isEmpty()) {
                            setupPDFView(Integer.parseInt(input));
                        } else return;
                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void openFile(String fileName) {
        File file = new File(Global.ExtFolderPath + fileName);
        if (file.exists()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), mType);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        } else {
            new AlertDialog.Builder(this).setTitle("The file does not exist!")
                    .setMessage("Please delete it and download it again.")
                    .setPositiveButton("OK", null).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(Global.A_enter, Global.B_exit);
    }
}