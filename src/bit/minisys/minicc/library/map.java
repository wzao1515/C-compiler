package bit.minisys.minicc.library;

import java.util.ArrayList;

public class map {
    public node[] n=new node[1000];

    public void initialize() {
        for(int i=0;i<1000;i++) {
            n[i]=new node();
        }
        ArrayList<String> temp=new ArrayList<>();
        n[0].setName("CMPL_UNIT");
        temp.add("FUNC_LIST");
        n[0].setSet(temp);
        n[0].setFirst("int");n[0].setFirst("void");
        n[1].setName("FUNC_LIST");
        temp.clear();
        temp.add("FUNC_DEF");temp.add("FUNC_LIST");
        n[1].setSet(temp);
        temp.clear();
        temp.add("");
        n[1].setSet(temp);
        n[1].setFirst("int");n[1].setFirst("void");
        n[2].setName("FUNC_DEF");
        temp.clear();
        temp.add("TYPE_SPEC");temp.add("identifier");temp.add("(");temp.add(")");temp.add("CODE_BLOCK");
        n[2].setSet(temp);
        temp.clear();
        temp.add("");
        n[2].setSet(temp);
        n[2].setFirst("int");n[2].setFirst("void");
        n[3].setName("TYPE_SPEC");
        temp.clear();
        temp.add("int");
        n[3].setSet(temp);
        temp.clear();
        temp.add("void");
        n[3].setSet(temp);
        n[3].setFirst("int");n[3].setFirst("void");
        n[5].setName("CODE_BLOCK");
        temp.clear();
        temp.add("{");temp.add("STMT_LIST");temp.add("}");
        n[5].setSet(temp);
        n[5].setFirst("{");
        n[6].setName("STMT_LIST");
        temp.clear();
        temp.add("STMT");temp.add("STMT_LIST");
        n[6].setSet(temp);
        temp.clear();
        temp.add("");
        n[6].setSet(temp);
        n[6].setFirst("return");n[6].setFirst("i");n[6].setFirst("j");
        n[6].setFirst("printf");n[6].setFirst("if");n[6].setFirst("for");
        n[6].setFirst("}");//由于可能为空，因此把follow集合加进first
        n[6].setFirst("int");
        n[4].setName("PRINT_STMT");
        temp.clear();
        temp.add("printf");temp.add("(");temp.add("WORD");temp.add(")");
        n[4].setSet(temp);
        n[4].setFirst("printf");
        n[7].setName("STMT");
        temp.clear();
        temp.add("TYPE_SPEC");temp.add("identifier");
        n[7].setSet(temp);
        temp.clear();
        temp.add("RTN_STMT");
        n[7].setSet(temp);
        temp.clear();
        temp.add("ID_STMT");
        n[7].setSet(temp);
        temp.clear();
        temp.add("SELECTION_STMT");
        n[7].setSet(temp);
        temp.clear();
        temp.add("PRINT_STMT");
        n[7].setSet(temp);
        temp.clear();
        temp.add("FOR_STMT");
        n[7].setSet(temp);
        n[7].setFirst("return");;n[7].setFirst("printf");n[7].setFirst("if");
        n[7].setFirst("int");n[7].setFirst("for");n[7].setFirst("identifier");
        n[8].setName("RTN_STMT");
        temp.clear();
        temp.add("return");temp.add("EXPR");
        n[8].setSet(temp);
        n[8].setFirst("return");
        n[10].setName("identifier");
        n[10].setFirst("identifier");
        n[11].setName("EXPR");
        n[11].setFirst("const_i");
        n[11].setFirst("identifier");
        temp.clear();
        temp.add("const_i");
        n[11].setSet(temp);
        temp.clear();
        temp.add("identifier");
        n[11].setSet(temp);
        n[12].setName("SELECTION_STMT");
        temp.clear();
        temp.add("if");temp.add("(");temp.add("JUDGE");temp.add(")");temp.add("BLOCK");
        n[12].setSet(temp);
        n[12].setFirst("if");
        n[13].setName("WORD");
        n[14].setName("FOR_STMT");
        temp.clear();
        temp.add("for");temp.add("(");temp.add("STMT");temp.add("JUDGE");temp.add("STMT");temp.add(")");temp.add("BLOCK");
        n[14].setSet(temp);
        n[14].setFirst("for");
        n[15].setName("JUDGE");
        temp.clear();
        temp.add("identifier");temp.add("JU");temp.add("EXPR");
        n[15].setSet(temp);
        n[15].setFirst("identifier");
        n[16].setName("JU");
        temp.clear();
        temp.add(">");
        n[16].setSet(temp);
        temp.clear();
        temp.add("<");
        n[16].setSet(temp);
        temp.clear();
        temp.add("!=");
        n[16].setSet(temp);
        temp.clear();
        temp.add("==");
        n[16].setSet(temp);
        n[16].setFirst("<");n[16].setFirst(">");n[16].setFirst("!=");n[16].setFirst("==");
        n[17].setName("const_i");
        n[17].setFirst("const_i");
        n[20].setName("BLOCK");
        temp.clear();
        temp.add("STMT");
        n[20].setSet(temp);
        temp.clear();
        temp.add("CODE_BLOCK");
        n[20].setSet(temp);
        n[20].setFirst("{");n[20].setFirst("printf");n[20].setFirst("for");n[20].setFirst("if");
        n[20].setFirst("return");n[20].setFirst("int");n[20].setFirst("identifier");
        n[21].setName("ID_STMT");
        temp.clear();
        temp.add("identifier");temp.add("ASS_OR_INC");
        n[21].setSet(temp);
        n[21].setFirst("identifier");
        n[22].setName("ASS_OR_INC");
        temp.clear();
        temp.add("ASS_");
        n[22].setSet(temp);
        temp.clear();
        temp.add("INC_");
        n[22].setSet(temp);
        n[22].setFirst("++");n[22].setFirst("=");
        n[23].setName("ASS_");
        temp.clear();
        temp.add("=");temp.add("EXPR");
        n[23].setSet(temp);
        n[23].setFirst("=");
        n[24].setName("INC_");
        temp.clear();
        temp.add("++");
        n[24].setSet(temp);
        n[24].setFirst("++");
    }

    public node find(String s) {
        for(node m:n) {
            if (m.check(s) == true) {
                return m;
            }
        }
        return null;
    }
}
