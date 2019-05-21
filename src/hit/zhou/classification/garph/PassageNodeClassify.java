package hit.zhou.classification.garph;

import com.alibaba.fastjson.JSON;
import hit.zhou.graph.basic.PassageNode;
import hit.zhou.graph.basic.nlp.Sentence;
import hit.zhou.graph.basic.nlp.Word;
import hit.zhou.graph.tools.FileUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PassageNodeClassify extends PassageNode {
    public PassageNodeClassify(String path) {
        super(path);
    }

    @Override
    protected void countEntryExecutor(String countEntryPath) throws IOException {
        String nlpFilePath = getNlpFilePath();
        if(nlpFilePath == null){
            throw new IllegalArgumentException();
        }
        String nlpJsonArrayString = FileUtil.readString(nlpFilePath);
        List<Sentence> sentenceList = JSON.parseArray(nlpJsonArrayString,Sentence.class);
        Map<String,Float> wordStringCountMap = new HashMap<>();
        for(Sentence sentence:sentenceList){
            List<Word> wordList = sentence.getWordsList();
            for(Word word:wordList){
                if(word.getPos().matches("n[s|h|i|l|z]*")){
                    if(wordStringCountMap.containsKey(word.getWordString())){
                        float oldNum = wordStringCountMap.get(word.getWordString());
                        wordStringCountMap.put(word.getWordString(),oldNum + 1);
                    }
                    else{
                        wordStringCountMap.put(word.getWordString(),1f);
                    }
                }
            }
        }
        String wordStringCountMapJsonString = JSON.toJSONString(wordStringCountMap);
        FileUtil.save(countEntryPath,wordStringCountMapJsonString.getBytes(),false);
    }

}
