package com.llx278.uimocker2;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * Created by llx on 05/01/2018.
 */

public class ReflectUtil {

    public static Field findFieldRecursiveImpl(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            while (true) {
                clazz = clazz.getSuperclass();
                if (clazz == null || clazz.equals(Object.class))
                    break;

                try {
                    return clazz.getDeclaredField(fieldName);
                } catch (NoSuchFieldException ignored) {}
            }
            throw e;
        }
    }

    public static boolean isAssignedFrom(String className,Object object) {

        Class<?> aClass = object.getClass();
        if (aClass.getName().equals(className)) {
            return true;
        } else {
            while (!aClass.getName().equals(Object.class.getName())) {
                aClass = aClass.getSuperclass();
                if (aClass.getName().equals(className)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获得自定义view中所有可能是显示在屏幕上的字符串列表
     * @param customView 自定义的view
     * @param regex 待匹配的正则表达式，如果是空串或null，则返回能找到的所有文本
     * @return 可能的字符串列表
     */
    public static ArrayList<String> getCustomViewText(View customView,String regex) {
        Pattern pattern = null;
        if (!TextUtils.isEmpty(regex)) {
            try{
                pattern = Pattern.compile(regex);
            }catch(PatternSyntaxException e){
                pattern = Pattern.compile(regex, Pattern.LITERAL);
            }
        }

        Class<?> currentClass = customView.getClass();
        ArrayList<String> fieldStrList = new ArrayList<>();
        while (!currentClass.getName().equals(View.class.getName())) {
            Field[] declaredFields = currentClass.getDeclaredFields();
            for (Field field : declaredFields) {
                try {
                    field.setAccessible(true);
                    Object o = field.get(customView);
                    if (o != null && o instanceof CharSequence) {
                        String str = o.toString();
                        if (!TextUtils.isEmpty(str)) {
                            if (pattern != null) {
                                Matcher matcher = pattern.matcher(str);
                                if (matcher.find()){
                                    if (!fieldStrList.contains(str)) {
                                        fieldStrList.add(str);
                                    }
                                }
                            } else {
                                if (!fieldStrList.contains(str)) {
                                    fieldStrList.add(str);
                                }
                            }
                        }
                    }
                } catch (IllegalAccessException ignored) {
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        return fieldStrList;
    }
}
