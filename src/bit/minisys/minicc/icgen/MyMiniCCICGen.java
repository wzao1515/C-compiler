package bit.minisys.minicc.icgen;


import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class MyMiniCCICGen implements IMiniCCICGen {

    private List<Element> nodeQueue = new ArrayList<>();

    public void writerDocumentToNewFile(Document document, String oFile) throws Exception {
        //输出格式
        OutputFormat format = OutputFormat.createPrettyPrint();
        //设置编码
        format.setEncoding("UTF-8");
        //XMLWriter 指定输出文件以及格式
        XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(oFile), "UTF-8"), format);

        //写入新文件
        writer.write(document);
        writer.flush();
        writer.close();
    }

    //push every statement into queue, using recursion
    public void pushQueue(Element x) {
        if (x.elements().size() == 0)
            return;
        else {
            Element y = x.element("STMT");
            nodeQueue.add(y);
            x = x.element("STMT_LIST");
            pushQueue(x);
        }
    }

    public void run(String iFile, String oFile) throws Exception {
        System.out.println("Intermediate Code Generating...");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        //using dom4j to read parsing tree
        SAXReader reader = new SAXReader();
        Document doc = reader.read(iFile);
        Element root = doc.getRootElement();
        //using dom4j to create new tree
        Element ICC = DocumentHelper.createElement("IC");
        Document document = DocumentHelper.createDocument(ICC);
        ICC.addAttribute("name", iFile);
        //add function, assuming that there is only main function
        Element temp1 = ICC.addElement("functions");
        Element temp2 = temp1.addElement("function");
        root = root.element("CMPL_UNIT").element("FUNC_LIST").element("FUNC_DEF").element("CODE_BLOCK").element("STMT_LIST");
        pushQueue(root);
        Integer count = 0;
        Integer tempCount = 0;
        Integer labelCount = 0;

        while (!nodeQueue.isEmpty()) {
            count++;
            Element temp3 = temp2.addElement("quaternion\t");
            Element top = nodeQueue.remove(0);
            temp3.addAttribute("addr\t", count.toString());
            List<Element> sub_node = top.elements();  //judge the type of the statement
            System.out.println(sub_node.get(0).getName() + " " + sub_node.size());
            if (sub_node.size() > 2) {  //define identifier
                for (Element se : sub_node) {
                    if (se.getName().equals("TYPE_SPEC")) {
                        Element s = se.element("typeSpecifier");
                        temp3.addAttribute("op\t", s.getText());
                    } else if (se.getName().equals("identifier")) {
                        Element s = se.element("directDeclaractor");
                        temp3.addAttribute("arg1\t", s.getText());
                    } else continue;
                }
                temp3.addAttribute("arg2\t", "");
                temp3.addAttribute("result\t", "");
            } else if (sub_node.size() == 1) {  //statements like for, if ...
                if (sub_node.get(0).getName().equals("FOR_STMT")) {
                    Element def = sub_node.get(0).element("STMT"); // e1
                    temp3.addAttribute("op\t", "=");
                    def = def.element("postExpression");
                    Element id = (Element) def.elements().get(0);
                    id = id.element("directDeclaractor");
                    temp3.addAttribute("arg1\t", id.getText());
                    Element ass = (Element) def.elements().get(1);
                    ass = ass.element("ASS_").element("EXPR").element("postExpression").element("primaryExpression");
                    temp3.addAttribute("arg2\t", ass.getText());
                    temp3.addAttribute("result\t", "");

                    temp3 = temp2.addElement("quaternion");  //e2
                    count++;
                    temp3.addAttribute("addr\t", count.toString());
                    Element judge = sub_node.get(0).element("JUDGE");
                    Element op = (Element) judge.elements().get(1);
                    temp3.addAttribute("op\t", op.element("primaryExpression").getText());
                    Element a1 = (Element) judge.elements().get(0);
                    temp3.addAttribute("arg1\t", a1.element("directDeclaractor").getText());
                    Element a2 = (Element) judge.elements().get(2);
                    a2 = a2.element("postExpression").element("primaryExpression");
                    temp3.addAttribute("arg2\t", a2.getText());
                    tempCount++;
                    labelCount++;
                    temp3.addAttribute("result\t", "T" + tempCount.toString());
                    temp3.addAttribute("label\t", "L" + labelCount.toString());

                    temp3 = temp2.addElement("quaternion\t");  //jf
                    count++;
                    temp3.addAttribute("addr\t", count.toString());
                    temp3.addAttribute("op\t", "jf");
                    temp3.addAttribute("arg1\t", "T" + tempCount.toString());
                    temp3.addAttribute("arg2\t", "");
                    labelCount++;
                    temp3.addAttribute("result\t", "L" + labelCount.toString());


                    Element rear = DocumentHelper.createElement("ret");
                    Element r = rear.addElement("L" + labelCount.toString());
                    nodeQueue.add(rear);

                    Element jump = DocumentHelper.createElement("jump");
                    Element j = jump.addElement("j");
                    labelCount--;
                    Element label = jump.addElement("L" + labelCount.toString());
                    labelCount++;
                    nodeQueue.add(0, jump);
                    List<Element> sub_sub_node = sub_node.get(0).elements();
                    Element stmt = sub_sub_node.get(4);
                    nodeQueue.add(0, stmt);
                    Element bc = sub_node.get(0).element("BLOCK");
                    bc = bc.element("STMT");
                    nodeQueue.add(0, bc);
                }
                if(sub_node.get(0).getName().matches("L\\d")) {
                    temp3.addAttribute("op","");
                    temp3.addAttribute("arg1","");
                    temp3.addAttribute("arg2","");
                    temp3.addAttribute("result","");
                    temp3.addAttribute("label",sub_node.get(0).getName());
                }
                if (sub_node.get(0).getName().equals("SELECTION_STMT")) {
                    temp3.addAttribute("addr\t", count.toString());
                    Element judge = sub_node.get(0).element("JUDGE");
                    Element op = (Element) judge.elements().get(1);
                    temp3.addAttribute("op\t", op.element("primaryExpression").getText());
                    Element a1 = (Element) judge.elements().get(0);
                    temp3.addAttribute("arg1\t", a1.element("directDeclaractor").getText());
                    Element a2 = (Element) judge.elements().get(2);
                    a2 = a2.element("postExpression").element("primaryExpression");
                    temp3.addAttribute("arg2\t", a2.getText());
                    tempCount++;
                    temp3.addAttribute("result\t", "T" + tempCount.toString());


                    temp3 = temp2.addElement("quaternion\t");  //jf
                    count++;
                    temp3.addAttribute("addr\t", count.toString());
                    temp3.addAttribute("op\t", "jf");
                    temp3.addAttribute("arg1\t", "T" + tempCount.toString());
                    temp3.addAttribute("arg2\t", "");
                    labelCount++;
                    temp3.addAttribute("result\t", "L" + labelCount.toString());

                    Element rear = DocumentHelper.createElement("ret");  //j
                    Element r = rear.addElement("L" + labelCount.toString());
                    nodeQueue.add(0,rear);

                    Element bc = sub_node.get(0).element("BLOCK"); //block code
                    bc = bc.element("postExpression");
                    nodeQueue.add(0, bc);
                }
                if (sub_node.get(0).getName().equals("postExpression")) {
                    List<Element> sub_sub_node = sub_node.get(0).elements();
                    if (sub_sub_node.size() == 2) {
                        Element i = sub_sub_node.get(0).element("directDeclaractor");
                        Element op = sub_sub_node.get(1).element("INC_").element("primaryExpression");
                        String operator = "";
                        if (op.getText().equals("++"))
                            operator = "+";
                        else if (op.getText().equals("--"))
                            operator = "-";
                        temp3.addAttribute("op\t", operator);
                        temp3.addAttribute("arg1\t", i.getText());
                        temp3.addAttribute("arg2\t", "1");
                        tempCount++;
                        temp3.addAttribute("result\t", "T" + tempCount.toString());
                        count++;
                        temp3 = temp2.addElement("quaternion\t");
                        temp3.addAttribute("addr\t", count.toString());
                        temp3.addAttribute("op\t", "=");
                        temp3.addAttribute("arg1\t", i.getText());
                        temp3.addAttribute("arg2\t", "T" + tempCount.toString());
                        temp3.addAttribute("result\t", "");
                    }

                }
            } else if (sub_node.size() == 2) {
                temp3.addAttribute("op\t", "j");
                String match = "";
                for (Element e : sub_node) {
                    if (e.getName().matches("L\\d")) { //regular expression
                        match = e.getName();
                        break;
                    }
                }
                temp3.addAttribute("arg1", match);
                temp3.addAttribute("arg2", "");
                temp3.addAttribute("result", "");
            }
        }
        writerDocumentToNewFile(document, oFile);
        System.out.println("5. Intermediate code Generating finished!");
    }
}
