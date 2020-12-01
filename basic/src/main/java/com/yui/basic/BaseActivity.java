package com.yui.basic;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    //    当前模块能拿到，其他模块还是不能直接拿到的，所以可以在BaseActivity中将这个值拿到，然后其他module从这个BaseActivity拿
    final public boolean is_application = BuildConfig.is_application;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }


    public boolean getIsApplication(){
        return is_application;
    }
}
