package com.llx278.uimocker2;

import android.text.TextUtils;
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

    /**
     * 获得自定义view中所有可能是显示在屏幕上的字符串列表
     * @param customView 自定义的view
     * @return 可能的字符串列表
     */
    public static ArrayList<String> getCustomViewText(View customView,String regex) {

        Pattern pattern = null;
        try{
            pattern = Pattern.compile(regex);
        }catch(PatternSyntaxException e){
            pattern = Pattern.compile(regex, Pattern.LITERAL);
        }

        Class<? extends View> customViewClass = customView.getClass();
        Class<?> currentClass = customViewClass;
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
                            Matcher matcher = pattern.matcher(str);
                            if (matcher.find()){
                                if (!fieldStrList.contains(str)) {
                                    fieldStrList.add(str);
                                }
                            }
                        }
                    }
                } catch (IllegalAccessException ignored) {
                }
            }
            currentClass = customViewClass.getSuperclass();
        }
        return fieldStrList;
    }
}
