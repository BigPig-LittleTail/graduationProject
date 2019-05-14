package hit.zhou.tools.kmeans;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Kmeans{
    public static void kmeans(Map<String,KeyWordVector> vectors,List<Cluster> clusters){
        do {
            for(Map.Entry<String,KeyWordVector> entry:vectors.entrySet()){
                float[] vectorData = entry.getValue().getVector();
                int index = enSureWhichClusterSet(vectorData,clusters);
                clusters.get(index).getVectors().add(entry.getValue());
            }
        }while(!reCaculateCenters(vectors,clusters));

        for(int i = 0;i<clusters.size();i++){
            final Cluster cluster = clusters.get(i);
            Comparator<KeyWordVector> comparator = new Comparator<KeyWordVector>() {
                @Override
                public int compare(KeyWordVector o1, KeyWordVector o2) {
                    float[] vector1 = o1.getVector();
                    float[] vector2 = o2.getVector();

                    float distance1 = CalculateHelper.calculateInner(vector1,cluster.getCenter()) / (float) Math.sqrt(CalculateHelper.moudle(cluster.getCenter()));
                    float distance2 = CalculateHelper.calculateInner(vector2,cluster.getCenter()) / (float) Math.sqrt(CalculateHelper.moudle(cluster.getCenter()));
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
            cluster.getVectors().sort(comparator);
        }
    }

    private static boolean reCaculateCenters(Map<String,KeyWordVector> vectors,List<Cluster> clusters){
        boolean isShutDown = true;
        for(int i = 0;i<clusters.size();i++){
            Cluster cluster = clusters.get(i);
            float[] newCenter = new float[cluster.getCenter().length];
            float[] oldCenter = cluster.getCenter();
            for(KeyWordVector key:cluster.getVectors()){
                float[] vector = vectors.get(key.getWord()).getVector();
                for(int j = 0;j<vector.length;j++){
                    newCenter[j] = newCenter[j] + vector[j];
                }
            }
            for(int j = 0;j<newCenter.length;j++){
                newCenter[j] = newCenter[j] / cluster.getVectors().size();
            }
            float distance = calculateVector2VectorDistance(oldCenter,newCenter);
            if(Math.abs(1-distance) > 1e-8){
                isShutDown = false;
            }
            cluster.setCenter(newCenter);
        }
        if(!isShutDown){
            for(int i = 0;i<clusters.size();i++){
                clusters.get(i).getVectors().clear();
            }
        }
        return isShutDown;
    }


    private static int enSureWhichClusterSet(float[] vector, List<Cluster> clusters) {
        int bestSimilarClusterIndex = -1;
        float maxSimilarDistance = -1;
        for(int j = 0;j < clusters.size();j++){
            float distance = calculateVector2VectorDistance(vector,clusters.get(j).getCenter());
            if(distance > maxSimilarDistance){
                bestSimilarClusterIndex = j;
                maxSimilarDistance = distance;
            }
        }
        return bestSimilarClusterIndex;
    }


    private static float calculateVector2VectorDistance(float[] vector1, float[] vector2) {
        return CalculateHelper.calculateInner(vector1,vector2)
                /(float)(Math.sqrt((double)CalculateHelper.moudle(vector1)) * Math.sqrt(CalculateHelper.moudle(vector2)));
    }


}
