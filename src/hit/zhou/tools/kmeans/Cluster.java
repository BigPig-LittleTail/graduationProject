package hit.zhou.tools.kmeans;

import hit.zhou.EntryType;

import java.util.ArrayList;
import java.util.List;

public class Cluster{
    private List<EntryType> types;
    private List<KeyWordVector> vectors;
    private float[] center;

    public Cluster(float[] center,List<EntryType> types){
        this.center = center;
        this.types = types;
        this.vectors = new ArrayList<>();
    }

    public void setCenter(float[] center) {
        this.center = center;
    }

    public void setVectors(List<KeyWordVector> vectors) {
        this.vectors = vectors;
    }

    public float[] getCenter() {
        return center;
    }

    public List<EntryType> getTypes() {
        return types;
    }

    public List<KeyWordVector> getVectors() {
        return vectors;
    }
}
