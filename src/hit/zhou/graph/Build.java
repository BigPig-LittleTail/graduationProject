package hit.zhou.graph;

import hit.zhou.graph.basic.PassageNode;
import hit.zhou.graph.basic.PassageTree;
import hit.zhou.graph.helper.KmeansExecuteHelper;
import hit.zhou.graph.helper.graph_build_helper.ComplexEntry;
import hit.zhou.graph.helper.graph_build_helper.GraphBuildExecuteHelper;
import hit.zhou.graph.helper.graph_build_helper.Neo4jHelper;
import hit.zhou.graph.tools.LtpBaseOpLocal;
import hit.zhou.graph.tools.kmeans.Cluster;
import hit.zhou.graph.tools.kmeans.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Build {

    private static final String NLP_FILE_NAME = "nlp_result.txt";
    private static final String RDF_FILE_NAME = "rdf_result.txt";
    private static final String COUNT_ENTRY_FILE_NAME = "count_entry_result.txt";
    private static final String KEY_WORD_FILE_NAME = "key_word_result.txt";
    private static final Map<EntryType,Integer> typeIntegerMap = new HashMap<EntryType, Integer>(){{
        put(EntryType.体育,0);
        put(EntryType.娱乐,1);
        put(EntryType.家居,2);
        put(EntryType.彩票,3);
        put(EntryType.房产,4);
        put(EntryType.教育,5);
        put(EntryType.时尚,6);
        put(EntryType.时政,7);
        put(EntryType.星座,8);
        put(EntryType.游戏,9);
        put(EntryType.社会,10);
        put(EntryType.科技,11);
        put(EntryType.股票,12);
        put(EntryType.财经,13);
    }
    };
    private static final List<EntryType> typesLocation = new ArrayList<EntryType>(){{
        add(EntryType.体育);
        add(EntryType.娱乐);
        add(EntryType.家居);
        add(EntryType.彩票);
        add(EntryType.房产);
        add(EntryType.教育);
        add(EntryType.时尚);
        add(EntryType.时政);
        add(EntryType.星座);
        add(EntryType.游戏);
        add(EntryType.社会);
        add(EntryType.科技);
        add(EntryType.股票);
        add(EntryType.财经);
    }
    };


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        LtpBaseOpLocal ltpBaseOpLocal = new LtpBaseOpLocal("C:/Users/zhou/Desktop/3.4.0/ltp_data_v3.4.0/ltp_data_v3.4.0/");
        PassageTree passageTree = new PassageTree();
        passageTree.buildPassageTree("D:\\graduation\\graphSourse\\nlpSourse\\", false);
        passageTree.nlp(ltpBaseOpLocal, "D:\\graduation\\graphMidResult\\", NLP_FILE_NAME, false);
        passageTree.rdf("D:\\graduation\\graphMidResult\\", RDF_FILE_NAME, false);
        passageTree.countEntry("D:\\graduation\\graphMidResult\\", COUNT_ENTRY_FILE_NAME, false);
        passageTree.buildKeyWordFilePath("D:\\graduation\\graphMidResult\\", KEY_WORD_FILE_NAME);
        List<PassageNode> passageNodesLevel3 = passageTree.getPassageNodeListByLevel(3);


//        KeyWordExecutorHelper.keyWord(passageNodesLevel3,passageNodesLevel3,false);

        Map<String, Vector<EntryType>> headVectorsMap = new HashMap<>();
        Map<String, Vector<EntryType>> tailVectorsMap = new HashMap<>();
        List<Cluster<EntryType>> clustersHead = new ArrayList<>();
        List<Cluster<EntryType>> clustersTail = new ArrayList<>();

        clustersHead  = KmeansExecuteHelper.readClusters("D:\\graduation\\graphMidResult\\cluster\\clusterHead");
        clustersTail = KmeansExecuteHelper.readClusters("D:\\graduation\\graphMidResult\\cluster\\clusterTail");

        /**
         * 第一次聚类
         * */
//        KmeansExecuteHelper.passageNodes2Vectors(passageNodesLevel3,typeIntegerMap,headVectorsMap,tailVectorsMap);
//        KmeansExecuteHelper.initClusters(typesLocation,clustersHead);
//        KmeansExecuteHelper.initClusters(typesLocation,clustersTail);
//        Kmeans.kmeans(headVectorsMap,clustersHead);
//        Kmeans.kmeans(tailVectorsMap,clustersTail);
//        KmeansExecuteHelper.saveClusters(clustersHead,"D:\\graduation\\graphMidResult\\cluster\\clusterHead");
//        KmeansExecuteHelper.saveClusters(clustersTail,"D:\\graduation\\graphMidResult\\cluster\\clusterTail");


        /**
         * 实体抽取结果
         * ****/
//        System.out.println(clustersHead.size());
//        for(Cluster<EntryType> cluster:clustersHead){
////            cluster.sub(0.6f);
//            if(cluster.getVectors().size() > 500){
//                System.out.println();System.out.println();
//                for(EntryType entryType:cluster.getTypes()){
//                    System.out.print(entryType + ",");
//                }
//                System.out.println(cluster.getVectors().size());
//                for(int i = 0;i<10;i++){
//                    System.out.print(cluster.getVectors().get(i).getWordString() + ",");
//                }
//            }
//        }
//        System.out.println(); System.out.println(); System.out.println();
//
//        System.out.println(clustersTail.size());
//        for(Cluster<EntryType> cluster:clustersTail){
////            cluster.sub(0.6f);
//            if(cluster.getVectors().size() > 500){
//                System.out.println();System.out.println();
//                for(EntryType entryType:cluster.getTypes()){
//                    System.out.print(entryType + ",");
//                }
//                System.out.println(cluster.getVectors().size());
//                for(int i = 0;i<10;i++){
//                    System.out.print(cluster.getVectors().get(i).getWordString() + ",");
//                }
//            }
//
//        }



        for(Cluster<EntryType> cluster:clustersHead) {
            cluster.sub(0.3f);
        }

        for(Cluster<EntryType> cluster:clustersTail){
            cluster.sub(0.3f);
        }

        Map<String, ComplexEntry<EntryType>> word2ComplexEntry = GraphBuildExecuteHelper.MergeHeadAndTailClustersTest(typeIntegerMap,clustersHead,clustersTail);



        System.out.println(word2ComplexEntry.size());
        String count = Neo4jHelper.newInstance().coutRelationShip();
        System.out.println(count);

        //        GraphBuildExecuteHelper.graphBuild(word2ComplexEntry,passageNodesLevel3);


        return;

    }
}
