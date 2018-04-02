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
            "    for (var key in document.all) {\n" +
            "        try {\n" +
            "            promptElement(document.all[key]);\n" +
            "        } catch(ignored) {}\n" +
            "    }\n" +
            "    finished();\n" +
            "}\n" +
            "\n" +
            "function allTexts() {\n" +
            "    var range = document.createRange();\n" +
            "    var walk = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT, null, false);\n" +
            "    while (n = walk.nextNode()) {\n" +
            "        try {\n" +
            "            promptText(n, range);\n" +
            "        } catch(ignored) {}\n" +
            "    }\n" +
            "    finished();\n" +
            "}\n" +
            "\n" +
            "function htmlContent() {\n" +
            "    var content = document.getElementsByTagName(\"html\")[0].innerHtml;\n" +
            "    promptElement(content);\n" +
            "    finished();\n" +
            "}\n" +
            "\n" +
            "function clickElement(element) {\n" +
            "    var e = document.createEvent('MouseEvents');\n" +
            "    e.initMouseEvent('click', true, true, window, 1, 0, 0, 0, 0, false, false, false, false, 0, null);\n" +
            "    element.dispatchEvent(e);\n" +
            "}\n" +
            "\n" +
            "function id(id, click) {\n" +
            "    var element = document.getElementById(id);\n" +
            "    if (element != null) {\n" +
            "        if (click == 'true') {\n" +
            "            clickElement(element);\n" +
            "            finished();\n" +
            "            return;\n" +
            "        }\n" +
            "        promptElement(element);\n" +
            "        finished();\n" +
            "        return;\n" +
            "\n" +
            "    }\n" +
            "\n" +
            "    try {\n" +
            "        for (var key in document.all) {\n" +
            "            element = document.all[key];\n" +
            "            if (element.id == id) {\n" +
            "                if (click == 'true') {\n" +
            "                    clickElement(element);\n" +
            "                    finished();\n" +
            "                    return;\n" +
            "                }\n" +
            "                promptElement(element);\n" +
            "                finished();\n" +
            "                return;\n" +
            "            }\n" +
            "\n" +
            "        }\n" +
            "    } catch(ignored) {}\n" +
            "\n" +
            "}\n" +
            "\n" +
            "function xpath(xpath, click) {\n" +
            "    var elements = document.evaluate(xpath, document, null, XPathResult.ORDERED_NODE_ITERATOR_TYPE, null);\n" +
            "\n" +
            "    if (elements) {\n" +
            "        var element = elements.iterateNext();\n" +
            "        while (element) {\n" +
            "            if (click == 'true') {\n" +
            "                clickElement(element);\n" +
            "                break;\n" +
            "            } else {\n" +
            "                promptElement(element);\n" +
            "                element = elements.iterateNext();\n" +
            "            }\n" +
            "        }\n" +
            "        finished();\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "function cssSelector(cssSelector, click) {\n" +
            "    var elements = document.querySelectorAll(cssSelector);\n" +
            "    for (var key in elements) {\n" +
            "        if (elements != null) {\n" +
            "            try {\n" +
            "                if (click == 'true') {\n" +
            "                    clickElement(elements[key]);\n" +
            "                    break;\n" +
            "                } else {\n" +
            "                    promptElement(elements[key]);\n" +
            "                }\n" +
            "            } catch(ignored) {}\n" +
            "        }\n" +
            "    }\n" +
            "    finished();\n" +
            "}\n" +
            "\n" +
            "function name(name, click) {\n" +
            "    var walk = document.createTreeWalker(document.body, NodeFilter.SHOW_ELEMENT, null, false);\n" +
            "    while (n = walk.nextNode()) {\n" +
            "        try {\n" +
            "            var attributeName = n.getAttribute('name');\n" +
            "            if (attributeName != null && attributeName.trim().length > 0 && attributeName == name) {\n" +
            "                if (click == 'true') {\n" +
            "                    clickElement(n);\n" +
            "                    break;\n" +
            "                } else {\n" +
            "                    promptElement(n);\n" +
            "                }\n" +
            "            }\n" +
            "        } catch(ignored) {}\n" +
            "    }\n" +
            "    finished();\n" +
            "}\n" +
            "\n" +
            "function attribute(attr, value, click) {\n" +
            "    var walk = document.createTreeWalker(document.body, NodeFilter.SHOW_ELEMENT, null, false);\n" +
            "    while (n = walk.nextNode()) {\n" +
            "        try {\n" +
            "            var attributeName = n.getAttribute(attr);\n" +
            "            if (attributeName != null && attributeName.trim().length > 0 && attributeName == value) {\n" +
            "                if (click == 'true') {\n" +
            "                    clickElement(n);\n" +
            "                    break;\n" +
            "                } else {\n" +
            "                    promptElement(n);\n" +
            "                }\n" +
            "            }\n" +
            "        } catch(ignored) {}\n" +
            "    }\n" +
            "    finished();\n" +
            "}\n" +
            "\n" +
            "function className(nameOfClass, click) {\n" +
            "    var walk = document.createTreeWalker(document.body, NodeFilter.SHOW_ELEMENT, null, false);\n" +
            "    while (n = walk.nextNode()) {\n" +
            "        try {\n" +
            "            var className = n.className;\n" +
            "            if (className != null && className.trim().length > 0 && className == nameOfClass) {\n" +
            "                if (click == 'true') {\n" +
            "                    clickElement(n);\n" +
            "                    break;\n" +
            "                } else {\n" +
            "                    promptElement(n);\n" +
            "                }\n" +
            "            }\n" +
            "        } catch(ignored) {}\n" +
            "    }\n" +
            "    finished();\n" +
            "}\n" +
            "\n" +
            "function textContent(text, click) {\n" +
            "    var range = document.createRange();\n" +
            "    var walk = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT, null, false);\n" +
            "    while (n = walk.nextNode()) {\n" +
            "        try {\n" +
            "            var textContent = n.textContent;\n" +
            "            if (textContent.trim() == text.trim()) {\n" +
            "                if (click == 'true') {\n" +
            "                    clickElement(n);\n" +
            "                    break;\n" +
            "                } else {\n" +
            "                    promptText(n, range);\n" +
            "                }\n" +
            "            }\n" +
            "        } catch(ignored) {}\n" +
            "    }\n" +
            "    finished();\n" +
            "}\n" +
            "\n" +
            "function tagName(tagName, click) {\n" +
            "    var elements = document.getElementsByTagName(tagName);\n" +
            "    for (var key in elements) {\n" +
            "        if (elements != null) {\n" +
            "            try {\n" +
            "                if (click == 'true') {\n" +
            "                    clickElement(elements[key]);\n" +
            "                    break;\n" +
            "                } else {\n" +
            "                    promptElement(elements[key]);\n" +
            "                }\n" +
            "            } catch(ignored) {}\n" +
            "        }\n" +
            "    }\n" +
            "    finished();\n" +
            "}\n" +
            "\n" +
            "function enterTextById(id, text) {\n" +
            "    var element = document.getElementById(id);\n" +
            "    if (element != null) element.value = text;\n" +
            "\n" +
            "    finished();\n" +
            "}\n" +
            "\n" +
            "function enterTextByXpath(xpath, text) {\n" +
            "    var element = document.evaluate(xpath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;\n" +
            "    if (element != null) element.value = text;\n" +
            "\n" +
            "    finished();\n" +
            "}\n" +
            "\n" +
            "function enterTextByCssSelector(cssSelector, text) {\n" +
            "    var element = document.querySelector(cssSelector);\n" +
            "    if (element != null) element.value = text;\n" +
            "\n" +
            "    finished();\n" +
            "}\n" +
            "\n" +
            "function enterTextByName(name, text) {\n" +
            "    var walk = document.createTreeWalker(document.body, NodeFilter.SHOW_ELEMENT, null, false);\n" +
            "    while (n = walk.nextNode()) {\n" +
            "        var attributeName = n.getAttribute('name');\n" +
            "        if (attributeName != null && attributeName.trim().length > 0 && attributeName == name) n.value = text;\n" +
            "    }\n" +
            "    finished();\n" +
            "}\n" +
            "\n" +
            "function enterTextByClassName(name, text) {\n" +
            "    var walk = document.createTreeWalker(document.body, NodeFilter.SHOW_ELEMENT, null, false);\n" +
            "    while (n = walk.nextNode()) {\n" +
            "        var className = n.className;\n" +
            "        if (className != null && className.trim().length > 0 && className == name) n.value = text;\n" +
            "    }\n" +
            "    finished();\n" +
            "}\n" +
            "\n" +
            "function enterTextByTextContent(textContent, text) {\n" +
            "    var walk = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT, null, false);\n" +
            "    while (n = walk.nextNode()) {\n" +
            "        var textValue = n.textContent;\n" +
            "        if (textValue == textContent) n.parentNode.value = text;\n" +
            "    }\n" +
            "    finished();\n" +
            "}\n" +
            "\n" +
            "function enterTextByTagName(tagName, text) {\n" +
            "    var elements = document.getElementsByTagName(tagName);\n" +
            "    if (elements != null) {\n" +
            "        elements[0].value = text;\n" +
            "    }\n" +
            "    finished();\n" +
            "}\n" +
            "\n" +
            "function promptElement(element) {\n" +
            "    var split = \"\\#dkqjf765kdj09d\\#\";\n" +
            "    var id = element.id;\n" +
            "    var text = element.innerText;\n" +
            "    if (text.trim().length == 0) {\n" +
            "        text = element.value;\n" +
            "    }\n" +
            "    var name = element.getAttribute('name');\n" +
            "    var className = element.className;\n" +
            "    var tagName = element.tagName;\n" +
            "    var attributes = \"\";\n" +
            "    var htmlAttributes = element.attributes;\n" +
            "    var html = element.innerHTML;\n" +
            "    for (var i = 0,\n" +
            "    htmlAttribute; htmlAttribute = htmlAttributes[i]; i++) {\n" +
            "        attributes += htmlAttribute.name + \"::\" + htmlAttribute.value;\n" +
            "        if (i + 1 < htmlAttributes.length) {\n" +
            "            attributes += \"#$\";\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    var rect = element.getBoundingClientRect();\n" +
            "    prompt('inject_result:elem:' + id + split + text + split + name + split + className + split + tagName + split + rect.left + split + rect.top + split + rect.width + split + rect.height + split + attributes + split + html);\n" +
            "}\n" +
            "\n" +
            "function promptText(element, range) {\n" +
            "    var split = \"\\#dkqjf765kdj09d\\#\";\n" +
            "    var text = element.textContent;\n" +
            "    if (text.trim().length > 0) {\n" +
            "        range.selectNodeContents(element);\n" +
            "        var rect = range.getBoundingClientRect();\n" +
            "        if (rect.width > 0 && rect.height > 0 && rect.left >= 0 && rect.top >= 0) {\n" +
            "            var id = element.parentNode.id;\n" +
            "            var name = element.parentNode.getAttribute('name');\n" +
            "            var className = element.parentNode.className;\n" +
            "            var tagName = element.parentNode.tagName;\n" +
            "            prompt('inject_result:text:' + id + split + text + split + name + split + className + split + tagName + split + rect.left + split + rect.top + split + rect.width + split + rect.height);\n" +
            "        }\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "function debug(text) {\n" +
            "    prompt('inject_result:debu:' + text);\n" +
            "}\n" +
            "\n" +
            "function finished() {\n" +
            "    prompt('inject_result:fini:finish');\n" +
            "}";
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
