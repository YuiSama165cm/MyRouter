package com.yui.member;

import com.yui.arouter.IRouter;
//这个实际无用，只是编译时注解用的模板
public class ActivityUtil implements IRouter {
    @Override
    public void putActivity() {
        //将当前模块中，所有的Activity的类对象加入到路由表中
        //但是这样100个module就要写100次ActivityUtil，里面100个activity就要写100次；所以手动写不现实，
        // 因此还是要写注解来完成ActivityUtil
        com.yui.arouter.Arouter.getInstance().addActivity("member/member",com.yui.member.MemberActivity.class);//最好的方式就是包名+类名，因为生成的ActivityUtil，我们并不清楚它属于哪个包

    }
}
