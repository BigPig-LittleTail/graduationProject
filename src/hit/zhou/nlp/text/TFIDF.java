package hit.zhou.nlp.text;

import hit.zhou.hepler.FileUtil;
import hit.zhou.nlp.ltp.LtpBaseOpLocal;

import java.io.IOException;
import java.util.*;

public class TFIDF {
    private Dir dir;
    private HashSet<String> stopWordSet;
    private boolean isStopWord;
    public TFIDF(Dir dir,LtpBaseOpLocal ltpBaseOpLocal) {
        this.dir = dir;
        this.isStopWord = false;
        init(ltpBaseOpLocal);
    }

    public TFIDF(Dir dir,LtpBaseOpLocal ltpBaseOpLocal,String stopWordPath){
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
        init(ltpBaseOpLocal);
    }

    private void init(LtpBaseOpLocal ltpBaseOpLocal){
        List<MyFile> myFiles = dir.getFileList();
        for(MyFile myFile:myFiles) {
            try {
                String string = FileUtil.readString(myFile.getPath());
                setMap2Rate(string,ltpBaseOpLocal,myFile);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void setMap2Rate(String fileString, LtpBaseOpLocal ltpBaseOpLocal, MyFile myFile){
        List<String> stringList = new ArrayList<>();
        ltpBaseOpLocal.splitSentence(fileString,stringList);
        Map<String,Integer> word2Count = new HashMap<>();
        Map<String,Float> word2Rate = new HashMap<>();
        List<String> words = new ArrayList<>();
        int totalWordNum = 0;
        for(String s:stringList) {
            if (s.equals("")) {
                continue;
            }
            ltpBaseOpLocal.segmentor(s, words);
            for (String word : words) {
                if(isStopWord && stopWordSet.contains(word)) {
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
        for(Map.Entry<String,Integer> entry:word2Count.entrySet()){
            String word = entry.getKey();
            int count = entry.getValue();
            float rate = (float) count/totalWordNum;
            word2Rate.put(word,rate);
        }
//        System.out.println(word2Rate);
        myFile.setWord2Rate(word2Rate);
    }


    public boolean setAllKeyWordListToAllFile(int keyWordListSize){
        List<MyFile> list = dir.getFileList();
        if(list.size() == 0)
            return false;
        boolean result = true;
        for(MyFile myFile:list){
            if(!setKeyWordListToMyFile(keyWordListSize,myFile.getFileName()))
                result = false;
        }
        return result;
    }


    public boolean setKeyWordListToMyFile(String fileName){
        return setKeyWordListToMyFile(Integer.MAX_VALUE,fileName);
    }


    public boolean setKeyWordListToMyFile(int keyWordListSize,String fileName){
        int filesNum = dir.getFileCount();
        List<MyFile> myFiles = dir.getFileList();
        if(dir.isFileExist(fileName)){
            MyFile myFile = dir.getFileByName(fileName);
            Map<String,Float> myFileWord2Rate = myFile.getWord2Rate();
            List<WordCount<Float>> keyWordList = new ArrayList<>();
            for(Map.Entry<String,Float> entry:myFileWord2Rate.entrySet()){
                String word = entry.getKey();
                float tf = entry.getValue();
                int existInOtherFileNum = 0;
                for(MyFile listItemFile:myFiles){
                    Map<String,Float> listItemFileWord2Rate = listItemFile.getWord2Rate();
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
            System.out.println(realKeyWordList);
            myFile.setKeyWord(realKeyWordList);
            return true;
        }
        return false;
    }

}
