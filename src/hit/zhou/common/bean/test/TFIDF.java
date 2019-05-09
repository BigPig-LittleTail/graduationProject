package hit.zhou.common.bean.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import hit.zhou.common.tools.FileUtil;

import java.io.IOException;
import java.util.*;

public class TFIDF {
    public static void transNodeList2SetList(List<PassageNode> allNodeList,List<Set<String>> headSetList,List<Set<String>> tailSetList) throws IOException{
        for(PassageNode node:allNodeList){
            String entryCountFilePath = node.getEntryCountFilePath();
            String entryCountJsonMapString = FileUtil.readString(entryCountFilePath);
            Map<String,Map<String,Float>> nodeEntryCount = JSON.parseObject(
                    entryCountJsonMapString,new TypeReference<Map<String,Map<String,Float>>>(){});
            Map<String,Float> head2Count = nodeEntryCount.get(PassageNode.HEAD_STRING);
            Map<String,Float> tail2Count = nodeEntryCount.get(PassageNode.TAIL_STRING);

            Set<String> nodeHeadSet = head2Count.keySet();
            Set<String> nodeTailSet = tail2Count.keySet();

            headSetList.add(nodeHeadSet);
            tailSetList.add(nodeTailSet);
        }
    }

    public static void keyWord(List<PassageNode> inputNodeList,List<PassageNode> allNodeList) throws IOException{
        List<Set<String>> headSetList = new ArrayList<>();
        List<Set<String>> tailSetList = new ArrayList<>();
        transNodeList2SetList(allNodeList,headSetList,tailSetList);

        for(PassageNode inputNode:inputNodeList){
            keyWord(headSetList, tailSetList, inputNode);
        }
    }

    public static void keyWord(List<Set<String>> headSetList, List<Set<String>> tailSetList, PassageNode inputNode) throws IOException {
        String entryCountFilePath = inputNode.getEntryCountFilePath();
        String entryCountJsonMapString = FileUtil.readString(entryCountFilePath);
        Map<String, Map<String,Float>> nodeEntryCount = JSON.parseObject(
                entryCountJsonMapString,new TypeReference<Map<String,Map<String,Float>>>(){});
        Map<String,Float> head2Count = nodeEntryCount.get(PassageNode.HEAD_STRING);
        Map<String,Float> tail2Count = nodeEntryCount.get(PassageNode.TAIL_STRING);

        Map<String,Float> head2Tfidf = tfidf(head2Count,headSetList);
        Map<String,Float> tail2Tfidf = tfidf(tail2Count,tailSetList);

        String keyWordFilePath = inputNode.getKeyWordFilePath();
        Comparator<Map.Entry<String,Float>> comparator = (o1, o2)-> {
                if(o1.getValue() > o2.getValue()){
                    return -1;
                }
                else if(o1.getValue() < o2.getValue()){
                    return 1;
                }
                else {
                    return 0;
                }
        };
        List<Map.Entry<String,Float>> headKeyWordList = new ArrayList<>(head2Tfidf.entrySet());
        List<Map.Entry<String,Float>> tailKeyWordList = new ArrayList<>(tail2Tfidf.entrySet());
        headKeyWordList.sort(comparator);
        tailKeyWordList.sort(comparator);
        Map<String,List<Map.Entry<String,Float>>> keyWordResult = new HashMap<>();
        keyWordResult.put(PassageNode.HEAD_STRING,headKeyWordList);
        keyWordResult.put(PassageNode.TAIL_STRING,tailKeyWordList);

        String keyWordFileJsonString = JSON.toJSONString(keyWordResult);
        FileUtil.saveTest(keyWordFilePath,keyWordFileJsonString.getBytes(),false);
    }


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
