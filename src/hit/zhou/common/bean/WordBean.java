package hit.zhou.common.bean;

public class WordBean {
    private String word;
    private String pos;
    private String dp;
    private String ner;

    public WordBean(String word){
        this.word = word;
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

    public String getNer() {
        return ner;
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

    public void setNer(String ner) {
        this.ner = ner;
    }
}
