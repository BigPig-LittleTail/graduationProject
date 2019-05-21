package hit.zhou.graph.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import hit.zhou.graph.basic.PassageNode;
import hit.zhou.graph.tools.FileUtil;
import hit.zhou.graph.tools.TFIDF;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class KeyWordExecutorHelper {
    public static void transNodeList2SetList(List<PassageNode> allNodeList, List<Set<String>> headSetList, List<Set<String>> tailSetList) throws IOException {
        for(PassageNode node:allNodeList){
            String entryCountFilePath = node.getEntryCountFilePath();
            String entryCountJsonMapString = FileUtil.readString(entryCountFilePath);
            Map<String, Map<String,Float>> nodeEntryCount = JSON.parseObject(
                    entryCountJsonMapString,new TypeReference<Map<String,Map<String,Float>>>(){});
            Map<String,Float> head2Count = nodeEntryCount.get(PassageNode.HEAD_STRING);
            Map<String,Float> tail2Count = nodeEntryCount.get(PassageNode.TAIL_STRING);

            Set<String> nodeHeadSet = head2Count.keySet();
            Set<String> nodeTailSet = tail2Count.keySet();

            headSetList.add(nodeHeadSet);
            tailSetList.add(nodeTailSet);
        }
    }

    public static void keyWord(List<PassageNode> inputNodeList,List<PassageNode> allNodeList,boolean isReKeyWord) throws IOException{
        List<Set<String>> headSetList = new ArrayList<>();
        List<Set<String>> tailSetList = new ArrayList<>();
        transNodeList2SetList(allNodeList,headSetList,tailSetList);

        for(PassageNode inputNode:inputNodeList){
            keyWord(headSetList, tailSetList, inputNode,isReKeyWord);
        }
    }

    public static void keyWord(List<Set<String>> headSetList, List<Set<String>> tailSetList, PassageNode inputNode,boolean isReKeyWord) throws IOException {
        String keyWordFilePath = inputNode.getKeyWordFilePath();
        File file = new File(keyWordFilePath);
        if(!file.exists() || isReKeyWord){
            String entryCountFilePath = inputNode.getEntryCountFilePath();
            String entryCountJsonMapString = FileUtil.readString(entryCountFilePath);
            Map<String, Map<String,Float>> nodeEntryCount = JSON.parseObject(
                    entryCountJsonMapString,new TypeReference<Map<String,Map<String,Float>>>(){});
            Map<String,Float> head2Count = nodeEntryCount.get(PassageNode.HEAD_STRING);
            Map<String,Float> tail2Count = nodeEntryCount.get(PassageNode.TAIL_STRING);

            Map<String,Float> head2Tfidf = TFIDF.tfidf(head2Count,headSetList);
            Map<String,Float> tail2Tfidf = TFIDF.tfidf(tail2Count,tailSetList);


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
            FileUtil.save(keyWordFilePath,keyWordFileJsonString.getBytes(),false);
        }
    }

}
