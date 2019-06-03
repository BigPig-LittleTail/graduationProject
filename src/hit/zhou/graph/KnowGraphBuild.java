package hit.zhou.graph;

import hit.zhou.graph.basic.PassageNode;
import hit.zhou.graph.basic.PassageTree;
import hit.zhou.graph.helper.KeyWordExecutorHelper;
import hit.zhou.graph.helper.KmeansExecuteHelper;
import hit.zhou.graph.helper.graph_build_helper.ComplexEntry;
import hit.zhou.graph.helper.graph_build_helper.GraphBuildExecuteHelper;
import hit.zhou.graph.tools.LtpBaseOpLocal;
import hit.zhou.graph.tools.kmeans.Cluster;
import hit.zhou.graph.tools.kmeans.Kmeans;
import hit.zhou.graph.tools.kmeans.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class KnowGraphBuild {
    private static final String NLP_FILE_NAME = "nlp_result.txt";
    private static final String RDF_FILE_NAME = "rdf_result.txt";
    private static final String COUNT_ENTRY_FILE_NAME = "count_entry_result.txt";
    private static final String KEY_WORD_FILE_NAME = "key_word_result.txt";
    private static final Map<EntryType,Integer> typeIntegerMap = new HashMap<EntryType, Integer>(){{
        put(EntryType.法律,0);
        put(EntryType.行政法规,1);
        put(EntryType.部门规章,2);
        put(EntryType.地方政府规章,3);
    }
    };

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        LtpBaseOpLocal ltpBaseOpLocal = new LtpBaseOpLocal("C:/Users/zhou/Desktop/3.4.0/ltp_data_v3.4.0/ltp_data_v3.4.0/", "C:\\Users\\zhou\\Desktop\\words");
        PassageTree passageTree = new PassageTree();
        passageTree.buildPassageTree("C:/Users/zhou/Desktop/loyalChange/", false);
        passageTree.nlp(ltpBaseOpLocal, "C:/Users/zhou/Desktop/myFinal/", NLP_FILE_NAME, false);
        passageTree.rdf("C:/Users/zhou/Desktop/myFinal/", RDF_FILE_NAME, false);
        passageTree.countEntry("C:/Users/zhou/Desktop/myFinal/", COUNT_ENTRY_FILE_NAME, false);
        passageTree.buildKeyWordFilePath("C:/Users/zhou/Desktop/myFinal/", KEY_WORD_FILE_NAME);
        List<PassageNode> passageNodesLevel3 = passageTree.getPassageNodeListByLevel(3);

        KeyWordExecutorHelper.keyWord(passageNodesLevel3,passageNodesLevel3,false);

        Map<String, Vector<EntryType>> headVectorsMap = new HashMap<>();
        Map<String, Vector<EntryType>> tailVectorsMap = new HashMap<>();


        KmeansExecuteHelper.passageNodes2Vectors(passageNodesLevel3,typeIntegerMap,headVectorsMap,tailVectorsMap);

        List<Cluster<EntryType>> clustersHead = new ArrayList<>();
        List<Cluster<EntryType>> clustersTail = new ArrayList<>();

        float[] center1 = new float[]{
                1,0,0,0
        };
        float[] center2 = new float[]{
                0,1,0,0
        };
        float[] center3 = new float[]{
                0,0,1,0
        };
        float[] center4 = new float[]{
                0,0,0,1
        };
        
        float[] center5 = new float[]{
                1,1,0,0
        };

        float[] center6 = new float[]{
                1,0,1,0
        };
        float[] center7 = new float[]{
                1,0,0,1
        };
        float[] center8 = new float[]{
                0,1,1,0
        };
        float[] center9 = new float[]{
                0,1,0,1
        };

        float[] center10 = new float[]{
                0,0,1,1
        };

        float[] center11 = new float[]{
                1,1,1,0
        };

        float[] center12 = new float[]{
                1,1,0,1
        };

        float[] center13 = new float[]{
                1,0,1,1
        };

        float[] center14 = new float[]{
                0,1,1,1
        };

        float[] center15 = new float[]{
                1,1,1,1
        };
        

        List<EntryType> types1 = new ArrayList<EntryType>(){
            {
                add(EntryType.法律);
            }
        };

        List<EntryType> types2 = new ArrayList<EntryType>(){
            {
                add(EntryType.行政法规);
            }
        };

        List<EntryType> types3 = new ArrayList<EntryType>(){
            {
                add(EntryType.部门规章);
            }
        };

        List<EntryType> types4 = new ArrayList<EntryType>(){
            {
                add(EntryType.地方政府规章);
            }
        };


        List<EntryType> types5 = new ArrayList<EntryType>(){
            {
                add(EntryType.法律);
                add(EntryType.行政法规);
            }
        };

        List<EntryType> types6 = new ArrayList<EntryType>(){
            {
                add(EntryType.法律);
                add(EntryType.部门规章);
            }
        };

        List<EntryType> types7 = new ArrayList<EntryType>(){
            {
                add(EntryType.法律);
                add(EntryType.地方政府规章);
            }
        };

        List<EntryType> types8 = new ArrayList<EntryType>(){
            {
                add(EntryType.行政法规);
                add(EntryType.部门规章);
            }
        };
        
        List<EntryType> types9 = new ArrayList<EntryType>(){
            {
                add(EntryType.行政法规);
                add(EntryType.地方政府规章);
            }
        };

        List<EntryType> types10 = new ArrayList<EntryType>(){
            {
                add(EntryType.部门规章);
                add(EntryType.地方政府规章);
            }
        };

        List<EntryType> types11 = new ArrayList<EntryType>(){
            {
                add(EntryType.法律);
                add(EntryType.行政法规);
                add(EntryType.部门规章);
            }
        };

        List<EntryType> types12 = new ArrayList<EntryType>(){
            {
                add(EntryType.法律);
                add(EntryType.行政法规);
                add(EntryType.地方政府规章);
            }
        };

        List<EntryType> types13 = new ArrayList<EntryType>(){
            {
                add(EntryType.法律);
                add(EntryType.部门规章);
                add(EntryType.地方政府规章);
            }
        };
        List<EntryType> types14 = new ArrayList<EntryType>(){
            {
                add(EntryType.行政法规);
                add(EntryType.部门规章);
                add(EntryType.地方政府规章);
            }
        };

        List<EntryType> types15 = new ArrayList<EntryType>(){
            {
                add(EntryType.法律);
                add(EntryType.行政法规);
                add(EntryType.部门规章);
                add(EntryType.地方政府规章);
            }
        };
        
        clustersHead.add(new Cluster<>(center1,types1));
        clustersHead.add(new Cluster<>(center2,types2));
        clustersHead.add(new Cluster<>(center3,types3));
        clustersHead.add(new Cluster<>(center4,types4));
        clustersHead.add(new Cluster<>(center5,types5));
        clustersHead.add(new Cluster<>(center6,types6));
        clustersHead.add(new Cluster<>(center7,types7));
        clustersHead.add(new Cluster<>(center8,types8));
        clustersHead.add(new Cluster<>(center9,types9));
        clustersHead.add(new Cluster<>(center10,types10));
        clustersHead.add(new Cluster<>(center11,types11));
        clustersHead.add(new Cluster<>(center12,types12));
        clustersHead.add(new Cluster<>(center13,types13));
        clustersHead.add(new Cluster<>(center14,types14));
        clustersHead.add(new Cluster<>(center15,types15));

        clustersTail.add(new Cluster<>(center1,types1));
        clustersTail.add(new Cluster<>(center2,types2));
        clustersTail.add(new Cluster<>(center3,types3));
        clustersTail.add(new Cluster<>(center4,types4));
        clustersTail.add(new Cluster<>(center5,types5));
        clustersTail.add(new Cluster<>(center6,types6));
        clustersTail.add(new Cluster<>(center7,types7));
        clustersTail.add(new Cluster<>(center8,types8));
        clustersTail.add(new Cluster<>(center9,types9));
        clustersTail.add(new Cluster<>(center10,types10));
        clustersTail.add(new Cluster<>(center11,types11));
        clustersTail.add(new Cluster<>(center12,types12));
        clustersTail.add(new Cluster<>(center13,types13));
        clustersTail.add(new Cluster<>(center14,types14));
        clustersTail.add(new Cluster<>(center15,types15));

        Kmeans.kmeans(headVectorsMap,clustersHead);
        Kmeans.kmeans(tailVectorsMap,clustersTail);

//        for(Cluster cluster:clustersHead){
//            cluster.sub(0.1f);
//        }
//
//        for(Cluster cluster:clustersTail){
//            cluster.sub(0.1f);
//        }

        Map<String, ComplexEntry<EntryType>> word2ComplexEntry = GraphBuildExecuteHelper.MergeHeadAndTailClusters(typeIntegerMap,clustersHead,clustersTail);
//        GraphBuildExecuteHelper.graphBuild(word2ComplexEntry,passageNodesLevel3);


        return;

    }

}