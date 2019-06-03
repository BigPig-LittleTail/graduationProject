package hit.zhou.graph.tools.kmeans;

import java.util.List;
import java.util.Map;

public class Kmeans {


    public static <T extends Enum> void kmeans(Map<String, Vector<T>> vectors, List<Cluster<T>> clusters){
        do {
            for(Map.Entry<String, Vector<T>> entry:vectors.entrySet()){
                float[] vectorData = new float[entry.getValue().dimensionSize()];
                for(int i = 0;i<vectorData.length;i++){
                    vectorData[i] = entry.getValue().getDataByIndex(i);
                }
                int index = enSureWhichClusterSet(vectorData,clusters);
                clusters.get(index).add(entry.getValue());
            }
        }while(!reCaculateCenters(clusters));

        for(Cluster<T> cluster:clusters){
            cluster.sortVectors();
        }

    }

    private static<T extends Enum> boolean reCaculateCenters(List<Cluster<T>> clusters){
        boolean isStop = true;
        for(Cluster<T> cluster:clusters){
            float distance = cluster.recaculateCenter();
            if(Math.abs(1-distance) > 1e-8){
                isStop = false;
            }
        }
        if(!isStop){
            for(Cluster<T> cluster:clusters){
                cluster.clear();
            }
        }
        return isStop;
    }

    private static <T extends Enum> int enSureWhichClusterSet(float[] vector, List<Cluster<T>> clusters) {
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
