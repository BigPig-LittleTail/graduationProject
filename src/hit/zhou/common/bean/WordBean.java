package hit.zhou.common.bean;

public class WordBean {
    private String word;
    private String pos;
    private int parent;
    private String dp;

    public WordBean(String word,String pos,int parent,String dp){
        this.word = word;
        this.pos = pos;
        this.parent = parent;
        this.dp = dp;

    }

    public int getParent() {
        return parent;
    }

    public String getWord() {
        return word;
    }

    public String getPos() {
        return pos;
    }

    public String getDp() {
        return dp;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }


    public void setParent(int parent) {
        this.parent = parent;
    }
}
