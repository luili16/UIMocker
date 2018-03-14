package com.llx278.uimocker2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 * Created by llx on 2018/3/13.
 */

class JavaScriptCreator {

    private static final String JAVA_SCRIPT = "function allWebElements() {\n" +
            "\tfor (var key in document.all){\n" +
            "\t\ttry{\n" +
            "\t\t\tpromptElement(document.all[key]);\t\t\t\n" +
            "\t\t}catch(ignored){}\n" +
            "\t}\n" +
            "\tfinished();\n" +
            "}\n" +
            "\n" +
            "function allTexts() {\n" +
            "\tvar range = document.createRange();\n" +
            "\tvar walk=document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT, null, false); \n" +
            "\twhile(n=walk.nextNode()){\n" +
            "\t\ttry{\n" +
            "\t\t\tpromptText(n, range);\n" +
            "\t\t}catch(ignored){}\n" +
            "\t} \n" +
            "\tfinished();\n" +
            "}\n" +
            "\n" +
            "function htmlContent() {\n" +
            "    var content = document.getElementsByTagName(\"html\")[0].innerHtml;\n" +
            "    promptElement(content);\n" +
            "    finished();\n" +
            "}\n" +
            "\n" +
            "function clickElement(element){\n" +
            "\tvar e = document.createEvent('MouseEvents');\n" +
            "\te.initMouseEvent('click', true, true, window, 1, 0, 0, 0, 0, false, false, false, false, 0, null);\n" +
            "\telement.dispatchEvent(e);\n" +
            "}\n" +
            "\n" +
            "function id(id, click) {\n" +
            "\tvar element = document.getElementById(id);\n" +
            "\tif(element != null){ \n" +
            "\n" +
            "\t\tif(click == 'true'){\n" +
            "\t\t\tclickElement(element);\n" +
            "\t\t}else{\n" +
            "\t\t\tpromptElement(element);\n" +
            "\t\t}\n" +
            "\t} \n" +
            "\telse {\n" +
            "\t\tfor (var key in document.all){\n" +
            "\t\t\ttry{\n" +
            "\t\t\t\telement = document.all[key];\n" +
            "\t\t\t\tif(element.id == id) {\n" +
            "\t\t\t\t\tif(click == 'true'){\n" +
            "\t\t\t\t\t\tclickElement(element);\n" +
            "\t\t\t\t\t}else{\n" +
            "\t\t\t\t\t\tpromptElement(element);\n" +
            "\t\t\t\t\t}\n" +
            "\t\t\t\t}\n" +
            "\t\t\t} catch(ignored){}\t\t\t\n" +
            "\t\t}\n" +
            "\t}\n" +
            "\tfinished(); \n" +
            "}\n" +
            "\n" +
            "function xpath(xpath, click) {\n" +
            "\tvar elements = document.evaluate(xpath, document, null, XPathResult.ORDERED_NODE_ITERATOR_TYPE, null); \n" +
            "\n" +
            "\tif (elements){\n" +
            "\t\tvar element = elements.iterateNext();\n" +
            "\t\twhile(element) {\n" +
            "\t\t\tif(click == 'true'){\n" +
            "\t\t\t\tclickElement(element);\n" +
            "\t\t\t}else{\n" +
            "\t\t\t\tpromptElement(element);\n" +
            "\t\t\t\telement = elements.iterateNext();\n" +
            "\t\t\t}\n" +
            "\t\t}\n" +
            "\t\tfinished();\n" +
            "\t}\n" +
            "}\n" +
            "\n" +
            "function cssSelector(cssSelector, click) {\n" +
            "\tvar elements = document.querySelectorAll(cssSelector);\n" +
            "\tfor (var key in elements) {\n" +
            "\t\tif(elements != null){ \n" +
            "\t\t\ttry{\n" +
            "\t\t\t\tif(click == 'true'){\n" +
            "\t\t\t\t\tclickElement(elements[key]);\n" +
            "\t\t\t\t}\n" +
            "\t\t\t\telse{\n" +
            "\t\t\t\t\tpromptElement(elements[key]);\n" +
            "\t\t\t\t}\t\n" +
            "\t\t\t}catch(ignored){}  \n" +
            "\t\t}\n" +
            "\t}\n" +
            "\tfinished(); \n" +
            "}\n" +
            "\n" +
            "function name(name, click) {\n" +
            "\tvar walk=document.createTreeWalker(document.body, NodeFilter.SHOW_ELEMENT, null, false); \n" +
            "\twhile(n=walk.nextNode()){\n" +
            "\t\ttry{\n" +
            "\t\t\tvar attributeName = n.getAttribute('name');\n" +
            "\t\t\tif(attributeName != null && attributeName.trim().length>0 && attributeName == name){\n" +
            "\t\t\t\tif(click == 'true'){\n" +
            "\t\t\t\t\tclickElement(n);\n" +
            "\t\t\t\t}\n" +
            "\t\t\t\telse{\n" +
            "\t\t\t\t\tpromptElement(n);\n" +
            "\t\t\t\t}\t\n" +
            "\t\t\t}\n" +
            "\t\t}catch(ignored){} \n" +
            "\t} \n" +
            "\tfinished();\n" +
            "}\n" +
            "\n" +
            "function className(nameOfClass, click) {\n" +
            "\tvar walk=document.createTreeWalker(document.body, NodeFilter.SHOW_ELEMENT, null, false); \n" +
            "\twhile(n=walk.nextNode()){\n" +
            "\t\ttry{\n" +
            "\t\t\tvar className = n.className; \n" +
            "\t\t\tif(className != null && className.trim().length>0 && className == nameOfClass) {\n" +
            "\t\t\t\tif(click == 'true'){\n" +
            "\t\t\t\t\tclickElement(n);\n" +
            "\t\t\t\t}\n" +
            "\t\t\t\telse{\n" +
            "\t\t\t\t\tpromptElement(n);\n" +
            "\t\t\t\t}\t\n" +
            "\t\t\t}\n" +
            "\t\t}catch(ignored){} \n" +
            "\t} \n" +
            "\tfinished(); \n" +
            "}\n" +
            "\n" +
            "function textContent(text, click) {\n" +
            "\tvar range = document.createRange();\n" +
            "\tvar walk=document.createTreeWalker(document.body,NodeFilter.SHOW_TEXT,null,false); \n" +
            "\twhile(n=walk.nextNode()){ \n" +
            "\t\ttry{\n" +
            "\t\t\tvar textContent = n.textContent; \n" +
            "\t\t\tif(textContent.trim() == text.trim()){  \n" +
            "\t\t\t\tif(click == 'true'){\n" +
            "\t\t\t\t\tclickElement(n);\n" +
            "\t\t\t\t}\n" +
            "\t\t\t\telse{\n" +
            "\t\t\t\t\tpromptText(n, range);\n" +
            "\t\t\t\t}\n" +
            "\t\t\t}\n" +
            "\t\t}catch(ignored){} \n" +
            "\t} \n" +
            "\tfinished();  \n" +
            "}\n" +
            "\n" +
            "function tagName(tagName, click) {\n" +
            "\tvar elements = document.getElementsByTagName(tagName);\n" +
            "\tfor (var key in elements) {\n" +
            "\t\tif(elements != null){ \n" +
            "\t\t\ttry{\n" +
            "\t\t\t\tif(click == 'true'){\n" +
            "\t\t\t\t\tclickElement(elements[key]);\n" +
            "\t\t\t\t}\n" +
            "\t\t\t\telse{\n" +
            "\t\t\t\t\tpromptElement(elements[key]);\n" +
            "\t\t\t\t}\t\n" +
            "\t\t\t}catch(ignored){}  \n" +
            "\t\t}\n" +
            "\t}\n" +
            "\tfinished();\n" +
            "}\n" +
            "\n" +
            "function enterTextById(id, text) {\n" +
            "\tvar element = document.getElementById(id);\n" +
            "\tif(element != null)\n" +
            "\t\telement.value = text;\n" +
            "\n" +
            "\tfinished(); \n" +
            "}\n" +
            "\n" +
            "function enterTextByXpath(xpath, text) {\n" +
            "\tvar element = document.evaluate(xpath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null ).singleNodeValue;\n" +
            "\tif(element != null)\n" +
            "\t\telement.value = text;\n" +
            "\n" +
            "\tfinished(); \n" +
            "}\n" +
            "\n" +
            "function enterTextByCssSelector(cssSelector, text) {\n" +
            "\tvar element = document.querySelector(cssSelector);\n" +
            "\tif(element != null)\n" +
            "\t\telement.value = text;\n" +
            "\n" +
            "\tfinished(); \n" +
            "}\n" +
            "\n" +
            "function enterTextByName(name, text) {\n" +
            "\tvar walk=document.createTreeWalker(document.body, NodeFilter.SHOW_ELEMENT, null, false); \n" +
            "\twhile(n=walk.nextNode()){\n" +
            "\t\tvar attributeName = n.getAttribute('name');\n" +
            "\t\tif(attributeName != null && attributeName.trim().length>0 && attributeName == name) \n" +
            "\t\t\tn.value=text;  \n" +
            "\t} \n" +
            "\tfinished();\n" +
            "}\n" +
            "\n" +
            "function enterTextByClassName(name, text) {\n" +
            "\tvar walk=document.createTreeWalker(document.body, NodeFilter.SHOW_ELEMENT, null, false); \n" +
            "\twhile(n=walk.nextNode()){\n" +
            "\t\tvar className = n.className; \n" +
            "\t\tif(className != null && className.trim().length>0 && className == name) \n" +
            "\t\t\tn.value=text;\n" +
            "\t}\n" +
            "\tfinished();\n" +
            "}\n" +
            "\n" +
            "function enterTextByTextContent(textContent, text) {\n" +
            "\tvar walk=document.createTreeWalker(document.body,NodeFilter.SHOW_TEXT, null, false); \n" +
            "\twhile(n=walk.nextNode()){ \n" +
            "\t\tvar textValue = n.textContent; \n" +
            "\t\tif(textValue == textContent) \n" +
            "\t\t\tn.parentNode.value = text; \n" +
            "\t}\n" +
            "\tfinished();\n" +
            "}\n" +
            "\n" +
            "function enterTextByTagName(tagName, text) {\n" +
            "\tvar elements = document.getElementsByTagName(tagName);\n" +
            "\tif(elements != null){\n" +
            "\t\telements[0].value = text;\n" +
            "\t}\n" +
            "\tfinished();\n" +
            "}\n" +
            "\n" +
            "function promptElement(element) {\n" +
            "    var split = \"\\#dkqjf765kdj09d\\#\";\n" +
            "\tvar id = element.id;\n" +
            "\tvar text = element.innerText;\n" +
            "\tif(text.trim().length == 0){\n" +
            "\t\ttext = element.value;\n" +
            "\t}\n" +
            "\tvar name = element.getAttribute('name');\n" +
            "\tvar className = element.className;\n" +
            "\tvar tagName = element.tagName;\n" +
            "\tvar attributes = \"\";\n" +
            "\tvar htmlAttributes = element.attributes;\n" +
            "\tvar html = element.innerHTML;\n" +
            "\tfor (var i = 0, htmlAttribute; htmlAttribute = htmlAttributes[i]; i++){\n" +
            "\t\tattributes += htmlAttribute.name + \"::\" + htmlAttribute.value;\n" +
            "\t\tif (i + 1 < htmlAttributes.length) {\n" +
            "\t\t\tattributes += \"#$\";\n" +
            "\t\t}\n" +
            "\t}\n" +
            "\n" +
            "\tvar rect = element.getBoundingClientRect();\n" +
            "\tif(rect.width > 0 && rect.height > 0 && rect.left >= 0 && rect.top >= 0){\n" +
            "\t\tprompt('inject_result:elem:' + id + split + text + split + name + split + className + split + tagName + split + rect.left + split + rect.top + split + rect.width + split + rect.height + split + attributes + split + html);\n" +
            "\t}\n" +
            "}\n" +
            "\n" +
            "function promptText(element, range) {\n" +
            "    var split = \"\\#dkqjf765kdj09d\\#\";\n" +
            "\tvar text = element.textContent;\n" +
            "\tif(text.trim().length>0) {\n" +
            "\t\trange.selectNodeContents(element);\n" +
            "\t\tvar rect = range.getBoundingClientRect();\n" +
            "\t\tif(rect.width > 0 && rect.height > 0 && rect.left >= 0 && rect.top >= 0){\n" +
            "\t\t\tvar id = element.parentNode.id;\n" +
            "\t\t\tvar name = element.parentNode.getAttribute('name');\n" +
            "\t\t\tvar className = element.parentNode.className;\n" +
            "\t\t\tvar tagName = element.parentNode.tagName;\n" +
            "\t\t\tprompt('inject_result:text:'+id + split + text + split + name + split + className + split + tagName + split + rect.left + split + rect.top + split + rect.width + split + rect.height);\n" +
            "\t\t}\n" +
            "\t}\n" +
            "}\n" +
            "\n" +
            "function finished(){\n" +
            "\tprompt('inject_result:fini:finish');\n" +
            "}\n";
    String createJavaScript(String function,String frame) {
        String javaScript = setWebFrame(JAVA_SCRIPT,frame);
        return "javascript:" + javaScript + function;
    }

    private String setWebFrame(String javascript,String frame){

        if(frame.isEmpty() || frame.equals("document")){
            return javascript;
        }
        javascript = javascript.replaceAll(Pattern.quote("document, "),
                "document.getElementById(\""+frame+"\").contentDocument, ");
        javascript = javascript.replaceAll(Pattern.quote("document.body, "),
                "document.getElementById(\""+frame+"\").contentDocument, ");
        return javascript;
    }

    private String getJavaScriptAsString() {
        InputStream fis = null;
        try {
            fis = new FileInputStream("/data/local/tmp/RobotiumWeb.js");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        StringBuilder javaScript = new StringBuilder();

        try {
            BufferedReader input =  new BufferedReader(new InputStreamReader(fis));
            String line = null;
            while (( line = input.readLine()) != null){
                javaScript.append(line);
                javaScript.append("\n");
            }
            input.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return javaScript.toString();
    }

}
