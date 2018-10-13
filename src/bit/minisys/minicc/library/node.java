package bit.minisys.minicc.library;

import java.util.ArrayList;
import java.util.List;

public class node {
    public List<String> first = new ArrayList<>();
    public ArrayList<ArrayList<String>> set = new ArrayList<ArrayList<String>>();
    private String name = "";

    public boolean check(String s) {
        if (s.equals(name))
            return true;
        return false;
    }

    public void setSet(ArrayList<String> s) {
        ArrayList<String> t = new ArrayList<>();
        for (String m : s) {
            t.add(m);
        }
        set.add(t);
    }

    public void setFirst(String s) {
        first.add(s);
    }

    public boolean findFirst(String s) {
        if (first == null) return false;
        for (String t : first) {
            if (t.equals(s)) return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String s) {
        name = s;
    }

}
