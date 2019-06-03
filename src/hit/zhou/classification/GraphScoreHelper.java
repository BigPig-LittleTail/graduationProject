package hit.zhou.classification;

import com.alibaba.fastjson.JSON;
import hit.zhou.graph.EntryType;
import hit.zhou.graph.basic.PassageNode;
import hit.zhou.graph.basic.rdf.MyEntry;
import hit.zhou.graph.basic.rdf.MyRDF;
import hit.zhou.graph.helper.graph_build_helper.Neo4jHelper;
import hit.zhou.graph.tools.FileUtil;
import org.neo4j.graphdb.Direction;

import java.io.IOException;
import java.util.*;

public class GraphScoreHelper {
    public static Map<PassageNode,Map<EntryType,Float>> buildPassageNodeScoreMapByGraph(List<PassageNode> passageNodes,final Map<EntryType,Integer> typeIntegerMap)
    throws IOException{
        Map<PassageNode,Map<EntryType,Float>> resultMap = new HashMap<>();
        for(PassageNode passageNode:passageNodes){
            Map<EntryType,Float> graphScore = scoreByGraph(passageNode,typeIntegerMap);
            if(graphScore != null){
                resultMap.put(passageNode,graphScore);
            }
        }
        return resultMap;
    }

    private static Map<EntryType,Float> scoreByGraph(PassageNode passageNode,final Map<EntryType,Integer> typeIntegerMap) throws IOException {
        String rdfJsonArrayString = FileUtil.readString(passageNode.getRdfFilePath());
        List<MyRDF> rdfList = JSON.parseArray(rdfJsonArrayString,MyRDF.class);

        Map<EntryType,Float> score = new HashMap<>();
        for(MyRDF rdf:rdfList){
            MyEntry head = rdf.getHead();
            MyEntry tail = rdf.getTail();
            Map<EntryType,Float> headMap = new HashMap<>();
            Map<EntryType,Float> tailMap = new HashMap<>();
            Neo4jHelper.newInstance().scoreHeadOrTailEntry(head,typeIntegerMap, Direction.OUTGOING,headMap);
            Neo4jHelper.newInstance().scoreHeadOrTailEntry(tail,typeIntegerMap, Direction.INCOMING,tailMap);
            for(Map.Entry<EntryType,Float> entryHead:headMap.entrySet()){
                float sc;
                if(tailMap.containsKey(entryHead.getKey())){
                    sc = tailMap.get(entryHead.getKey()) + entryHead.getValue();

                }
                else{
                    sc = entryHead.getValue();
                }

                if(score.containsKey(entryHead.getKey())){
                    float old = score.get(entryHead.getKey());
                    score.put(entryHead.getKey(),sc+old);
                }
                else{
                    score.put(entryHead.getKey(),sc);
                }

            }

            for(Map.Entry<EntryType,Float> entryTail:tailMap.entrySet()){
                if(headMap.containsKey(entryTail.getKey())){
                    continue;
                }
                else{
                    float sc = entryTail.getValue();
                    if(score.containsKey(entryTail.getKey())){
                        float old = score.get(entryTail.getKey());
                        score.put(entryTail.getKey(),sc+old);
                    }
                    else{
                        score.put(entryTail.getKey(),sc);
                    }
                }
            }
        }
        float total = 0;
        for(Map.Entry<EntryType,Float> entry:score.entrySet()){
            total += entry.getValue();
        }
        if(total == 0){
            return null;
        }
        else{
            for(Map.Entry<EntryType,Float> entry:score.entrySet()){
                score.put(entry.getKey(),entry.getValue() / total);
            }
        }
//        List<Map.Entry<EntryType,Float>> result = new ArrayList<>(score.entrySet());
//        Comparator<Map.Entry<EntryType,Float>> comparator = (o1, o2) ->{
//            if(o1.getValue() > o2.getValue())
//                return -1;
//            else if(o1.getValue() < o2.getValue())
//                return 1;
//            else
//                return 0;
//        };
//        result.sort(comparator);
        return score;
    }
}
