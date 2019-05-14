package hit.zhou.basic.rdf;

public class MyRelation {
    private String verb;
    private String feature;
    public MyRelation(String verb){
        this.verb = verb;
    }

    public MyRelation(String verb, String feature){
        this.verb = verb;
        this.feature = feature;
    }

    public String getFeature() {
        return feature;
    }

    public String getVerb() {
        return verb;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }
}
