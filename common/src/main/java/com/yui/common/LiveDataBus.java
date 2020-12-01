package com.yui.common;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理所有的LiveData的
 * */
public class LiveDataBus {
    private static LiveDataBus liveDataBus = new LiveDataBus();

    //存储所有的liveData的容器
    private Map<String, BusMutableLiveData<Object>> map;

    private LiveDataBus(){
        map = new HashMap<>();
    }

    public static LiveDataBus getInstance(){
        return liveDataBus;
    }

    //存和取一体的方法
    public synchronized<T> BusMutableLiveData<T> with(String key,Class<T> type){
        if(!map.containsKey(key)){
            map.put(key,new BusMutableLiveData<Object>());
        }
        return (BusMutableLiveData<T>) map.get(key);
    }
    //处理粘性事件（即观察者还没注册，被观察者已经发送过消息了，所以观察者一注册就能够收到消息）
    //如果要处理粘性事件，可以给观察者添加版本号，就可以不接收创建之前的事件
    //下面的类，是处理粘性事件的
    public class BusMutableLiveData<T> extends MutableLiveData<T>{
        //是否拦截粘性事件 false:不拦截    true：拦截
        private boolean isViscosity = false;

        public void observe(@NonNull LifecycleOwner owner, boolean isViscosity, @NonNull Observer<T> observer){
            this.isViscosity = isViscosity;
            observe(owner, observer);
        }

        //重写observe
        @Override
        public void observe(@NonNull LifecycleOwner owner,@NonNull Observer<? super T> observer){
            super.observe(owner, observer);
            try{
                if(isViscosity){//不需要粘性
                    //通过反射  获取到mVersion 获取到mLastVersion   将mVersion的值给mLastVersion
                    hook(observer);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private void hook(Observer<? super T> observer) throws Exception{
            //获取LiveData的类对象
            Class<LiveData> liveDataClass = LiveData.class;
            //根据类对象获取到mVersion的反射对象
            Field mVersionField = liveDataClass.getDeclaredField("mVersion");
            //打开权限
            mVersionField.setAccessible(true);
            //获取到mObservers的反射对象
            Field mObserversField = liveDataClass.getDeclaredField("mObservers");
            //打开权限
            mObserversField.setAccessible(true);
            //从当前的liveData对象中获取mObservers这个成员变量在当前对象中的值
            Object mObservers = mObserversField.get(this);
            //获取到mObservers这个map的get方法
            Method get = mObservers.getClass().getDeclaredMethod("get",Object.class);
            //打开权限
            get.setAccessible(true);
            //执行get方法
            Object invokeEntry = get.invoke(mObservers,observer);
            //定义一个空对象 LifecycleBoundObserver
            Object observerWrapper = null;
            if(invokeEntry != null&&invokeEntry instanceof Map.Entry){
                observerWrapper = ((Map.Entry)invokeEntry).getValue();
            }
            if(observerWrapper == null){
                throw new NullPointerException("ObserverWrapper不能为空");
            }

//            Class clazz = observerWrapper.getClass();
            //得到observerWrapper的类对象
            Class<?> aClass = observerWrapper.getClass().getSuperclass();
            //获取mLastVersion的发射对象
            Field mLastVersionField = aClass.getDeclaredField("mLastVersion");
            //打开权限
            mLastVersionField.setAccessible(true);
            //获取到mVersion的值
            Object o = mVersionField.get(this);
            //把它的值赋值给mLastVersion
            mLastVersionField.set(observerWrapper,o);
        }
    }


}
