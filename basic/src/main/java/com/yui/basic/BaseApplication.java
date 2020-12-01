package com.yui.basic;

import android.app.Application;
import android.util.Log;

import com.yui.arouter.Arouter;

public class BaseApplication extends Application {
////    当前模块能拿到，其他模块还是不能直接拿到的，所以可以在application中将这个值拿到，然后其他module从这个application拿
//    public static boolean is_application = BuildConfig.is_application;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("BaseApplication------>","im come in");
        Arouter.getInstance().init(this);//在这初始化Arouter
    }
}
