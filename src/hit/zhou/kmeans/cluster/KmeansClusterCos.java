package hit.zhou.kmeans.cluster;

import hit.zhou.kmeans.helper.CalculateHelper;

import java.util.Comparator;

public class KmeansClusterCos extends KmeansClusterAbstract{

    public KmeansClusterCos(KeyWordVector initCenterMass) {
        super(initCenterMass);
    }

    @Override
    protected float calculateVector2VectorDistance(float[] vector1, float[] vector2) {
         return CalculateHelper.calculateInner(vector1,vector2)
                /(float)(Math.sqrt((double)CalculateHelper.moudle(vector1)) * Math.sqrt(CalculateHelper.moudle(vector2)));
    }

    @Override
    public void sortVectors(){
        Comparator<KeyWordVector> comparator = new Comparator<KeyWordVector>() {
            @Override
            public int compare(KeyWordVector o1, KeyWordVector o2) {
                float[] vector1 = o1.getVector();
                float[] vector2 = o2.getVector();
                float distance1 = CalculateHelper.calculateInner(vector1,getCenterMass()) / (float) Math.sqrt(CalculateHelper.moudle(getCenterMass()));
                float distance2 = CalculateHelper.calculateInner(vector2,getCenterMass()) / (float) Math.sqrt(CalculateHelper.moudle(getCenterMass()));
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
