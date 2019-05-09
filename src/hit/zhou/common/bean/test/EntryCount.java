package hit.zhou.common.bean.test;

public class EntryCount {
    private String wordString;
    private float inD;
    private float outD;

    public EntryCount(String wordString){
        this.wordString = wordString;
        this.inD = 0;
        this.outD = 0;
    }

    public EntryCount(String wordString,float inD,float outD){
        this.wordString = wordString;
        this.inD = inD;
        this.outD = outD;
    }

    public String getWordString() {
        return wordString;
    }

    public float getInD() {
        return inD;
    }

    public float getOutD() {
        return outD;
    }

    public void setWordString(String wordString) {
        this.wordString = wordString;
    }

    public void setInD(float inD) {
        this.inD = inD;
    }

    public void setOutD(float outD) {
        this.outD = outD;
    }
}
