package hit.zhou;

import com.alibaba.fastjson.JSON;
import hit.zhou.basic.PassageNode;
import hit.zhou.basic.PassageTree;
import hit.zhou.helper.KmeansParamsBuildHelper;
import hit.zhou.tools.LtpBaseOpLocal;
import hit.zhou.basic.rdf.MyEntry;
import hit.zhou.basic.rdf.MyRDF;
import hit.zhou.basic.rdf.MyRelation;
import hit.zhou.tools.FileUtil;
import hit.zhou.tools.kmeans.Cluster;
import hit.zhou.tools.kmeans.KeyWordVector;
import hit.zhou.tools.kmeans.Kmeans;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Try {
    private static final String NLP_FILE_NAME = "nlp_result.txt";
    private static final String RDF_FILE_NAME = "rdf_result.txt";
    private static final String COUNT_ENTRY_FILE_NAME = "count_entry_result.txt";
    private static final String KEY_WORD_FILE_NAME = "key_word_result.txt";

    public static void main(String[] args) throws IOException,ExecutionException, InterruptedException{
        LtpBaseOpLocal ltpBaseOpLocal = new LtpBaseOpLocal("C:/Users/zhou/Desktop/3.4.0/ltp_data_v3.4.0/ltp_data_v3.4.0/","C:\\Users\\zhou\\Desktop\\words");
        PassageTree passageTree = new PassageTree();
        passageTree.buildPassageTree("C:/Users/zhou/Desktop/loyalChange/",false);
        passageTree.nlp(ltpBaseOpLocal,"C:/Users/zhou/Desktop/rdfTest/",NLP_FILE_NAME,false);
        passageTree.rdf("C:/Users/zhou/Desktop/rdfTTTTest/",RDF_FILE_NAME, true);
        passageTree.countEntry("C:/Users/zhou/Desktop/rdfTest/",COUNT_ENTRY_FILE_NAME,false);
        passageTree.buildKeyWordFilePath("C:/Users/zhou/Desktop/rdfTest/",KEY_WORD_FILE_NAME);
        List<PassageNode> passageNodesLevel3 = passageTree.getPassageNodeListByLevel(3);
//        KeyWordExecutorHelper.keyWord(passageNodesLevel3,passageNodesLevel3,false);

        List<List<Map.Entry<String,Float>>> headDimensionList = new ArrayList<>();
        List<List<Map.Entry<String,Float>>> tailDimensionList = new ArrayList<>();

        KmeansParamsBuildHelper.transPassageNodesList2allDimensionList(passageNodesLevel3,headDimensionList,tailDimensionList);

        float[] headCenter1 = {
          1,
          0,
        };

        float[] headCenter2 = {
                0,
                1,
        };

        float[] headCenter3 = {
                1,
                1,
        };

        List<EntryType> types1 = new ArrayList<>();
        types1.add(EntryType.法律);
        List<EntryType> types2 = new ArrayList<>();
        types2.add(EntryType.行政法规);
        List<EntryType> types3 = new ArrayList<>();
        types3.add(EntryType.法律);
        types3.add(EntryType.行政法规);


        List<Cluster> headClusters = new ArrayList<>();
        headClusters.add(new Cluster(headCenter1,types1));
        headClusters.add(new Cluster(headCenter2,types2));
        headClusters.add(new Cluster(headCenter3,types3));

        List<Cluster> tailClusters = new ArrayList<>();
        float[] tailCenter1 = {
                1,
                0,
        };

        float[] tailCenter2 = {
                0,
                1,
        };

        float[] tailCenter3 = {
                1,
                1,
        };
        tailClusters.add(new Cluster(tailCenter1,types1));
        tailClusters.add(new Cluster(tailCenter2,types2));
        tailClusters.add(new Cluster(tailCenter3,types3));



        Map<String, KeyWordVector> vectorsHead = KmeansParamsBuildHelper.transAllDimensionList2Vectors(headDimensionList);
        Kmeans.kmeans(vectorsHead,headClusters);

        Map<String, KeyWordVector> vectorsTail = KmeansParamsBuildHelper.transAllDimensionList2Vectors(tailDimensionList);
        Kmeans.kmeans(vectorsTail,tailClusters);

//        saveClusters("C:/Users/zhou/Desktop/cccclusterTest/",list);


//        GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();

//        GraphDatabaseService db= dbFactory.newEmbeddedDatabase(new File("D:\\neo4j\\neo4j-community-3.5.5\\data\\databases\\graph.db\\"));
//        db.createNode((EntryType[])types1.toArray());

//        GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
//        GraphDatabaseService db= dbFactory.newEmbeddedDatabase(new File("D:\\neo4j\\neo4j-community-3.5.5\\data\\databases\\graph.db\\"));
//        for(PassageNode passageNode:passageNodes){
//            String rdfJsonArrayString = FileUtil.readString(passageNode.getRdfFilePath());
//            List<MyRDF> rdfList = JSON.parseArray(rdfJsonArrayString,MyRDF.class);
//            for(MyRDF rdf:rdfList){
//                try (Transaction tx = db.beginTx()) {
//                    MyEntry head = rdf.getHead();
//                    MyEntry tail = rdf.getTail();
//                    MyRelation relation = rdf.getRelation();
//                    Node headNode = db.createNode(Typeee.法律);
//                    headNode.setProperty("word",head.getWordString());
//                    headNode.setProperty("feature",head.getFeature());
//                    Node tailNode = db.createNode(Typeee.法律);
//                    tailNode.setProperty("word",tail.getWordString());
//                    tailNode.setProperty("feature",tail.getFeature());
//                    Relationship relationship = headNode.createRelationshipTo(tailNode,Typeee.法律);
//                    relationship.setProperty("verb",relation.getVerb());
//                    relationship.setProperty("feature",relation.getFeature());
//
//                    tx.success();
//                }
//            }
//        }

//        passageTree.getPassageNode("C:\\Users\\zhou\\Desktop\\loyalChange\\法律\\中华人民共和国宪法.txt").nlp(ltpBaseOpLocal,"C:\\Users\\zhou\\Desktop\\zzz.txt",true);


        GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
        GraphDatabaseService db= dbFactory.newEmbeddedDatabase(new File("D:\\neo4j\\neo4j-community-3.5.5\\data\\databases\\graph.db\\"));

        // 这个String 到时候可以改成KeyWordVector，如果需要
        Map<EntryType,Map<String,Node>> EntryTypeHeadMap = new HashMap<>();
        Map<EntryType,Map<String,Node>> EntryTypeTailMap = new HashMap<>();

        for(Cluster cluster:headClusters){
            List<EntryType> types = cluster.getTypes();
            List<KeyWordVector> keyWordVectorList = cluster.getVectors().subList(0,10);
            for(KeyWordVector keyWordVector:keyWordVectorList){
                Node node;
                try (Transaction tx = db.beginTx()) {
                    node = db.createNode(types.toArray(new EntryType[types.size()]));
                    node.setProperty("word",keyWordVector.getWord());
                    tx.success();
                }
                for (EntryType entryType : types) {
                    if(!EntryTypeHeadMap.containsKey(entryType)){
                        Map<String,Node> nodeMap = new HashMap<>();
                        nodeMap.put(keyWordVector.getWord(),node);
                        EntryTypeHeadMap.put(entryType,nodeMap);
                    }
                    else {
                        EntryTypeHeadMap.get(entryType).put(keyWordVector.getWord(),node);
                    }
                }
            }
        }

        for(Cluster cluster:tailClusters){
            List<EntryType> types = cluster.getTypes();
            List<KeyWordVector> keyWordVectorList = cluster.getVectors().subList(0,10);
            for(KeyWordVector keyWordVector:keyWordVectorList){
                Node node;
                try (Transaction tx = db.beginTx()) {
                    node = db.createNode(types.toArray(new EntryType[types.size()]));
                    node.setProperty("word",keyWordVector.getWord());
                    tx.success();
                }
                for (EntryType entryType:types) {
                    if(!EntryTypeTailMap.containsKey(entryType)){
                        Map<String,Node> nodeMap = new HashMap<>();
                        nodeMap.put(keyWordVector.getWord(),node);
                        EntryTypeTailMap.put(entryType,nodeMap);
                    }
                    else {
                        EntryTypeTailMap.get(entryType).put(keyWordVector.getWord(),node);
                    }
                }
            }
        }

        passageNodesLevel3 = passageNodesLevel3.subList(0,10);
        for(PassageNode passageNode:passageNodesLevel3){
            String rdfString = FileUtil.readString(passageNode.getRdfFilePath());
            List<MyRDF> passageNodeRdfList = JSON.parseArray(rdfString,MyRDF.class);
            System.out.println(passageNode.getName());
            for(MyRDF rdf:passageNodeRdfList){
                MyEntry head = rdf.getHead();
                MyEntry tail = rdf.getTail();
                MyRelation relation = rdf.getRelation();
                Node headNode;
                Node tailNode;

                EntryType headType = head.getType();
                EntryType tailType = tail.getType();
                if(EntryTypeHeadMap.containsKey(headType) && EntryTypeTailMap.containsKey(tailType)){
                    if(EntryTypeHeadMap.get(headType).containsKey(head.getWordString()) &&
                            EntryTypeTailMap.get(tailType).containsKey(tail.getWordString())){
                        try (Transaction tx = db.beginTx()){
                            if(head.getFeature() == null || head.getFeature().equals("")){
                                headNode = EntryTypeHeadMap.get(headType).get(head.getWordString());
                            }
                            else{
                                Node topNode = EntryTypeHeadMap.get(headType).get(head.getWordString());
                                headNode = db.createNode(headType);
                                headNode.setProperty("word",head.getWordString());
                                headNode.setProperty("feature",head.getFeature());
                                Relationship relationshipBaoHan = topNode.createRelationshipTo(headNode,EntryType.包含);
                            }

                            if(tail.getFeature() == null || tail.getFeature().equals("")){
                                tailNode = EntryTypeTailMap.get(tailType).get(tail.getWordString());
                            }
                            else {
                                Node topNode = EntryTypeTailMap.get(tailType).get(tail.getWordString());
                                tailNode = db.createNode(tailType);
                                tailNode.setProperty("word",tail.getWordString());
                                tailNode.setProperty("feature",tail.getFeature());
                                Relationship relationshipBaoHan = topNode.createRelationshipTo(tailNode,EntryType.包含);
                            }

                            Relationship relationship = headNode.createRelationshipTo(tailNode,headType);
                            relationship.setProperty("verb",relation.getVerb());
                            relationship.setProperty("feature",relation.getFeature());
                            tx.success();
                        }
                    }
                }

            }
        }

        return;
    }

    private static void saveClusters(String saveDirPath,List<List<String>> clusters ){
        Date nowDate = new Date( );
        SimpleDateFormat format = new SimpleDateFormat ("yyyy-MM-dd-hhmmss");
        String saveRealDirPath = saveDirPath + format.format(nowDate) + "/";
        File file = new File(saveRealDirPath);
        if(!file.exists()){
            file.mkdirs();
        }
        for(int i = 0;i < clusters.size();i++){
            String jsonArray = JSON.toJSONString(clusters.get(i));
            String realPath = saveRealDirPath + "Cluster_"+ i + ".txt";
            FileUtil.save(realPath,jsonArray.getBytes(),false);
        }
    }



}
