package hit.zhou.tools.kmeans;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClusterTest<T extends Enum> {
    private float[] center;
    private List<VectorTest<T>> vectors;
    private List<T> types;

    public ClusterTest(float[] center,List<T> types){
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

    public int getTypesSize(){
        return types.size();
    }

    public int getVectorsSize(){
        return vectors.size();
    }

    public VectorTest<T> get(int index){
        return vectors.get(index);
    }

    public void add(VectorTest<T> vector){
        vectors.add(vector);
    }

    public void clear(){
        vectors.clear();
    }


    public float recaculateCenter(){
        float[] newCenter = new float[center.length];
        float[] oldCenter = this.center;
        for(VectorTest<T> vector:vectors){
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
        Comparator<VectorTest<T>> comparator = (o1,o2)->{
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
