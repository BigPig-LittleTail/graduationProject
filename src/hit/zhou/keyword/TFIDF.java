package hit.zhou.keyword;

import hit.zhou.common.bean.Dir;
import hit.zhou.common.bean.MyFile;
import hit.zhou.common.bean.WordCount;
import hit.zhou.common.tools.FileUtil;
import hit.zhou.ltp.LtpBaseOpLocal;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class TFIDF {
    private Dir dir;
    private HashSet<String> stopWordSet;
    private boolean isStopWord;
    private static ExecutorService pool = Executors.newFixedThreadPool(4);

    private static final String MAP_2_RATE_FILE_NAME = "map2rate.txt";
    private static final String KEY_WORD_FILE_NAME = "key_word.txt";

    public TFIDF(Dir dir) {
        this.dir = dir;
        this.isStopWord = false;
    }

    public TFIDF(Dir dir,String stopWordPath){
        this.dir = dir;
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

    public Map<String,Map<String,Float>> buildAllFileMap2Rate(LtpBaseOpLocal ltpBaseOpLocal,String readPath,String savePath)
            throws InterruptedException,IOException,ExecutionException {
        List<MyFile> myFiles = dir.getFileList();
        Map<String, Map<String, Float>> allMaps = new ConcurrentHashMap<>();
        File dirFile = new File(savePath);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
        List<Future> futures = new ArrayList<>();
        for (MyFile myFile : myFiles) {
            String filePath = readPath + myFile.getFileName() + "/" + MAP_2_RATE_FILE_NAME;
            File file = new File(filePath);
            if (!file.exists()) {
                String fileSavePath = savePath + myFile.getFileName() + "/";
                Map2RateRunnable r = new Map2RateRunnable(
                        allMaps, myFile, ltpBaseOpLocal, isStopWord, stopWordSet,
                        fileSavePath, MAP_2_RATE_FILE_NAME);
                futures.add(pool.submit(r));
            } else {
                Map<String, Float> fileMap = getStringFloatMapFromFile(filePath);
                allMaps.put(myFile.getFileName(),fileMap);
            }
        }
        for(Future f:futures){
            f.get();
        }
        pool.shutdown();
        ltpBaseOpLocal.releaseSegmentor();
        return allMaps;
    }

    private Map<String, Float> getStringFloatMapFromFile(String filePath) throws IOException {
        Map<String,Float> fileMap = new HashMap<>();
        String mapString = FileUtil.readString(filePath);
        String[] mapStringArray = mapString.split("\r\n");
        for(String string : mapStringArray){
            String[] mapEntry = string.split("\t");
            fileMap.put(mapEntry[0],Float.valueOf(mapEntry[1]));
        }
        return fileMap;
    }


    public boolean getAllFileKeyWordList(int keyWordListSize,Map<String,Map<String,Float>> allFileWord2Rate,String readDirPath,String saveDirPath)
    throws IOException {
        List<MyFile> list = dir.getFileList();
        if(list.size() == 0)
            return false;
        File dirPath = new File(saveDirPath);
        if(!dirPath.exists()){
            dirPath.mkdirs();
        }
        boolean result = true;
        for(MyFile myFile:list){
            String fileReadPath = readDirPath + myFile.getFileName() + "/" + KEY_WORD_FILE_NAME;
            String fileSavePath = saveDirPath + myFile.getFileName() + "/";
            if(!getFilefKeyWordList(keyWordListSize,myFile.getFileName(),allFileWord2Rate,fileReadPath,fileSavePath))
                result = false;
        }
        return result;
    }


    public boolean getFilefKeyWordList(int keyWordListSize,String fileName,Map<String,Map<String,Float>> allFileWord2Rate,String readPath,String savePath)
    throws IOException{
        File keyWordFile = new File(readPath);
        if(keyWordFile.exists() && keyWordFile.isFile()){
            List<WordCount<Float>> realKeyWordList = new ArrayList<>();
            String keyWordListString = FileUtil.readString(readPath);
            String[] keyWordListStringArray = keyWordListString.split("\r\n");
            for(String string : keyWordListStringArray){
                String[] wordCountString = string.split("\t");
                WordCount<Float> wordCount = new WordCount<>(wordCountString[0],Float.valueOf(wordCountString[1]));
                realKeyWordList.add(wordCount);
            }
            MyFile myFile = dir.getFileByName(fileName);
            myFile.setKeyWord(realKeyWordList);
            return true;
        }
        int filesNum = dir.getFileCount();
        List<MyFile> myFiles = dir.getFileList();
        if(allFileWord2Rate.containsKey(fileName)){
            MyFile myFile = dir.getFileByName(fileName);
            Map<String,Float> myFileWord2Rate = allFileWord2Rate.get(fileName);
            List<WordCount<Float>> keyWordList = new ArrayList<>();
            for(Map.Entry<String,Float> entry:myFileWord2Rate.entrySet()){
                String word = entry.getKey();
                float tf = entry.getValue();
                int existInOtherFileNum = 0;
                for(MyFile listItemFile:myFiles){
                    Map<String,Float> listItemFileWord2Rate = allFileWord2Rate.get(listItemFile.getFileName());
                    if(listItemFileWord2Rate.containsKey(word)){
                        existInOtherFileNum++;
                    }
                }
                float idf = (float) Math.log((((double) filesNum + 1)/(double) existInOtherFileNum + 1)) + 1 ;
                keyWordList.add(new WordCount<>(word,tf * idf));
            }
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
            List<WordCount<Float>> realKeyWordList = new ArrayList<>(keyWordList.subList(0,keyWordRealSize));
            myFile.setKeyWord(realKeyWordList);
            saveKeyWordFile(savePath,realKeyWordList);
            return true;
        }
        return false;
    }

    private void saveKeyWordFile(String dirPath,List<WordCount<Float>> keyWord){
        String writeString = "";
        for(WordCount<Float> word2KeyWordRate:keyWord){
            writeString = writeString + word2KeyWordRate.getWord() + "\t" + word2KeyWordRate.getcountOrRate() + "\r\n";
        }
        FileUtil.save(dirPath,KEY_WORD_FILE_NAME,writeString.getBytes(),false);
    }

}
