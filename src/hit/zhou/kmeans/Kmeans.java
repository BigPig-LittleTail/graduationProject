package hit.zhou.kmeans;

import hit.zhou.common.bean.Dir;
import hit.zhou.common.bean.MyFile;
import hit.zhou.common.bean.WordCount;

import java.util.*;

public class Kmeans {
    private Dir dir;
    private HashMap<String,KeyWordVector> word2VectorMap;
    private List<KmeansCluster> clusters;

    private static final String ERROR_INIT = "Init error";

    public Kmeans(Dir dir){
        this.dir = dir;
        this.word2VectorMap = new HashMap<>();
        this.clusters = new ArrayList<>();
    }

    public void buildVectors(){
        List<MyFile> allFiles = dir.getFileList();
        int dimension = dir.getFileCount();
        for(int i = 0;i < dimension;i++){
            MyFile myFile = allFiles.get(i);
            List<WordCount<Float>> thisFileKeyWord = myFile.getKeyWord();
            int weightTotal = thisFileKeyWord.size();
            for(int j = 0;j < weightTotal;j++){
                WordCount<Float> wordCount = thisFileKeyWord.get(j);
                int weight = (weightTotal - j) * 100;
                String word = wordCount.getWord();
                if(word2VectorMap.containsKey(word)){
                    KeyWordVector keyWordVector = word2VectorMap.get(word);
                    keyWordVector.setVectorData(i,weight);
                }
                else{
                    KeyWordVector keyWordVector = new KeyWordVector(word,dimension);
                    keyWordVector.setVectorData(i,weight);
                    word2VectorMap.put(word,keyWordVector);
                }
            }
        }
    }

    public void buildTestVectors(){
        List<MyFile> allFiles = dir.getFileList();
        int fileCount = allFiles.size();
        for(int i = 0;i < fileCount;i++){
            MyFile myFile = allFiles.get(i);
            List<WordCount<Float>> thisFileKeyWord = myFile.getKeyWord();
            int dimension = thisFileKeyWord.size();
            for(int j = 0;j < dimension;j++){
                WordCount<Float> wordCount = thisFileKeyWord.get(j);
                String word = wordCount.getWord();
                if(word2VectorMap.containsKey(word)){
                    KeyWordVector keyWordVector = word2VectorMap.get(word);
                    float vectorData = keyWordVector.getVectorData(j);
                    keyWordVector.setVectorData(j,++vectorData);
                }
                else{
                    KeyWordVector keyWordVector = new KeyWordVector(word,dimension);
                    keyWordVector.setVectorData(j,1);
                    word2VectorMap.put(word,keyWordVector);
                }
            }
        }
    }


    public void kmeans(int k){
        if(!initKclusterCenter(k))
            throw new IllegalArgumentException(ERROR_INIT);
        initCluster();
        for(int i = 0;i < 400;i++){
            continueKmeasReal();
        }
    }

    private void continueKmeasReal(){
        for(KmeansCluster kmeansCluster:clusters){
            kmeansCluster.clear();
        }
        initCluster();
    }



    private void continueKmeans(){
        for(int j = 0;j < clusters.size();j++){
            KmeansCluster cluster = clusters.get(j);
            HashSet<KeyWordVector> set = cluster.getCluster();
            for(KeyWordVector keyWordVector:set){
                int clusterIndex = j;
                float[] compareVector = keyWordVector.getVector();
                float[] centerMass= cluster.getCenterMass();
                float distanceToCenterMass = 0;
                for(int i = 0;i < centerMass.length;i++){
                    distanceToCenterMass += (centerMass[i] - compareVector[i]) * (centerMass[i] - compareVector[i]);
                }

                for(int m = 0;m < clusters.size();m++){
                    if(m == j)
                        continue;
                    float distanceToOtherCenterMass = 0;
                    KmeansCluster thisCluster = clusters.get(m);
                    float[] thisClusterCenterMass = thisCluster.getCenterMass();
                    for(int i = 0;i < centerMass.length;i++){
                        distanceToOtherCenterMass += (thisClusterCenterMass[i] - compareVector[i]) * (thisClusterCenterMass[i] - compareVector[i]);
                    }
                    if(distanceToOtherCenterMass < distanceToCenterMass){
                        clusterIndex = m;
                        distanceToCenterMass = distanceToOtherCenterMass;
                    }
                }
                if(clusterIndex != j){
                    cluster.remove(keyWordVector);
                    clusters.get(clusterIndex).add(keyWordVector);
                }
            }
        }
        for(KmeansCluster cluster:clusters){
            cluster.reCalculateCenterMass();
        }
    }

    private void initCluster(){
        for(Map.Entry<String,KeyWordVector> keyWordEntry:word2VectorMap.entrySet()){
            KeyWordVector keyWordVector = keyWordEntry.getValue();
            float[] compareVector = keyWordVector.getVector();
            float minDistance = Float.MAX_VALUE;
            int minClusterIndex = -1;
            for(int j = 0;j < clusters.size();j++){
                KmeansCluster currentCluster = clusters.get(j);
                float distance = 0;
                float[] clusterCenterMass = currentCluster.getCenterMass();
                for(int i = 0;i < clusterCenterMass.length;i++){
                    distance += (clusterCenterMass[i] - compareVector[i])*
                            (clusterCenterMass[i] - compareVector[i]);
                }
                System.out.println(j + "," + distance);
                if(distance < minDistance){
                    minClusterIndex = j;
                    minDistance = distance;
                }
            }
            KmeansCluster minCluster = clusters.get(minClusterIndex);
            minCluster.add(keyWordVector);
        }
        for(KmeansCluster cluster:clusters){
            cluster.reCalculateCenterMass();
        }
    }

    private boolean initKclusterCenter(int k){
        KeyWordVector keyWordVector = getRandomInitKeyWordVector();
        clusters.add(new KmeansCluster(keyWordVector));
        KmeansCluster kCluster = new KmeansCluster(keyWordVector);

        for(int j = 1;j<k;j++){
            KeyWordVector maxDistanceVector = null;
            float maxDistance = 0;
            for(Map.Entry<String,KeyWordVector> compareEntry:word2VectorMap.entrySet()){
                KeyWordVector compareVector  = compareEntry.getValue();
                float[] compareRealVector = compareVector.getVector();
                float[] cetnerKClusterCenterMass = kCluster.getCenterMass();
                float distance = 0;
                for(int i = 0;i < cetnerKClusterCenterMass.length;i++){
                    distance += (cetnerKClusterCenterMass[i] - compareRealVector[i])
                            * (cetnerKClusterCenterMass[i] - compareRealVector[i]);
                }
                if(distance > maxDistance && !kCluster.contains(compareVector)){
                    maxDistanceVector = compareVector;
                    maxDistance = distance;
                }
            }
            if(maxDistanceVector == null)
                return false;
            clusters.add(new KmeansCluster(maxDistanceVector));
            kCluster.add(maxDistanceVector);
            kCluster.reCalculateCenterMass();
        }
        return true;
    }

    private KeyWordVector getRandomInitKeyWordVector() {
        Random random = new Random();
        int randomFileIndex = random.nextInt(dir.getFileCount());
        MyFile myFile = dir.getFileList().get(randomFileIndex);
        int randomKeyWordIndex = random.nextInt(myFile.getKeyWordListSize());
        WordCount<Float> keyWord = myFile.getKeyWord().get(randomKeyWordIndex);
        String keyWordString = keyWord.getWord();
        return word2VectorMap.get(keyWordString);
    }


}
