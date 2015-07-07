package com.nepotech.practicalanswers;

import android.os.Build;
import android.os.Environment;

import java.io.File;

public class Global {
    public static final String baseUrl = "http://answers.practicalaction.org/";
    public static String url = "http://answers.practicalaction.org/dev2/webservice/paJSON.php";
    //public static String url = "http://192.168.0.110/json/paJSON.php";
    //public static final String baseUrl = "http://192.168.0.110/panswer/";

    public static final String CHARSET = "utf8"; // charset for URLDecoder

    public static final String ExtFolderName = "PractialAction";
    public static final String ExtFolderPath = Environment
            .getExternalStorageDirectory().toString() + File.separator +
            Global.ExtFolderName + File.separator;



    public static boolean isLolliop() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }
}
