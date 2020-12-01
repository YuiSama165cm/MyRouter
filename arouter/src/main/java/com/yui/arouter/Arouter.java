package com.yui.arouter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import dalvik.system.DexFile;

/**
 * 中间人
 * */
public class Arouter {
    private static Arouter arouter = new Arouter();
    //上下文
    private Context context;

    //装载了所有的Activity的类对象   路由表
    private HashMap<String,Class<? extends Activity>> map;//Class<? extends Activity>表示必须是activity的子类

    private Arouter(){
        map = new HashMap<>();
    }

    public static Arouter getInstance(){
        return arouter;
    }

    //初始化：程序一开始application的时候就要去加载
    public void init(Context context){
        this.context = context;
        //执行所有生成文件的里面的putActivity的方法

        //找到这些类
        List<String> classNames = getClassName("com.yui.util");
        for (String className : classNames) {
            try {
                //得到util的类对象
                Class<?> utilClass = Class.forName(className);
                //进行第二次验证，判断是否为IRouter的子类
                if(IRouter.class.isAssignableFrom(utilClass)){
                    IRouter iRouter = (IRouter)utilClass.newInstance();
                    iRouter.putActivity();
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }

    /**
     * 将类对象添加进路由表的方法
     * @param key
     * @param clazz
     * */
    public void addActivity(String key,Class<? extends Activity> clazz){
        if(key != null //key不为空
                && clazz !=null //类不为空
                && !map.containsKey(key)){//map中不存在这个key
            map.put(key,clazz);

        }
    }

    /**
     * 跳转窗体的方法
     * @param key
     * @param bundle
     * */
    public void jumpActivity(String key, Bundle bundle){
        Class<? extends Activity> activityClass = map.get(key);
        if (activityClass != null){
            Intent intent = new Intent(context,activityClass);
            if(bundle != null){
                intent.putExtras(bundle);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            context.startActivity(intent);
        }
    }

    /**
     * 通过包名获取这个包下面的所有的类名
     * @param packageName
     * @return
     * */
    private List<String> getClassName(String packageName){
        //创建一个class对象的集合
        List<String> classList = new ArrayList<>();
        try{
            //把当前应用的apk存储路径给dexFile
            DexFile df = new DexFile(context.getPackageCodePath());
            Enumeration<String> entries = df.entries();
            while (entries.hasMoreElements()){
                String className = (String)entries.nextElement();
                if(className.contains(packageName)){
                    classList.add(className);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return classList;

    }


}
