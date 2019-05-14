package hit.zhou.tools.kmeans;

import hit.zhou.EntryType;

import java.util.List;

public class KeyWordVector {
    private List<EntryType> types;
    private String word;
    private float[] vector;

    public KeyWordVector(String word,float[] vector){
        this.word = word;
        this.vector = vector;
    }

    public void setVector(float[] vector) {
        this.vector = vector;
    }

    public float[] getVector() {
        return vector;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setTypes(List<EntryType> types) {
        this.types = types;
    }

    public List<EntryType> getTypes() {
        return types;
    }
}
