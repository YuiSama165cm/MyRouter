package com.yui.member;

import android.os.Bundle;

import com.yui.annotation.BindPath;
import com.yui.basic.BaseActivity;

@BindPath("menber/menber")
public class MemberActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
    }
}
