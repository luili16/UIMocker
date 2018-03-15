package com.llx278.uimocker2;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by llx on 04/01/2018.
 */

public class Parser {

    private Scroller mScroller;
    private InstrumentationDecorator mInstrumentation;

    public Parser(Scroller scroller, InstrumentationDecorator instrumentation) {
        mScroller = scroller;
        mInstrumentation = instrumentation;
    }

    /**
     * 将当前的View转化为xml
     * @param target 待转化的view
     * @param path 保存路径
     * @throws IOException
     */
    public void dump(View target,String path) throws IOException {
        try {

            if(!createFileIfNeed(path)){
                Logger.e("Parser.dump(View,path) : path :" + path + " 文件不能创建!",null);
                return;
            }

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlSerializer xmlSerializer = factory.newSerializer();
            FileWriter stringWriter = new FileWriter(path);
            xmlSerializer.setOutput(stringWriter);
            xmlSerializer.startDocument("utf-8", true);
            dumpRecursive(target,xmlSerializer);
            xmlSerializer.endDocument();
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean createFileIfNeed(String path) throws IOException {
        File f = new File(path);
        if (!f.exists()){
            if(!f.createNewFile()){
                return false;
            }
        }
        return true;
    }

    /**
     * 将当前的listView转换为xml
     * @param target
     * @param path
     */
    public void dumpListView(ListView target,String path){

    }


    private void dumpRecursive(View parent, XmlSerializer xmlSerializer) throws IOException {
        if (!(parent instanceof ViewGroup)) {
            String className = parent.getClass().getName().
                    replace(".","_").replace("$","-");
            xmlSerializer.startTag(null, className);
            addAttribute(parent,xmlSerializer);
            if (parent instanceof TextView) {
                xmlSerializer.text(((TextView) parent).getText().toString());
            }
            xmlSerializer.endTag(null, className);
        } else {
            String className = parent.getClass().getName().
                    replace(".","_").replace("$","-");
            xmlSerializer.startTag(null,className);
            addAttribute(parent,xmlSerializer);
            int count = ((ViewGroup) parent).getChildCount();
            for (int i = 0; i < count; i++) {
                View child = ((ViewGroup) parent).getChildAt(i);
                dumpRecursive(child,xmlSerializer);
            }
            xmlSerializer.endTag(null,className);
        }
    }

    private void addAttribute(View parent,XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.attribute(null,"id",String.valueOf(parent.getId()));
        xmlSerializer.attribute(null,"clickable",String.valueOf(parent.isClickable()));
        xmlSerializer.attribute(null,"longClickable",String.valueOf(parent.isLongClickable()));
        xmlSerializer.attribute(null,"focusable",String.valueOf(parent.isFocusable()));
    }
}
