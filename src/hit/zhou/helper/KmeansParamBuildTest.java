package hit.zhou.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import hit.zhou.EntryType;
import hit.zhou.basic.PassageNode;
import hit.zhou.tools.FileUtil;
import hit.zhou.tools.kmeans.VectorTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KmeansParamBuildTest {
    public static void passageNodes2Vectors(List<PassageNode> passageNodes,final Map<EntryType,Integer> typeIndexMap,
                                            Map<String,VectorTest<EntryType>> headVectors,Map<String,VectorTest<EntryType>> tailVectors) throws IOException {
        Map<EntryType,Float> headTypeNumberMap = new HashMap<>();
        Map<EntryType,Float> tailTypeNumberMap = new HashMap<>();

        for(PassageNode passageNode:passageNodes){
            String keyWordJsonString = FileUtil.readString(passageNode.getKeyWordFilePath());
            Map<String,List<Map.Entry<String,Float>>> keyWordMap = JSON.parseObject(
                    keyWordJsonString,new TypeReference<Map<String,List<Map.Entry<String,Float>>>>(){});
            List<Map.Entry<String,Float>> headKeyWordList = keyWordMap.get(PassageNode.HEAD_STRING);
            List<Map.Entry<String,Float>> tailKeyWordList = keyWordMap.get(PassageNode.TAIL_STRING);

            EntryType type = passageNode.getType();
            countNumber(headTypeNumberMap, headKeyWordList, type);
            countNumber(tailTypeNumberMap, tailKeyWordList, type);

            buildVectors(typeIndexMap, headVectors, headKeyWordList, type);
            buildVectors(typeIndexMap, tailVectors, tailKeyWordList, type);
        }

        for(EntryType type:typeIndexMap.keySet()){
            normal(headVectors, headTypeNumberMap, type);
            normal(tailVectors, tailTypeNumberMap, type);
        }
    }

    private static void normal(Map<String, VectorTest<EntryType>> vectors, Map<EntryType, Float> typeNumberMap, EntryType type) {
        for(Map.Entry<String,VectorTest<EntryType>> entry: vectors.entrySet()){
            float notNormal = entry.getValue().getDataByType(type);
            float totalNumber = typeNumberMap.get(type);
            entry.getValue().setDataByType(notNormal / totalNumber,type);
        }
    }

    private static void countNumber(Map<EntryType, Float> typeNumberMap, List<Map.Entry<String, Float>> keyWordList, EntryType type) {
        if(typeNumberMap.containsKey(type)){
            float number = typeNumberMap.get(type);
            typeNumberMap.put(type,number + keyWordList.size());
        }
        else{
            float initNumber = keyWordList.size();
            typeNumberMap.put(type,initNumber);
        }
    }

    private static void buildVectors(Map<EntryType,Integer> typeIntegerMap, Map<String, VectorTest<EntryType>> vectors, List<Map.Entry<String, Float>> keyWordList, EntryType type) {
        for(Map.Entry<String,Float> keyWord: keyWordList){
            if(vectors.containsKey(keyWord.getKey())){
                float tempData = vectors.get(keyWord.getKey()).getDataByType(type);
                Object o = keyWord.getValue();
                float data = Float.parseFloat(o.toString());
                vectors.get(keyWord.getKey()).setDataByType(tempData + data,type);
            }
            else{
                float[] vectorData = new float[typeIntegerMap.size()];
                VectorTest<EntryType> vector = new VectorTest<>(keyWord.getKey(),vectorData,typeIntegerMap);
                Object o = keyWord.getValue();
                float data = Float.parseFloat(o.toString());
                vector.setDataByType(data,type);
                vectors.put(keyWord.getKey(),vector);
            }
        }
    }

}
