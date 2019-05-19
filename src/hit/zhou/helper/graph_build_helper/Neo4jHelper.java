package hit.zhou.helper.graph_build_helper;

import com.alibaba.fastjson.JSON;
import hit.zhou.EntryType;
import hit.zhou.RelationType;
import hit.zhou.basic.rdf.MyEntry;
import hit.zhou.basic.rdf.MyRelation;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Neo4jHelper {
    private static Neo4jHelper instance = null;
    private static GraphDatabaseService dbService;
    private static final String FILE_PATH = "D:\\neo4j\\neo4j-community-3.5.5\\data\\databases\\graph.db\\";

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
                node.setProperty("word",entry.getWordString());
                node.setProperty("feature","");
                node.setProperty("headVector",headVectorString);
                node.setProperty("tailVector",tailVectorString);
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
                ResourceIterator<Node> iter = dbService.findNodes(type,"word",entry.getWordString(),"feature","");
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


    public Node relateSimpleEntryAndComplexEntry(ComplexEntry<EntryType> complexEntry, MyEntry simpleEntry){
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
                String key = type.name() + "count";
                for (Relationship relationship : complexNode.getRelationships(RelationType.包含)) {
                    Node mayExistNode = relationship.getEndNode();
                    String wordString = (String)mayExistNode.getProperty("word");
                    String feature = (String)mayExistNode.getProperty("feature");
                    if(wordString.equals(simpleEntry.getWordString()) && feature.equals(simpleEntry.getFeature())){
                        if(mayExistNode.hasLabel(type)){
                            int nowCount = (Integer) mayExistNode.getProperty(key);
                            mayExistNode.setProperty(key,nowCount + 1);
                        }
                        else{
                            mayExistNode.addLabel(type);
                            mayExistNode.setProperty(key,1);
                        }
                        findSimpleEntry = true;
                        simpleEntryNode = mayExistNode;
                        break;
                    }
                }
                if(!findSimpleEntry){
                    simpleEntryNode = dbService.createNode(type);
                    simpleEntryNode.setProperty("word",simpleEntry.getWordString());
                    simpleEntryNode.setProperty("feature",simpleEntry.getFeature());
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
                    String feature = (String) relationship.getProperty("feature");
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
                relationship.setProperty("feature",relation.getFeature());
                relationship.setProperty(key,1);
            }
            tx.success();
        }
    }

}
