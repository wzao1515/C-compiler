package bit.minisys.minicc.parser;

import bit.minisys.minicc.library.map;
import bit.minisys.minicc.library.node;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class CMyMiniCCParser implements IMiniCCParser {
    private static map m = new map();
    private List<String> st = new ArrayList<String>();
    private List<String> temparray = new ArrayList<String>();
    private List<String> identify = new ArrayList<String>();

    public void run(String iFile, String oFile) throws IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        System.out.println("Syntax analyzing...");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(iFile);
        Document document = db.newDocument();
        Element PT = document.createElement("ParserTree");
        Element temp2 = document.createElement("CMPL_UNIT");
        PT.appendChild(temp2);
        Stack<String> st = new Stack<>();
        Stack<Element> elest = new Stack<>(); //存储xml节点的栈
        Stack<Element> ele = new Stack<>();
        NodeList nl = doc.getElementsByTagName("token");
        m.initialize(); //初始化文法子集
        st.push("CMPL_UNIT");
        elest.push(temp2);
        for (int i = 0; i < nl.getLength(); i++) {
            //得到类型和值
            String type = doc.getElementsByTagName("type").item(i).getFirstChild().getNodeValue();
            //System.out.println(type);
            String value = doc.getElementsByTagName("value").item(i).getFirstChild().getNodeValue();
            if (type.equals("identifier")) {
                if (Objects.equals(doc.getElementsByTagName("type").item(i - 1).getFirstChild().getNodeValue(), "keyword")) {
                    for (String id : identify) {
                        if (id.equals(value)) {
                            System.out.println("重复定义变量" + value + "!");
                            return;
                        }
                    }
                    identify.add(value);
                } else {
                    int flag = 0;
                    for (String id : identify) {
                        if (id.equals(value)) {
                            flag = 1;
                        }
                    }
                    if (flag == 0) {
                        System.out.println("变量" + value + "未声明！");
                        return;
                    }
                }
            }
            if (value.equals("#")) {
                break;
            }
            if (value.equals(";")) {
                Element temp1 = document.createElement("punctuation");
                System.out.println(value);
                temp2 = (Element) temp2.getParentNode();
                temp1.setTextContent(value);
                temp2.appendChild(temp1);
                continue;
            }
            //字符串顶常数
            int count = 0;
            if (type.equals("const_i")) {
                while (true) {
                    String top = st.pop();
                    temp2 = elest.pop();
                    if (type.equals(top)) {
                        Element temp1 = document.createElement("primaryExpression");
                        System.out.println(value);
                        temp1.setTextContent(value);
                        temp2.appendChild(temp1);
                        break;
                    } else {
                        node T = m.find(top);
                        count = 0;
                        for (int j = 0; j < T.set.size(); j++) {
                            if (m.find(T.set.get(j).get(0)).findFirst(type)) {
                                for (int k = T.set.get(j).size() - 1; k >= 0; k--) {
                                    st.push(T.set.get(j).get(k));
                                    System.out.println(T.set.get(j).get(k));
                                    count++;
                                }
                                break;
                            }
                        }
                        for (int j = 0; j < count; j++) {
                            temparray.add(st.pop());
                        }
                        for (String tag : temparray) {
                            Element temp1;
                            temp1 = document.createElement("postExpression");
                            temp2.appendChild(temp1);
                            ele.push(temp1);
                        }
                        for (int j = temparray.size() - 1; j >= 0; j--) {
                            st.push(temparray.get(j));
                        }
                        temparray.clear();
                        while (!ele.empty()) {
                            elest.push(ele.pop());
                        }
                    }
                }//while true
            }//if
            else if (type.equals("identifier")) {
                while (true) {
                    String top = st.pop();
                    temp2 = elest.pop();
                    if (type.equals(top)) {
                        Element temp1 = document.createElement("directDeclaractor");
                        System.out.println(value);
                        temp1.setTextContent(value);
                        temp2.appendChild(temp1);
                        break;
                    } else {
                        node T = m.find(top);
                        count = 0;
                        for (int j = 0; j < T.set.size(); j++) {
                            if (T.set.get(j).get(0).equals(type)) {

                                for (int k = T.set.get(j).size() - 1; k >= 0; k--) {
                                    st.push(T.set.get(j).get(k));
                                    System.out.println(T.set.get(j).get(k));
                                    count++;
                                }
                                break;
                            } else if (m.find(T.set.get(j).get(0)).findFirst(type)) {
                                for (int k = T.set.get(j).size() - 1; k >= 0; k--) {
                                    st.push(T.set.get(j).get(k));
                                    System.out.println(T.set.get(j).get(k));
                                    count++;
                                }
                                break;
                            }
                        }
                        for (int j = 0; j < count; j++) {
                            temparray.add(st.pop());
                        }
                        for (String tag : temparray) {
                            Element temp1;
                            temp1 = document.createElement("postExpression");
                            temp2.appendChild(temp1);
                            ele.push(temp1);
                        }
                        for (int j = temparray.size() - 1; j >= 0; j--) {
                            st.push(temparray.get(j));
                        }
                        temparray.clear();
                        while (!ele.empty()) {
                            elest.push(ele.pop());
                        }
                    }
                }//while true
            } else {
                //栈顶出栈
                while (true) {
                    int flag = 0;
                    String top = st.pop();
                    if (top.equals("WORD")) {
                        if (type.equals("stringLiteral")) {
                            temp2 = elest.pop();
                            Element temp1 = document.createElement("primaryExpression");
                            System.out.println(value);
                            temp1.setTextContent(value);
                            temp2.appendChild(temp1);
                            break;
                        }
                    }
                    if (top.equals("(") || top.equals(")") || top.equals("{") || top.equals("}")) {
                        if (top.equals(value)) {
                            System.out.println(value);
                            break;
                        }
                    }
                    node T = m.find(top);
                    if (T == null) {
                        if (top.equals(value)) {
                            break;
                        }
                        System.out.println("不匹配！" + top + value);
                        return;
                    }
                    temp2 = elest.pop();
                    if (!T.findFirst(value)) {
                        System.out.println("Failed2!" + value + T.getName());
                        return;
                    }
                    count = 0;
                    for (int j = 0; j < T.set.size(); j++) {
                        if (T.set.get(j).get(0).equals(value)) {
                            for (int k = T.set.get(j).size() - 1; k >= 0; k--) {
                                st.push(T.set.get(j).get(k));
                                System.out.println(T.set.get(j).get(k));
                                count++;
                            }
                            break;
                        }
                        if (m.find(T.set.get(j).get(0)) == null) continue;
                        if (m.find(T.set.get(j).get(0)).findFirst(value)) {
                            for (int k = T.set.get(j).size() - 1; k >= 0; k--) {
                                st.push(T.set.get(j).get(k));
                                System.out.println(T.set.get(j).get(k));
                                count++;
                            }
                            break;
                        }
                    }
                    for (int j = 0; j < count; j++) {
                        temparray.add(st.pop());
                    }
                    for (int j = 0; j < temparray.size(); j++) {
                        String tag = temparray.get(j);
                        Element temp3;
                        try {
                            if (tag.equals("(") || tag.equals(")") || tag.equals("{") || tag.equals("}")) {
                                temp3 = document.createElement("punctuation");
                                temp3.setTextContent(tag);
                                temp2.appendChild(temp3);
                                continue;
                            }
                            if (m.find(tag) == null) {
                                if (temp2.getTagName() == "TYPE_SPEC") {
                                    temp3 = document.createElement("typeSpecifier");
                                    temp3.setTextContent(tag);
                                    temp2.appendChild(temp3);
                                } else if (temp2.getTagName() == "ID") {
                                    temp3 = document.createElement("directDeclarator");
                                    temp3.setTextContent(tag);
                                    temp2.appendChild(temp3);
                                } else {
                                    temp3 = document.createElement("primaryExpression");
                                    temp3.setTextContent(tag);
                                    temp2.appendChild(temp3);
                                }
                                continue;
                            } else {
                                temp3 = document.createElement(tag);
                                temp2.appendChild(temp3);
                                ele.push(temp3);
                            }
                        } catch (DOMException s) {
                            s.getMessage();
                        }

                    }
                    while (!ele.empty()) {
                        elest.push(ele.pop());
                    }
                    for (int j = temparray.size() - 1; j >= 0; j--) {
                        st.push(temparray.get(j));
                    }
                    temparray.clear();
                } //while true
            }//else

        }//for
        while (!st.empty()) {
            st.pop();
        }
        document.appendChild(PT);
        TransformerFactory tff = TransformerFactory.newInstance();
        Transformer tf = tff.newTransformer();
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        tf.transform(new DOMSource(document), new StreamResult(oFile));
        System.out.println("3. SyntaxAnalyse finished!");
    }

}
