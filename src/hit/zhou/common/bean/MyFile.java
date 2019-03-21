package hit.zhou.common.bean;

import java.util.List;
import java.util.Map;

public class MyFile {
    private String dirPath;
    private String fileName;
    private Map<String,Float> word2rate;
    private List<WordCount<Float>> keyWord;
    private Dir parent;

    public MyFile(Dir parent,String dirPath,String fileName){
        this.parent = parent;
        this.dirPath = dirPath;
        this.fileName = fileName;
    }


    public void setKeyWord(List<WordCount<Float>> keyWord) {
        this.keyWord = keyWord;
    }

    public String getDirPath() {
        return dirPath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPath(){
        return dirPath + fileName;
    }

    public List<WordCount<Float>> getKeyWord() {
        return keyWord;
    }

    public int getKeyWordListSize(){
        return keyWord.size();
    }

    public void setWord2rate(Map<String, Float> word2rate) {
        this.word2rate = word2rate;
    }

    public Map<String, Float> getWord2rate() {
        return word2rate;
    }

    public Dir getParent() {
        return parent;
    }
}
