package hit.zhou.basic.nlp;

public class Word {
    private String wordString;
    private String pos;
    private int parent;
    private String dp;

    public Word(String wordString, String pos, int parent, String dp){
        this.wordString = wordString;
        this.pos = pos;
        this.parent = parent;
        this.dp = dp;

    }

    public int getParent() {
        return parent;
    }

    public String getWordString() {
        return wordString;
    }

    public String getPos() {
        return pos;
    }

    public String getDp() {
        return dp;
    }

    public void setWordString(String wordString) {
        this.wordString = wordString;
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
