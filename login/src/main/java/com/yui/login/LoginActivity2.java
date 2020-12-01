package com.yui.login;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yui.annotation.BindPath;
import com.yui.arouter.Arouter;
import com.yui.basic.BaseActivity;

@BindPath("login/login2")
public class LoginActivity2 extends BaseActivity {
    private TextView jump;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        jump = (TextView)findViewById(R.id.jump);
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Arouter.getInstance().jumpActivity("main/main",null);
            }
        });
    }
}
