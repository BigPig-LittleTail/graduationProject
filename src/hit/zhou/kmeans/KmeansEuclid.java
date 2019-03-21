package hit.zhou.kmeans;

import hit.zhou.common.bean.DirTest;
import hit.zhou.kmeans.cluster.Creater;
import hit.zhou.kmeans.cluster.KeyWordVector;
import hit.zhou.kmeans.cluster.KmeansClusterEuclid;

import java.util.List;
import java.util.Map;

public class KmeansEuclid extends KmeansAbstract<KmeansClusterEuclid> {

    public KmeansEuclid(Creater<KmeansClusterEuclid> builder) {
        super(builder);
    }

    @Override
    public void normalization(List<DirTest> allTypeList, Map<String, KeyWordVector> word2VectorMap, int dimension) {
        for(int i = 0;i < dimension;i++){
            DirTest dirChild = allTypeList.get(i);
            int count = dirChild.getFileCount();
            for(Map.Entry<String, KeyWordVector> keyWordVectorEntry:word2VectorMap.entrySet()){
                KeyWordVector keyWordVector = keyWordVectorEntry.getValue();
                float weight = (keyWordVector.getVectorData(i) / count) * 100;
                keyWordVector.setVectorData(i,weight);
            }
        }
    }

    @Override
    public int enSureWhichClusterSet(KeyWordVector keyWordVector, List<KmeansClusterEuclid> clusters) {
        float[] compareVector = keyWordVector.getVector();
        int bestSimilarClusterIndex = -1;
        float maxNotSimilarDistance = Float.MIN_VALUE;
        for(int j = 0;j < clusters.size();j++){
            KmeansClusterEuclid currentCluster = clusters.get(j);
            float distance = currentCluster.calculateVector2CenterMass(compareVector);
            if(distance < maxNotSimilarDistance){
                bestSimilarClusterIndex = j;
                maxNotSimilarDistance = distance;
            }
        }
        return bestSimilarClusterIndex;
    }

    @Override
    public KeyWordVector getNotSimilarKeyWordVector(KmeansClusterEuclid kCluster, Map<String, KeyWordVector> word2VectorMap) {
        KeyWordVector notSimilarVector = null;
        float maxNotSimilarDistance = 0;
        for(Map.Entry<String,KeyWordVector> compareEntry:word2VectorMap.entrySet()){
            KeyWordVector compareVector  = compareEntry.getValue();
            float[] compareRealVector = compareVector.getVector();
            float distance = kCluster.calculateVector2CenterMass(compareRealVector);

            if(distance > maxNotSimilarDistance && !kCluster.contains(compareVector)){
                notSimilarVector = compareVector;
                maxNotSimilarDistance = distance;
            }
        }
        return notSimilarVector;
    }

    @Override
    public boolean isCanStop(List<KmeansClusterEuclid> clusters) {
        boolean canStop = true;
        for(KmeansClusterEuclid cluster:clusters){
            float distance = cluster.reCalculateCenterMass();
            if( Math.abs(distance) > 1e-8 ){
                canStop = false;
            }
        }
        return canStop;
    }
}
