package com.nepotech.practicalanswers;

import android.os.Environment;

import java.io.File;

public class Global {
    public static final String baseUrl = "http://172.16.31.240/panswer/";
    public static String url = "http://172.16.31.240/json/paJSON.php";
    //public static String url = "http://192.168.0.110/json/paJSON.php";
    //public static final String baseUrl = "http://192.168.0.110/panswer/";

    public static final String ExtFolderName = "PractialAction";
    public static final String ExtFolderPath = Environment
                        .getExternalStorageDirectory().toString() + File.separator +
                        Global.ExtFolderName + File.separator;
}
