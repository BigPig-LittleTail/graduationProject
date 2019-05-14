package hit.zhou.tools.kmeans;

public class CalculateHelper {
    public static float calculateInner(float[] p, float[] q){
        float result = 0;
        for(int i =0;i < p.length;i++){
            result += p[i] * q[i];
        }
        return result;
    }

    public static float moudle(float[] p){
        float result = 0;
        for(int i =0;i < p.length;i++){
            result += p[i] * p[i];
        }
        return result;
    }

    public static float euclidDistance(float[] oldCenterMass, float[] newCenterMass){
        float distance = 0;
        for(int i = 0;i < oldCenterMass.length;i++){
            distance += (oldCenterMass[i] - newCenterMass[i]) * (oldCenterMass[i] - newCenterMass[i]);
        }
        return distance;
    }


}
