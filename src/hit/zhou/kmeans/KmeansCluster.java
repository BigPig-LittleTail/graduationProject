package hit.zhou.kmeans;

import java.util.HashSet;

public class KmeansCluster {
    private HashSet<KeyWordVector> cluster;
    private float[] centerMass;
    public KmeansCluster(KeyWordVector initCenterMass){
        cluster = new HashSet<>();
        this.centerMass = initCenterMass.getVector().clone();
        cluster.add(initCenterMass);
    }

    public void reCalculateCenterMass(){
        int pointNum = cluster.size();
        for(int i = 0;i < centerMass.length;i++){
            centerMass[i] = 0;
        }
        for(KeyWordVector keyWordVector:cluster){
            float[] vector = keyWordVector.getVector();
            for(int i = 0;i< vector.length;i++){
                centerMass[i] += vector[i];
            }
        }
        for(int i = 0;i < centerMass.length;i++){
            centerMass[i] = centerMass[i] / pointNum;
        }
    }

    public float[] getCenterMass() {
        return centerMass;
    }

    public boolean contains(KeyWordVector keyWordVector){
        return cluster.contains(keyWordVector);
    }

    public void add(KeyWordVector keyWordVector){
        cluster.add(keyWordVector);
    }

    public boolean remove(KeyWordVector keyWordVector){
        return cluster.remove(keyWordVector);
    }

    public HashSet<KeyWordVector> getCluster() {
        return cluster;
    }

    public void clear(){
        cluster.clear();
    }
}
