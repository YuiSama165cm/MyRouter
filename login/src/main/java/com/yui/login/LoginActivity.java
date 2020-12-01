package com.yui.login;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;

import com.yui.annotation.BindPath;
import com.yui.arouter.Arouter;
import com.yui.basic.BaseActivity;
import com.yui.common.LiveDataBus;

@BindPath("login/login")
public class LoginActivity extends BaseActivity {
    private TextView jump;
    LiveDataBus.BusMutableLiveData<String> shao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        shao = LiveDataBus.getInstance().with("shao", String.class);
        //这么注册，会有粘性事件（即观察者还没注册，被观察者已经发送过消息了，所以观察者一注册就能够收到消息）
        //如果要处理粘性事件，可以给观察者添加版本号，就可以不接收创建之前的事件
        //这儿重写BusMutableLiveData，处理粘性事件
        shao.observe(LoginActivity.this,true, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.e("shao--->3",s);
            }
        });
        Bundle bundle = this.getIntent().getExtras();
        String name = bundle.getString("name");
        Toast.makeText(this,name,Toast.LENGTH_LONG).show();
        jump = (TextView)findViewById(R.id.jump);
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shao.postValue("这是后面发送的通知");
                Arouter.getInstance().jumpActivity("login/login2",null);
            }
        });
    }
}
