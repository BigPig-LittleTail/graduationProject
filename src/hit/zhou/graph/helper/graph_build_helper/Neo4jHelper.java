package hit.zhou.graph.helper.graph_build_helper;

import com.alibaba.fastjson.JSON;
import hit.zhou.graph.EntryType;
import hit.zhou.graph.RelationType;
import hit.zhou.graph.basic.rdf.MyEntry;
import hit.zhou.graph.basic.rdf.MyRelation;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neo4jHelper {
    private static Neo4jHelper instance = null;
    private static GraphDatabaseService dbService;
    private static final String FILE_PATH = "D:\\neo4j\\neo4j-community-3.5.5\\data\\databases\\graph.db\\";
    private static final String KEY_STRING_WORD = "word";
    private static final String KEY_STRING_FEATURE = "feature";
    private static final String KEY_STRING_HEAD_VECTOR = "headVector";
    private static final String KEY_STRING_TAIL_VECTOR = "tailVector";


    private Neo4jHelper(){
        dbService = new GraphDatabaseFactory().newEmbeddedDatabase(new File(FILE_PATH));
        registerShutdownHook(dbService);
    }

    public static Neo4jHelper newInstance(){
        if(instance == null){
           synchronized (Neo4jHelper.class){
               if(instance == null){
                   instance = new Neo4jHelper();
               }
           }
        }
        return instance;
    }

    private static void registerShutdownHook(GraphDatabaseService dbService){
        Runtime.getRuntime().addShutdownHook(new Thread(dbService::shutdown));
    }


    public void createComplexNode(ComplexEntry<EntryType> entry){
        Node node = getComplexNode(entry);
        if(node == null){
            try(Transaction tx = dbService.beginTx()){
                EntryType[] types = new EntryType[entry.typesSize()];
                for(int i = 0;i<types.length;i++){
                    types[i] = entry.getType(i);
                }
                List<Float> headVector = new ArrayList<>();
                List<Float> tailVector = new ArrayList<>();
                int size = entry.getDimensionSize();
                for(int i = 0;i<size;i++){
                    headVector.add(entry.getHeadDataByIndex(i));
                    tailVector.add(entry.getTailDataByIndex(i));
                }
                String headVectorString = JSON.toJSONString(headVector);
                String tailVectorString  = JSON.toJSONString(tailVector);

                node = dbService.createNode(types);
                node.setProperty(KEY_STRING_WORD,entry.getWordString());
                node.setProperty(KEY_STRING_FEATURE,"");
                node.setProperty(KEY_STRING_HEAD_VECTOR,headVectorString);
                node.setProperty(KEY_STRING_TAIL_VECTOR,tailVectorString);
                tx.success();
            }
        }
    }


    private Node getComplexNode(ComplexEntry<EntryType> entry){
        Node node = null;
        long nodeId = -1;
        for(int i = 0;i < entry.typesSize();i++){
            EntryType type = entry.getType(i);
            try(Transaction tx = dbService.beginTx()){
                ResourceIterator<Node> iter = dbService.findNodes(type,KEY_STRING_WORD,entry.getWordString(),KEY_STRING_FEATURE,"");
                if(iter.hasNext()){
                    Node tempNode = iter.next();
                    long tempNodeId = tempNode.getId();
                    System.out.println(tempNode.getId());
                    if(tempNodeId != nodeId){
                        if(nodeId == -1){
                            node = tempNode;
                            nodeId = tempNodeId;
                        }
                        else {
                            throw new IllegalArgumentException("为什么有多个？");
                        }
                    }
                }
                if(iter.hasNext()){
                    throw new IllegalArgumentException("为什么有多个？");
                }
                tx.success();
            }
        }
        return node;
    }


    public Node relateSimpleEntryAndComplexEntry(ComplexEntry<EntryType> complexEntry, MyEntry simpleEntry,Direction direction){
        Node complexNode = getComplexNode(complexEntry);
        if(complexNode == null){
            throw new IllegalArgumentException("没有？");
        }
        EntryType type = simpleEntry.getType();
        
        Node simpleEntryNode = null;
        try(Transaction tx = dbService.beginTx()){
            if(simpleEntry.getFeature().equals("")){
                simpleEntryNode = complexNode;
            }
            else{
                boolean findSimpleEntry = false;
                String key = type.name() + direction.name();
                for (Relationship relationship : complexNode.getRelationships(RelationType.包含)) {
                    Node mayExistNode = relationship.getEndNode();
                    String wordString = (String)mayExistNode.getProperty(KEY_STRING_WORD);
                    String feature = (String)mayExistNode.getProperty(KEY_STRING_FEATURE);
                    if(wordString.equals(simpleEntry.getWordString()) && feature.equals(simpleEntry.getFeature())){
                        if(!mayExistNode.hasLabel(type)){
                            mayExistNode.addLabel(type);
                        }
                        if(mayExistNode.hasProperty(key)){
                            int nowCount = (Integer) mayExistNode.getProperty(key);
                            mayExistNode.setProperty(key,nowCount + 1);
                        }
                        else{
                            mayExistNode.setProperty(key,1);
                        }

                        findSimpleEntry = true;
                        simpleEntryNode = mayExistNode;
                        break;
                    }
                }
                if(!findSimpleEntry){
                    simpleEntryNode = dbService.createNode(type);
                    simpleEntryNode.setProperty(KEY_STRING_WORD,simpleEntry.getWordString());
                    simpleEntryNode.setProperty(KEY_STRING_FEATURE,simpleEntry.getFeature());
                    simpleEntryNode.setProperty(key,1);
                    complexNode.createRelationshipTo(simpleEntryNode,RelationType.包含);
                }
            }
            tx.success();
        }
        return simpleEntryNode;
    }

    public void relateSimpleEntryAndSimpleEntry(Node headNode, Node tailNode,EntryType nodeType,MyRelation relation){
        boolean hasTheRelation = false;
        String key = nodeType.name() + "count";
        try(Transaction tx = dbService.beginTx()){
            for(Relationship relationship:headNode.getRelationships(Direction.OUTGOING)){
                if(relationship.isType(RelationType.包含)){
                    continue;
                }
                long mayTailNodeId = relationship.getEndNodeId();
                if(mayTailNodeId == tailNode.getId()){
                    RelationshipType relationshipType = relationship.getType();
                    String feature = (String) relationship.getProperty(KEY_STRING_FEATURE);
                    if(relation.getFeature().equals(feature) && relation.getVerb().equals(relationshipType.name())){
                        if(relationship.hasProperty(key)){
                            int number = (Integer) relationship.getProperty(key);
                            relationship.setProperty(key,number + 1);
                        }
                        else{
                            relationship.setProperty(key,1);
                        }
                        hasTheRelation = true;
                        break;
                    }
                }
            }
            if(!hasTheRelation ){
                String verb = relation.getVerb();
                RelationshipType relationshipType = () -> verb;
                Relationship relationship = headNode.createRelationshipTo(tailNode,relationshipType);
                relationship.setProperty(KEY_STRING_FEATURE,relation.getFeature());
                relationship.setProperty(key,1);
            }
            tx.success();
        }
    }


    public boolean scoreHeadOrTailEntry(MyEntry entry,final Map<EntryType,Integer> typeIndexMap,Direction direction,Map<EntryType,Float> scoreMap){
        Map<EntryType,Float> typeScoreHeadMap = new HashMap<>();
        Map<EntryType,Float> typeScoreTailMap = new HashMap<>();
        Node father = scoreFatherEntry(entry,typeIndexMap,typeScoreHeadMap,typeScoreTailMap);


        Map<EntryType,Float> fatherScoreMap;

        switch (direction){
            case OUTGOING:
                fatherScoreMap = typeScoreHeadMap;
                break;
            case INCOMING:
                fatherScoreMap = typeScoreTailMap;
                break;
            default:
                fatherScoreMap = new HashMap<>();
                break;
        }

        if(father == null){
            return false;
        }
        else{
//            System.out.println(father.getId());
            if(entry.getFeature().equals("")){
                scoreMap.putAll(fatherScoreMap);
            }
            else{
                Node node = null;
                float maxSimilar = 0;

                try(Transaction tx = dbService.beginTx()) {
                    for(Relationship relationship:father.getRelationships(RelationType.包含)){
                        Node mayHeadNode = relationship.getEndNode();
                        String mayNodeFeature = (String) mayHeadNode.getProperty(KEY_STRING_FEATURE);
                        float maySimilar = ((float) entry.getFeature().length()) / ((float) mayNodeFeature.length());
                        if(mayNodeFeature.contains(entry.getFeature()) && maySimilar > maxSimilar){
                            node = mayHeadNode;
                            maxSimilar = maySimilar;
                        }
                    }
                    if(node == null){
                        scoreMap.putAll(fatherScoreMap);
                        return true;
                    }
                    else{
                        for(Label label:node.getLabels()){
                            EntryType type = EntryType.valueOf(label.name());
                            String key = type.name() + direction.name();
                            if(node.hasProperty(key)){
                                int count = (Integer) node.getProperty(key);
                                if(fatherScoreMap.containsKey(type)){
                                    float scoreOld = fatherScoreMap.get(type);
                                    scoreMap.put(type,scoreOld * count);
                                }
                            }
                        }
                    }
                    tx.success();
                }
            }
            return true;
        }
    }



    public void scoreSingleEntry(MyEntry entry,final Map<EntryType,Integer> typeIndexMap,Map<EntryType,Float> scoreMap){
        Map<EntryType,Float> typeScoreHeadMap = new HashMap<>();
        Map<EntryType,Float> typeScoreTailMap = new HashMap<>();
        scoreFatherEntry(entry,typeIndexMap,typeScoreHeadMap,typeScoreTailMap);
        if(entry.getFeature().equals("")){
            for(EntryType type:typeScoreHeadMap.keySet()){
                scoreMap.put(type,typeScoreHeadMap.get(type) + typeScoreTailMap.get(type));
            }
        }
    }


    private Node scoreFatherEntry(MyEntry entry,final Map<EntryType,Integer> typeIndexMap,Map<EntryType,Float> typeScoreHeadMap,
                                 Map<EntryType,Float> typeScoreTailMap){
        String cypherFather = "match (n {" + KEY_STRING_WORD + ":\""+entry.getWordString()+"\"," + KEY_STRING_FEATURE + ":\"\"}) return n";
        Node father = null;
        try(Transaction tx = dbService.beginTx()) {
            Result searchFatherResult = dbService.execute(cypherFather);
            ResourceIterator<Node> iteratorFather = searchFatherResult.columnAs("n");
            if(iteratorFather.hasNext()){
                father = iteratorFather.next();
                if(iteratorFather.hasNext()){
                    throw new IllegalArgumentException("为啥还有重复的");
                }
                String headVectorString = (String) father.getProperty(KEY_STRING_HEAD_VECTOR);
                String tailVectorString = (String) father.getProperty(KEY_STRING_TAIL_VECTOR);
                List<Float> headVector = JSON.parseArray(headVectorString,Float.class);
                List<Float> tailVector = JSON.parseArray(tailVectorString,Float.class);
                for(Label label:father.getLabels()){
                    EntryType type = EntryType.valueOf(label.name());
                    int typeIndex = typeIndexMap.get(type);
                    typeScoreHeadMap.put(type,headVector.get(typeIndex));
                    typeScoreTailMap.put(type,tailVector.get(typeIndex));
                }
            }
            tx.success();
        }
        return father;
    }

}
