package hit.zhou.keyword;

import hit.zhou.common.bean.MyFileTest;
import hit.zhou.common.tools.FileUtil;
import hit.zhou.ltp.LtpBaseOpLocal;

import java.io.IOException;
import java.util.*;

public class Map2RateRunnableTest implements Runnable {
    private MyFileTest myFile;
    private LtpBaseOpLocal ltpBaseOpLocal;
    private boolean isStopWord;
    private HashSet<String> stopWords;
    private String savePath;

    public Map2RateRunnableTest(MyFileTest myFile, LtpBaseOpLocal ltpBaseOpLocal, boolean isStopWord, HashSet<String> stopWords,
                                String savePath){
        this.myFile = myFile;
        this.ltpBaseOpLocal = ltpBaseOpLocal;
        this.isStopWord  = isStopWord;
        this.stopWords = stopWords;
        this.savePath = savePath;
    }

    @Override
    public void run() {
        try {
            String string = FileUtil.readString(myFile.getPath());
            Map<String,Integer> fileMap = new HashMap<>();
            int totalNum = getMap2Count(string,ltpBaseOpLocal,fileMap);
            myFile.setWord2Count(fileMap);
            myFile.setTotalCount(totalNum);
            saveMap2CountToFile(fileMap);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void saveMap2CountToFile(Map<String, Integer> fileMap) {
        String writeString = "";
        for(Map.Entry<String,Integer> entry:fileMap.entrySet()){
            writeString = writeString + entry.getKey() + "\t" + entry.getValue() + "\r\n";
        }
        FileUtil.saveTest(savePath,writeString.getBytes(),false);
    }

    private int getMap2Count(String fileString, LtpBaseOpLocal ltpBaseOpLocal,Map<String,Integer> word2Count) {
        List<String> stringList = new ArrayList<>();
        ltpBaseOpLocal.splitSentence(fileString, stringList);
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
                if(word.matches(".*[^\\u4e00-\\u9fa5].*")){
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
        return totalWordNum;
    }

}
