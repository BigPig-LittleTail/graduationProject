package hit.zhou.tools.kmeans;

import java.util.List;
import java.util.Map;

public class KmeansTest {
    public static <T extends Enum> void kmeans(Map<String,VectorTest<T>> vectors, List<ClusterTest<T>> clusters){
        do {
            for(Map.Entry<String,VectorTest<T>> entry:vectors.entrySet()){
                float[] vectorData = new float[entry.getValue().dimensionSize()];
                for(int i = 0;i<vectorData.length;i++){
                    vectorData[i] = entry.getValue().getDataByIndex(i);
                }
                int index = enSureWhichClusterSet(vectorData,clusters);
                clusters.get(index).add(entry.getValue());
            }
        }while(!reCaculateCenters(clusters));

        for(ClusterTest<T> cluster:clusters){
            cluster.sortVectors();
        }

    }

    private static<T extends Enum> boolean reCaculateCenters(List<ClusterTest<T>> clusters){
        boolean isStop = true;
        for(ClusterTest<T> cluster:clusters){
            float distance = cluster.recaculateCenter();
            if(Math.abs(1-distance) > 1e-8){
                isStop = false;
            }
        }
        if(!isStop){
            for(ClusterTest<T> cluster:clusters){
                cluster.clear();
            }
        }
        return isStop;
    }

    private static <T extends Enum> int enSureWhichClusterSet(float[] vector, List<ClusterTest<T>> clusters) {
        int bestSimilarClusterIndex = -1;
        float maxSimilarDistance = -1;
        for(int j = 0;j < clusters.size();j++){
            float distance = clusters.get(j).caculateVector2Center(vector);
            if(distance > maxSimilarDistance){
                bestSimilarClusterIndex = j;
                maxSimilarDistance = distance;
            }
        }
        return bestSimilarClusterIndex;
    }



}
