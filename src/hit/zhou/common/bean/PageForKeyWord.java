package hit.zhou.common.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageForKeyWord extends Page {
    private int totalCount;
    private Map<String,Integer> word2Count;
    private List<WordCount<Float>> keyWord;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public PageForKeyWord(Dir parent, String dirPath, String fileName) {
        super(parent,dirPath,fileName);
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

    public List<WordCount<Float>> getKeyWord() {
        return keyWord;
    }

    public int getKeyWordListSize(){
        return keyWord.size();
    }


}
