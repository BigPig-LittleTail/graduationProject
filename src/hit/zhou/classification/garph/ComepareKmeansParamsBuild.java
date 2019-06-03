package hit.zhou.classification.garph;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import hit.zhou.graph.EntryType;
import hit.zhou.graph.basic.PassageNode;
import hit.zhou.graph.helper.KmeansExecuteHelper;
import hit.zhou.graph.tools.FileUtil;
import hit.zhou.graph.tools.kmeans.Vector;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComepareKmeansParamsBuild extends KmeansExecuteHelper {

    public static void passageNodes2Vectors(List<PassageNode> passageNodes, final Map<EntryType,Integer> typeIndexMap,
                                            Map<String, Vector<EntryType>> vectorMap) throws IOException {
        Map<EntryType,Float> headTypeNumberMap = new HashMap<>();

        for(PassageNode passageNode:passageNodes){
            String keyWordJsonString = FileUtil.readString(passageNode.getKeyWordFilePath());
            List<Map.Entry<String,Float>> keyWordList = JSON.parseObject(
                    keyWordJsonString,new TypeReference<List<Map.Entry<String,Float>>>(){});

            EntryType type = passageNode.getType();
            countNumber(headTypeNumberMap, keyWordList, type);

            buildVectors(typeIndexMap, vectorMap, keyWordList, type);
        }

        for(EntryType type:typeIndexMap.keySet()){
            normal(vectorMap, headTypeNumberMap, type);
        }
    }



}
