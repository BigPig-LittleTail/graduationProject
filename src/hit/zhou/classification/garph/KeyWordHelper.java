package hit.zhou.classification.garph;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import hit.zhou.graph.basic.PassageNode;
import hit.zhou.graph.tools.FileUtil;
import hit.zhou.graph.tools.TFIDF;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class KeyWordHelper {
    public static void transNodeList2SetList(List<PassageNode> allNodeList, List<Set<String>> setList) throws IOException {
        for(PassageNode node:allNodeList){
            String entryCountFilePath = node.getEntryCountFilePath();
            String entryCountJsonMapString = FileUtil.readString(entryCountFilePath);
            Map<String,Float> nodeEntryCount = JSON.parseObject(
                    entryCountJsonMapString,new TypeReference<Map<String,Float>>(){});
            Set<String> nodeHeadSet = nodeEntryCount.keySet();
            setList.add(nodeHeadSet);
        }
    }


    public static void keyWord(List<PassageNode> inputNodeList,List<PassageNode> allNodeList,boolean isReKeyWord) throws IOException{
        List<Set<String>> setList = new ArrayList<>();
        transNodeList2SetList(allNodeList,setList);

        for(PassageNode inputNode:inputNodeList){
            keyWord(setList,inputNode,isReKeyWord);
        }
    }

    public static void keyWord(List<Set<String>> setList, PassageNode inputNode,boolean isReKeyWord) throws IOException {
        String keyWordFilePath = inputNode.getKeyWordFilePath();
        File file = new File(keyWordFilePath);
        if(!file.exists() || isReKeyWord){
            String entryCountFilePath = inputNode.getEntryCountFilePath();
            String entryCountJsonMapString = FileUtil.readString(entryCountFilePath);
            Map<String,Float> nodeEntryCount = JSON.parseObject(
                    entryCountJsonMapString,new TypeReference<Map<String,Float>>(){});

            Map<String,Float> tfidfMap = TFIDF.tfidf(nodeEntryCount,setList);

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
            List<Map.Entry<String,Float>> keyWordList = new ArrayList<>(tfidfMap.entrySet());
            keyWordList.sort(comparator);

            String keyWordFileJsonString = JSON.toJSONString(keyWordList);
            FileUtil.save(keyWordFilePath,keyWordFileJsonString.getBytes(),false);
        }
    }


}
