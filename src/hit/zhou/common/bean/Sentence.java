package hit.zhou.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Sentence implements Serializable {
    private List<WordBean> wordsList;
    public Sentence(){
        wordsList = new ArrayList<>();
    }

    public void buildWordBean(List<String> wordStringList, List<String> posList,List<Integer> parentList,List<String> deprelList){
        int length = wordStringList.size();
        for(int i = 0;i < length;i++){
            String wordString = wordStringList.get(i);
            String pos = posList.get(i);
            int parent = parentList.get(i);
            String dp = deprelList.get(i);
            WordBean wordBean = new WordBean(wordString,pos,parent,dp);
            wordsList.add(wordBean);
        }
    }

    public List<WordBean> getWordsList() {
        return wordsList;
    }

    public void setWordsList(List<WordBean> wordsList) {
        this.wordsList = wordsList;
    }
}
