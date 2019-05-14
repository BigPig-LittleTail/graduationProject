package hit.zhou.basic.rdf;

import hit.zhou.EntryType;

public class MyEntry {
    private EntryType type;
    private String wordString;
    private String feature;

    public MyEntry(String wordString){
        this.wordString = wordString;
    }

    public MyEntry(String wordString,String feature){
        this.wordString = wordString;
        this.feature = feature;
    }

    public MyEntry(String wordString,String feature,EntryType type){
        this.wordString = wordString;
        this.feature = feature;
        this.type = type;
    }

    public EntryType getType() {
        return type;
    }

    public String getFeature() {
        return feature;
    }

    public String getWordString() {
        return wordString;
    }

    public void setType(EntryType type) {
        this.type = type;
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
