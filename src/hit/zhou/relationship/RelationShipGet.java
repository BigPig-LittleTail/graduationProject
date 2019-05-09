package hit.zhou.relationship;

import com.alibaba.fastjson.JSON;
import hit.zhou.common.bean.PageForNlp;
import hit.zhou.common.bean.Relation;
import hit.zhou.common.bean.Sentence;
import hit.zhou.common.bean.Word;
import hit.zhou.common.tools.FileUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class RelationShipGet {
    private HashMap<Relation,Relation> relationAll;

    public RelationShipGet(){
        relationAll = new HashMap<>();
    }

    public HashMap<Relation, Relation> getRelationAll() {
        return relationAll;
    }

    public void relationshipGet(String head, String tail, PageForNlp pageForNlp) throws IOException{
        List<Sentence> sentencesList = readFromFile(pageForNlp.getNlpResultPath());
        for(Sentence sentence:sentencesList){
            relationshipGet(head,tail,sentence);
        }
    }

    private void printSentence(Sentence sentence){
        List<Word> list = sentence.getWordsList();
        StringBuilder outt = new StringBuilder();
        for(int i = 0;i < list.size();i++){
            Word word = list.get(i);
            String wordTemp = "(" + word.getWordString() + "," + (i+1) +")"+ word.getPos() + ",(" + word.getDp() +"," +
                    word.getParent() + ")|";
            outt.append(wordTemp);
        }
        System.out.println(outt.toString());
    }


    private void relationshipGet(String head,String tail,Sentence sentence){
        List<Word> wordsList = sentence.getWordsList();
        for(int i = 0;i < wordsList.size();i++){
            if(head.equals(wordsList.get(i).getWordString()) && wordsList.get(i).getPos().matches("n.*")){
                for(int j = wordsList.size() - 1;j > i;j--) {
                    if(tail.equals(wordsList.get(j).getWordString()) && wordsList.get(j).getPos().matches("n.*")){
                        printSentence(sentence);
                        int[] result = new int[2];
                        if(getRelationIndexAndDistacne(wordsList,i,j,result)){
                            int relatioinIndex = result[0];
                            int distacne = result[1];
                            Relation relation = new Relation(head,tail,wordsList.get(relatioinIndex).getWordString(),sentence,distacne);
                            if(relationAll.containsKey(relation)){
                                relationAll.get(relation).addSameNameRelation(relation);
                            }
                            else {
                                relationAll.put(relation,relation);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean getRelationIndexAndDistacne(List<Word> wordsList, int i, int j, int[] result) {
        int countI = countLinkLength(wordsList, i);
        int countJ = countLinkLength(wordsList, j);
        int indexFirst;
        int parentIndexFirst;
        int indexSecond;
        int parentIndexSecond;
        int step;
        boolean normal;
        if(countI > countJ){
            step = countI - countJ;
            indexFirst = i;
            indexSecond = j;
            normal = true;
        }
        else{
            step = countJ - countI;
            indexFirst = j;
            indexSecond = i;
            int temp = countJ;
            countJ = countI;
            countI = temp;
            normal = false;
        }
        int togetherLength = 1;
        parentIndexFirst = wordsList.get(indexFirst).getParent();
        parentIndexSecond = wordsList.get(indexSecond).getParent();

        if(wordsList.get(indexFirst).getDp().equals("ATT") && !wordsList.get(parentIndexFirst - 1).getPos().equals("r")){
            return false;
        }

        while (step > 0){
            indexFirst = parentIndexFirst - 1;
            parentIndexFirst = wordsList.get(indexFirst).getParent();
            step--;
            if(wordsList.get(indexFirst).getDp().equals("COO"))
                countI--;
            if(wordsList.get(indexFirst).getDp().equals("ATT") && !wordsList.get(parentIndexFirst - 1).getPos().equals("r")){
                return false;
            }
        }
        while(parentIndexFirst != parentIndexSecond){
            indexFirst = parentIndexFirst - 1;
            indexSecond = parentIndexSecond - 1;
            parentIndexFirst = wordsList.get(indexFirst).getParent();
            parentIndexSecond = wordsList.get(indexSecond).getParent();
            if(wordsList.get(indexFirst).getDp().equals("COO"))
                countI--;
            if(wordsList.get(indexSecond).getDp().equals("COO"))
                countJ++;
            togetherLength++;
        }
        if(((normal && wordsList.get(indexFirst).getDp().equals("SBV") && wordsList.get(indexSecond).getDp().equals("VOB")) ||
                (!normal && wordsList.get(indexSecond).getDp().equals("SBV") && wordsList.get(indexFirst).getDp().equals("VOB") ))
                && wordsList.get(parentIndexFirst - 1).getPos().equals("v")
                && togetherLength == 1){
            result[0] = parentIndexFirst - 1;
            result[1] = Math.abs(countI - countJ) + 2 * togetherLength;
            return true;
        }
        return false;
    }

    private int countLinkLength(List<Word> wordsLink, int index) {
        int count = 1;
        int nowIndex = index;
        int parentIndex =  wordsLink.get(nowIndex).getParent();
        while(parentIndex > 0){
            nowIndex = parentIndex - 1;
            parentIndex = wordsLink.get(nowIndex).getParent();
            count++;
        }
        return  count;
    }


    private List<Sentence> readFromFile(String filePath) throws IOException {
        String jsonArrayString = FileUtil.readString(filePath);
        return JSON.parseArray(jsonArrayString,Sentence.class);
    }

}
