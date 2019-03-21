package hit.zhou.kmeans;

import hit.zhou.common.bean.DirTest;
import hit.zhou.common.bean.MyFileTest;
import hit.zhou.common.bean.WordCount;
import hit.zhou.kmeans.cluster.Creater;
import hit.zhou.kmeans.cluster.KeyWordVector;
import hit.zhou.kmeans.cluster.KmeansClusterAbstract;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public  abstract class KmeansAbstract<T extends KmeansClusterAbstract> {
    private Map<String, KeyWordVector> word2VectorMap;
    private List<T> clusters;
    private Creater<T> builder;
    private static final String ERROR_INIT = "Init error";
    private static final String ClUSTER_CENTER_MASS_FILE_NAME = "center_mass.txt";
    private static final String KEYWORDVECTOR_FILE_NAME = "vectors.txt";

    public KmeansAbstract(Creater<T> builder){
        this.word2VectorMap = new HashMap<>();
        this.clusters = new ArrayList<>();
        this.builder = builder;
    }

    public abstract void normalization(List<DirTest> allTypeList,Map<String, KeyWordVector> word2VectorMap,int dimension);
    public abstract int enSureWhichClusterSet(KeyWordVector keyWordVector,List<T> clusters);
    public abstract KeyWordVector getNotSimilarKeyWordVector(T kCluster, Map<String, KeyWordVector> word2VectorMap);
    public abstract boolean isCanStop(List<T> clusters);


    public void saveClusters(String saveDirPath){
        Date nowDate = new Date( );
        SimpleDateFormat format = new SimpleDateFormat ("yyyy-MM-dd-hhmmss");
        String saveRealDirPath = saveDirPath + format.format(nowDate) + "/";
        File file = new File(saveRealDirPath);
        if(!file.exists()){
            file.mkdirs();
        }
        for(int i = 0;i < clusters.size();i++){
            T cluster = clusters.get(i);
            String realDirPath = saveRealDirPath + "Cluster_"+ i + "/";
            File readlDirFile = new File(realDirPath);
            if(!readlDirFile.exists()){
                readlDirFile.mkdirs();
            }
            cluster.saveCenterMass(realDirPath + ClUSTER_CENTER_MASS_FILE_NAME);
            cluster.saveVectors(realDirPath + KEYWORDVECTOR_FILE_NAME);
        }
    }

    public void kmeans(int k, int maxExecuteCount){
        if(!initKclusterCenter(k))
            throw new IllegalArgumentException(ERROR_INIT);
        int count = 0;
        while(count < maxExecuteCount){
            realKmeans();
            boolean canStop = isCanStop();
            if(canStop)
                break;
            count++;
        }
        for(T cluster:clusters){
            cluster.sortVectors();
        }
    }



    private boolean isCanStop() {
        return isCanStop(clusters);
    }

    private void realKmeans(){
        for(T kmeansCluster:clusters){
            kmeansCluster.clear();
        }
        setVectorToCluster();
    }

    private void setVectorToCluster(){
        for(Map.Entry<String,KeyWordVector> keyWordEntry:word2VectorMap.entrySet()){
            KeyWordVector keyWordVector = keyWordEntry.getValue();
            int index = enSureWhichClusterSet(keyWordVector);
            T minCluster = clusters.get(index);
            minCluster.add(keyWordVector);
        }

    }

    private int enSureWhichClusterSet(KeyWordVector keyWordVector) {
        return enSureWhichClusterSet(keyWordVector,clusters);
    }


    private boolean initKclusterCenter(int k){
        KeyWordVector keyWordVector = getRandomInitKeyWordVector();
        clusters.add(builder.create(keyWordVector));
        T kCluster = builder.create(keyWordVector);
        for(int j = 1;j < k; j++){
            KeyWordVector notSimilarKeyWordVector = getNotSimilarKeyWordVector(kCluster);
            if(notSimilarKeyWordVector == null)
                return false;
            clusters.add(builder.create(notSimilarKeyWordVector));
            kCluster.add(notSimilarKeyWordVector);
            kCluster.reCalculateCenterMass();
        }
        return true;
    }

    private KeyWordVector getNotSimilarKeyWordVector(T kCluster) {
        return getNotSimilarKeyWordVector(kCluster,word2VectorMap);
    }

    private KeyWordVector getRandomInitKeyWordVector() {
        Random random = new Random();
        int index = random.nextInt(word2VectorMap.size());
        int i = 0;
        for(Map.Entry<String,KeyWordVector> entry:word2VectorMap.entrySet()){
            if(i == index){
                return entry.getValue();
            }
            i++;
        }
        return null;
    }


    public void buildVectors(DirTest dirNode, int targetLevel){
        realBuildVectors(dirNode,targetLevel);
    }

    private void realBuildVectors(DirTest dirNode,int targetLevel){
        // targetLevel是聚类完成后类的层
        // 获得targetLevel的子List<DirTest>的队列，队列里的每一个List都是一维
        Deque<List<DirTest>> queue = getTargetLevelLists(dirNode, targetLevel);
        List<DirTest> allDirParentList = new ArrayList<>();
        while(!queue.isEmpty()){
            List<DirTest> dirParentList  = queue.poll();
            allDirParentList.addAll(dirParentList);
        }
        int dimension = allDirParentList.size();
        // 将KeyWord转化成多维向量KeyWordVector
        for(int i = 0;i < dimension;i++){
            DirTest dirChild = allDirParentList.get(i);

            List<MyFileTest> thisLevelFileList = dirChild.getFileList();
            for(MyFileTest thisLevelFile:thisLevelFileList){
                List<WordCount<Float>> keyWordList = thisLevelFile.getKeyWord();
                transKeyWordToKeyWordVector(dimension,i,keyWordList);
            }

            List<DirTest> thisLevelDirList = dirChild.getDirCihldList();
            for(DirTest thisLevelDir:thisLevelDirList){
                List<WordCount<Float>> keyWordList = thisLevelDir.getKeyWordList();
                transKeyWordToKeyWordVector(dimension, i, keyWordList);
            }
        }
        normalization(allDirParentList, dimension);
    }

    private void normalization(List<DirTest> allTypeList, int dimension) {
        normalization(allTypeList,word2VectorMap,dimension);
    }

    private Deque<List<DirTest>> getTargetLevelLists(DirTest dirNode, int targetLevel) {
        int currentLevel = 1;
        Deque<List<DirTest>> queue = new LinkedList<>();
        List<DirTest> node = dirNode.getDirCihldList();
        queue.offer(node);
        List<DirTest> lastNode = node;
        currentLevel++;
        while(!queue.isEmpty() && currentLevel < targetLevel){
            do{
                node = queue.peek();
                for (DirTest dirChild:node){
                    queue.offer(dirChild.getDirCihldList());
                }
            }while(queue.poll() != lastNode);
            lastNode = queue.peekLast();
            currentLevel++;
        }
        return queue;
    }

    private void transKeyWordToKeyWordVector(int dimension, int i, List<WordCount<Float>> keyWordList) {
        for(WordCount<Float> wordCount:keyWordList){
            String word = wordCount.getWord();
            float tfidf = wordCount.getcountOrRate();
            if(word2VectorMap.containsKey(word)){
                KeyWordVector keyWordVector = word2VectorMap.get(word);
                float weight = keyWordVector.getVectorData(i) + tfidf;
                keyWordVector.setVectorData(i,weight);
            }
            else{
                KeyWordVector keyWordVector = new KeyWordVector(word,dimension);
                keyWordVector.setVectorData(i,tfidf);
                word2VectorMap.put(word,keyWordVector);
            }
        }
    }

}
