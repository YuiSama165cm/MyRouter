package com.yui.myrouter;


import android.app.IntentService;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.yui.annotation.BindPath;
import com.yui.arouter.Arouter;
import com.yui.basic.BaseActivity;
import com.yui.common.LiveDataBus;

import java.net.Socket;

@BindPath("main/main")//这名称可以自己定
public class MainActivity extends BaseActivity {
    private TextView jump;

    MutableLiveData<String> liveData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ViewPager
        getIsApplication();//这边直接获取basic中的BuildConfig中的属性值
//        Boolean aa = BuildConfig.is_application;

//        IntentService
        //能够主动回调onchanged方法的  只有两个地方，一个是postValue以及setValue；另一个就是onstateChanged（观察生命周期的改变）
        //使用livedata的子类MutableLiveData；里面有一个泛型，MutableLiveData里面有个post方法和get方法
        liveData = LiveDataBus.getInstance().with("shao",String.class);
        liveData.postValue("222222222");
        //接口  1对1
        //观察者（本质上也是接口）  1对N
        //注册观察者
//        //liveData不管什么时候注册观察者，都能被回调，因为
//        liveData.observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(String s) {
//                Log.e("shao--->",s);
//            }
//        });//注册我们的观察者
//
//
//        //livedata将观察者放在了
//        //注册观察者2
//        liveData.observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(String s) {
//                Log.e("shao2--->",s);
//            }
//        });//注册我们的观察者2


        jump = (TextView)findViewById(R.id.jump);
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //postValue可以在任意线程下被调用，但是setValue只能在主线程下被调用
                //postValue内部是用setValue实现的，但是它在最终都会切换到主线程；它的操作就是在调用setValue之前切换到了主线程
//                liveData.postValue("12312312321");
//                liveData.postValue("222222222");
//                liveData.postValue("333333333");//这时候只会回33333，因为后面的会把前面的覆盖掉

                //通过路由跳转
                Bundle bundle = new Bundle();
                bundle.putString("name" , "Lily");
                Arouter.getInstance().jumpActivity("login/login",bundle);

//                Arouter.getInstance().jumpActivity("login/login",null);
            }
        });
    }
}
