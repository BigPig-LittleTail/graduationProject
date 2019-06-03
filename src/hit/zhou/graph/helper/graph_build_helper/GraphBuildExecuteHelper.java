package hit.zhou.graph.helper.graph_build_helper;

import com.alibaba.fastjson.JSON;
import hit.zhou.graph.EntryType;
import hit.zhou.graph.basic.PassageNode;
import hit.zhou.graph.basic.rdf.MyEntry;
import hit.zhou.graph.basic.rdf.MyRDF;
import hit.zhou.graph.basic.rdf.MyRelation;
import hit.zhou.graph.tools.FileUtil;
import hit.zhou.graph.tools.kmeans.Cluster;
import hit.zhou.graph.tools.kmeans.Vector;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphBuildExecuteHelper {
    public static Map<String,ComplexEntry<EntryType>> MergeHeadAndTailClustersTest(Map<EntryType,Integer> entryTypeIntegerMap, List<Cluster<EntryType>> headClusters, List<Cluster<EntryType>> tailClusters){
        Map<String,ComplexEntry<EntryType>> resultMap = new HashMap<>();

        Map<String,ComplexEntry<EntryType>> wordStringMapComlpexEntry = new HashMap<>();
        for(Cluster<EntryType> cluster:headClusters){
            int typesSize = cluster.typeSize();
            int vectorsSize = cluster.vectorsSize();
            for(int i = 0;i < vectorsSize;i++){
                Vector<EntryType> vector = cluster.get(i);
                if(!wordStringMapComlpexEntry.containsKey(vector.getWordString())){
                    ComplexEntry<EntryType> complexEntry = new ComplexEntry<>(vector.getWordString(),entryTypeIntegerMap,vector.dimensionSize());
                    for(int j = 0;j < vector.dimensionSize();j++){
                        complexEntry.setHeadDataByIndex(vector.getDataByIndex(j),j);
                    }
                    for(int j = 0;j < typesSize;j++){
                        complexEntry.addType(cluster.getType(j));
                    }
                    wordStringMapComlpexEntry.put(vector.getWordString(),complexEntry);
                }
                else {
                    System.out.println("为什么会有重复的实体出现");
                }
            }
        }

        for(Cluster<EntryType> cluster:tailClusters){
            int typesSize = cluster.typeSize();
            int vectorsSize = cluster.vectorsSize();
            for(int i = 0;i < vectorsSize;i++){
                Vector<EntryType> vector = cluster.get(i);
                if(wordStringMapComlpexEntry.containsKey(vector.getWordString())) {
                    ComplexEntry<EntryType> complexEntry = wordStringMapComlpexEntry.get(vector.getWordString());
                    for(int j = 0;j < vector.dimensionSize();j++){
                        complexEntry.setTailDataByIndex(vector.getDataByIndex(j),j);
                    }
                    for(int j = 0;j < typesSize;j++){
                        if(complexEntry.containsType(cluster.getType(j)))
                            continue;
                        complexEntry.addType(cluster.getType(j));
                    }
                    resultMap.put(vector.getWordString(),complexEntry);
                }
            }
        }
        return resultMap;
    }


    public static Map<String,ComplexEntry<EntryType>> MergeHeadAndTailClusters(Map<EntryType,Integer> entryTypeIntegerMap, List<Cluster<EntryType>> headClusters, List<Cluster<EntryType>> tailClusters){
        Map<String,ComplexEntry<EntryType>> wordStringMapComlpexEntry = new HashMap<>();
        for(Cluster<EntryType> cluster:headClusters){
            int typesSize = cluster.typeSize();
            int vectorsSize = cluster.vectorsSize();
            for(int i = 0;i < vectorsSize;i++){
                Vector<EntryType> vector = cluster.get(i);
                if(!wordStringMapComlpexEntry.containsKey(vector.getWordString())){
                    ComplexEntry<EntryType> complexEntry = new ComplexEntry<>(vector.getWordString(),entryTypeIntegerMap,vector.dimensionSize());
                    for(int j = 0;j < vector.dimensionSize();j++){
                        complexEntry.setHeadDataByIndex(vector.getDataByIndex(j),j);
                    }
                    for(int j = 0;j < typesSize;j++){
                        complexEntry.addType(cluster.getType(j));
                    }
                    wordStringMapComlpexEntry.put(vector.getWordString(),complexEntry);
                }
                else {
                    System.out.println("为什么会有重复的实体出现");
                }
            }
        }

        for(Cluster<EntryType> cluster:tailClusters){
            int typesSize = cluster.typeSize();
            int vectorsSize = cluster.vectorsSize();
            for(int i = 0;i < vectorsSize;i++){
                Vector<EntryType> vector = cluster.get(i);
                if(!wordStringMapComlpexEntry.containsKey(vector.getWordString())){
                    ComplexEntry<EntryType> complexEntry = new ComplexEntry<>(vector.getWordString(),entryTypeIntegerMap,vector.dimensionSize());
                    for(int j = 0;j < vector.dimensionSize();j++){
                        complexEntry.setTailDataByIndex(vector.getDataByIndex(j),j);
                    }
                    for(int j = 0;j < typesSize;j++){
                        if(complexEntry.containsType(cluster.getType(j)))
                            continue;
                        complexEntry.addType(cluster.getType(j));
                    }
                    wordStringMapComlpexEntry.put(vector.getWordString(),complexEntry);
                }
                else {
                    ComplexEntry<EntryType> complexEntry = wordStringMapComlpexEntry.get(vector.getWordString());
                    for(int j = 0;j < vector.dimensionSize();j++){
                        complexEntry.setTailDataByIndex(vector.getDataByIndex(j),j);
                    }
                    for(int j = 0;j < typesSize;j++){
                        if(complexEntry.containsType(cluster.getType(j)))
                            continue;
                        complexEntry.addType(cluster.getType(j));
                    }
                }
            }
        }
        return wordStringMapComlpexEntry;
    }

    public static void graphBuild(Map<String,ComplexEntry<EntryType>> wordComplexEntryMap, List<PassageNode> passageNodes) throws IOException{
        complexEntryBuild(wordComplexEntryMap);
        for(PassageNode passageNode:passageNodes) {
            String rdfString = FileUtil.readString(passageNode.getRdfFilePath());
            List<MyRDF> passageNodeRdfList = JSON.parseArray(rdfString, MyRDF.class);
            for (MyRDF rdf : passageNodeRdfList) {
                MyEntry head = rdf.getHead();
                MyEntry tail = rdf.getTail();
                MyRelation relation = rdf.getRelation();
                if (wordComplexEntryMap.containsKey(head.getWordString()) && wordComplexEntryMap.containsKey(tail.getWordString())) {
                    ComplexEntry<EntryType> entryHead = wordComplexEntryMap.get(head.getWordString());
                    ComplexEntry<EntryType> entryTail = wordComplexEntryMap.get(tail.getWordString());
                    simpleEntryRelateComplexEntryAndSimpleEntry(entryHead,entryTail,head,tail,relation);
                }
            }

        }
    }

    private static void complexEntryBuild(Map<String,ComplexEntry<EntryType>> wordComplexEntryMap){
        for(Map.Entry<String,ComplexEntry<EntryType>> complexEntry:wordComplexEntryMap.entrySet()){
            Neo4jHelper.newInstance().createComplexNode(complexEntry.getValue());
        }
    }

    private static void simpleEntryRelateComplexEntryAndSimpleEntry(ComplexEntry<EntryType> complexEntryHead,ComplexEntry<EntryType> complexEntryTail,MyEntry simpleEntryHead,MyEntry simpleEntryTail,MyRelation simpleEntryRelation) throws IOException {
        Node headNode = Neo4jHelper.newInstance().relateSimpleEntryAndComplexEntry(complexEntryHead,simpleEntryHead, Direction.OUTGOING);
        Node tailNode = Neo4jHelper.newInstance().relateSimpleEntryAndComplexEntry(complexEntryTail,simpleEntryTail, Direction.INCOMING);
        EntryType type;
        if(simpleEntryHead.getType() != simpleEntryTail.getType()){
            throw new IllegalArgumentException();
        }
        else{
            type = simpleEntryHead.getType();
        }
        Neo4jHelper.newInstance().relateSimpleEntryAndSimpleEntry(headNode,tailNode,type,simpleEntryRelation);
    }


}
