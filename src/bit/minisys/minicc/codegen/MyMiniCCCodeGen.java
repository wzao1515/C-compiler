package bit.minisys.minicc.codegen;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MyMiniCCCodeGen implements IMiniCCCodeGen {
    //file content
    private StringBuffer sb = new StringBuffer();

    public static boolean delFile(String fileName) {
        Boolean bool = false;
        File file = new File(fileName);
        try {
            if (file.exists()) {
                file.delete();
                bool = true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return bool;
    }

    public void run(String iFile, String oFile) throws IOException, ParserConfigurationException, SAXException, DocumentException {
        System.out.println("Code Generating...");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        SAXReader reader = new SAXReader();
        Document doc = reader.read(iFile);
        Element root = doc.getRootElement();
        //System.out.println(iFile);
        File file = new File(oFile);
        if (file.exists())
            delFile(oFile);
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file, false); //如果追加方式用true

        sb.append(".386\n");
        sb.append(".model flat,stdcall\n" +
                "option casemap:none\n");
        sb.append(".data\n");
        sb.append(".code\nstart:\n");

        root = root.element("functions");
        List<Element> func_list = root.elements();


        //each function is into consideration
        for (Element func : func_list) {
            List<Element> quaternions = func.elements();
            String judge = "";
            boolean addFlag = true;
            for (Element quaternion : quaternions) {

                String op = quaternion.attributeValue("op");
                String arg1 = quaternion.attributeValue("arg1");
                String arg2 = quaternion.attributeValue("arg2");
                //define
                if (op.matches("\\w\\w\\w\\w*")) { //regular expression int/char/...
                    //insert after .data
                    if (arg2.equals(""))
                        arg2 = "?";
                    sb.insert(sb.indexOf(".data") + 6, arg1 + "\tdword\t" + arg2 + "\t\n");
                    continue;
                }
                //has label
                String label = quaternion.attributeValue("label");
                if (label != null)
                    if (label.matches("L\\d")) {
                        sb.append(label + ":\n");
                    }
                if (op.equals("=")) {
                    if (!addFlag && !arg2.matches("T\\d")) {
                        sb.append("\tmov\teax," + arg2 + "\t\n");
                        sb.append("\tmov\t" + arg1 + ",eax\t\n");
                    } else if (!addFlag && arg2.matches("T\\d")) {
                        //inc,dec
                        continue;
                    } else {
                        sb.append("\tmov\t" + arg1 + ",eax\t\n");
                        addFlag = false;
                    }
                }
                if (op.equals("<") || op.equals(">") || op.equals(">=") || op.equals("<=") || op.equals("==")) {
                    sb.append("\tmov\t" + "eax," + arg1 + "\t\n");
                    sb.append("\tmov\t" + "ebx," + arg2 + "\t\n");
                    sb.append("\tcmp\t" + "eax" + "," + "ebx" + "\t\n");
                    judge = op;
                }
                if (op.equals("jf")) {
                    String result = quaternion.attributeValue("result");
                    op = judge;
                    //j false : < == jb    if < then jna result
                    if (op.equals("<"))
                        sb.append("\tjnb\t" + result + "\t\n");
                    if (op.equals(">"))
                        sb.append("\tjna\t" + result + "\t\n");
                    if (op.equals(">="))
                        sb.append("\tjb\t" + result + "\t\n");
                    if (op.equals("<="))
                        sb.append("\tja\t" + result + "\t\n");
                    if (op.equals("=="))
                        sb.append("\tjne\t" + result + "\t\n");
                }
                if (op.equals("+") || op.equals("-")) {
                    if (arg2.equals("1")) {
                        if (op.equals("+"))
                            sb.append("\tinc\t" + arg1 + "\t\n");
                        if (op.equals("-"))
                            sb.append("\tdec\t" + arg1 + "\t\n");

                    } else {
                        String result = quaternion.attributeValue("result");
                        if (result.matches("T\\d")) {
                            addFlag = true;
                        }
                        if (op.equals("+")) {
                            sb.append("\tmov\teax," + arg1 + "\t\n");
                            sb.append("\tadd\teax," + arg2 + "\t\n");
                        }
                        if (op.equals("-")) {
                            sb.append("\tmov\teax," + arg1 + "\t\n");
                            sb.append("\tsub\teax," + arg2 + "\t\n");
                        }
                    }
                }
                if (op.equals("j")) {
                    sb.append("\tjmp\t" + arg1 + "\t\n");
                }
            }
        }
        sb.append("\tret\nend start");
        out.write(sb.toString().getBytes("utf-8"));//注意需要转换对应的字符集
        out.close();
        System.out.println("6. Code Generating finished!");
    }
}
