package hit.zhou.common.bean.test;

public class MyEntry {
    private String wordString;
    private String feature;

    public MyEntry(String wordString){
        this.wordString = wordString;
    }

    public MyEntry(String wordString,String feature){
        this.wordString = wordString;
        this.feature = feature;
    }

    public void setFeatureFromDpNode(DpNode dpNode){

    }

    public String getFeature() {
        return feature;
    }

    public String getWordString() {
        return wordString;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public void setWordString(String wordString) {
        this.wordString = wordString;
    }

    @Override
    public int hashCode(){
        return wordString.hashCode() + feature.hashCode();
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof MyEntry)){
            return false;
        }
        MyEntry myEntry = (MyEntry) o;
        return this.wordString.equals(myEntry.getWordString()) && this.feature.equals(myEntry.getFeature());
    }

}
