package hit.zhou.common.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFileTest{
    private String dirPath;
    private String fileName;
    private DirTest parent;
    private int totalCount;
    private Map<String,Integer> word2Count;
    private List<WordCount<Float>> keyWord;


    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public MyFileTest(DirTest parent, String dirPath, String fileName) {
        this.parent = parent;
        this.dirPath = dirPath;
        this.fileName = fileName;
        this.word2Count = new HashMap<>();
    }

    public Map<String, Integer> getWord2Count() {
        return word2Count;
    }

    public void setWord2Count(Map<String, Integer> word2Count) {
        this.word2Count = word2Count;
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

    public DirTest getParent() {
        return parent;
    }


}
