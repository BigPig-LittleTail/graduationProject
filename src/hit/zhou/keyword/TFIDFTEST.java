package hit.zhou.keyword;

import hit.zhou.common.bean.DirTest;
import hit.zhou.common.bean.MyFileTest;
import hit.zhou.common.bean.WordCount;
import hit.zhou.common.tools.FileUtil;
import hit.zhou.ltp.LtpBaseOpLocal;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TFIDFTEST {
    private HashSet<String> stopWordSet;
    private boolean isStopWord;
    private static ExecutorService pool = Executors.newFixedThreadPool(4);

    private static final String MAP_2_RATE_FILE_NAME = "map2rate.txt";
    private static final String KEY_WORD_FILE_NAME = "key_word.txt";

    public void buildKeyWord(int keyWordSize,DirTest dirNode,String saveDirPath) throws IOException{
        String saveTopDirPath = saveDirPath + dirNode.getDirName() + "/";
        List<DirTest> dirChildDirList = dirNode.getDirCihldList();
        for(DirTest dirChildDir:dirChildDirList){
            buildKeyWord(keyWordSize,dirChildDir,saveTopDirPath);
        }
        DirTest parent = dirNode.getParent();
        if(parent == null){
            return;
        }

        List<Map<String,Integer>> allBrohterDirChildMaps = new ArrayList<>();
        List<DirTest> brotherDirList = parent.getDirCihldList();
        for(DirTest brotherDir:brotherDirList){
            if(brotherDir == dirNode){
                continue;
            }
            List<MyFileTest> brotherDirChildFileList = brotherDir.getFileList();
            for(MyFileTest brotherDirChildFile:brotherDirChildFileList){
                allBrohterDirChildMaps.add(brotherDirChildFile.getWord2Count());
            }
            List<DirTest> brotherDirChildDirList = brotherDir.getDirCihldList();
            for(DirTest brotherDirChildDir:brotherDirChildDirList){
                allBrohterDirChildMaps.add(brotherDirChildDir.getWord2Count());
            }
        }

        List<MyFileTest> dirNodeChildFileList = dirNode.getFileList();
        for(MyFileTest dirNodeChildFile:dirNodeChildFileList){
            String saveKeyWordPath = saveTopDirPath + dirNodeChildFile.getFileName() + "/" + KEY_WORD_FILE_NAME;
            File keyWordFile = new File(saveKeyWordPath);
            List<WordCount<Float>> realKeyWordList;
            if(keyWordFile.exists()){
                realKeyWordList = readKeyWordFromFile(saveKeyWordPath);
            }
            else{
                Map<String,Integer> dirNodeChildFileWord2Count = dirNodeChildFile.getWord2Count();
                int dirNodeChildFileWordTotalNum = dirNodeChildFile.getTotalCount();
                List<WordCount<Float>> keyWordList = getInitKeyWordLsit(allBrohterDirChildMaps, dirNodeChildFileWord2Count,
                        dirNodeChildFileWordTotalNum);
                realKeyWordList = transInitKeyWordList2RealKeyWordList(keyWordSize,keyWordList);
                saveKeyWordFile(saveKeyWordPath,realKeyWordList);
            }
            dirNodeChildFile.setKeyWord(realKeyWordList);
        }

        for(DirTest dirChildDir:dirChildDirList){
            String saveKeyWordPath = saveTopDirPath + dirChildDir.getDirName() + "/" + KEY_WORD_FILE_NAME;
            File keyWordFile = new File(saveKeyWordPath);
            List<WordCount<Float>> realKeyWordList;
            if(keyWordFile.exists()){
                realKeyWordList = readKeyWordFromFile(saveKeyWordPath);
            }
            else {
                Map<String,Integer> dirNodeChildDirWord2Count = dirChildDir.getWord2Count();
                int dirNodeChildDirWordTotalNum = dirChildDir.getTotalNum();
                List<WordCount<Float>> keyWordList = getInitKeyWordLsit(allBrohterDirChildMaps,dirNodeChildDirWord2Count,
                        dirNodeChildDirWordTotalNum);
                realKeyWordList = transInitKeyWordList2RealKeyWordList(keyWordSize,keyWordList);
                saveKeyWordFile(saveKeyWordPath,realKeyWordList);
            }
            dirChildDir.setKeyWordList(realKeyWordList);
        }

    }

    private List<WordCount<Float>> readKeyWordFromFile(String readPath) throws IOException {
        List<WordCount<Float>> realKeyWordList = new ArrayList<>();
        String keyWordListString = FileUtil.readString(readPath);
        String[] keyWordListStringArray = keyWordListString.split("\r\n");
        for(String string : keyWordListStringArray){
            String[] wordCountString = string.split("\t");
            WordCount<Float> wordCount = new WordCount<>(wordCountString[0],Float.valueOf(wordCountString[1]));
            realKeyWordList.add(wordCount);
        }
        return realKeyWordList;
    }

    private void saveKeyWordFile(String savePath,List<WordCount<Float>> keyWord){
        String writeString = "";
        for(WordCount<Float> word2KeyWordRate:keyWord){
            writeString = writeString + word2KeyWordRate.getWord() + "\t" + word2KeyWordRate.getcountOrRate() + "\r\n";
        }
        FileUtil.saveTest(savePath,writeString.getBytes(),false);
    }

    private List<WordCount<Float>> getInitKeyWordLsit(List<Map<String, Integer>> allBrohterDirChildMaps, Map<String, Integer> dirNodeChildWord2Count, int dirNodeChildFileTotalNum) {
        List<WordCount<Float>> keyWordList = new ArrayList<>();
        for(Map.Entry<String,Integer> entry:dirNodeChildWord2Count.entrySet()){
            String word = entry.getKey();
            int countNum = entry.getValue();
            float tf = ((float) countNum)/ ((float) dirNodeChildFileTotalNum);
            float idf = calculateIdf(word,allBrohterDirChildMaps);
            keyWordList.add(new WordCount<Float>(word,tf * idf));
        }
        return keyWordList;
    }

    private List<WordCount<Float>> transInitKeyWordList2RealKeyWordList(int keyWordListSize,List<WordCount<Float>> keyWordList){
        Comparator<WordCount<Float>> comparator = new Comparator<WordCount<Float>>() {
            @Override
            public int compare(WordCount<Float> o1, WordCount<Float> o2) {
                if(o1.getcountOrRate() > o2.getcountOrRate())
                    return -1;
                else if(o1.getcountOrRate() < o2.getcountOrRate())
                    return 1;
                else
                    return 0;
            }
        };
        keyWordList.sort(comparator);
        int keyWordRealSize = keyWordListSize > keyWordList.size() ? keyWordList.size() : keyWordListSize;
        return new ArrayList<>(keyWordList.subList(0,keyWordRealSize));
    }

    private float calculateIdf(String word,List<Map<String,Integer>> allBrohterDirChildMaps){
        int totalFileNum = allBrohterDirChildMaps.size();
        int existInOtherFile = 0;
        for(Map<String,Integer> word2Count:allBrohterDirChildMaps){
            if(word2Count.containsKey(word)){
                existInOtherFile++;
            }
        }
        float idf = (float) (Math.log(((double) totalFileNum + 1)/((double) existInOtherFile + 1)) + 1 );
        return idf;
    }


    public void buildMap2Count(DirTest dirNode, LtpBaseOpLocal ltpBaseOpLocal,String saveDirPath) throws IOException, InterruptedException,ExecutionException {
        List<Future> futures = new ArrayList<>();
        realBuildMap2Count(dirNode,ltpBaseOpLocal,saveDirPath,futures);
        for(Future f:futures){
            f.get();
        }
        pool.shutdown();
        ltpBaseOpLocal.releaseSegmentor();
        realBuildMap2CountDir(dirNode,saveDirPath);
    }

    private void realBuildMap2CountDir(DirTest dirNode,String saveDirPath) throws IOException{
        String saveTopDirPath = saveDirPath + dirNode.getDirName() + "/";

        List<DirTest> dirChildList = dirNode.getDirCihldList();
        for(DirTest dirChild:dirChildList){
            realBuildMap2CountDir(dirChild,saveTopDirPath);
        }

        String dirMap2WordPath = saveTopDirPath + MAP_2_RATE_FILE_NAME;
        File dirMap2WordFile = new File(dirMap2WordPath);
        if(!dirMap2WordFile.exists()){
            List<MyFileTest> fileChildList = dirNode.getFileList();
            int totalNum = 0;
            Map<String,Integer> word2CountDir = new HashMap<>();
            for(MyFileTest fileChild:fileChildList){
                totalNum += fileChild.getTotalCount();
                Map<String,Integer> word2CountFile = fileChild.getWord2Count();
                for(Map.Entry<String,Integer> entry:word2CountFile.entrySet()){
                    String key = entry.getKey();
                    int value = entry.getValue();
                    if(word2CountDir.containsKey(key)){
                        word2CountDir.put(key,word2CountDir.get(key) + value);
                    }
                    else {
                        word2CountDir.put(key,value);
                    }
                }
            }
            for(DirTest dirChild:dirChildList){
                totalNum += dirChild.getTotalNum();
                Map<String,Integer> word2CountDirChild = dirChild.getWord2Count();
                for(Map.Entry<String,Integer> entry:word2CountDirChild.entrySet()){
                    String key = entry.getKey();
                    int value = entry.getValue();
                    if(word2CountDir.containsKey(key)){
                        word2CountDir.put(key,word2CountDir.get(key) + value);
                    }
                    else {
                        word2CountDir.put(key,value);
                    }
                }
            }
            dirNode.setTotalNum(totalNum);
            dirNode.setWord2Count(word2CountDir);
            saveMap2CountToFile(dirMap2WordPath,word2CountDir);
        }
        else{
            Map<String,Integer> fileMap = new HashMap<>();
            int totalCount = readMap2CountFromFile(dirMap2WordPath,fileMap);
            dirNode.setWord2Count(fileMap);
            dirNode.setTotalNum(totalCount);
        }
    }

    private void saveMap2CountToFile(String savePath,Map<String, Integer> fileMap) {
        String writeString = "";
        for(Map.Entry<String,Integer> entry:fileMap.entrySet()){
            writeString = writeString + entry.getKey() + "\t" + entry.getValue() + "\r\n";
        }
        FileUtil.saveTest(savePath,writeString.getBytes(),false);
    }


    private void realBuildMap2Count(DirTest dirNode, LtpBaseOpLocal ltpBaseOpLocal,String saveDirPath,List<Future> futures) throws IOException, InterruptedException,ExecutionException {
        // 指定存储路径
        File saveDirFile = new File(saveDirPath);
        if(!saveDirFile.exists()){
            saveDirFile.mkdirs();
        }
        // 真正的存储路径顶级是指定savePath加上dir的名字
        String saveTopDirPath = saveDirPath + dirNode.getDirName() + "/";
        File saveTopDirFile = new File(saveTopDirPath);
        if(!saveTopDirFile.exists()){
            saveTopDirFile.mkdirs();
        }

        List<DirTest> childDirList = dirNode.getDirCihldList();
        for(DirTest childDir:childDirList){
            realBuildMap2Count(childDir,ltpBaseOpLocal,saveTopDirPath,futures);
        }

        List<MyFileTest> childFileList = dirNode.getFileList();

        for(MyFileTest childFile:childFileList){
            // 每个文件对应的文件夹路径，用来保存文件的map，keyword等结果
            String childFileSaveDirPath = saveTopDirPath + childFile.getFileName() + "/";
            String childFileMap2CountPath = childFileSaveDirPath + MAP_2_RATE_FILE_NAME;
            File childFileMap2CountFile = new File(childFileMap2CountPath);
            if(!childFileMap2CountFile.exists()){
                // 如果map不存在，则直接多线程分词，写入文件
                File childFileSaveDir = new File(childFileSaveDirPath);
                if(!childFileSaveDir.exists()){
                    childFileSaveDir.mkdirs();
                }
                Map2RateRunnableTest r = new Map2RateRunnableTest(childFile,ltpBaseOpLocal,isStopWord,stopWordSet,childFileMap2CountPath);
                futures.add(pool.submit(r));
            }
            else {
                // 如果map存在，则读入
                Map<String,Integer> fileMap = new HashMap<>();
                int totalCount = readMap2CountFromFile(childFileMap2CountPath,fileMap);
                childFile.setWord2Count(fileMap);
                childFile.setTotalCount(totalCount);
            }
        }
    }

    private int readMap2CountFromFile(String filePath,Map<String,Integer> fileMap) throws IOException{
        String mapString = FileUtil.readString(filePath);
        System.out.println(filePath);
        String[] mapStringArray = mapString.split("\r\n");
        int totalCount = 0;
        for(String string : mapStringArray){
            String[] mapEntry = string.split("\t");
            int wordCount = Integer.valueOf(mapEntry[1]);
            totalCount += wordCount;
            fileMap.put(mapEntry[0],wordCount);
        }
        return totalCount;
    }



    public TFIDFTEST(String stopWordPath){
        initStopWordSet(stopWordPath);
    }

    private void initStopWordSet(String stopWordPath){
        try {
            String fileString = FileUtil.readString(stopWordPath);
            String[] stopWords = fileString.split("\r\n");
            this.stopWordSet = new HashSet<>();
            this.isStopWord = true;
            for(String string:stopWords){
                if(string.equals(""))
                    continue;
                stopWordSet.add(string);
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }



}
