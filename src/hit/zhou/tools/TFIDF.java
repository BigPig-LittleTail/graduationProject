package hit.zhou.tools;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TFIDF {
    public static Map<String,Float> tfidf(Map<String,Float> nodeWordString2Count,List<Set<String>> allSetList) throws IOException {
        Map<String,Float> nodeWordString2Tfidf = new HashMap<>();
        float totalCount = caculateWordStringTotalCount(nodeWordString2Count);
        for(Map.Entry<String,Float> entry:nodeWordString2Count.entrySet()){
            float tf = entry.getValue() / totalCount;
            float idf = caculateWordStringIdf(entry.getKey(),allSetList);
            nodeWordString2Tfidf.put(entry.getKey(),tf * idf);
        }
        return nodeWordString2Tfidf;
    }

    private static float caculateWordStringIdf(String wordString,List<Set<String>> allSetList){
        float totalNodeNum = allSetList.size();
        float wordStringInOtherFile = 0;
        for(Set<String> hashSet:allSetList){
            if(hashSet.contains(wordString)){
                wordStringInOtherFile++;
            }
        }
        return (float)(Math.log(((double) totalNodeNum + 1)/((double) wordStringInOtherFile + 1)) + 1 );
    }


    private static float caculateWordStringTotalCount(Map<String,Float> nodeWordString2Count){
        float totalCount = 0;
        for(Map.Entry<String,Float> entry:nodeWordString2Count.entrySet()){
            totalCount += entry.getValue();
        }
        return totalCount;
    }

}
