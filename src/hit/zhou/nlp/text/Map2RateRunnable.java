package hit.zhou.nlp.text;

import hit.zhou.hepler.FileUtil;
import hit.zhou.nlp.ltp.LtpBaseOpLocal;

import java.io.IOException;
import java.util.*;

public class Map2RateRunnable implements Runnable {
    private MyFile myFile;
    private LtpBaseOpLocal ltpBaseOpLocal;
    private boolean isStopWord;
    private HashSet<String> stopWords;
    private Map<String,Map<String,Float>> allMaps;
    private String saveDirPath;
    private String fileName;

    public Map2RateRunnable(
            Map<String, Map<String,Float>> allMaps, MyFile myFile, LtpBaseOpLocal ltpBaseOpLocal, boolean isStopWord, HashSet<String> stopWords,
                            String saveDirPath, String fileName){
        this.allMaps = allMaps;
        this.myFile = myFile;
        this.ltpBaseOpLocal = ltpBaseOpLocal;
        this.isStopWord  = isStopWord;
        this.stopWords = stopWords;
        this.saveDirPath = saveDirPath;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        try {
            String string = FileUtil.readString(myFile.getPath());
            Map<String,Float> fileMap = getMap2Rate(string,ltpBaseOpLocal);
            allMaps.put(myFile.getFileName(),fileMap);
            String writeString = "";
            for(Map.Entry<String,Float> entry:fileMap.entrySet()){
                writeString = writeString + entry.getKey() + "\t" + entry.getValue() + "\r\n";
            }
            FileUtil.save(saveDirPath,fileName,writeString.getBytes(),false);
        }catch (IOException e){
            e.printStackTrace();
        }

    }


    private Map<String,Float> getMap2Rate(String fileString, LtpBaseOpLocal ltpBaseOpLocal) {
        List<String> stringList = new ArrayList<>();
        ltpBaseOpLocal.splitSentence(fileString, stringList);
        Map<String, Integer> word2Count = new HashMap<>();
        Map<String, Float> word2Rate = new HashMap<>();
        List<String> words = new ArrayList<>();
        int totalWordNum = 0;
        for (String s : stringList) {
            if (s.equals("")) {
                continue;
            }
            ltpBaseOpLocal.segmentor(s, words);
            for (String word : words) {
                if (isStopWord && stopWords.contains(word)) {
                    continue;
                }
                totalWordNum++;
                if (word2Count.containsKey(word)) {
                    int count = word2Count.get(word);
                    word2Count.put(word, ++count);
                } else {
                    word2Count.put(word, 1);
                }
            }
        }
        for (Map.Entry<String, Integer> entry : word2Count.entrySet()) {
            String word = entry.getKey();
            int count = entry.getValue();
            float rate = (float) count / totalWordNum;
            word2Rate.put(word, rate);
        }
        return word2Rate;
    }
}
