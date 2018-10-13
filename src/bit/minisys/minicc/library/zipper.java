package bit.minisys.minicc.library;

public class zipper {
    private String label; //label number
    private boolean define;
    private boolean use;

    public boolean isDefine() {
        return define;
    }

    public void setDefine(boolean define) {
        this.define = define;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }
}
