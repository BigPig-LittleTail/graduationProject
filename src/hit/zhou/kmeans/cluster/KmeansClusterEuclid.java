package hit.zhou.kmeans.cluster;

import hit.zhou.kmeans.helper.CalculateHelper;

import java.util.Comparator;

public class KmeansClusterEuclid extends KmeansClusterAbstract{
    public KmeansClusterEuclid(KeyWordVector initCenterMass) {
        super(initCenterMass);
    }
    @Override
    protected float calculateVector2VectorDistance(float[] vector1, float[] vector2) {
        return CalculateHelper.euclidDistance(vector1,vector2);
    }
    @Override
    public void sortVectors(){
        Comparator<KeyWordVector> comparator = new Comparator<KeyWordVector>() {
            @Override
            public int compare(KeyWordVector o1, KeyWordVector o2) {
                float[] vector1 = o1.getVector();
                float[] vector2 = o2.getVector();
                float distance1 = calculateVector2CenterMass(vector1);
                float distance2 = calculateVector2CenterMass(vector2);
                if(distance1 > distance2){
                    return -1;
                }
                else if(distance1 < distance2){
                    return 1;
                }
                else {
                    return 0;
                }
            }
        };
        super.sortVectors(comparator);
    }


}
