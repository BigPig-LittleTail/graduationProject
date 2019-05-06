package hit.zhou.kmeans;

import hit.zhou.common.bean.Dir;
import hit.zhou.kmeans.cluster.Creater;
import hit.zhou.kmeans.cluster.KeyWordVector;
import hit.zhou.kmeans.cluster.KmeansClusterCos;

import java.util.List;
import java.util.Map;

public class KmeansCos extends KmeansAbstract<KmeansClusterCos> {

    public KmeansCos(Creater<KmeansClusterCos> builder) {
        super(builder);
    }

    @Override
    public void normalization(List<Dir> allTypeList, Map<String, KeyWordVector> word2VectorMap, int dimension) {
        for(int i = 0;i < dimension;i++){
            Dir dirChild = allTypeList.get(i);
            int count = dirChild.getFileCount();
            for(Map.Entry<String, KeyWordVector> keyWordVectorEntry:word2VectorMap.entrySet()){
                KeyWordVector keyWordVector = keyWordVectorEntry.getValue();
                float weight = (keyWordVector.getVectorData(i) / count) * 100;
                keyWordVector.setVectorData(i,weight);
            }
        }
    }

    @Override
    public int enSureWhichClusterSet(KeyWordVector keyWordVector, List<KmeansClusterCos> clusters) {
        float[] compareVector = keyWordVector.getVector();
        int bestSimilarClusterIndex = -1;
        float maxSimilarDistance = -1;
        for(int j = 0;j < clusters.size();j++){
            KmeansClusterCos currentCluster = clusters.get(j);
            float distance = currentCluster.calculateVector2CenterMass(compareVector);
            if(distance > maxSimilarDistance){
                bestSimilarClusterIndex = j;
                maxSimilarDistance = distance;
            }
        }
        return bestSimilarClusterIndex;
    }

    @Override
    public KeyWordVector getNotSimilarKeyWordVector(KmeansClusterCos kCluster, Map<String, KeyWordVector> word2VectorMap) {
        KeyWordVector notSimilarVector = null;
        float minSimilarDistance = Float.MAX_VALUE;
        for(Map.Entry<String,KeyWordVector> compareEntry:word2VectorMap.entrySet()){
            KeyWordVector compareVector  = compareEntry.getValue();
            float[] compareRealVector = compareVector.getVector();
            float distance = kCluster.calculateVector2CenterMass(compareRealVector);

            if(distance < minSimilarDistance && !kCluster.contains(compareVector)){
                notSimilarVector = compareVector;
                minSimilarDistance = distance;
            }
        }
        return notSimilarVector;
    }

    @Override
    public boolean isCanStop(List<KmeansClusterCos> clusters) {
        boolean canStop = true;
        for(KmeansClusterCos cluster:clusters){
            float distance = cluster.reCalculateCenterMass();
            if( Math.abs(1-distance) > 1e-8 ){
                canStop = false;
            }
        }
        return canStop;
    }
}
