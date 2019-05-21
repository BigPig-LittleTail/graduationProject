package hit.zhou.graph.basic.nlp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Sentence implements Serializable {
    private List<Word> wordsList;
    public Sentence(){
        wordsList = new ArrayList<>();
    }

    public void buildWordList(List<String> wordStringList, List<String> posList,List<Integer> parentList,List<String> deprelList){
        int length = wordStringList.size();
        for(int i = 0;i < length;i++){
            String wordString = wordStringList.get(i);
            String pos = posList.get(i);
            int parent = parentList.get(i);
            String dp = deprelList.get(i);
            Word word = new Word(wordString,pos,parent,dp);
            wordsList.add(word);
        }
    }

    public List<Word> getWordsList() {
        return wordsList;
    }

}
