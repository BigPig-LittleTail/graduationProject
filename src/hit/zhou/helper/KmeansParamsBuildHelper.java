package hit.zhou.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import hit.zhou.basic.PassageNode;
import hit.zhou.tools.FileUtil;
import hit.zhou.tools.kmeans.KeyWordVector;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KmeansParamsBuildHelper {
    public static void transPassageNodesList2allDimensionList(List<PassageNode> passageNodesList, List<List<Map.Entry<String,Float>>> headDimensionList, List<List<Map.Entry<String,Float>>> tailDimensionList) throws IOException {
        Map<String,List<Map.Entry<String,Float>>> parentMapheadList = new HashMap<>();
        Map<String,List<Map.Entry<String,Float>>> parentMaptailList = new HashMap<>();
        for(PassageNode passageNode:passageNodesList){
            String keyWordJsonString = FileUtil.readString(passageNode.getKeyWordFilePath());
            Map<String,List<Map.Entry<String,Float>>> keyWordMap = JSON.parseObject(
                    keyWordJsonString,new TypeReference<Map<String,List<Map.Entry<String,Float>>>>(){});
            List<Map.Entry<String,Float>> headKeyWordList = keyWordMap.get(PassageNode.HEAD_STRING);
            List<Map.Entry<String,Float>> tailKeyWordList = keyWordMap.get(PassageNode.TAIL_STRING);
            if(parentMapheadList.containsKey(passageNode.getParent())){
                parentMapheadList.get(passageNode.getParent()).addAll(headKeyWordList);
            }
            else{
                parentMapheadList.put(passageNode.getParent(),headKeyWordList);
            }

            if(parentMaptailList.containsKey(passageNode.getParent())){
                parentMaptailList.get(passageNode.getParent()).addAll(tailKeyWordList);
            }
            else {
                parentMaptailList.put(passageNode.getParent(),tailKeyWordList);
            }
        }
        for(Map.Entry<String,List<Map.Entry<String,Float>>> dimensionListEntry:parentMapheadList.entrySet()){
            headDimensionList.add(dimensionListEntry.getValue());
        }

        for(Map.Entry<String,List<Map.Entry<String,Float>>> dimensionListEntry:parentMaptailList.entrySet()){
            tailDimensionList.add(dimensionListEntry.getValue());
        }
    }



    public static Map<String, KeyWordVector> transAllDimensionList2Vectors(List<List<Map.Entry<String,Float>>> allDimensionList){
        Map<String,KeyWordVector> vectors = new HashMap<>();
        int dimensionSize = allDimensionList.size();
        for(int i = 0;i<dimensionSize;i++){
            List<Map.Entry<String,Float>> dimensionList = allDimensionList.get(i);
            for(Map.Entry<String,Float> entry:dimensionList){
                if(vectors.containsKey(entry.getKey())){
                    float[] vector = vectors.get(entry.getKey()).getVector();
                    Object o = entry.getValue();
                    float x = Float.parseFloat(o.toString());
                    vector[i] = vector[i] + x;
                }
                else{
                    float[] vector = new float[dimensionSize];
                    Object o = entry.getValue();
                    float x = Float.parseFloat(o.toString());
                    vector[i] = x;
                    vectors.put(entry.getKey(),new KeyWordVector(entry.getKey(),vector));
                }
            }
        }
        for(Map.Entry<String,KeyWordVector> entry:vectors.entrySet()){
            float[] vector = entry.getValue().getVector();
            for(int i = 0;i<dimensionSize;i++){
                int normalSize = allDimensionList.get(i).size();
                vector[i] = vector[i] / normalSize;
            }
        }
        return vectors;
    }

}
