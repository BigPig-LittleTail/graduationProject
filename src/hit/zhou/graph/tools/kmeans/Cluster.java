package hit.zhou.graph.tools.kmeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Cluster<T extends Enum> implements Serializable {
    private float[] center;
    private List<Vector<T>> vectors;
    private List<T> types;

    public Cluster(float[] center, List<T> types,List<Vector<T>> vectors){
        this.center = new float[center.length];
        for(int i = 0;i<center.length;i++){
            this.center[i] = center[i];
        }
        this.types = types;
        this.vectors = vectors;
    }

    public float[] getCenter() {
        return center;
    }

    public List<T> getTypes() {
        return types;
    }

    public List<Vector<T>> getVectors() {
        return vectors;
    }

    public void setTypes(List<T> types) {
        this.types = types;
    }

    public void setCenter(float[] center) {
        this.center = center;
    }

    public void setVectors(List<Vector<T>> vectors) {
        this.vectors = vectors;
    }


    public Cluster(float[] center, List<T> types){
        this.center = new float[center.length];
        for(int i = 0;i<center.length;i++){
            this.center[i] = center[i];
        }
        this.types = types;
        this.vectors = new ArrayList<>();
    }

    public T getType(int index) {
        return types.get(index);
    }

    public int typeSize(){
        return types.size();
    }

    public int vectorsSize(){
        return vectors.size();
    }

    public Vector<T> get(int index){
        return vectors.get(index);
    }

    public void add(Vector<T> vector){
        vectors.add(vector);
    }

    public void clear(){
        vectors.clear();
    }

    public void sub(float percent){
        int last = (int)Math.ceil(percent * vectors.size());
        System.out.println(last);
        if(last <= vectors.size()){
            vectors = vectors.subList(0,last);
        }
    }


    public float recaculateCenter(){
        if(vectors.isEmpty()){
            return 1f;
        }

        float[] newCenter = new float[center.length];
        float[] oldCenter = this.center;
        for(Vector<T> vector:vectors){
            for(int i = 0;i < center.length;i++){
                newCenter[i] += vector.getDataByIndex(i);
            }
        }
        for(int i = 0;i < center.length;i++){
            newCenter[i] = newCenter[i] / vectors.size();
        }
        float distance = calculateVector2VectorDistance(oldCenter,newCenter);
        this.center = newCenter;
        return distance;
    }

    public float caculateVector2Center(float[] vector){
        return calculateVector2VectorDistance(vector,this.center);
    }

    public void sortVectors(){
        Comparator<Vector<T>> comparator = (o1, o2)->{
                float distance1 = o1.shadow(this.center);
                float distance2 = o2.shadow(this.center);
                if(distance1 > distance2){
                    return -1;
                }
                else if(distance1 < distance2){
                    return 1;
                }
                else {
                    return 0;
                }

        };
        vectors.sort(comparator);
    }


    private float calculateVector2VectorDistance(float[] vector1, float[] vector2) {
        return CalculateHelper.calculateInner(vector1,vector2)
                /(float)(Math.sqrt((double)CalculateHelper.moudle(vector1)) * Math.sqrt(CalculateHelper.moudle(vector2)));
    }

}
