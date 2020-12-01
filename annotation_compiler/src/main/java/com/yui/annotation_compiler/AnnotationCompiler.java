package com.yui.annotation_compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.yui.annotation.BindPath;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * 注解处理器（有多少模块依赖了这个注解处理器，它就会执行多少次）
 *      去代码中找到专门用来标记Activity的注解   得到它所标记的类
 *      生成activityUtil类
 * */
@AutoService(Processor.class) //标记注解处理器
//@SupportedAnnotationTypes({"com.yui.annotation.BindPath"})//也可以不写getSupportedAnnotationTypes方法，直接在这使用注解完成声明操作
////@SupportedAnnotationTypes({"com.yui.annotation.BindPath,com.yui.annotation.XXXXX"})//这里是一个数组
public class AnnotationCompiler extends AbstractProcessor {//AbstractProcessor只在java包中
    //生成java文件的工具
    Filer filer;

    //初始化
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();//这里的processingEnvironment与processingEnv是同一个内容
    }

    /**
     * 声明支持的java版本
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }

    /**
     * 声明你这个注解处理器要找的注解是谁
     *
     * @return
     */
    //也可以直接@SupportedAnnotationTypes({"com.yui.annotation.BindPath"})声明
    @Override
    public Set<String> getSupportedAnnotationTypes() {//set集合，可以处理多个注解
        Set<String> types = new HashSet<>();
        types.add(BindPath.class.getCanonicalName());//把需要处理的注解添加进去，这儿传入包名和类名
//        types.add(Override.class.getCanonicalName());//这样就得到了用了Override的包名和类名，不过这儿不需要
        return types;
    }


    /**
     * 这就是我们的核心方法 去找程序中标记了的内容 都在这个方法中
     *
     * @param set
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //怎么在这个方法中，触发要找的api？
        //得到模块中标记了bindPath的注解的内容
        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.
                getElementsAnnotatedWith(BindPath.class);//因为可能多个类都被标记了，所以是个集合
        //为什么通过这段api，得到的是element？
        //因为在整个注解处理器的概念里面，它把我们的类封装成了节点文件


        //不管注解放在哪个位置，都摆脱不了下面四个类型
        //TypeElement 类节点
        //ExecutableElement 方法节点
        //VariableElement 成员变量节点
        //PackageElement 包节点

        Map<String, String> map = new HashMap<>();
        for (Element element : elementsAnnotatedWith) {
            TypeElement typeElement = (TypeElement) element;
            //typeElement.getAnnotation(BindPath.class)拿到注解，并且获取它注解名的value
            String key = typeElement.getAnnotation(BindPath.class).value();
            //获取到包名和类名
            String activityName = typeElement.getQualifiedName().toString();
            map.put(key, activityName + ".class");
        }
        //生成文件
        if (map.size() > 0) {
            //生成我们的文件
            createClass(map);

        }
        return false;
    }

    //因为有多少模块依赖了这个注解处理器，注解处理器就会执行多少次，所以createClass下面获取的类，肯定是在一个module中的
    private void createClass(Map<String, String> map) {
        try {
            //创建一个方法
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("putActivity")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class);
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String activityName = map.get(key);
                //生成方法体
                methodBuilder.addStatement("com.yui.arouter.Arouter.getInstance().addActivity(\"" + key + "\"," + activityName + ")");
            }
            MethodSpec methodSpec = methodBuilder.build();

            //获取到接口的类
            ClassName iRouter = ClassName.get("com.yui.arouter", "IRouter");
            //创建工具类：因为会执行多次，所以加上时间作为区别
            TypeSpec typeSpec = TypeSpec.classBuilder("ActivityUtil" + System.currentTimeMillis())
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(iRouter)
                    .addMethod(methodSpec)
                    .build();

            //构建目录对象
            JavaFile javaFile = JavaFile.builder("com.yui.util", typeSpec).build();

            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
